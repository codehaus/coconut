/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.values;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M5;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ValuesRetain extends AbstractCacheTCKTest {

    /**
     * {@link Set#clear()} lazy starts the cache.
     */
    @Test
    public void retainAllLazyStart() {
        c = newCache(0);
        assertFalse(c.isStarted());
        c.keySet().retainAll(Collections.singleton(M1.getValue()));
        checkLazystart();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void retainAll() {
        c = newCache(1);
        c.values().retainAll(Collections.singleton(M1.getValue()));
        assertEquals(1, c.size());

        c.values().retainAll(Collections.singleton(M2.getValue()));
        assertEquals(0, c.size());
        c = newCache(5);
        c.values().retainAll(
                Arrays.asList(M1.getValue(),1, M3.getValue(), 2, M5.getValue()));
        assertEquals(3, c.size());
        assertTrue(c.values().contains(M1.getValue()) && c.values().contains(M3.getValue())
                && c.values().contains(M5.getValue()));

    }

    @Test(expected = NullPointerException.class)
    public void retainAllNPE() {
        newCache(5).values().retainAll(null);
    }
}
