/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.core.entryset;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M1_KEY_NULL;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M5;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
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
        assertSize(1);
        c.entrySet().retainAll(Collections.singleton(M1_KEY_NULL));
        assertSize(0);
        c = newCache(1);
        c.entrySet().retainAll(Collections.singleton(M2));
        assertSize(0);
        c = newCache(5);
        c.entrySet().retainAll(Arrays.asList(M1, "F", M3, "G", M5));
        assertSize(3);
        assertTrue(c.entrySet().contains(M1) && c.entrySet().contains(M3)
                && c.entrySet().contains(M5));

    }

    @Test(expected = NullPointerException.class)
    public void retainAllNPE() {
        newCache(5).entrySet().retainAll(null);
    }
}
