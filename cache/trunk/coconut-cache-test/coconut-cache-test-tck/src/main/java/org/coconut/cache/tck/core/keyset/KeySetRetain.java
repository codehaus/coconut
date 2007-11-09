/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.keyset;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M5;

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
public class KeySetRetain extends AbstractCacheTCKTest {

    /**
     * {@link Set#clear()} lazy starts the cache.
     */
    @Test
    public void retainAllLazyStart() {
        c = newCache(0);
        assertFalse(c.isStarted());
        c.keySet().retainAll(Collections.singleton(M1.getKey()));
        checkLazystart();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void retainAll() {
        c = newCache(1);
        c.keySet().retainAll(Collections.singleton(M1.getKey()));
        assertEquals(1, c.size());

        c.keySet().retainAll(Collections.singleton(M2.getKey()));
        assertEquals(0, c.size());
        c = newCache(5);
        c.keySet().retainAll(
                Arrays.asList(M1.getKey(), "F", M3.getKey(), "G", M5.getKey()));
        assertEquals(3, c.size());
        assertTrue(c.keySet().contains(M1.getKey()) && c.keySet().contains(M3.getKey())
                && c.keySet().contains(M5.getKey()));

    }

    @Test(expected = NullPointerException.class)
    public void retainAllNPE() {
        newCache(5).keySet().retainAll(null);
    }
}
