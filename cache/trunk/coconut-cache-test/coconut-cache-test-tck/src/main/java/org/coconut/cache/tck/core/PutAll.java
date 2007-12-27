/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M1_KEY_NULL;
import static org.coconut.test.CollectionTestUtil.M1_NULL_VALUE;
import static org.coconut.test.CollectionTestUtil.M4;
import static org.coconut.test.CollectionTestUtil.M5;
import static org.coconut.test.CollectionTestUtil.MNAN1;
import static org.coconut.test.CollectionTestUtil.asMap;

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
        init();
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
        init();
        assertFalse(c.isStarted());
        c.putAll(asMap(M1, M5));
        checkLazystart();
    }

    /**
     * {@link Cache#putAll(Map)} should fail when the cache is shutdown.
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
        init();
        putAll((Map.Entry) null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void putAllKeyMappingNPE() {
        init();
        c.putAll(asMap(M1, M1_NULL_VALUE));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void putAllValueMappingNPE() {
        init();
        c.putAll(asMap(M1, M1_KEY_NULL));
    }
}
