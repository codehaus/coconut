/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.values;

import static org.coconut.test.CollectionUtils.*;
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
 * @version $Id: KeySetModifying.java 392 2007-10-07 11:24:10Z kasper $
 */
public class ValuesRemove extends AbstractCacheTCKTest {

    @Test(expected = NullPointerException.class)
    public void removeNPE() {
        newCache(0).keySet().remove(null);
    }

    @Test(expected = NullPointerException.class)
    public void removeNPE2() {
        newCache(5).values().remove(null);
    }

    @Test
    public void remove() {
        c = newCache(0);
        assertFalse(c.values().remove(MNAN2.getValue()));
        assertFalse(c.values().remove(MNAN1.getValue()));

        c = newCache(5);
        assertTrue(c.values().remove(M1.getValue()));
        assertEquals(4, c.size());
        assertFalse(c.values().contains(M1.getValue()));

        c = newCache(1);
        assertTrue(c.values().remove(M1.getValue()));
        assertTrue(c.isEmpty());
    }

    /**
     * {@link Cache#put(Object, Object)} lazy starts the cache.
     */
    @Test
    public void removeLazyStart() {
        c = newCache();
        assertFalse(c.isStarted());
        c.values().remove(MNAN1.getValue());
        checkLazystart();
    }

    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test(expected = IllegalStateException.class)
    public void removeShutdownISE() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        c.values().remove(MNAN1.getValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void removeAll() {
        c = newCache(5);
        assertFalse(c.values().removeAll(Arrays.asList(1, 3)));
        assertTrue(c.values().removeAll(Arrays.asList(4, M2.getValue(), 5)));
        assertEquals(4, c.size());
        assertFalse(c.values().contains(M2.getValue()));
        assertTrue(c.values().removeAll(Arrays.asList(M1.getValue(), M4.getValue())));
        assertFalse(c.values().contains(M4.getValue()));
        assertFalse(c.values().contains(M1.getValue()));
        assertEquals(2, c.size());
    }

    @Test(expected = NullPointerException.class)
    public void removeAllNPE() {
        newCache(0).values().removeAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void removeAllNPE1() {
        newCache(5).values().removeAll(null);
    }

    //TODO fix
    @Test //(expected = NullPointerException.class)
    public void removeAllNPE2() {
        //newCache(5).values().removeAll(Arrays.asList(M1.getValue(), null));
    }

    /**
     * {@link Cache#put(Object, Object)} lazy starts the cache.
     */
    @Test
    public void removeAllLazyStart() {
        c = newCache();
        assertFalse(c.isStarted());
        c.values().removeAll(Arrays.asList(M1.getValue(), M2.getValue()));
        checkLazystart();
    }

    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test(expected = IllegalStateException.class)
    public void removeAllShutdownISE() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should fail
        c.values().removeAll(Arrays.asList(M1.getValue(), M2.getValue()));
    }
}
