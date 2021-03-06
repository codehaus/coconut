/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.parallel;

import static org.coconut.test.CollectionTestUtil.M1_TO_M5_KEY_SET;

import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;

import org.coconut.cache.ParallelCache;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.service.parallel.ParallelApply.Copy;
import org.coconut.operations.Ops.Procedure;
import org.junit.Test;

public class WithKeys extends AbstractCacheTCKTest {

    public ParallelCache.WithMapping<Integer> p() {
        return eviction().getParallelCache().withKeys();
    }

    @Test(expected = NullPointerException.class)
    public void applyNPE() {
        p().apply(null);
    }

    @Test(expected = NullPointerException.class)
    public void applyNPE1() {
        init(5);
        p().apply(null);
    }

    @Test
    public void apply() {
        Copy copy = new Copy();
        p().apply(copy);
        assertEquals(0, copy.list.size());
        init(5);
        p().apply(copy);
        assertEquals(5, copy.list.size());
        assertEquals(new HashSet(M1_TO_M5_KEY_SET), new HashSet(copy.list));
    }

    @Test
    public void size() {
        assertEquals(0, p().size());
        init(5);
        assertEquals(5, p().size());
    }

    @Test
    public void shutdownNoISE() {
        init(5);
        c.shutdown();
        Copy copy = new Copy();
        p().apply(copy);
        assertEquals(0, copy.list.size());
    }

    static class Copy implements Procedure<Integer> {
        final CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<Integer>();

        public void apply(Integer t) {
            list.add(t);
        }
    }
}
