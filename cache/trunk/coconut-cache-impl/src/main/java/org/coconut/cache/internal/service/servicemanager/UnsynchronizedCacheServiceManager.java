/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.InternalCacheSupport;
import org.coconut.cache.internal.service.attribute.DefaultCacheAttributeService;
import org.coconut.cache.internal.service.entry.UnsynchronizedEntryFactoryService;
import org.coconut.cache.internal.service.exceptionhandling.DefaultCacheExceptionService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.AsynchronousShutdownObject;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.picocontainer.ComponentAdapter;
import org.coconut.internal.picocontainer.defaults.DefaultPicoContainer;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedObject;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class UnsynchronizedCacheServiceManager extends AbstractCacheServiceManager
        implements CacheServiceManagerService {

    private final DefaultPicoContainer container = new DefaultPicoContainer();

    private final Cache<?, ?> cache;

    private final CacheConfiguration<?, ?> conf;

    private final List<ServiceHolder> externalServices = new ArrayList<ServiceHolder>();

    private final List<ServiceHolder> internalServices = new ArrayList<ServiceHolder>();

    private final Map<Class<?>, Object> publicServices = new HashMap<Class<?>, Object>();

    private RunState status = RunState.NOTRUNNING;

    public UnsynchronizedCacheServiceManager(Cache<?, ?> cache,
            InternalCacheSupport<?, ?> helper, CacheConfiguration<?, ?> conf) {
        this.conf = conf;
        this.cache = cache;
        for (CacheLifecycle a : conf.serviceManager().getAllServices()) {
            externalServices.add(new ServiceHolder(a));
        }
        initializePicoContainer(cache, helper, conf);
        publicServices.put(CacheServiceManagerService.class, ServiceManagerUtil
                .wrapService(this));
    }

    /** {@inheritDoc} */
    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        return isTerminated();
    }

    /** {@inheritDoc} */
    public Map<Class<?>, Object> getAllServices() {
        return new HashMap<Class<?>, Object>(publicServices);
    }

    /** {@inheritDoc} */
    RunState getRunState() {
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
    public <T> T getInternalService(Class<T> type) {
        T service = (T) container.getComponentInstanceOfType(type);
        if (service == null) {
            throw new IllegalArgumentException("Unknown service " + type);
        }
        return service;
    }

    /** {@inheritDoc} */
    public boolean hasService(Class<?> type) {
        lazyStart(false);
        return publicServices.containsKey(type);
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
    public void lazyStart(boolean failIfShutdown) {
        if (failIfShutdown && status.isShutdown()) {
            throw new IllegalStateException("Cache has been shutdown");
        }
        prestart();

    }

    /** {@inheritDoc} */
    public void prestart() {
        if (status == RunState.NOTRUNNING) {
            List<AbstractCacheLifecycle> l = container
                    .getComponentInstancesOfType(AbstractCacheLifecycle.class);

            for (AbstractCacheLifecycle a : l) {
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
            // register mbeans
            CacheManagementService cms = (CacheManagementService) publicServices
                    .get(CacheManagementService.class);
            if (cms != null) {
                for (ServiceHolder si : internalServices) {
                    si.registerMBeans(cms.getRoot());
                }
            }

            // started
            for (ServiceHolder si : internalServices) {
                si.started(cache);
            }
            status = RunState.RUNNING;
        }
    }

    /** {@inheritDoc} */
    public void registerService(Class type,
            Class<? extends AbstractCacheLifecycle> service) {
        if (status != RunState.NOTRUNNING) {
            throw new IllegalStateException(
                    "CacheServiceManager has already been started");
        }
        ComponentAdapter ca = container.getComponentAdapter(type);
        container.registerComponentImplementation(type, service);
    }

    /** {@inheritDoc} */
    public void registerServices(Class<? extends AbstractCacheLifecycle>... services) {
        for (Class<? extends AbstractCacheLifecycle> service : services) {
            registerService(service, service);
        }
    }

    /** {@inheritDoc} */
    public void shutdown() {
        if (status==RunState.RUNNING) {
            status =RunState.SHUTDOWN;
            for (ServiceHolder si : internalServices) {
                si.shutdown();
            }
            for (ServiceHolder si : internalServices) {
                si.terminated();
            }
            status = RunState.TERMINATED;
        }
    }

    /** {@inheritDoc} */
    public void shutdownNow() {
        shutdown();// synchronous shutdown
    }

    private void initializePicoContainer(Cache<?, ?> cache,
            InternalCacheSupport<?, ?> helper, CacheConfiguration<?, ?> conf) {
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

        private final Map<Class<?>, Object> published = new HashMap<Class<?>, Object>();

        private final Collection<ServiceHolder> children = new ArrayList<ServiceHolder>();

        private final Collection<ManagedObject> managedChildren = new ArrayList<ManagedObject>();
        final CacheLifecycle service;

        ServiceHolder(CacheLifecycle service) {
            this.service = service;
            if (service instanceof CompositeService) {
                for (Object o : ((CompositeService) service).getChildServices()) {
                    if (o instanceof CacheLifecycle) {
                        children.add(new ServiceHolder((CacheLifecycle) o));
                    }
                    if (o instanceof ManagedObject) {
                        managedChildren.add((ManagedObject) o);
                    }
                }
            }
        }

        Map<Class<?>, Object> getPublicService() {
            return published;
        }

        void initialize(CacheConfiguration conf) {
            service.initialize(conf);
            service.registerServices(published);
            for (ServiceHolder cl : children) {
                cl.initialize(conf);
            }
        }

        void registerMBeans(ManagedGroup parent) {
            if (service instanceof ManagedObject) {
                ((ManagedObject) service).manage(parent);
            }
            for (ManagedObject cl : managedChildren) {
                cl.manage(parent);
            }
        }

        void start(Map c) {
            service.start(c);
            for (ServiceHolder cl : children) {
                cl.start(c);
            }
        }

        void started(Cache<?, ?> c) {
            service.started(c);
            for (ServiceHolder cl : children) {
                cl.started(c);
            }
        }

        void shutdown() {
            service.shutdown();
            for (ServiceHolder cl : children) {
                cl.shutdown();
            }
        }

        void terminated() {
            service.terminated();
            for (ServiceHolder cl : children) {
                cl.terminated();
            }
        }
    }

    public void shutdownServiceAsynchronously(AsynchronousShutdownObject service2) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public <T> T getService(Class<T> type) {
        lazyStart(false);
        T t = (T) publicServices.get(type);
        if (t == null) {
            throw new IllegalArgumentException("Unknown service " + type);
        }
        return t;
    }

    public void start() {}
}
