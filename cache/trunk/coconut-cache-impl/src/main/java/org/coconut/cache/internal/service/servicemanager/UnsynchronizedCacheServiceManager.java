/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.internal.service.exceptionhandling.CacheExceptionService;
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
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class UnsynchronizedCacheServiceManager extends AbstractCacheServiceManager implements
        CacheServiceManagerService {

    private final CacheExceptionService ces;

    private final DefaultPicoContainer container = new DefaultPicoContainer();

    private final List<ManagedObject> managedObjects = new ArrayList<ManagedObject>();

    private final Map<Class<?>, Object> publicServices = new HashMap<Class<?>, Object>();

    private RuntimeException startupException;

    private RunState status = RunState.NOTRUNNING;

    final List<ServiceHolder> externalServices = new ArrayList<ServiceHolder>();

    final List<ServiceHolder> internalServices = new ArrayList<ServiceHolder>();

    ServiceHolder serviceBeingShutdown;

    public UnsynchronizedCacheServiceManager(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
            CacheConfiguration<?, ?> conf,
            Collection<Class<? extends AbstractCacheLifecycle>> classes) {
        super(cache, conf);
        for (Object o : conf.serviceManager().getObjects()) {
            if (o instanceof CacheLifecycle) {
                externalServices.add(new ServiceHolder((CacheLifecycle) o, false));
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

        publicServices.put(CacheServiceManagerService.class, ServiceManagerUtil.wrapService(this));

        /* Initialize Exception Service */
        ces = (CacheExceptionService) container
                .getComponentInstanceOfType(CacheExceptionService.class);
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
    public <T> T getService(Class<T> type) {
        lazyStart(false);
        T t = (T) publicServices.get(type);
        if (t == null) {
            throw new IllegalArgumentException("Unknown service " + type);
        }
        return t;
    }

    /** {@inheritDoc} */
    public boolean hasService(Class<?> type) {
        return publicServices.containsKey(type);
    }

    /** {@inheritDoc} */
    public void lazyStart(boolean failIfShutdown) {
        if (status != RunState.RUNNING) {
            if (startupException != null) {
                throw startupException;
            } else if (status == RunState.NOTRUNNING) {
                doStart();
            } else if (failIfShutdown && status.isShutdown()) {
                throw new IllegalStateException("Cache has been shutdown");
            }
        }
    }

    /** {@inheritDoc} */
    public void shutdown() {
        if (status == RunState.NOTRUNNING) {
            status = RunState.TERMINATED;
        } else if (status == RunState.RUNNING) {
            getCache().clear();
            status = RunState.SHUTDOWN;
            for (ServiceHolder si : internalServices) {
                si.shutdown();
            }
            for (ServiceHolder si : externalServices) {
                si.shutdown();
            }
            serviceBeingShutdown = null;
            tryTerminate();
        }
    }

    protected void tryTerminate() {
        doTerminate();
    }

    protected void doTerminate() {
        status = RunState.TERMINATED;
        for (ServiceHolder si : internalServices) {
            si.terminated();
        }
        for (ServiceHolder si : externalServices) {
            si.terminated();
        }
        ces.getExceptionHandler().terminated();
    }

    /** {@inheritDoc} */
    public void shutdownNow() {
        shutdown();// synchronous shutdown
    }

    public void shutdownServiceAsynchronously(AsynchronousShutdownObject service) {
        throw new UnsupportedOperationException();
    }

    private void doStart() {
        List<AbstractCacheLifecycle> l = container
                .getComponentInstancesOfType(AbstractCacheLifecycle.class);

        for (AbstractCacheLifecycle a : l) {
            internalServices.add(new ServiceHolder(a, true));
        }
        try {
            for (ServiceHolder si : internalServices) {
                si.initialize(getConf());
                publicServices.putAll(si.getPublicService());
                // TODO; check if we have conflicting services
            }
            for (ServiceHolder si : externalServices) {
                si.initialize(getConf());
                publicServices.putAll(si.getPublicService());
                // TODO; check if we have conflicting services
            }
            for (ServiceHolder si : internalServices) {
                si.start(publicServices);
            }
            for (ServiceHolder si : externalServices) {
                si.start(publicServices);
            }

            // register mbeans
            CacheManagementService cms = (CacheManagementService) publicServices
                    .get(CacheManagementService.class);
            if (cms != null) {
                for (ServiceHolder si : internalServices) {
                    if (si.service instanceof ManagedObject) {
                        ((ManagedObject) si.service).manage(cms);
                    }
                    si.registerMBeans(cms);
                }
                for (ServiceHolder si : externalServices) {
                    si.registerMBeans(cms);
                }
                for (ManagedObject si : managedObjects) {
                    si.manage(cms);
                }

            }
            // started
            for (ServiceHolder si : internalServices) {
                si.started(getCache());
            }
            for (ServiceHolder si : externalServices) {
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
        status = RunState.RUNNING;
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
            service.initialize(conf);
            service.registerServices(published);
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
