/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import static org.coconut.cache.Caches.emptyCache;
import static org.coconut.test.TestUtil.assertIsSerializable;
import static org.coconut.test.TestUtil.serializeAndUnserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the {@link CacheServicesOld} class.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheServicesTest.java 427 2007-11-10 13:15:25Z kasper $
 */
@SuppressWarnings("unchecked")
@RunWith(JMock.class)
public class CachesTest {

    /** The junit mockery. */
    private final Mockery context = new JUnit4Mockery();

    /** The default cache to test upon. */
    private Cache<Integer, String> cache;

    /**
     * Setup the cache mock.
     */
    @Before
    public void setupCache() {
        cache = context.mock(Cache.class);
    }

    /**
     * Tests {@link Caches#runClear(Cache)}.
     */
    @Test
    public void runClear() {
        context.checking(new Expectations() {
            {
                one(cache).clear();
            }
        });
        Caches.runClear(cache).run();
    }

    /**
     * Tests that {@link Caches#runClear(Cache)} throws a {@link NullPointerException}
     * when invoked with a null argument.
     */
    @Test(expected = NullPointerException.class)
    public void runClearNPE() {
        Caches.runClear(null);
    }

    @Test
    public void emptyCache_() throws Exception {
        Cache<Integer, String> c = Caches.emptyCache();
        assertSame(c, Caches.emptyCache());
        assertFalse(emptyCache().awaitTermination(1, TimeUnit.NANOSECONDS));
        assertFalse(Caches.emptyCache().containsKey(1));
        assertFalse(Caches.emptyCache().containsValue(2));
        assertEquals(0, Caches.emptyCache().entrySet().size());
        assertEquals(new HashMap(), Caches.emptyCache());
        assertEquals("emptymap", emptyCache().getName());
        assertEquals(0L, emptyCache().volume());
        assertEquals(new HashMap().hashCode(), Caches.emptyCache().hashCode());
        assertFalse(emptyCache().isShutdown());
        assertFalse(emptyCache().isStarted());
        assertFalse(emptyCache().isTerminated());
        assertEquals(0, Caches.emptyCache().size());
        assertTrue(Caches.emptyCache().isEmpty());
        assertEquals(0, Caches.emptyCache().keySet().size());
        assertEquals(0, Caches.emptyCache().values().size());
        assertNull(emptyCache().get(1));
        assertNull(emptyCache().getEntry(2));
        assertNull(emptyCache().peek(2));
        assertNull(emptyCache().peekEntry(2));
        assertEquals(2, emptyCache().getAll(Arrays.asList(1, 2)).size());
        assertTrue(emptyCache().getAll(Arrays.asList(1, 2)).containsKey(1));
        assertNull(emptyCache().getAll(Arrays.asList(1, 2)).get(1));
        assertFalse(Caches.emptyCache().equals(new HashSet()));

        try {
            assertNull(emptyCache().put(1, 2));
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}

        try {
            emptyCache().putAll(Collections.singletonMap(1, 2));
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}

        try {
            assertNull(emptyCache().putIfAbsent(1, 2));
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}

        assertNull(emptyCache().remove(1));
        assertFalse(emptyCache().remove(1, 2));
        emptyCache().removeAll(Arrays.asList(1, 2, 3));
        try {
            assertNull(emptyCache().replace(1, 2));
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ok) {/* ok */}

        assertFalse(emptyCache().replace(1, 2, 3));
        emptyCache().clear();
        emptyCache().shutdown();
        emptyCache().shutdownNow();
    }

    /**
     * Tests that EMPTY_MAP is serializable and maintains the singleton property.
     * 
     * @throws Exception
     *             something went wrong
     */
    @Test
    public void emptyCacheSerialization() throws Exception {
        assertIsSerializable(Caches.emptyCache());
        assertSame(Caches.emptyCache(), serializeAndUnserialize(new Caches.EmptyCache()));
        assertIsSerializable(Caches.NO_SERVICES);
        assertSame(Caches.NO_SERVICES, serializeAndUnserialize(Caches.NO_SERVICES));
    }

    @Test
    public void emptyCacheServices() {
        CacheServiceManagerService s = emptyCache().services().servicemanager();
        assertEquals(1, s.getAllServices().size());
        assertSame(s, s.getAllServices().get(CacheServiceManagerService.class));
        assertTrue(s.hasService(CacheServiceManagerService.class));
        assertFalse(s.hasService(CacheManagementService.class));
    }

    @Test(expected = NullPointerException.class)
    public void emptyCacheServicesNPE() {
        emptyCache().services().servicemanager().getService(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyCacheServicesIAE() {
        emptyCache().services().servicemanager().getService(CacheManagementService.class);
    }

    public static void main(String[] args) {
        Map m = Collections.EMPTY_MAP;
        m.clear();
        System.out.println("bye");
    }
}
