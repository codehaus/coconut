package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Test;

public class Hits extends AbstractCacheTCKTestBundle {
    @Test
    public void dummyTest() {

    }

    public void testHits() {
        c = newCache(1);
        CacheEntry<Integer, String> ce = getEntry(M1);
        assertEquals(0l, ce.getHits());

        get(M1);
        assertEquals(1l, ce.getHits());

        getAll(M1, M2);
        assertEquals(2l, ce.getHits());

        peek(M1);
        assertEquals(2l, ce.getHits());
    }

}
