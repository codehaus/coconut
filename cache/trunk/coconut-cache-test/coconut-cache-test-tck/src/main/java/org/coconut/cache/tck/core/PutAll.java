/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_KEY_NULL;
import static org.coconut.test.CollectionUtils.M1_NULL_VALUE;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.MNAN1;
import static org.coconut.test.CollectionUtils.asMap;

import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests put operations for a cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class PutAll extends AbstractCacheTCKTest {

    @SuppressWarnings("unchecked")
    @Test
    public void putAll() {
        c = newCache();
        c.putAll(asMap(M1, M5));
        assertEquals(2, c.size());
        assertTrue(c.entrySet().contains(M1));
        assertTrue(c.entrySet().contains(M5));

        c.putAll(asMap(M1, M5));
        assertEquals(2, c.size());

        c.putAll(asMap(MNAN1, M4));
        assertEquals(3, c.size());
        assertFalse(c.entrySet().contains(M1));

    }

    /**
     * {@link Cache#put(Object, Object)} lazy starts the cache.
     */
    @Test
    public void putAllLazyStart() {
        c = newCache();
        assertFalse(c.isStarted());
        c.putAll(asMap(M1, M5));
        checkLazystart();
    }

    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     */
    @Test(expected = IllegalStateException.class)
    public void putAllShutdownISE() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        c.putAll(asMap(M1, M5));
    }

    @Test(expected = NullPointerException.class)
    public void putAllNPE() {
        c = newCache();
        putAll((Map.Entry) null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void putAllKeyMappingNPE() {
        c = newCache();
        c.putAll(asMap(M1, M1_NULL_VALUE));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void putAllValueMappingNPE() {
        c = newCache();
        c.putAll(asMap(M1, M1_KEY_NULL));
    }
}
