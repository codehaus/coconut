/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.servicemanager;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M1_TO_M5_MAP;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.test.throwables.RuntimeException1;
import org.junit.Before;
import org.junit.Test;

public class StartupFailed extends AbstractCacheTCKTest {

    @Test
    public void awaitTermination() throws InterruptedException {
        assertFalse(c.awaitTermination(1, TimeUnit.NANOSECONDS));
        failSize();
        assertTrue(c.awaitTermination(10, TimeUnit.SECONDS));
    }

    @Test
    public void clear() {
        try {
            c.clear();
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    @Test
    public void containsKey() {
        try {
            c.containsKey(1);
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    @Test
    public void containsValue() {
        try {
            c.containsValue("1");
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    @Test
    public void entrySetClear() {
        // TODO test all methods on entrySet
        try {
            c.entrySet().clear();
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    @Test
    public void get() {
        try {
            c.get(1);
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    @Test
    public void getAll() {
        try {
            c.getAll(Arrays.asList(1, 2, 3));
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    @Test
    public void getEntry() {
        try {
            c.getEntry(1);
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    /**
     * {@link Cache#getName()} does not fail.
     */
    @Test
    public void getName() {
        c.getName();
        failSize();
        c.getName();
        failedLater();
    }

    @Test
    public void getVolume() {
        try {
            c.getVolume();
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    @Test
    public void isEmpty() {
        try {
            c.isEmpty();
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    @Test
    public void size() {
        try {
            c.size();
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {}
        failedLater();
    }

    @Test
    public void peek() {
        try {
            c.peek(1);
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    @Test
    public void peekEntry() {
        try {
            c.peekEntry(1);
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    @Test
    public void put() {
        try {
            c.put(1, "1");
            fail("should throw CacheException");
        } catch (RuntimeException1 ok) {

        }
        failedLater();
    }

    public void putAll() {
        try {
            c.putAll(M1_TO_M5_MAP);
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    public void putIfAbsent() {
        try {
            c.putIfAbsent(1, "A");
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    public void remove1() {
        try {
            c.remove(1);
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    public void remove2() {
        try {
            c.remove(1, "A");
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    public void removeAll() {
        try {
            c.removeAll(Arrays.asList(1, 2, 3));
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    public void replace2() {
        try {
            c.replace(1, "B");
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    public void replace3() {
        try {
            c.replace(1, "A", "B");
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    @Test
    public void keySetClear() {
        // TODO test all methods on keySet
        try {
            c.keySet().clear();
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    @Test
    public void valuesClear() {
        // TODO test all methods on values
        try {
            c.values().clear();
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    @Test
    public void shutdown() {
        c.shutdown();
        c.size(); // does not fail, cache was never started
        try {
            put(M1);
            fail("should throw IllegalStateException");
        } catch (IllegalStateException ce) {
            // ignore
        }
        c.shutdown();
    }

    @Test
    public void shutdownNow() {
        c.shutdownNow();
        c.size(); // does not fail, cache was never started
        try {
            put(M1);
            fail("should throw IllegalStateException");
        } catch (IllegalStateException ce) {
            // ignore
        }
        c.shutdownNow();
    }

    @Test
    public void isShutdown() {
        assertFalse(c.isShutdown());
        failSize();
        assertTrue(c.isShutdown());
    }

    @Test
    public void isStarted() {
        assertFalse(c.isStarted());
        failSize();
        assertFalse(c.isStarted());
    }

    @Test
    public void isTerminated() throws Exception {
        assertFalse(c.isTerminated());
        failSize();
        c.awaitTermination(10, TimeUnit.SECONDS);
        assertTrue(c.isTerminated());
    }

    @Test
    public void getServiceStartupFailed() {
        try {
            c.getService(CacheServiceManagerService.class);
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
        failedLater();
    }

    @Before
    public void setup() {
        c = newStartupFailedCache();
    }

    private void failedLater() {
        try {
            c.getService(CacheServiceManagerService.class);
            fail("should throw CacheException");
        } catch (IllegalStateException ce) {
            assertTrue(ce.getCause() instanceof RuntimeException1);
        }
        try {
            c.size();
            fail("should throw CacheException");
        } catch (IllegalStateException ce) {
            assertTrue(ce.getCause() instanceof RuntimeException1);
        }
    }

    private void failSize() {
        try {
            c.size();
            fail("should throw CacheException");
        } catch (RuntimeException1 ce) {

        }
    }
}
