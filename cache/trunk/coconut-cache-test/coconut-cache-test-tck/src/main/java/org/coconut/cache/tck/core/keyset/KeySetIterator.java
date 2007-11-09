/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.keyset;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_TO_M5_KEY_SET;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class KeySetIterator extends AbstractCacheTCKTest {

    @Test
    @SuppressWarnings("unused")
    public void iterator() {
        int count = 0;
        c = newCache();
        for (Integer entry : c.keySet()) {
            count++;
        }
        assertEquals(0, count);
        c = newCache(5);
        Iterator<Integer> iter = c.keySet().iterator();
        while (iter.hasNext()) {
            assertTrue(M1_TO_M5_KEY_SET.contains(iter.next()));
            count++;
        }
        assertEquals(5, count);
    }

    /**
     * {@link Set#iterator()} lazy starts the cache.
     */
    @Test
    public void iteratorLazyStart() {
        c = newCache(0);
        c.keySet().iterator();
        checkLazystart();
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorNextNSE() {
        c = newCache(1);
        Iterator<Integer> iter = c.keySet().iterator();
        iter.next();
        iter.next();
    }

    @Test
    @SuppressWarnings("unused")
    public void iteratorRemove() {
        c = newCache(5);
        Iterator<Integer> iter = c.keySet().iterator();
        while (iter.hasNext()) {
            int next = iter.next();
            if (next % 2 == 0) {
                iter.remove();
            }
        }
        assertEquals(3, c.size());
        assertTrue(c.keySet().contains(M1.getKey()));
        assertFalse(c.keySet().contains(M2.getKey()));
        assertTrue(c.keySet().contains(M3.getKey()));
        assertFalse(c.keySet().contains(M4.getKey()));
        assertTrue(c.keySet().contains(M5.getKey()));
    }

    @Test(expected = IllegalStateException.class)
    public void iteratorRemoveISE() {
        newCache().keySet().iterator().remove();
    }

    @Test(expected = IllegalStateException.class)
    public void iteratorRemoveISE1() {
        newCache(1).keySet().iterator().remove();
    }

    @Test(expected = IllegalStateException.class)
    public void iteratorRemoveISE2() {
        c = newCache(1);
        Iterator<Integer> iter = c.keySet().iterator();
        iter.next();
        iter.remove();
        iter.remove();// should throw
    }

    /**
     * {@link Set#iterator()} fails when the cache is shutdown.
     */
    @Test(expected = IllegalStateException.class)
    public void iteratorShutdown() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();
        c.keySet().iterator();
    }
}
