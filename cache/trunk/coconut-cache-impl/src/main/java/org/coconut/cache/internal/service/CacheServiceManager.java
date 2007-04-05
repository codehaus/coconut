/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.event.DefaultCacheEventService;
import org.coconut.cache.internal.service.eviction.DefaultCacheEvictionService;
import org.coconut.cache.internal.service.eviction.InternalCacheEvictionService;
import org.coconut.cache.internal.service.expiration.AbstractCacheExpirationService;
import org.coconut.cache.internal.service.expiration.InternalCacheExpirationUtils;
import org.coconut.cache.internal.service.joinpoint.NoOpAfterCacheOperation;
import org.coconut.cache.service.event.CacheEventConfiguration;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.picocontainer.defaults.DefaultPicoContainer;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheServiceManager<K, V> implements InternalCacheServiceManager<K, V> {

    private static final int RUNNING = 1;

    private static final int SHUTDOWN = 1 << 1;

    private static final RuntimePermission shutdownPerm = new RuntimePermission(
            "modifyCache");

    private static final int STOP = 1 << 2;

    private static final int TERMINATED = 1 << 3;

    public final DefaultPicoContainer container = new DefaultPicoContainer();

    private final CacheConfiguration<K, V> conf;

    public final Map<Class, Object> instanciated = new ConcurrentHashMap<Class, Object>();

    private final AtomicInteger runState = new AtomicInteger();

    private final List<ShutdownRunnable> shutdownRequests = new CopyOnWriteArrayList<ShutdownRunnable>();

    private final CountDownLatch termination = new CountDownLatch(1);

    public CacheServiceManager(Cache<K, V> cache, CacheConfiguration<K, V> conf) {
        container.registerComponentInstance(this);
        container.registerComponentInstance(cache);
        container.registerComponentInstance(conf);
        container.registerComponentInstance(conf.getClock());
        for (AbstractCacheServiceConfiguration c : conf.getServices()) {
            container.registerComponentInstance(c);
        }
        this.conf = conf;
    }

    public void registerServiceImplementations(Class... services) {
        for (Class c : services) {
            container.registerComponentImplementation(c);
        }
    }

    public boolean checkNotRunning() {
        return runState.get() == 0;
    }

    public void checkStarted() {
        int state = runState.get();
        if (state != RUNNING) {

        }
    }

    /**
     * @param name
     * @return
     */
    public <T> T getAsCacheService(Class<T> clazz) {
        if (DefaultCacheEventService.class.isAssignableFrom(clazz)) {
            if (conf.getServiceConfiguration(CacheEventConfiguration.class) == null) {
                return (T) new NoOpAfterCacheOperation();
            }
        }
        if (InternalCacheEvictionService.class.isAssignableFrom(clazz)) {
            if (conf.getServiceConfiguration(CacheEvictionConfiguration.class) == null) {
                T t = (T) new DefaultCacheEvictionService(
                        new CacheEvictionConfiguration());
                container.unregisterComponent(DefaultCacheEvictionService.class);
                container.registerComponentInstance(t);
                return t;
            }
        }
        if (AbstractCacheExpirationService.class.isAssignableFrom(clazz)) {
            if (conf.getServiceConfiguration(CacheExpirationConfiguration.class) == null) {
                return (T) InternalCacheExpirationUtils.DUMMY;
            }
        }
        T t = (T) container.getComponentInstanceOfType(clazz);
        if (t == null) {
            System.out.println("No service " + clazz);
        }
        return t;
    }

    public <T> T getServiceOrThrow(Class<T> type) {
        T service = getService(type);
        if (service == null) {
            throw new IllegalArgumentException("Unknown service " + type);
        }
        return service;

    }

    public <T> T getService(Class<T> type) {
        Object cs = instanciated.get(type);
        if (cs == null) {
            cs = (Object) container.getComponentInstanceOfType(type);
            instanciated.put(type, cs);
        }
        return (T) cs;
    }

    public void initializeApm(ManagedGroup root) {
    // for (InternalCacheService<K, V> cs : instanciated.values()) {
    // if (cs instanceof AbstractCacheService) {
    // ((AbstractCacheService<K, V>) cs).addTo(root);
    // }
    // }
    }

    public boolean isTerminated() {
        return runStateAtLeast(runState.get(), TERMINATED);
    }

    public boolean isTerminating() {
        int c = runState.get();
        return !isRunning(c) && runStateLessThan(c, TERMINATED);
    }

    public void shutdown() {
        checkShutdownAccess();
        advanceRunState(SHUTDOWN);
        shutdownAll();
        tryTerminate();
    }

    /**
     * already at least the given target.
     * 
     * @param targetState
     *            the desired state, either SHUTDOWN or STOP (but not TIDYING or
     *            TERMINATED -- use tryTerminate for that)
     */
    private void advanceRunState(int targetState) {
        for (;;) {
            int c = runState.get();
            if (runStateAtLeast(c, targetState) || runState.compareAndSet(c, targetState))
                break;
        }
    }

    private void checkShutdownAccess() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(shutdownPerm);
        }
    }

    private void serviceShutdown(Runnable service) {
        shutdownRequests.remove(service);
        tryTerminate();
    }

    private void shutdownAll() {
        for (Object service : instanciated.values()) {
            if (service instanceof InternalCacheService) {

            }
            InternalCacheService sr = (InternalCacheService) service;
            try {
                // service.shutdown(sr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // if (!sr.isDone()) {
            // shutdownRequests.add(sr);
            // }
        }
        checkShutdownAccess();
        advanceRunState(SHUTDOWN);
        tryTerminate();
    }

    private synchronized void tryTerminate() {
        int c = runState.get();
        if (c != TERMINATED) {
            if (shutdownRequests.size() == 0) {
                try {
                    terminated();
                } finally {
                    runState.set(TERMINATED);
                    termination.countDown();
                }
            }
        }
    }

    protected void terminated() {

    }

    class ShutdownRunnable implements Runnable {
        private final CountDownLatch latch = new CountDownLatch(1);

        private final Object service;

        ShutdownRunnable(Object service) {
            this.service = service;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            latch.countDown();
            // serviceShutdown(service);
        }

        boolean isDone() {
            return latch.getCount() == 0;
        }
    }

    private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

    private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

    private static boolean runStateLessThan(int c, int s) {
        return c < s;
    }

    /**
     * 
     */
    public void initializeAll() {
    // TODO Auto-generated method stub

    }

    /**
     * @see org.coconut.cache.internal.service.InternalCacheServiceManager#hasService(java.lang.Class)
     */
    public boolean hasService(Class serviceType) {
        return false;
    }
}
