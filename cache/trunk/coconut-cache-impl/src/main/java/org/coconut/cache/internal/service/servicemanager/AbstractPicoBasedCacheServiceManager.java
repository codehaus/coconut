package org.coconut.cache.internal.service.servicemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.internal.service.servicemanager.AbstractCacheServiceManager.RunState;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycleInitializer;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.picocontainer.PicoContainer;
import org.coconut.internal.picocontainer.defaults.DefaultPicoContainer;

public abstract class AbstractPicoBasedCacheServiceManager extends AbstractCacheServiceManager {

    /** The cache exception handler. */
    final CacheExceptionHandler ces;

    /** The container with services. */
    final DefaultPicoContainer container = new DefaultPicoContainer();

    final Map<Class<?>, Object> publicServices;

    final List<ServiceHolder> services = new ArrayList<ServiceHolder>();

    /**
     * Creates a new AbstractPicoBasedCacheServiceManager.
     * 
     * @param cache
     *            the cache we are managing
     * @throws NullPointerException
     *             if the specified cache is null
     */
    AbstractPicoBasedCacheServiceManager(Cache<?, ?> cache,
            InternalCacheSupport<?, ?> cacheSupport, CacheConfiguration<?, ?> conf,
            Collection<Class<? extends AbstractCacheLifecycle>> classes) {
        super(cache);
        container.registerComponentInstance(this);
        container.registerComponentInstance(getCache().getName());
        container.registerComponentInstance(conf.getClock());
        container.registerComponentInstance(getCache());
        container.registerComponentInstance(cacheSupport);
        container.registerComponentInstance(conf);
        for (AbstractCacheServiceConfiguration<?, ?> c : conf.getAllConfigurations()) {
            container.registerComponentInstance(c);
        }
        for (Class<? extends AbstractCacheLifecycle> cla : classes) {
            container.registerComponentImplementation(cla);
        }

        services.addAll(initializeServices());
        ces = initializeCacheExceptionService();
        try {
            Map<Class<?>, Object> tmp = new HashMap<Class<?>, Object>();
            tmp.put(CacheServiceManagerService.class, ServiceManagerUtil.wrapService(this));
            tmp.putAll(initialize());
            publicServices = Collections.unmodifiableMap(tmp);
        } catch (RuntimeException e) {
            ces.terminated(tryTerminateServices());
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

    private Map<Class<?>, Object> initialize() {
        Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
        final CacheConfiguration conf = (CacheConfiguration) container
                .getComponentInstance(CacheConfiguration.class);
        final CacheExceptionHandler ces = ((InternalCacheExceptionService) container
                .getComponentInstanceOfType(InternalCacheExceptionService.class))
                .getExceptionHandler();
        for (ServiceHolder si : services) {
            try {
                final Map<Class<?>, Object> tmpMap = new HashMap<Class<?>, Object>();
                si.initialize(new CacheLifecycleInitializer() {
                    public CacheConfiguration<?, ?> getCacheConfiguration() {
                        return conf;
                    }

                    public Class<? extends Cache> getCacheType() {
                        return getCache().getClass();
                    }

                    public <T> void registerService(Class<T> clazz, T service) {
                        tmpMap.put(clazz, service);
                    }
                });
                map.putAll(tmpMap);
            } catch (RuntimeException re) {
                ces.cacheInitializationFailed(conf, getCache().getClass(), si.getService(), re);
                throw re;
            }
        }
        return map;
    }

    private CacheExceptionHandler initializeCacheExceptionService() {
        CacheConfiguration conf = (CacheConfiguration) container
                .getComponentInstance(CacheConfiguration.class);
        CacheExceptionHandler ces = ((InternalCacheExceptionService) container
                .getComponentInstanceOfType(InternalCacheExceptionService.class))
                .getExceptionHandler();
        ces.initialize(conf);
        return ces;
    }

    private List<ServiceHolder> initializeServices() {
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
        CacheConfiguration conf = (CacheConfiguration) container
                .getComponentInstance(CacheConfiguration.class);
        for (Object service : conf.serviceManager().getObjects()) {
            if (service instanceof CacheLifecycle) {
                services.add(new ServiceHolder((CacheLifecycle) service, false));
            }
        }
        return services;
    }

    protected void doTerminate() {
        RunState state = getRunState();
        if (state != RunState.TERMINATED) {
            if (state != RunState.COULD_NOT_START) {
                setRunState(RunState.TERMINATED);
            }
            ces.terminated(tryTerminateServices());
        }
    }

    abstract void setRunState(RunState state);

    Map<CacheLifecycle, RuntimeException> tryTerminateServices() {
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
        return m;
    }
}
