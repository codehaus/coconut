/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.entryset;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.MNAN1;
import static org.coconut.test.CollectionUtils.MNAN2;

import java.util.Arrays;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.test.CollectionUtils;
import org.junit.Test;

/**
 * Tests the modifying functions of a keySet().
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class EntrySetRemove extends AbstractCacheTCKTest {

    @Test(expected = NullPointerException.class)
    public void removeNPE() {
        newCache(0).entrySet().remove(null);
    }

    @Test(expected = NullPointerException.class)
    public void removeNPE2() {
        newCache(5).entrySet().remove(null);
    }

    @Test
    public void remove() {
        c = newCache();
        assertFalse(c.entrySet().remove(1));
        assertFalse(c.entrySet().remove(MNAN1));
        c = newCache(5);
        assertTrue(c.entrySet().remove(M1));
        assertSize(4);
        assertFalse(c.entrySet().contains(M1));

        c = newCache(1);
        assertTrue(c.entrySet().remove(M1));
        assertTrue(c.isEmpty());
    }

    /**
     * {@link Cache#put(Object, Object)} lazy starts the cache.
     */
    @Test
    public void removeLazyStart() {
        c = newCache();
        assertFalse(c.isStarted());
        c.entrySet().remove(MNAN1);
        checkLazystart();
    }

    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     */
    @Test(expected = IllegalStateException.class)
    public void removeShutdownISE() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        c.entrySet().remove(MNAN1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void removeAll() {
        c = newCache(5);
        assertFalse(c.entrySet().removeAll(Arrays.asList(MNAN1, MNAN2)));
        assertTrue(c.entrySet().removeAll(Arrays.asList(MNAN1, M2, MNAN2)));
        assertSize(4);
        assertFalse(c.entrySet().contains(M2));
        assertTrue(c.entrySet().removeAll(Arrays.asList(M1, M4)));
        assertFalse(c.entrySet().contains(M4));
        assertFalse(c.entrySet().contains(M1));
        assertSize(2);
    }

    @Test(expected = NullPointerException.class)
    public void removeAllNPE() {
        newCache(0).entrySet().removeAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void removeAllNPE1() {
        newCache(5).entrySet().removeAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void removeAllNPE2() {
        newCache(5).entrySet().removeAll(Arrays.asList(1, null));
    }

    /**
     * {@link Cache#put(Object, Object)} lazy starts the cache.
     */
    @Test
    public void removeAllLazyStart() {
        c = newCache();
        assertFalse(c.isStarted());
        c.entrySet().removeAll(CollectionUtils.asList(2, 3));
        checkLazystart();
    }

    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     */
    @Test(expected = IllegalStateException.class)
    public void removeAllShutdownISE() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        c.entrySet().removeAll(CollectionUtils.asList(2, 3));
    }
}
