/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.entryset;

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
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EntrySetRetain extends AbstractCacheTCKTest {

    /**
     * {@link Set#clear()} lazy starts the cache.
     */
    @Test
    public void retainAllLazyStart() {
        c = newCache(0);
        assertFalse(c.isStarted());
        c.entrySet().retainAll(Collections.singleton(M1));
        checkLazystart();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void retainAll() {
        c = newCache(1);
        c.entrySet().retainAll(Collections.singleton(M1));
        assertEquals(1, c.size());

        c.entrySet().retainAll(Collections.singleton(M2));
        assertEquals(0, c.size());
        c = newCache(5);
        c.entrySet().retainAll(Arrays.asList(M1, "F", M3, "G", M5));
        assertEquals(3, c.size());
        assertTrue(c.entrySet().contains(M1) && c.entrySet().contains(M3)
                && c.entrySet().contains(M5));

    }

    @Test(expected = NullPointerException.class)
    public void retainAllNPE() {
        newCache(5).entrySet().retainAll(null);
    }
}
