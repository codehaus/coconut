package org.coconut.cache.internal.service.servicemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.picocontainer.defaults.DefaultPicoContainer;

public abstract class AbstractPicoBasedCacheServiceManager extends AbstractCacheServiceManager {

    final Map<Class<?>, Object> publicServices;

    /** The cache exception handler. */
    final CacheExceptionHandler ces;

    /** The container with services. */
    final DefaultPicoContainer container = new DefaultPicoContainer();

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

        services.addAll(ServiceManagerUtil.initializeServices(container));
        ces = ServiceManagerUtil.initializeCacheExceptionService(container);
        try {
            Map<Class<?>, Object> tmp = new HashMap<Class<?>, Object>();
            tmp.put(CacheServiceManagerService.class, ServiceManagerUtil.wrapService(this));
            tmp.putAll(ServiceManagerUtil.initialize(container, services, getCache().getClass()));
            publicServices = Collections.unmodifiableMap(tmp);
        } catch (RuntimeException e) {
            ces.terminated(tryTerminateServices());
            throw e;
        }
    }

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

    /** {@inheritDoc} */
    public <T> T getInternalService(Class<T> type) {
        T service = (T) container.getComponentInstanceOfType(type);
        if (service == null) {
            throw new IllegalArgumentException("Unknown service " + type);
        }
        return service;
    }
    
    /** {@inheritDoc} */
    public final Map<Class<?>, Object> getAllServices() {
        return publicServices;
    }
}
