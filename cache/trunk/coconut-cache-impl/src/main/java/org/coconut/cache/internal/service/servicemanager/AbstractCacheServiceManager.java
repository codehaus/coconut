/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.debug.InternalDebugService;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.internal.service.listener.InternalCacheListener;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.servicemanager.CacheLifecycle.Initializer;
import org.coconut.internal.util.ClassUtils;
import org.coconut.internal.util.TimeFormatter;
import org.coconut.management.ManagedLifecycle;

public abstract class AbstractCacheServiceManager implements InternalCacheServiceManager {

    private CacheConfiguration<?, ?> conf;

    /** The cache debug services. */
    private final InternalDebugService debugService;

    private final InternalCacheListener listener;

    /** A map of services that can be retrieved from {@link Cache#getService(Class)}. */
    private volatile Map<Class<?>, Object> publicServices;

    /** The list of services. */
    private final List<ServiceHolder> services = new ArrayList<ServiceHolder>();

    private int userServices;

    /** The cache we are managing. */
    final Cache<?, ?> cache;

    /** The cache exception services. */
    final InternalCacheExceptionService ces;

    /**
     * Creates a new AbstractPicoBasedCacheServiceManager.
     * 
     * @param cache
     *            the cache we are managing
     * @throws NullPointerException
     *             if the specified cache is null
     */
    AbstractCacheServiceManager(ServiceComposer composer) {
        this.cache = composer.getCache();
        this.conf = composer.getInternalService(CacheConfiguration.class);
        ces = composer.getInternalService(InternalCacheExceptionService.class);
        debugService = composer.getInternalService(InternalDebugService.class);
        listener = composer.getInternalService(InternalCacheListener.class);

        services.addAll(createServiceHolders(composer, conf));
    }

    /** {@inheritDoc} */
    public final Map<Class<?>, Object> getAllServices() {
        return publicServices;
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
        return getRunState().isStarted() && !ces.startupFailed();
    }

    /** {@inheritDoc} */
    public boolean isTerminated() {
        return getRunState().isTerminated();
    }

    public void lazyStart() {
        lazyStart(false);
    }

    public void lazyStartFailIfShutdown() {
        lazyStart(true);
    }

    /** {@inheritDoc} */
    public void shutdown() {
        shutdown(false);
    }

    /** {@inheritDoc} */
    public void shutdownNow() {
        shutdown(true);
    }

    private List<ServiceHolder> createServiceHolders(ServiceComposer container,
            CacheConfiguration conf) {
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

    private void initialize() {
        long initializationStart = System.nanoTime();
        ces.initialize(this.cache, conf);

        if (debugService.isDebugEnabled()) {
            debugService.debug("Cache initializing [name = " + cache.getName() + ", type = "
                    + cache.getClass() + "]\n   " + services.size()
                    + " services to initialize, (*) marked services are used specified");

        }
        if (debugService.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cache was initialized through this call:\n");
            StackTraceElement[] trace = new Exception().getStackTrace();
            for (int i = 0; i < Math.min(12, trace.length); i++) {
                sb.append("    ");
                sb.append(trace[i]);
                if (i < 11) {
                    sb.append("\n");
                }
            }
            debugService.trace(sb.toString());
            debugService.trace("Cache was initialized with the following configuration:\n" + conf);
        }
        Map<Class<?>, Object> tmp = new HashMap<Class<?>, Object>();
        tmp.put(CacheServiceManagerService.class, ServiceManagerUtil.wrapService(this));
        try {
            tmp.putAll(initialize(conf));
        } catch (RuntimeException e) {
            terminateServices();
            throw e;
        }
        publicServices = Collections.unmodifiableMap(tmp);

        debugService.debug("Cache initialized [name = " + cache.getName()
                + ", initialization time = "
                + TimeFormatter.SHORT_FORMAT.formatNanos(System.nanoTime() - initializationStart)
                + ", services initialized = " + services.size() + "]");

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
        for (ServiceHolder sh : services) {
            if (sh.isInternal()) {
                CacheLifecycle a = sh.getService();
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
        }
        for (Object service : conf.serviceManager().getObjects()) {
            if (service instanceof ManagedLifecycle) {
                managedObjects.add((ManagedLifecycle) service);
            }
        }
        return managedObjects;
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
                } catch (Throwable re) {
                    ces.startFailed(re, conf, si);
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
                if (si.isInternal()) {
                    userServices++;
                }
            } catch (Throwable re) {
                ces.startFailed(re, conf, si.getService());
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
        listener.afterStart();
    }

    private void startServices() {
        setRunState(RunState.STARTING);
        CacheServiceManagerService service = this;
        boolean debug = debugService.isDebugEnabled();
        if (debug) {
            debugService.debug("  calling CacheLifecycle.start()");
        }
        for (ServiceHolder si : services) {
            long start = System.nanoTime();
            try {
                si.start(service);
            } catch (Throwable re) {
                ces.startFailed(re, conf, si.getService());
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
        ces.terminated(m);
    }

    void doStart() {
        long startTime = System.nanoTime();
        initialize();
        if (debugService.isDebugEnabled()) {
            debugService.debug("Cache starting [name = " + cache.getName() + ", type = "
                    + cache.getClass() + "]");
            if (debugService.isTraceEnabled()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Cache was started through this call:\n");
                StackTraceElement[] trace = new Exception().getStackTrace();
                for (int i = 0; i < Math.min(10, trace.length); i++) {
                    sb.append("    ");
                    sb.append(trace[i]);
                    if (i < 9) {
                        sb.append("\n");
                    }
                }
                debugService.trace(sb.toString());
            }
            debugService.debug(services.size()
                    + " services to start, (*) marked services are used specified");

        }
        try {
            startServices();
            managementStart();
            servicesStarted();
        } finally {
            conf = null;
        }
        debugService.info("Cache started [name = " + cache.getName() + ", initial size = "
                + cache.size() + ", startup time = "
                + TimeFormatter.SHORT_FORMAT.formatNanos(System.nanoTime() - startTime)
                + ", services started = " + services.size() + "(" + userServices + " internal)]");
    }

    void doTerminate() {
        RunState state = getRunState();
        if (!state.isTerminated()) {
            listener.afterStop();
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
                    ces.serviceManagerShutdownFailed(e, sh.getService());
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
