/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.cache.internal.service.spi.InternalCacheSupport;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.AsynchronousShutdownObject;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycleInitializer;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.core.Clock;
import org.coconut.internal.picocontainer.defaults.DefaultPicoContainer;
import org.coconut.management.ManagedLifecycle;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class UnsynchronizedCacheServiceManager extends AbstractCacheServiceManager implements
        CacheServiceManagerService {

    private final CacheExceptionHandler ces;

    private final Clock clock;

    private CacheConfiguration conf;

    private final DefaultPicoContainer container = new DefaultPicoContainer();

    private final Map<Class<?>, Object> initializedPublicServices = new HashMap<Class<?>, Object>();

    private final List<ManagedLifecycle> managedObjects = new ArrayList<ManagedLifecycle>();

    private Map<Class<?>, Object> publicServices;

    private RuntimeException startupException;

    private long startupTime;

    private RunState status = RunState.NOTRUNNING;

    ServiceHolder serviceBeingShutdown;

    final LinkedList<ServiceHolder> services = new LinkedList<ServiceHolder>();

    public UnsynchronizedCacheServiceManager(Cache<?, ?> cache, InternalCacheSupport<?, ?> helper,
            CacheConfiguration<?, ?> conf,
            Collection<Class<? extends AbstractCacheLifecycle>> classes) {
        super(cache);
        this.conf = conf;
        clock = conf.getClock();
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
            if (a instanceof CompositeService) {
                for (Object o : ((CompositeService) a).getChildServices()) {
                    if (o instanceof CacheLifecycle) {
                        services.add(new ServiceHolder((CacheLifecycle) o, false));
                    }
                    if (o instanceof ManagedLifecycle) {
                        managedObjects.add((ManagedLifecycle) o);
                    }
                }
            }
            services.add(new ServiceHolder(a, true));
            if (a instanceof ManagedLifecycle) {
                managedObjects.add((ManagedLifecycle) a);
            }

        }
        // / Get public services
        for (Object service : conf.serviceManager().getObjects()) {
            if (service instanceof CacheLifecycle) {
                services.add(new ServiceHolder((CacheLifecycle) service, false));
            }
            if (service instanceof ManagedLifecycle) {
                managedObjects.add((ManagedLifecycle) service);
            }
        }
        /* Initialize Exception Service */
        ces = ((InternalCacheExceptionService) container
                .getComponentInstanceOfType(InternalCacheExceptionService.class))
                .getExceptionHandler();
        initializeServices();
    }

    /** {@inheritDoc} */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return isTerminated();
    }

    /** {@inheritDoc} */
    public Map<Class<?>, Object> getAllServices() {
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
    public <T> T getServiceFromCache(Class<T> serviceType) {
        if (serviceType == null) {
            throw new NullPointerException("serviceType is null");
        }
        lazyStart(false);
        T t = (T) publicServices.get(serviceType);
        if (t == null) {
            throw new IllegalArgumentException("Unknown service " + serviceType);
        }
        return t;
    }

    /** {@inheritDoc} */
    public <T> T getService(Class<T> serviceType) {
        if (serviceType == null) {
            throw new NullPointerException("serviceType is null");
        }
        T t = (T) publicServices.get(serviceType);
        if (t == null) {
            throw new IllegalArgumentException("Unknown service " + serviceType);
        }
        return t;
    }

    /** {@inheritDoc} */
    public boolean lazyStart(boolean failIfShutdown) {
        if (status != RunState.RUNNING) {
            if (startupException != null) {
                throw startupException;
            } else if (status == RunState.STARTING) {
                throw new IllegalStateException(
                        "Cannot invoke this method from CacheLifecycle.start(Map services), should be invoked from CacheLifecycle.started(Cache c)");
            } else if (status == RunState.NOTRUNNING) {
                doStart();
            } else if (failIfShutdown && status.isShutdown()) {
                throw new IllegalStateException("Cache has been shutdown");
            }
            // else if status==STARTING=throw illegalStateException()
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

    /**
     * Initializes all services.
     */
    private void initializeServices() {
        ces.initialize(conf);
        for (ServiceHolder si : services) {
            try {
                final Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
                si.initialize(new CacheLifecycleInitializer() {
                    public CacheConfiguration<?, ?> getCacheConfiguration() {
                        return conf;
                    }

                    public Class<? extends Cache> getCacheType() {
                        return getCache().getClass();
                    }

                    public <T> void registerService(Class<T> clazz, T service) {
                        map.put(clazz, service);
                    }
                });
                initializedPublicServices.putAll(map);
            } catch (RuntimeException re) {
                try {
                    ces.cacheInitializationFailed(conf, getCache().getClass(), si.service, re);
                } finally {
                    doTerminate();
                }
                throw re;
            }
        }
    }

    private void startServices() {
        CacheServiceManagerService wrapped = ServiceManagerUtil.wrapService(this);
        initializedPublicServices.put(CacheServiceManagerService.class, wrapped);
        publicServices = Collections.unmodifiableMap(initializedPublicServices);
        for (ServiceHolder si : services) {
            try {
                si.start(wrapped);
            } catch (RuntimeException re) {
                conf = null;
                startupException = new CacheException("Could not start the cache", re);
                status = RunState.COULD_NOT_START;
                tryShutdownServices();
                doTerminate();
                throw startupException;
            } catch (Error er) {
                conf = null;
                startupException = new CacheException("Could not start the cache", er);
                status = RunState.COULD_NOT_START;
                throw er;
            }
        }
    }
    
    private void doStart() {
        status = RunState.STARTING;
        startServices();
        try {
            
            // register mbeans
            CacheManagementService cms = (CacheManagementService) publicServices
                    .get(CacheManagementService.class);
            if (cms != null) {
                for (ManagedLifecycle si : managedObjects) {
                    si.manage(cms);
                }
            }
            status = RunState.RUNNING;
            // started
            for (ServiceHolder si : services) {
                si.started(getCache());
            }
            startupTime = clock.relativeTime();
            InternalCacheListener icl = getInternalService(InternalCacheListener.class);
            icl.afterStart(getCache());
        } catch (RuntimeException re) {
            startupException = new CacheException("Could not start cache", re);
            status = RunState.COULD_NOT_START;
            doTerminate();
            throw startupException;
        } catch (Error er) {
            startupException = new CacheException("Could not start cache", er);
            status = RunState.COULD_NOT_START;
            ces.terminated(tryTerminateServices());
            throw er;
        } finally {
            conf = null; // Conf can be GC'ed now
        }
    }
    private Map<CacheLifecycle, RuntimeException> tryTerminateServices() {
        Map<CacheLifecycle, RuntimeException> m = new HashMap<CacheLifecycle, RuntimeException>();
        for (Iterator<ServiceHolder> iterator = services.descendingIterator(); iterator.hasNext();) {
            ServiceHolder sh = iterator.next();
            if (sh.isInitialized()) {
                try {
                    sh.terminated();
                } catch (RuntimeException e) {
                    m.put(sh.service, e);
                }
            }
        }
        return m;
    }
    
    private Map<CacheLifecycle, RuntimeException> tryShutdownServices() {
        Map<CacheLifecycle, RuntimeException> m = new HashMap<CacheLifecycle, RuntimeException>();
        for (Iterator<ServiceHolder> iterator = services.descendingIterator(); iterator.hasNext();) {
            ServiceHolder sh = iterator.next();
            if (sh.isStarted()) {
                try {
                    sh.shutdown();
                } catch (RuntimeException e) {
                    m.put(sh.service, e);
                }
            }
        }
        return m;
    }

    protected void doTerminate() {
        if (status != RunState.COULD_NOT_START) {
            status = RunState.TERMINATED;
        }
        ces.terminated(tryTerminateServices());
    }

    protected void tryTerminate() {
        doTerminate();
    }

    /** {@inheritDoc} */
    RunState getRunState() {
        return status;
    }

    class ServiceHolder {
        private final boolean isInternal;

        private int state;

        volatile AsynchronousShutdownObject aso;

        final CacheLifecycle service;

        ServiceHolder(CacheLifecycle service, boolean isInternal) {
            this.isInternal = isInternal;
            this.service = service;
        }

        void initialize(CacheLifecycleInitializer cli) {
            state = 1;
            service.initialize(cli);
            state = 2;
        }

        boolean isInitialized() {
            return state >= 2;
        }
        boolean isStarted() {
            return state >= 4;
        }
        boolean isInternal() {
            return isInternal;
        }

        void shutdown() {
            state = 7;
            serviceBeingShutdown = this;
            service.shutdown();
            state = 8;
        }

        void start(CacheServiceManagerService serviceManager) {
            state = 3;
            service.start(serviceManager);
            state = 4;
        }

        void started(Cache<?, ?> c) {
            state = 5;
            service.started(c);
            state = 6;
        }

        void terminated() {
            state = 9;
            service.terminated();
            state = 10;
        }
    }
}
