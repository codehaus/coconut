/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.expiration;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.attribute.common.TimeToLiveAttribute;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.junit.Before;
import org.junit.Test;

public class ExpirationPutWithTimeouts extends AbstractExpirationTestBundle {

    @Before
    public void setupCache() {
        init();
    }

    @Test
    public void testExpirationDate() {
        clock.setTimestamp(10);
        put(M1, 5);
        assertEquals(15l, getEntry(M1).getExpirationTime());
        put(M1, 10);
        assertEquals(20l, getEntry(M1).getExpirationTime());
    }

    /**
     * {@link CacheExpirationService#put(Object, Object, long, TimeUnit) lazy starts the cache.
     */
    @Test
    public void putLazyStart() {
        init();
        assertFalse(c.isStarted());
        put(M1, 5);
        checkLazystart();
    }

    /**
     * {@link CacheExpirationService#put(Object, Object, long, TimeUnit) should  fail when cache is shutdown.
     *
     * @throws InterruptedException
     *             was interrupted
     */
    @Test(expected = IllegalStateException.class)
    public void putShutdownISE() {
        init(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        put(M1, 5);
    }

    @Test
    public void testPutTimeout() {
        expiration().put(1, "B", TimeToLiveAttribute.FOREVER, TimeUnit.SECONDS);
        assertSize(1);
        expiration().put(1, "C", TimeToLiveAttribute.FOREVER, TimeUnit.SECONDS);
        assertSize(1);
        assertEquals("C", c.get(1));
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutKeyNull() {
        expiration().put(null, "A", 0, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutKeyNull2() {
        expiration().put(null, "A", TimeToLiveAttribute.FOREVER, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutValueNull() {
        expiration().put(1, null, 0, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutValueNull2() {
        expiration().put(1, null, TimeToLiveAttribute.FOREVER, TimeUnit.SECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutTimeoutNegativeTimeout() {
        expiration().put(1, "A", -1, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutNull() {
        expiration().put(1, "A", 123, null);
    }

    /**
     * {@link CacheExpirationService#putAll(Map, long, TimeUnit) lazy starts the cache.
     */
    @Test
    public void putAllLazyStart() {
        init();
        assertFalse(c.isStarted());
        putAll(5, M1, M2);
        checkLazystart();
    }

    /**
     * {@link CacheExpirationService#putAll(Map, long, TimeUnit) should  fail when cache is shutdown.
     *
     * @throws InterruptedException
     *             was interrupted
     */
    @Test(expected = IllegalStateException.class)
    public void putAllShutdownISE() {
        init(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        putAll(5, M1, M2);
    }

    @Test
    public void testPutAllTimeout() {
        clock.setTimestamp(10);
        putAll(5, M1, M2);
        assertEquals(15l, getEntry(M1).getExpirationTime());
        assertEquals(15l, getEntry(M2).getExpirationTime());
        putAll(10, M1);
        assertEquals(20l, getEntry(M1).getExpirationTime());
        assertEquals(15l, getEntry(M2).getExpirationTime());
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutMapNull() {
        expiration().putAll(null, 0, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutAllTimeoutNullKeyMapping() {
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(0, "A");
        m.put(null, "D");
        expiration().putAll(m, TimeToLiveAttribute.FOREVER, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutAllTimeoutNullValueMapping() {
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(0, "A");
        m.put(5, null);
        expiration().putAll(m, TimeToLiveAttribute.FOREVER, TimeUnit.SECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutAllTimeoutNegativeTimeout() {
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(0, "A");
        expiration().putAll(m, -1, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutAllTimeoutNull() {
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(0, "A");
        expiration().putAll(m, 22, null);
    }
}
