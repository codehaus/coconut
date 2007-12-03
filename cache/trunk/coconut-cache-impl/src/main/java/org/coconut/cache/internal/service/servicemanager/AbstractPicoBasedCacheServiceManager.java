package org.coconut.cache.internal.service.servicemanager;

import java.util.Collection;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.picocontainer.defaults.DefaultPicoContainer;

public abstract class AbstractPicoBasedCacheServiceManager extends AbstractCacheServiceManager {

    /** The cache exception handler. */
    CacheExceptionHandler ces;

    /** The container with services. */
    final DefaultPicoContainer container = new DefaultPicoContainer();

    CacheConfiguration conf;

    /**
     * Creates a new AbstractPicoBasedCacheServiceManager.
     * 
     * @param cache
     *            the cache we are managing
     * @throws NullPointerException
     *             if the specified cache is null
     */
    AbstractPicoBasedCacheServiceManager(Cache<?, ?> cache, InternalCacheSupport<?, ?> cacheSupport,
            CacheConfiguration<?, ?> conf,
            Collection<Class<? extends AbstractCacheLifecycle>> classes) {
        super(cache);
        this.conf = conf;
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
    }

    /** {@inheritDoc} */
    public <T> T getInternalService(Class<T> type) {
        T service = (T) container.getComponentInstanceOfType(type);
        if (service == null) {
            throw new IllegalArgumentException("Unknown service " + type);
        }
        return service;
    }
}
