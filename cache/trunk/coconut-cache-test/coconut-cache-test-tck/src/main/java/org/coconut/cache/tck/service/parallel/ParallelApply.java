package org.coconut.cache.tck.service.parallel;

import static org.coconut.test.CollectionTestUtil.M1_TO_M5_SET;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.tck.service.management.ManagementShutdown.Op;
import org.coconut.management.ManagedGroup;
import org.coconut.operations.Ops.Procedure;
import org.junit.Test;

public class ParallelApply extends AbstractCacheTCKTest {

    @Test(expected = NullPointerException.class)
    public void applyNPE() {
        eviction().get().apply(null);
    }

    @Test(expected = NullPointerException.class)
    public void applyNPE1() {
        init(5);
        eviction().get().apply(null);
    }

    @Test
    public void apply() {
        Copy copy = new Copy();
        eviction().get().apply(copy);
        assertEquals(0, copy.chm.size());
        init(5);
        eviction().get().apply(copy);
        assertEquals(5, copy.chm.size());
        assertEquals(M1_TO_M5_SET, new HashSet(copy.chm.values()));
    }

    @Test
    public void shutdownNoISE() {
        init(5);
        c.shutdown();
        eviction().get().apply(new Copy());
    }

    @Test
    public void size() {
        assertEquals(0, eviction().get().size());
        init(5);
        assertEquals(5, eviction().get().size());
    }

    static class Copy implements Procedure<CacheEntry<Integer, String>> {
        final ConcurrentHashMap<Integer, CacheEntry<Integer, String>> chm = new ConcurrentHashMap<Integer, CacheEntry<Integer, String>>();

        public void apply(CacheEntry<Integer, String> t) {
            assertNull(chm.put(t.getKey(), t));
        }
    }
}
