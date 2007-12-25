package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionTestUtil.M1;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

public class SetValue extends AbstractCacheTCKTest {

    @Test
    public void getEntry() {
        c = newCache(5);
        CacheEntry<Integer, String> ce = c.getEntry(M1.getKey());
        try {
            ce.setValue("foo");
        } catch (UnsupportedOperationException ok) {}
    }

    @Test
    public void peekEntry() {
        c = newCache(5);
        CacheEntry<Integer, String> ce = c.peekEntry(M1.getKey());
        try {
            ce.setValue("foo");
        } catch (UnsupportedOperationException ok) {}
    }
}
