/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.expiration;

import static org.coconut.test.CollectionUtils.M1;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.expiration.CacheExpirationService;
import org.junit.Before;
import org.junit.Test;

public class ExpirationPutWithTimeouts extends AbstractExpirationTestBundle {

    @Before
    public void setupCache() {
        c = newCache();
    }

    @Test
    public void testExpirationDate() {
        clock.setTimestamp(10);
        put(M1, 5);
        assertEquals(15l, getEntry(M1).getExpirationTime());
        put(M1, 10);
        assertEquals(20l, getEntry(M1).getExpirationTime());
    }

    @Test
    public void testPutTimeout() {
        expiration().put(1, "B", CacheExpirationService.NEVER_EXPIRE, TimeUnit.SECONDS);
        assertEquals(1, c.size());
        expiration().put(1, "C", CacheExpirationService.NEVER_EXPIRE, TimeUnit.SECONDS);
        assertEquals(1, c.size());
        assertEquals("C", c.get(1));
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutKeyNull() {
        expiration().put(null, "A", CacheExpirationService.DEFAULT_EXPIRATION,
                TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutValueNull() {
        expiration().put(1, null, CacheExpirationService.DEFAULT_EXPIRATION,
                TimeUnit.SECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutTimeoutNegativeTimeout() {
        expiration().put(1, "A", -1, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutNull() {
        expiration().put(1, "A", 123, null);
    }

    @Test
    public void testPutAllTimeout() {
    // TODO add
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutMapNull() {
        expiration().putAll(null, CacheExpirationService.DEFAULT_EXPIRATION,
                TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutAllTimeoutNullKeyMapping() {
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(0, "A");
        m.put(null, "D");
        expiration().putAll(m, CacheExpirationService.NEVER_EXPIRE, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutAllTimeoutNullValueMapping() {
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(0, "A");
        m.put(5, null);
        expiration().putAll(m, CacheExpirationService.NEVER_EXPIRE, TimeUnit.SECONDS);
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
