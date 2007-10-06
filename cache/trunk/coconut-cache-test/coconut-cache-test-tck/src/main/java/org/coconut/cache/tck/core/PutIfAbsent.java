/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_KEY_NULL;
import static org.coconut.test.CollectionUtils.M1_NULL_VALUE;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.MNAN1;
import static org.coconut.test.CollectionUtils.asList;
import static org.coconut.test.CollectionUtils.asMap;

import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests put operations for a cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Put.java 383 2007-09-03 07:36:42Z kasper $
 */
public class PutIfAbsent extends AbstractCacheTCKTest {

    /**
     * Tests the putIfAbsent(K key, V value) method.
     */
    @Test
    public void putIfAbsent() {
        c = newCache();
        assertNull(c.putIfAbsent(M1.getKey(), M1.getValue()));
        assertEquals(M1.getValue(), c.get(M1.getKey()));
        assertEquals(M1.getValue(), c.putIfAbsent(M1.getKey(), M2.getValue()));
        assertFalse(c.containsValue(M2.getValue()));
    }

    /**
     * {@link Cache#put(Object, Object)} lazy starts the cache.
     */
    @Test
    public void putIfAbsentLazyStart() {
        c = newCache();
        assertFalse(c.isStarted());
        c.putIfAbsent(M1.getKey(), M1.getValue());
        checkLazystart();
    }
    
    /**
     * Tests that putIfAbsent(null, Object) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void putIfAbsentKeyNPE() {
        c = newCache();
        c.putIfAbsent(null, "A");
    }
    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test(expected = IllegalStateException.class)
    public void putIfAbsentShutdownISE() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        c.putIfAbsent(M1.getKey(), M1.getValue());
    }
    /**
     * Tests that putIfAbsent(Object, null) throws NPE.
     */
    @Test(expected = NullPointerException.class)
    public void putIfAbsentValueNPE() {
        c = newCache();
        c.putIfAbsent(1, null);
    }
}
