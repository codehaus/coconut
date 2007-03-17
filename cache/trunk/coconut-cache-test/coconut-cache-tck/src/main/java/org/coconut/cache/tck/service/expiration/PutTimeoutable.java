/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.expiration;

import static org.coconut.test.CollectionUtils.M1;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.expiration.CacheExpirationService;
import org.junit.Test;

public class PutTimeoutable extends ExpirationTestBundle {


    @Test
    public void testExpirationDate() {
        c = newCache(newConf().setClock(clock));
        clock.setRelativeTime(10);
        put(M1, 5);
        assertEquals(15l, getEntry(M1).getExpirationTime());
        put(M1, 10);
        assertEquals(20l, getEntry(M1).getExpirationTime());
    }
    
    @Test
    public void testPutTimeout() {
        service.put(1, "B", CacheExpirationService.NEVER_EXPIRE, TimeUnit.SECONDS);
        assertEquals(1, c0.size());
        service.put(1, "C", CacheExpirationService.NEVER_EXPIRE, TimeUnit.SECONDS);
        assertEquals(1, c0.size());
        assertEquals("C", c0.get(1));
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutKeyNull() {
        service.put(null, "A", CacheExpirationService.DEFAULT_EXPIRATION, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutValueNull() {
        service.put(1, null, CacheExpirationService.DEFAULT_EXPIRATION, TimeUnit.SECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutTimeoutNegativeTimeout() {
        service.put(1, "A", -1, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutNull() {
        service.put(1, "A", CacheExpirationService.DEFAULT_EXPIRATION, null);
    }

    @Test
    public void testPutAllTimeout() {
        // TODO add
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutMapNull() {
        service.putAll(null, CacheExpirationService.DEFAULT_EXPIRATION, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutAllTimeoutNullKeyMapping() {
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(0, "A");
        m.put(null, "D");
        service.putAll(m, CacheExpirationService.NEVER_EXPIRE, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutAllTimeoutNullValueMapping() {
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(0, "A");
        m.put(5, null);
        service.putAll(m, CacheExpirationService.NEVER_EXPIRE, TimeUnit.SECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutAllTimeoutNegativeTimeout() {
        service.putAll(new HashMap<Integer, String>(), -1, TimeUnit.SECONDS);
        fail("Did not throw IllegalArgumentException");
    }

    @Test(expected = NullPointerException.class)
    public void testPutAllTimeoutNull() {
        service.putAll(new HashMap<Integer, String>(), CacheExpirationService.DEFAULT_EXPIRATION,
                null);

    }

}
