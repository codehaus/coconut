/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.filter.CollectionFilters;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheServiceManager<K, V> implements InternalCacheServiceManager {

    private static final RuntimePermission shutdownPerm = new RuntimePermission(
            "modifyCache");

    private final CacheConfiguration<K, V> conf;

    private final Map<Class, InternalCacheService> instanciated = new ConcurrentHashMap<Class, InternalCacheService>();

    private List<Class<? extends InternalCacheService>> types = new ArrayList<Class<? extends InternalCacheService>>();

    private final List<ShutdownRunnable> shutdownRequests = new CopyOnWriteArrayList<ShutdownRunnable>();

    public CacheServiceManager(CacheConfiguration<K, V> conf) {
        this.conf = conf;
    }

    private final AtomicInteger runState = new AtomicInteger();

    private final CountDownLatch termination = new CountDownLatch(1);

    private static final int RUNNING = 1;

    private static final int SHUTDOWN = 1 << 1;

    private static final int STOP = 1 << 2;

    private static final int TERMINATED = 1 << 3;

    private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

    private static boolean runStateLessThan(int c, int s) {
        return c < s;
    }

    private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

    public boolean checkNotRunning() {
        return runState.get() == 0;
    }

    public void checkStarted() {
        int state = runState.get();
        if (state != RUNNING) {

        }
    }

    public boolean isTerminating() {
        int c = runState.get();
        return !isRunning(c) && runStateLessThan(c, TERMINATED);
    }

    public boolean isTerminated() {
        return runStateAtLeast(runState.get(), TERMINATED);
    }

    private void checkShutdownAccess() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(shutdownPerm);
        }
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

    public void shutdown() {
        checkShutdownAccess();
        advanceRunState(SHUTDOWN);
        shutdownAll();
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

    private void shutdownAll() {
        for (InternalCacheService service : instanciated.values()) {
            ShutdownRunnable sr = new ShutdownRunnable(service);
            try {
                service.shutdown(sr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!sr.isDone()) {
                shutdownRequests.add(sr);
            }

        }
        checkShutdownAccess();
        advanceRunState(SHUTDOWN);
        tryTerminate();
    }

    public void addService(Class<? extends InternalCacheService> impl) {
        types.add(impl);
    }

    public <T> T getService(Class<T> type) {
        T t = getService0(type);
        if (t == null) {
            (new Exception()).printStackTrace();
        }
        return t;
    }

    private <T> T getService0(Class<T> type) {
        InternalCacheService cs = instanciated.get(type);
        if (cs != null) {
            return (T) cs;
        }
        for (InternalCacheService c : instanciated.values()) {
            if (type.isAssignableFrom(c.getClass())) {
                instanciated.put(type, c);
                return (T) c;
            }
        }
        return null;
    }

    public <T> T initialize(Class<T> type) {
        if (type == null) {
            throw new NullPointerException("clazz is null");
        }
        T service = (T) getService0(type);
        if (service != null) {
            return service;
        }
        Class<T> clazz = null;
        for (Class c : types) {
            if (type.isAssignableFrom(c)) {
                clazz = c;
            }
        }
        if (clazz == null) {
            throw new IllegalStateException("No defined service " + type + " "
                    + types.toString());
        }
        Constructor<T> c = null;
        // T service = null;
        try {
            c = (Constructor<T>) clazz.getDeclaredConstructor(CacheConfiguration.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                    "Could not create cache instance, no public contructor taking a single CacheConfiguration instance",
                    e);
        }
        try {
            service = c.newInstance(conf);
            instanciated.put(clazz, (InternalCacheService) service);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    "Could not create cache instance, specified clazz " + clazz
                            + ") is an interface or abstract class", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not create instance of " + clazz, e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Constructor threw exception", e);
        }
        return service;
    }

    public void startAll(AbstractCache<K, V> c) {
        for (InternalCacheService<K, V> cs : instanciated.values()) {
            try {
                cs.start(c, (Map) Collections.EMPTY_MAP);
            } catch (Exception e) {
                e.printStackTrace();
                //shutdown cache
            }
        }
    }

    public void initializeApm(ManagedGroup root) {
        for (InternalCacheService<K, V> cs : instanciated.values()) {
            if (cs instanceof AbstractCacheService) {
                ((AbstractCacheService<K, V>) cs).addTo(root);
            }
        }
    }

    private void serviceShutdown(InternalCacheService service) {
        shutdownRequests.remove(service);
        tryTerminate();
    }

    class ShutdownRunnable implements Runnable {
        private final CountDownLatch latch = new CountDownLatch(1);

        private final InternalCacheService service;

        ShutdownRunnable(InternalCacheService service) {
            this.service = service;
        }

        boolean isDone() {
            return latch.getCount() == 0;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            latch.countDown();
            serviceShutdown(service);
        }
    }
}
