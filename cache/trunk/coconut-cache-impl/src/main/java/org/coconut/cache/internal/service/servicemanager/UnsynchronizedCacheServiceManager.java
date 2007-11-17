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
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.AsynchronousShutdownObject;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.picocontainer.defaults.DefaultPicoContainer;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedObject;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class UnsynchronizedCacheServiceManager extends AbstractCacheServiceManager implements
        CacheServiceManagerService {

    private final InternalCacheExceptionService ces;

    private final DefaultPicoContainer container = new DefaultPicoContainer();

    private final List<ManagedObject> managedObjects = new ArrayList<ManagedObject>();

    private final Map<Class<?>, Object> publicServices = new HashMap<Class<?>, Object>();

    private RuntimeException startupException;

    private RunState status = RunState.NOTRUNNING;

    ServiceHolder serviceBeingShutdown;

    final List<ServiceHolder> services = new ArrayList<ServiceHolder>();

    public UnsynchronizedCacheServiceManager(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
            CacheConfiguration<?, ?> conf,
            Collection<Class<? extends AbstractCacheLifecycle>> classes) {
        super(cache, conf);

        for (Object o : conf.serviceManager().getObjects()) {
            if (o instanceof CacheLifecycle) {
                services.add(new ServiceHolder((CacheLifecycle) o, false));
            }
            if (o instanceof ManagedObject) {
                managedObjects.add((ManagedObject) o);
            }
        }
        container.registerComponentInstance(this);
        container.registerComponentInstance(cache.getName());
        container.registerComponentInstance(cache);
        container.registerComponentInstance(helper);
        container.registerComponentInstance(conf);
        for (AbstractCacheServiceConfiguration<?, ?> c : conf.getAllConfigurations()) {
            container.registerComponentInstance(c);
        }
        for (Class<? extends AbstractCacheLifecycle> cla : classes) {
            container.registerComponentImplementation(cla);
        }
        List<AbstractCacheLifecycle> l = container
                .getComponentInstancesOfType(AbstractCacheLifecycle.class);

        for (AbstractCacheLifecycle a : l) {
            services.add(new ServiceHolder(a, true));
        }
        publicServices.put(CacheServiceManagerService.class, ServiceManagerUtil.wrapService(this));

        /* Initialize Exception Service */
        ces = (InternalCacheExceptionService) container
                .getComponentInstanceOfType(InternalCacheExceptionService.class);
        ces.getExceptionHandler().initialize(conf);
    }

    /** {@inheritDoc} */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return isTerminated();
    }

    /** {@inheritDoc} */
    public Map<Class<?>, Object> getAllServices() {
        return new HashMap<Class<?>, Object>(publicServices);
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
    public <T> T getService(Class<T> serviceType) {
        if (serviceType == null) {
            throw new NullPointerException("type is null");
        }
        lazyStart(false);
        T t = (T) publicServices.get(serviceType);
        if (t == null) {
            throw new IllegalArgumentException("Unknown service " + serviceType);
        }
        return t;
    }

    /** {@inheritDoc} */
    public boolean hasService(Class<?> type) {
        return publicServices.containsKey(type);
    }

    /** {@inheritDoc} */
    public boolean lazyStart(boolean failIfShutdown) {
        if (status != RunState.RUNNING) {
            if (startupException != null) {
                throw startupException;
            } else if (status == RunState.NOTRUNNING) {
                doStart();
            } else if (failIfShutdown && status.isShutdown()) {
                throw new IllegalStateException("Cache has been shutdown");
            }
            return status == RunState.RUNNING;
        }
        return true;
    }

    /** {@inheritDoc} */
    public void shutdown() {
        if (status == RunState.NOTRUNNING) {
            status = RunState.TERMINATED;
        } else if (status == RunState.RUNNING) {
            getCache().clear();
            status = RunState.SHUTDOWN;
            List<ServiceHolder> shutdown = new ArrayList<ServiceHolder>(services);
            Collections.reverse(shutdown);
            for (ServiceHolder si : shutdown) {
                si.shutdown();
            }
            serviceBeingShutdown = null;
            tryTerminate();
        }
    }

    /** {@inheritDoc} */
    public void shutdownNow() {
        shutdown();// synchronous shutdown
    }

    public void shutdownServiceAsynchronously(AsynchronousShutdownObject service) {
        throw new UnsupportedOperationException();
    }

    private void doStart() {
        try {
            status = RunState.RUNNING;
            for (ServiceHolder si : services) {
                si.initialize(getConf());
                publicServices.putAll(si.getPublicService());
            }

            for (ServiceHolder si : services) {
                si.start(publicServices);
            }

            // register mbeans
            CacheManagementService cms = (CacheManagementService) publicServices
                    .get(CacheManagementService.class);
            if (cms != null) {
                for (ServiceHolder si : services) {
                    if (si.isInternal && si.service instanceof ManagedObject) {
                        ((ManagedObject) si.service).manage(cms);
                    }
                    si.registerMBeans(cms);
                }
                for (ManagedObject si : managedObjects) {
                    si.manage(cms);
                }

            }
            // started
            for (ServiceHolder si : services) {
                si.started(getCache());
            }
        } catch (RuntimeException re) {
            startupException = new CacheException("Could not start cache", re);
            status = RunState.COULD_NOT_START;
            ces.getExceptionHandler().terminated();
            throw startupException;
        } catch (Error er) {
            startupException = new CacheException("Could not start cache", er);
            status = RunState.COULD_NOT_START;
            ces.getExceptionHandler().terminated();
            throw er;
        } finally {
            setConf(null); // Conf can be GC'ed now
        }
    }

    protected void doTerminate() {
        status = RunState.TERMINATED;
        List<ServiceHolder> shutdown = new ArrayList<ServiceHolder>(services);
        Collections.reverse(shutdown);
        for (ServiceHolder si : shutdown) {
            si.terminated();
        }
        ces.getExceptionHandler().terminated();
    }

    protected void tryTerminate() {
        doTerminate();
    }

    /** {@inheritDoc} */
    RunState getRunState() {
        return status;
    }

    class ServiceHolder {

        private final Collection<ServiceHolder> children = new ArrayList<ServiceHolder>();

        private final boolean isInternal;

        private final Collection<ManagedObject> managedChildren = new ArrayList<ManagedObject>();

        private final Map<Class<?>, Object> published = new HashMap<Class<?>, Object>();

        volatile AsynchronousShutdownObject aso;

        final CacheLifecycle service;

        ServiceHolder(CacheLifecycle service, boolean isInternal) {
            this.isInternal = isInternal;
            this.service = service;
            if (service instanceof CompositeService) {
                for (Object o : ((CompositeService) service).getChildServices()) {
                    if (o instanceof CacheLifecycle) {
                        children.add(new ServiceHolder((CacheLifecycle) o, isInternal));
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
            service.initialize(conf, published);
            for (ServiceHolder cl : children) {
                cl.initialize(conf);
            }
        }

        boolean isInternal() {
            return isInternal;
        }

        void registerMBeans(ManagedGroup parent) {
            for (ManagedObject cl : managedChildren) {
                cl.manage(parent);
            }
        }

        void shutdown() {
            serviceBeingShutdown = this;
            service.shutdown();
            for (ServiceHolder cl : children) {
                cl.shutdown();
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

        void terminated() {
            service.terminated();
            for (ServiceHolder cl : children) {
                cl.terminated();
            }
        }
    }
}
