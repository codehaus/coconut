package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionUtils.*;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.junit.Test;

public class KeyValue extends AbstractCacheTCKTestBundle {

    @Test
    public void get() {
        c = newCache(0);
        assertNull(c.getEntry(M1.getKey()));
        c = newCache(1);
        CacheEntry<Integer, String> ce = c.getEntry(M1.getKey());
        assertEquals(M1.getKey(), ce.getKey());
        assertEquals(M1.getValue(), ce.getValue());
    }
    
    @Test
    public void put() {
        c = newCache(0);
        assertNull(c.getEntry(M1.getKey()));
        c = newCache(1);
        CacheEntry<Integer, String> ce = c.getEntry(M1.getKey());
        c.put(M1.getKey(), M2.getValue());
        ce = c.getEntry(M1.getKey());
        assertEquals(M1.getKey(), ce.getKey());
        assertEquals(M2.getValue(), ce.getValue());
    }
}
