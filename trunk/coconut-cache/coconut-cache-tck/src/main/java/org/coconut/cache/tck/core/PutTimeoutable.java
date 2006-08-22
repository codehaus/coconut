/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;

public class PutTimeoutable extends CacheTestBundle {

    @Test
    public void testPutTimeout() {
        c0.put(1, "B", Cache.NEVER_EXPIRE, TimeUnit.SECONDS);
        assertEquals(1, c0.size());
        c0.put(1, "C", Cache.NEVER_EXPIRE, TimeUnit.SECONDS);
        assertEquals(1, c0.size());
        assertEquals("C", c0.get(1));
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutKeyNull() {
        c0.put(null, "A", Cache.DEFAULT_EXPIRATION, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutValueNull() {
        c0.put(1, null, Cache.DEFAULT_EXPIRATION, TimeUnit.SECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutTimeoutNegativeTimeout() {
        c0.put(1, "A", -1, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutNull() {
        c0.put(1, "A", Cache.DEFAULT_EXPIRATION, null);
    }

    @Test
    public void testPutAllTimeout() {
        // TODO add
    }

    @Test(expected = NullPointerException.class)
    public void testPutTimeoutMapNull() {
        c0.putAll(null, Cache.DEFAULT_EXPIRATION, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutAllTimeoutNullKeyMapping() {
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(0, "A");
        m.put(null, "D");
        c0.putAll(m, Cache.NEVER_EXPIRE, TimeUnit.SECONDS);
    }

    @Test(expected = NullPointerException.class)
    public void testPutAllTimeoutNullValueMapping() {
        Map<Integer, String> m = new HashMap<Integer, String>();
        m.put(0, "A");
        m.put(5, null);
        c0.putAll(m, Cache.NEVER_EXPIRE, TimeUnit.SECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutAllTimeoutNegativeTimeout() {
        c0.putAll(new HashMap<Integer, String>(), -1, TimeUnit.SECONDS);
        fail("Did not throw IllegalArgumentException");
    }

    @Test(expected = NullPointerException.class)
    public void testPutAllTimeoutNull() {
        c0.putAll(new HashMap<Integer, String>(), Cache.DEFAULT_EXPIRATION,
                null);

    }

}
