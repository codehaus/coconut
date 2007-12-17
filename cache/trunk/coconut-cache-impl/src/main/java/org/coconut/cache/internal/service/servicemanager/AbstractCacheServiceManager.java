/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
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
import org.coconut.cache.internal.service.debug.InternalDebugService;
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
import org.coconut.internal.util.ClassUtils;
import org.coconut.internal.util.TimeFormatter;
import org.coconut.management.ManagedLifecycle;

public abstract class AbstractCacheServiceManager implements InternalCacheServiceManager {

    /** The picocontainer used to wire servicers. */
    private final DefaultPicoContainer container = new DefaultPicoContainer();

    /** The cache debug services. */
    private final InternalDebugService debugService;

    /** A map of services that can be retrieved from {@link Cache#getService(Class)}. */
    private final Map<Class<?>, Object> publicServices;

    /** The list of services. */
    private final List<ServiceHolder> services = new ArrayList<ServiceHolder>();

    /** The cache we are managing. */
    final Cache<?, ?> cache;

    /** The cache exception services. */
    final InternalCacheExceptionService ces;

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
    AbstractCacheServiceManager(Cache<?, ?> cache, InternalCacheSupport<?, ?> cacheSupport,
            CacheConfiguration<?, ?> conf, Collection<Class<?>> classes) {
        long initializationStart = System.nanoTime();
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
        for (Class cla : ServiceManagerUtil.removeUnusedServices(conf, classes)) {
            container.registerComponentImplementation(cla);
        }
        services.addAll(createServiceHolders(conf));

        // initialize exception service
        ces = lookup(InternalCacheExceptionService.class);
        ces.getHandler().initialize(conf);
        debugService = lookup(InternalDebugService.class);
        if (debugService.isDebugEnabled()) {
            debugService.debug("Cache initializing [name = " + cache.getName() + ", type = "
                    + cache.getClass() + "]");
            debugService.debug("  " + services.size()
                    + " services to initialize, (*) marked services are used specified");

        }
        try {
            Map<Class<?>, Object> tmp = new HashMap<Class<?>, Object>();
            tmp.put(CacheServiceManagerService.class, ServiceManagerUtil.wrapService(this));
            tmp.putAll(initialize(conf));
            publicServices = Collections.unmodifiableMap(tmp);
        } catch (RuntimeException e) {
            terminateServices();
            throw e;
        }
        if (debugService.isDebugEnabled()) {
            debugService.debug("Cache initialized succesfully [initialization time = "
                    + TimeFormatter.SHORT_FORMAT.formatNanos(System.nanoTime()
                            - initializationStart) + ", services initialized = " + services.size()
                    + "]");
        }
    }

    /** {@inheritDoc} */
    public final Map<Class<?>, Object> getAllServices() {
        return publicServices;
    }

    /** {@inheritDoc} */
    public <T> T getInternalService(Class<T> type) {
        T service = (T) container.getComponentInstanceOfType(type);
        return service;
    }

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
    public <T> T getServiceFromCache(Class<T> serviceType) {
        lazyStart(false);
        return getService(serviceType);
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
    public boolean isStarted() {
        return getRunState().isStarted() && startupException == null;
    }

    /** {@inheritDoc} */
    public boolean isTerminated() {
        return getRunState().isTerminated();
    }

    /** {@inheritDoc} */
    public void shutdown() {
        shutdown(false);
    }

    /** {@inheritDoc} */
    public void shutdownNow() {
        shutdown(true);
    }

    private List<ServiceHolder> createServiceHolders(CacheConfiguration conf) {
        List<ServiceHolder> services = new ArrayList<ServiceHolder>();
        List<AbstractCacheLifecycle> l = container
                .getComponentInstancesOfType(AbstractCacheLifecycle.class);

        for (AbstractCacheLifecycle a : l) {
            services.add(new ServiceHolder(a, true));
        }
        // initialize internal services last
        for (AbstractCacheLifecycle a : l) {
            if (a instanceof CompositeService) {
                for (Object o : ((CompositeService) a).getChildServices()) {
                    if (o instanceof CacheLifecycle) {
                        services.add(new ServiceHolder((CacheLifecycle) o, false));
                    }
                }
            }
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
        final boolean debug = debugService.isDebugEnabled();
        for (ServiceHolder si : services) {
            try {
                final Map<Class<?>, Object> tmpMap = new HashMap<Class<?>, Object>();
                long start = System.nanoTime();
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
                if (debug && overrideInitialize(si.getService().getClass())) {
                    StringBuilder sb = new StringBuilder();
                    if (si.isInternal()) {
                        sb.append("  "); // indent
                    } else {
                        sb.append(" *"); // indent
                    }
                    sb.append(si.getService());
                    sb.append(": Initialized Succesfully [duration = ");
                    sb.append(TimeFormatter.SHORT_FORMAT.formatNanos(System.nanoTime() - start));
                    sb.append(", class = ");
                    sb.append(si.getService().getClass().getName());
                    sb.append("]");
                    debugService.debug(sb.toString());
                    for (Map.Entry<Class<?>, Object> me : tmpMap.entrySet()) {
                        debugService.debug("    Registering Service [key = "
                                + me.getKey().getName() + ", service = " + me.getValue() + "]");
                    }
                }
                result.putAll(tmpMap);
            } catch (RuntimeException re) {
                ces.initializationFailed(conf, si.getService(), re);
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
            boolean debug = debugService.isDebugEnabled();
            if (debug) {
                debugService.debug("  calling ManagedLifecycle.manage()");
            }
            List<ManagedLifecycle> managedObjects = initializeManagedObjects();
            for (ManagedLifecycle si : managedObjects) {
                long start = System.nanoTime();
                try {
                    si.manage(cms);
                } catch (RuntimeException re) {
                    startupException = new CacheException("Could not start the cache", re);
                    ces.getHandler().serviceManagerStartFailed(
                            ces.createContext(re, "Could not start the cache"),
                            lookup(CacheConfiguration.class), si);
                    shutdown();
                    throw startupException;
                }

                if (debug) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("    "); // indent
                    sb.append(si);
                    sb.append(": Managed Succesfully [duration = ");
                    sb.append(TimeFormatter.SHORT_FORMAT.formatNanos(System.nanoTime() - start));
                    sb.append(", class = ");
                    sb.append(si.getClass().getName());
                    sb.append("]");
                    debugService.debug(sb.toString());
                }
            }
        }
    }

    private void servicesStarted() {
        setRunState(RunState.RUNNING);
        boolean debug = debugService.isDebugEnabled();
        if (debug) {
            debugService.debug("  calling CacheLifecycle.started()");
        }
        for (ServiceHolder si : services) {
            long start = System.nanoTime();
            try {
                si.started(cache);
            } catch (RuntimeException re) {
                startupException = new CacheException("Could not start the cache", re);
                ces.getHandler().serviceManagerStartFailed(
                        ces.createContext(re, "Could not start the cache"),
                        lookup(CacheConfiguration.class), si.getService());
                shutdown();
                throw startupException;
            }
            if (debug && overrideStarted(si.getService().getClass())) {
                StringBuilder sb = new StringBuilder();
                if (si.isInternal()) {
                    sb.append("    "); // indent
                } else {
                    sb.append("   *"); // indent
                }
                sb.append(si.getService());
                sb.append(": Started Succesfully [duration = ");
                sb.append(TimeFormatter.SHORT_FORMAT.formatNanos(System.nanoTime() - start));
                sb.append(", class = ");
                sb.append(si.getService().getClass().getName());
                sb.append("]");
                debugService.debug(sb.toString());
            }
        }
        getInternalService(InternalCacheListener.class).afterStart(cache);
    }

    private void startServices() {
        setRunState(RunState.STARTING);
        CacheServiceManagerService service = lookup(CacheServiceManagerService.class);
        boolean debug = debugService.isDebugEnabled();
        if (debug) {
            debugService.debug("  calling CacheLifecycle.start()");
        }
        for (ServiceHolder si : services) {
            long start = System.nanoTime();
            try {
                si.start(service);
            } catch (Exception re) {
                startupException = new CacheException("Could not start the cache", re);
                ces.getHandler().serviceManagerStartFailed(
                        ces.createContext(re, "Could not start the cache"),
                        lookup(CacheConfiguration.class), si.getService());
                shutdown();
                throw startupException;
            }
            if (debug && overrideStart(si.getService().getClass())) {
                StringBuilder sb = new StringBuilder();
                if (si.isInternal()) {
                    sb.append("    "); // indent
                } else {
                    sb.append("   *"); // indent
                }
                sb.append(si.getService());
                sb.append(": Start Succesfully [duration = ");
                sb.append(TimeFormatter.SHORT_FORMAT.formatNanos(System.nanoTime() - start));
                sb.append(", class = ");
                sb.append(si.getService().getClass().getName());
                sb.append("]");
                debugService.debug(sb.toString());
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
        long startTime = System.nanoTime();
        if (debugService.isDebugEnabled()) {
            debugService.debug("Cache starting [name = " + cache.getName() + ", type = "
                    + cache.getClass() + "]");
            if (debugService.isTraceEnabled()) {
                    debugService.trace("Cache was started through this call:");
                StackTraceElement[] trace = new Exception().getStackTrace();
                for (int i = 0; i < Math.min(8, trace.length); i++)
                    debugService.trace("    " + trace[i]);
            }
            debugService.debug(services.size()
                    + " services to start, (*) marked services are used specified");

        }
        try {
            startServices();
            managementStart();
            servicesStarted();
        } finally {
            container.dispose();
        }
        if (debugService.isDebugEnabled()) {
            debugService.debug("Cache started succesfully [startup time = "
                    + TimeFormatter.SHORT_FORMAT.formatNanos(System.nanoTime() - startTime)
                    + ", services started = " + services.size() + "]");
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

    /**
     * Returns the state of the cache.
     * 
     * @return the state of the cache
     */
    abstract RunState getRunState();

    void initiateShutdown() {
        List<ServiceHolder> l = new ArrayList<ServiceHolder>(services);
        Collections.reverse(l);
        for (ServiceHolder sh : l) {
            if (sh.isStarted()) {
                try {
                    shutdownService(sh);
                } catch (Exception e) {
                    ces.getHandler().serviceManagerShutdownFailed(
                            ces.createContext(e, "Could not shutdown the service properly"),
                            sh.getService());
                }
            }
        }
    }

    abstract void setRunState(RunState state);

    abstract void shutdown(boolean shutdownNow);

    void shutdownService(ServiceHolder holder) throws Exception {
        holder.shutdown(new CacheLifecycle.Shutdown() {
            public void shutdownAsynchronously(Callable<?> callable) {
                throw new UnsupportedOperationException(
                        "Cache does not support asynchronous shutdown");
            }
        });
    }

    private static boolean overrideInitialize(Class c) {
        return ClassUtils.overridesMethod(AbstractCacheLifecycle.class, c, "initialize",
                Initializer.class);
    }

    private static boolean overrideStart(Class c) {
        return ClassUtils.overridesMethod(AbstractCacheLifecycle.class, c, "start",
                CacheServiceManagerService.class);
    }

    private static boolean overrideStarted(Class c) {
        return ClassUtils.overridesMethod(AbstractCacheLifecycle.class, c, "started", Cache.class);
    }
}
