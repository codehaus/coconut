/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.values;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M1_TO_M5_VALUES;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M4;
import static org.coconut.test.CollectionTestUtil.M5;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ValuesIterator extends AbstractCacheTCKTest {

    @Test
    @SuppressWarnings("unused")
    public void iterator() {
        int count = 0;
        init();
        for (String entry : c.values()) {
            count++;
        }
        assertEquals(0, count);
        c = newCache(5);
        Iterator<String> iter = c.values().iterator();
        while (iter.hasNext()) {
            assertTrue(M1_TO_M5_VALUES.contains(iter.next()));
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
        c.values().iterator();
        checkLazystart();
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorNextNSE() {
        c = newCache(1);
        Iterator<String> iter = c.values().iterator();
        iter.next();
        iter.next();
    }

    @Test
    @SuppressWarnings("unused")
    public void iteratorRemove() {
        c = newCache(5);
        Iterator<String> iter = c.values().iterator();
        while (iter.hasNext()) {
            String next = iter.next();
            if (next.equals(M2.getValue()) || next.equals(M4.getValue())) {
                iter.remove();
            }
        }
        assertEquals(3, c.size());
        assertTrue(c.values().contains(M1.getValue()));
        assertFalse(c.values().contains(M2.getValue()));
        assertTrue(c.values().contains(M3.getValue()));
        assertFalse(c.values().contains(M4.getValue()));
        assertTrue(c.values().contains(M5.getValue()));
    }

    @Test(expected = IllegalStateException.class)
    public void iteratorRemoveISE() {
        newCache().values().iterator().remove();
    }

    @Test(expected = IllegalStateException.class)
    public void iteratorRemoveISE1() {
        newCache(1).values().iterator().remove();
    }

    @Test(expected = IllegalStateException.class)
    public void iteratorRemoveISE2() {
        c = newCache(1);
        Iterator<String> iter = c.values().iterator();
        iter.next();
        iter.remove();
        iter.remove();// should throw
    }

    /**
     * {@link Set#iterator()} fails when the cache is shutdown.
     */
    @Test
    public void iteratorShutdown() {
        c = newCache(5);
        assertTrue(c.isStarted());
        c.shutdown();
        assertFalse(c.values().iterator().hasNext());
    }
}
