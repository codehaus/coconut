package org.coconut.cache.internal.service.servicemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.servicemanager.CacheLifecycle.Initializer;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.picocontainer.defaults.DefaultPicoContainer;
import org.coconut.management.ManagedLifecycle;

public abstract class AbstractCacheServiceManager implements InternalCacheServiceManager {

    /** The cache we are managing. */
    final Cache<?, ?> cache;

    /** The picocontainer used to wire servicers. */
    private final DefaultPicoContainer container = new DefaultPicoContainer();

    /** A map of services that can be retrieved from {@link Cache#getService(Class)}. */
    private final Map<Class<?>, Object> publicServices;

    /** The list of services. */
    private final List<ServiceHolder> services = new ArrayList<ServiceHolder>();

    /** The cache exception services. */
    private final InternalCacheExceptionService ces;

    /** Any exception that might have been encountered while starting up. */
    volatile RuntimeException startupException;

    /**
     * Creates a new AbstractPicoBasedCacheServiceManager.
     * 
     * @param cache
     *            the cache we are managing
     * @throws NullPointerException
     *             if the specified cache is null
     */
    AbstractCacheServiceManager(Cache<?, ?> cache,
            InternalCacheSupport<?, ?> cacheSupport, CacheConfiguration<?, ?> conf,
            Collection<Class<? extends AbstractCacheLifecycle>> classes) {
        this.cache = cache;
        container.registerComponentInstance(this);
        container.registerComponentInstance(cache.getName());
        container.registerComponentInstance(conf.getClock());
        container.registerComponentInstance(cache);
        container.registerComponentInstance(cacheSupport);
        container.registerComponentInstance(conf);
        for (AbstractCacheServiceConfiguration<?, ?> c : conf.getAllConfigurations()) {
            container.registerComponentInstance(c);
        }
        for (Class<? extends AbstractCacheLifecycle> cla : classes) {
            container.registerComponentImplementation(cla);
        }

        services.addAll(createServiceHolders(conf));

        // initialize exception service
        ces = lookup(InternalCacheExceptionService.class);
        ces.getHandler().initialize(conf);

        try {
            Map<Class<?>, Object> tmp = new HashMap<Class<?>, Object>();
            tmp.put(CacheServiceManagerService.class, ServiceManagerUtil.wrapService(this));
            tmp.putAll(initialize(conf));
            publicServices = Collections.unmodifiableMap(tmp);
        } catch (RuntimeException e) {
            terminateServices();
            throw e;
        }
    }

    /** {@inheritDoc} */
    public final Map<Class<?>, Object> getAllServices() {
        return publicServices;
    }

    /** {@inheritDoc} */
    public <T> T getInternalService(Class<T> type) {
        T service = (T) container.getComponentInstanceOfType(type);
        if (service == null) {
            throw new IllegalArgumentException("Unknown service " + type);
        }
        return service;
    }

    /** {@inheritDoc} */
    public boolean isStarted() {
        return getRunState().isStarted() && startupException == null;
    }

    private List<ServiceHolder> createServiceHolders(CacheConfiguration conf) {
        List<ServiceHolder> services = new ArrayList<ServiceHolder>();
        List<AbstractCacheLifecycle> l = container
                .getComponentInstancesOfType(AbstractCacheLifecycle.class);

        for (AbstractCacheLifecycle a : l) {
            if (a instanceof CompositeService) {
                for (Object o : ((CompositeService) a).getChildServices()) {
                    if (o instanceof CacheLifecycle) {
                        services.add(new ServiceHolder((CacheLifecycle) o, false));
                    }
                }
            }
            services.add(new ServiceHolder(a, true));
        }
        for (Object service : conf.serviceManager().getObjects()) {
            if (service instanceof CacheLifecycle) {
                services.add(new ServiceHolder((CacheLifecycle) service, false));
            }
        }
        return services;
    }

    private Map<Class<?>, Object> initialize(final CacheConfiguration conf) {
        Map<Class<?>, Object> result = new HashMap<Class<?>, Object>();
        for (ServiceHolder si : services) {
            try {
                final Map<Class<?>, Object> tmpMap = new HashMap<Class<?>, Object>();
                si.initialize(new Initializer() {
                    public CacheConfiguration<?, ?> getCacheConfiguration() {
                        return conf;
                    }

                    public Class<? extends Cache> getCacheType() {
                        return cache.getClass();
                    }

                    public <T> void registerService(Class<T> clazz, T service) {
                        tmpMap.put(clazz, service);
                    }
                });
                result.putAll(tmpMap);
            } catch (RuntimeException re) {
                ces.getHandler().lifecycleInitializationFailed(conf, cache.getClass(),
                        si.getService(), re);
                throw re;
            }
        }
        return result;
    }

    private List<ManagedLifecycle> initializeManagedObjects() {
        List<ManagedLifecycle> managedObjects = new ArrayList<ManagedLifecycle>();
        List<AbstractCacheLifecycle> l = container
                .getComponentInstancesOfType(AbstractCacheLifecycle.class);

        for (AbstractCacheLifecycle a : l) {
            if (a instanceof CompositeService) {
                for (Object o : ((CompositeService) a).getChildServices()) {
                    if (o instanceof ManagedLifecycle) {
                        managedObjects.add((ManagedLifecycle) o);
                    }
                }
            }
            if (a instanceof ManagedLifecycle) {
                managedObjects.add((ManagedLifecycle) a);
            }
        }
        CacheConfiguration conf = lookup(CacheConfiguration.class);
        for (Object service : conf.serviceManager().getObjects()) {
            if (service instanceof ManagedLifecycle) {
                managedObjects.add((ManagedLifecycle) service);
            }
        }
        return managedObjects;
    }

    private <T> T lookup(Class<? extends T> clazz) {
        return (T) container.getComponentInstanceOfType(clazz);
    }

    private void managementStart() {
        // register mbeans
        CacheManagementService cms = (CacheManagementService) publicServices
                .get(CacheManagementService.class);
        if (cms != null) {
            List<ManagedLifecycle> managedObjects = initializeManagedObjects();
            for (ManagedLifecycle si : managedObjects) {
                try {
                    si.manage(cms);
                } catch (RuntimeException re) {
                    startupException = new CacheException("Could not start the cache", re);
                    CacheConfiguration conf = lookup(CacheConfiguration.class);
                    ces.getHandler().lifecycleStartFailed(conf, cache.getClass(), null, re);
                    shutdown();
                    throw startupException;
                } catch (Error er) {
                    startupException = new CacheException("Could not start the cache", er);
                    setRunState(RunState.TERMINATED);
                    throw er;
                }
            }
        }
    }

    private void servicesStarted() {
        setRunState(RunState.RUNNING);
        for (ServiceHolder si : services) {
            try {
                si.started(cache);
            } catch (RuntimeException re) {
                startupException = new CacheException("Could not start the cache", re);
                final CacheConfiguration conf = (CacheConfiguration) container
                        .getComponentInstance(CacheConfiguration.class);
                ces.getHandler().lifecycleStartFailed(conf, cache.getClass(), si.getService(),
                        re);
                shutdown();
                throw startupException;
            } catch (Error er) {
                startupException = new CacheException("Could not start the cache", er);
                setRunState(RunState.TERMINATED);
                throw er;
            }
        }
        getInternalService(InternalCacheListener.class).afterStart(cache);
    }

    private void startServices() {
        setRunState(RunState.STARTING);
        CacheServiceManagerService service = lookup(CacheServiceManagerService.class);
        for (ServiceHolder si : services) {
            try {
                si.start(service);
            } catch (Exception re) {
                startupException = new CacheException("Could not start the cache", re);
                final CacheConfiguration conf = (CacheConfiguration) container
                        .getComponentInstance(CacheConfiguration.class);
                ces.getHandler().lifecycleStartFailed(conf, cache.getClass(), si.getService(),
                        re);
                shutdown();
                throw startupException;
            } catch (Error er) {
                startupException = new CacheException("Could not start the cache", er);
                setRunState(RunState.TERMINATED);
                throw er;
            }
        }
    }

    private void terminateServices() {
        Map<CacheLifecycle, RuntimeException> m = new HashMap<CacheLifecycle, RuntimeException>();
        List<ServiceHolder> l = new ArrayList<ServiceHolder>(services);
        Collections.reverse(l);
        for (ServiceHolder sh : l) {
            if (sh.isInitialized()) {
                try {
                    sh.terminated();
                } catch (RuntimeException e) {
                    m.put(sh.getService(), e);
                }
            }
        }
        ces.getHandler().terminated(m);
    }

    void checkStartupException() {
        RuntimeException re = startupException;
        if (re != null) {
            throw re;
        }
    }

    void doStart() {
        try {
            startServices();
            managementStart();
            servicesStarted();
        } finally {
            container.dispose();
        }
    }

    void doTerminate() {
        RunState state = getRunState();
        if (!state.isTerminated()) {
            try {
                terminateServices();
            } finally {
                setRunState(state.advanceToTerminated());
            }
        }
    }

    void initiateShutdown() {
        setRunState(RunState.SHUTDOWN);
        List<ServiceHolder> l = new ArrayList<ServiceHolder>(services);
        Collections.reverse(l);
        for (ServiceHolder sh : l) {
            if (sh.isStarted()) {
                try {
                    shutdownService(sh);
                } catch (RuntimeException e) {
                    ces.getHandler().lifecycleShutdownFailed(ces.createContext(), sh.getService(),
                            e);
                }
            }
        }
    }

    void initiateShutdownNow() {
        setRunState(RunState.STOP);
        List<ServiceHolder> l = new ArrayList<ServiceHolder>(services);
        Collections.reverse(l);
        for (ServiceHolder sh : l) {
            if (sh.isStarted()) {
                try {
                    sh.shutdownNow();
                } catch (RuntimeException e) {
                    ces.getHandler().lifecycleShutdownFailed(ces.createContext(), sh.getService(),
                            e);
                }
            }
        }
    }

    abstract void setRunState(RunState state);

    void shutdownService(ServiceHolder holder) {
        holder.shutdown(new CacheLifecycle.Shutdown() {
            public void shutdownAsynchronously(Callable<?> callable) {
                throw new UnsupportedOperationException(
                        "Cache does not support asynchronous shutdown");
            }
        });
    }
    /**
     * Returns the state of the cache.
     * 
     * @return the state of the cache
     */
    abstract RunState getRunState();
    
    /** {@inheritDoc} */
    public final <T> T getService(Class<T> serviceType) {
        if (serviceType == null) {
            throw new NullPointerException("serviceType is null");
        }
        T t = (T) getAllServices().get(serviceType);
        if (t == null) {
            throw new IllegalArgumentException("Unknown service " + serviceType);
        }
        return t;
    }

    /** {@inheritDoc} */
    public final boolean hasService(Class<?> type) {
        return getAllServices().containsKey(type);
    }

    /** {@inheritDoc} */
    public boolean isShutdown() {
        return getRunState().isShutdown();
    }

    /** {@inheritDoc} */
    public boolean isTerminated() {
        return getRunState().isTerminated();
    }

    /** {@inheritDoc} */
    public <T> T getServiceFromCache(Class<T> serviceType) {
        lazyStart(false);
        return getService(serviceType);
    }

}
