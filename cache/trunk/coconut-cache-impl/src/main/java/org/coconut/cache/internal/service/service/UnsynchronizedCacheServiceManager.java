/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheLifecycle;
import org.coconut.cache.internal.service.CacheHelper;
import org.coconut.cache.internal.service.attribute.DefaultCacheAttributeService;
import org.coconut.cache.internal.service.entry.UnsynchronizedEntryFactoryService;
import org.coconut.cache.internal.service.exceptionhandling.DefaultCacheExceptionService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.AbstractCacheService;
import org.coconut.cache.service.servicemanager.CacheService;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.picocontainer.ComponentAdapter;
import org.coconut.internal.picocontainer.defaults.DefaultPicoContainer;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedObject;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class UnsynchronizedCacheServiceManager extends
        AbstractInternalCacheServiceManager {

    public final DefaultPicoContainer container = new DefaultPicoContainer();

    private final Cache<?, ?> cache;

    private final CacheConfiguration<?, ?> conf;

    private final Map<Class<?>, Object> publicServices = new HashMap<Class<?>, Object>();

    private ServiceStatus status = ServiceStatus.NOTRUNNING;

    private final List<ServiceHolder> internalServices = new ArrayList<ServiceHolder>();
    
    private final List<ServiceHolder> externalServices = new ArrayList<ServiceHolder>();

    public UnsynchronizedCacheServiceManager(Cache<?, ?> cache, CacheHelper<?, ?> helper,
            CacheConfiguration<?, ?> conf) {
        this.conf = conf;
        this.cache = cache;
        for (CacheService a : conf.serviceManager().getAllServices()) {
            externalServices.add(new ServiceHolder(a));
        }
        initializePicoContainer(cache, helper, conf);
    }

    /** {@inheritDoc} */
    public Map<Class<?>, Object> getAllPublicServices() {
        return new HashMap<Class<?>, Object>(publicServices);
    }

    /** {@inheritDoc} */
    public ServiceStatus getCurrentState() {
        return status;
    }

    /** {@inheritDoc} */
    public <T> T getPublicService(Class<T> type) {
        lazyStart(false);
        T t = (T) publicServices.get(type);
        if (t == null) {
            throw new IllegalArgumentException("Unknown service " + type);
        }
        return t;
    }

    /** {@inheritDoc} */
    public List getPublicServices() {
        lazyStart(false);
        return new ArrayList(publicServices.values());
    }

    /** {@inheritDoc} */
    public <T> T getService(Class<T> type) {
        T service = (T) container.getComponentInstanceOfType(type);
        if (service == null) {
            throw new IllegalArgumentException("Unknown service " + type);
        }
        return service;
    }

    /** {@inheritDoc} */
    public boolean hasPublicService(Class type) {
        lazyStart(false);
        return publicServices.containsKey(type);
    }

    /** {@inheritDoc} */
    public void lazyStart(boolean failIfShutdown) {
        prestart();
    }

    /** {@inheritDoc} */
    public void prestart() {
        if (status == ServiceStatus.NOTRUNNING) {
            List<AbstractCacheService> l = container
                    .getComponentInstancesOfType(AbstractInternalCacheService.class);

            for (AbstractCacheService a : l) {
                internalServices.add(new ServiceHolder(a));
            }
            for (ServiceHolder si : internalServices) {
                si.initialize(conf);
                publicServices.putAll(si.getPublicService());
                // TODO; check if we have conflicting services
            }
            for (ServiceHolder si : internalServices) {
                si.start(publicServices);
            }
            //register mbeans
            CacheManagementService cms = (CacheManagementService) publicServices
                    .get(CacheManagementService.class);
            if (cms != null) {
                for (ServiceHolder si : internalServices) {
                    si.registerMBeans(cms.getRoot());
                }
            }
            
            //started
            for (ServiceHolder si : internalServices) {
                si.started(cache);
            }
            status = ServiceStatus.RUNNING;
        }
    }

    private void start() {
        // get all registered internal cache services

    }
    private void safeTerminateAll() {
        
    }
    /** {@inheritDoc} */
    public void registerService(Class type, Class<? extends AbstractCacheService> service) {
        if (status != ServiceStatus.NOTRUNNING) {
            throw new IllegalStateException(
                    "CacheServiceManager has already been started");
        }
        ComponentAdapter ca = container.getComponentAdapter(type);
        container.registerComponentImplementation(type, service);
    }

    /** {@inheritDoc} */
    public void registerServices(Class<? extends AbstractCacheService>... services) {
        for (Class<? extends AbstractCacheService> service : services) {
            registerService(service, service);
        }
    }

    private void initializePicoContainer(Cache<?, ?> cache, CacheHelper<?, ?> helper,
            CacheConfiguration<?, ?> conf) {
        container.registerComponentInstance(this);
        container.registerComponentInstance(cache.getName());
        container.registerComponentInstance(cache);
        container.registerComponentInstance(helper);
        container.registerComponentInstance(conf);
        container.registerComponentInstance(conf.getClock());
        container.registerComponentImplementation(DefaultCacheExceptionService.class);
        container.registerComponentImplementation(DefaultCacheAttributeService.class);
        container
                .registerComponentImplementation(UnsynchronizedEntryFactoryService.class);
        for (AbstractCacheServiceConfiguration<?, ?> c : conf.getAllConfigurations()) {
            container.registerComponentInstance(c);
        }
    }

    static class ServiceHolder {

        final CacheLifecycle service;

        private final Map<Class<?>, Object> published = new HashMap<Class<?>, Object>();

        ServiceHolder(CacheLifecycle service) {
            this.service = service;
        }

        Map<Class<?>, Object> getPublicService() {
            return published;
        }

        void initialize(CacheConfiguration conf) {
            service.initialize(conf);
            if (service instanceof CacheService) {
                ((CacheService) service).registerServices(published);
            }
        }

        void start(Map c) {
            if (service instanceof CacheService) {
                ((CacheService) service).start(c);
            }
        }

        void registerMBeans(ManagedGroup parent) {
            if (service instanceof ManagedObject) {
                ((ManagedObject) service).manage(parent);
            }
        }

        void started(Cache<?, ?> c) {
            service.started(c);
        }
    }

    /** {@inheritDoc} */
    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        return isTerminated();
    }

    /** {@inheritDoc} */
    public boolean isShutdown() {
        return status.isShutdown();
    }

    /** {@inheritDoc} */
    public boolean isStarted() {
        return status.isStarted();
    }

    /** {@inheritDoc} */
    public boolean isTerminated() {
        return status.isTerminated();
    }

    /** {@inheritDoc} */
    public void shutdown() {
        status = ServiceStatus.TERMINATED;
    }

    /** {@inheritDoc} */
    public void shutdownNow() {
        shutdown();// synchronous shutdown
    }
}
