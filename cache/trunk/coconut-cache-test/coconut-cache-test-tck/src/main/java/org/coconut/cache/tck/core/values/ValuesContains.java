/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.values;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M1_TO_M5_VALUES;
import static org.coconut.test.CollectionTestUtil.M5;
import static org.coconut.test.CollectionTestUtil.M6;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ValuesContains extends AbstractCacheTCKTest {

    /**
     * containsKey returns true for contained key
     */
    @Test
    public void contains() {
        c = newCache(5);
        assertTrue(c.values().contains(M1.getValue()));
        assertFalse(c.values().contains("aa"));
        assertFalse(c.values().contains(M6.getValue()));
    }

    @Test
    public void containsAll() {
        c = newCache(5);
        assertTrue(c.values().containsAll(M1_TO_M5_VALUES));
        assertFalse(c.values().containsAll(
                Arrays.asList(M1.getValue(), M5.getValue(), M6.getValue())));
    }

    /**
     * {@link Cache#containsKey} lazy starts the cache.
     */
    @Test
    public void containsAllLazyStart() {
        c = newCache(0);
        assertFalse(c.isStarted());
        c.values().containsAll(M1_TO_M5_VALUES);
        checkLazystart();
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void containsAllNPE() {
        newCache(5).values().containsAll(null);
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void containsAllNPE1() {
        newCache(5).values().containsAll(Arrays.asList(M1.getValue(), null));
    }

    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test
    public void containsAllShutdown() throws InterruptedException {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should not fail, but result is undefined until terminated
        c.values().containsAll(M1_TO_M5_VALUES);

        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));

        boolean containsKeys = c.values().containsAll(M1_TO_M5_VALUES);
        assertFalse(containsKeys);// cache should be empty
    }

    /**
     * {@link Cache#containsKey} lazy starts the cache.
     */
    @Test
    public void containsLazyStart() {
        c = newCache(0);
        assertFalse(c.isStarted());
        c.values().contains(M1.getValue());
        checkLazystart();
    }

    /**
     * containsKey(null) throws NPE
     */
    @Test(expected = NullPointerException.class)
    public void containsNPE() {
        newCache(5).values().contains(null);
    }

    /**
     * {@link Cache#containsKey()} should not fail when cache is shutdown.
     * 
     * @throws InterruptedException
     *             was interrupted
     */
    @Test
    public void containsShutdown() throws InterruptedException {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();

        // should not fail, but result is undefined until terminated
        c.values().contains(M1.getValue());

        assertTrue(c.awaitTermination(1, TimeUnit.SECONDS));

        boolean containsKey = c.values().contains(M1.getValue());
        assertFalse(containsKey);// cache should be empty
    }

}
