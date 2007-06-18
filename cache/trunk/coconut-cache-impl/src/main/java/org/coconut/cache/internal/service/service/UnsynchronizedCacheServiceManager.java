/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.attribute.DefaultCacheAttributeService;
import org.coconut.cache.internal.service.entry.UnsynchronizedEntryFactoryService;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.service.exceptionhandling.AbstractCacheExceptionHandler;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlers;
import org.coconut.cache.service.servicemanager.AbstractCacheService;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.core.Logger;
import org.coconut.internal.picocontainer.ComponentAdapter;
import org.coconut.internal.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class UnsynchronizedCacheServiceManager extends
        AbstractInternalCacheServiceManager {

    private ServiceStatus status = ServiceStatus.NOTRUNNING;

    private final CacheConfiguration<?, ?> conf;

    public final DefaultPicoContainer container = new DefaultPicoContainer();

    private final Cache<?, ?> cache;

    private final Map<Class<?>, Object> publicServices = new HashMap<Class<?>, Object>();

    public UnsynchronizedCacheServiceManager(Cache<?, ?> cache, CacheHelper<?, ?> helper,
            CacheConfiguration<?, ?> conf) {
        this.conf = conf;
        this.cache = cache;
        initializePicoContainer(cache, helper, conf);
    }

    private void initializePicoContainer(Cache<?, ?> cache, CacheHelper<?, ?> helper,
            CacheConfiguration<?, ?> conf) {
        container.registerComponentInstance(this);
        container.registerComponentInstance(cache.getName());
        container.registerComponentInstance(cache);
        container.registerComponentInstance(helper);
        container.registerComponentInstance(conf);
        container.registerComponentInstance(conf.getClock());
        AbstractCacheExceptionHandler<?, ?> h = conf.exceptionHandling()
                .getExceptionHandler();
        if (h == null) {
            h = CacheExceptionHandlers.defaultExceptionHandler();
        }
        h.setCacheName(cache.getName());
        Logger l = conf.exceptionHandling().getExceptionLogger();
        if (l == null) {
            l = conf.getDefaultLog();
        }
        if (l != null) {
            h.setLogger(l);
        }
        container.registerComponentInstance(h);
        container.registerComponentImplementation(DefaultCacheAttributeService.class);
        container
                .registerComponentImplementation(UnsynchronizedEntryFactoryService.class);
        for (AbstractCacheServiceConfiguration<?, ?> c : conf.getConfigurations()) {
            container.registerComponentInstance(c);
        }
    }

    /**
     * @see org.coconut.cache.internal.service.InternalCacheServiceManager#lazyStart(boolean)
     */
    public void lazyStart(boolean failIfShutdown) {
        prestart();
    }

    /**
     * @see org.coconut.cache.internal.service.service.InternalCacheServiceManager#getCurrentState()
     */
    public ServiceStatus getCurrentState() {
        return status;
    }

    /**
     * @see org.coconut.cache.internal.service.service.InternalCacheServiceManager#prestart()
     */
    public void prestart() {
        if (status == ServiceStatus.NOTRUNNING) {
            List<AbstractCacheService> l = container
                    .getComponentInstancesOfType(AbstractCacheService.class);

            List<ServiceInfo> info = new ArrayList<ServiceInfo>();
            for (AbstractCacheService a : l) {
                info.add(new ServiceInfo(a));
            }
            for (ServiceInfo si : info) {
                si.initialize(conf);
                publicServices.putAll(si.getPublicService());
            }
            for (ServiceInfo si : info) {
                si.start(publicServices);
            }
            for (ServiceInfo si : info) {
                si.started(cache);
            }
            status = ServiceStatus.RUNNING;
        }
    }

    static class ServiceInfo {
        ServiceInfo(AbstractCacheService service) {
            this.service = service;
        }

        final AbstractCacheService service;

        private ServiceStatus status = ServiceStatus.NOTRUNNING;

        private final Map<Class<?>, Object> published = new HashMap<Class<?>, Object>();

        void initialize(CacheConfiguration conf) {
            service.initialize(conf, published);
        }

        void start(Map c) {
            service.start(c);
        }

        void started(Cache<?, ?> c) {
            service.started(c);
        }

        Map<Class<?>, Object> getPublicService() {
            return published;
        }
    }

    public void registerService(Class type, Class<? extends AbstractCacheService> service) {
        if (status != ServiceStatus.NOTRUNNING) {
            throw new IllegalStateException(
                    "CacheServiceManager has already been started");
        }
        ComponentAdapter ca = container.getComponentAdapter(type);
// if (ca != null
// && DummyCacheService.class.isAssignableFrom(ca
// .getComponentImplementation())) {
// // unregister dummy
// container.unregisterComponent(type);
// }
        container.registerComponentImplementation(type, service);
    }

    /**
     * @see org.coconut.cache.internal.service.service.InternalCacheServiceManager#getPublicServices()
     */
    public List getPublicServices() {
        lazyStart(false);
        return new ArrayList(publicServices.values());
    }

    /**
     * @see org.coconut.cache.internal.service.service.InternalCacheServiceManager#hasPublicService(java.lang.Class)
     */
    public boolean hasPublicService(Class type) {
        lazyStart(false);
        return publicServices.containsKey(type);
    }

    /**
     * @see org.coconut.cache.internal.service.service.InternalCacheServiceManager#getPublicService(java.lang.Class)
     */
    public <T> T getPublicService(Class<T> type) {
        lazyStart(false);
        T t = (T) publicServices.get(type);
        if (t == null) {
            throw new IllegalArgumentException("Unknown service " + type);
        }
        return t;
    }

    /**
     * @see org.coconut.cache.internal.service.service.InternalCacheServiceManager#registerService(java.lang.Class)
     */
    public void registerServices(Class<? extends AbstractCacheService>... services) {
        for (Class<? extends AbstractCacheService> service : services) {
            registerService(service, service);
        }
    }

    /**
     * @see org.coconut.cache.internal.service.service.InternalCacheServiceManager#getService(java.lang.Class)
     */
    public <T> T getService(Class<T> type) {
        T service = (T) container.getComponentInstanceOfType(type);
        if (service == null) {
            throw new IllegalArgumentException("Unknown service " + type);
        }
        return service;
    }

    public Map<Class<?>, Object> getAllPublicServices() {
        return new HashMap<Class<?>, Object>((Map) publicServices);
    }
}
