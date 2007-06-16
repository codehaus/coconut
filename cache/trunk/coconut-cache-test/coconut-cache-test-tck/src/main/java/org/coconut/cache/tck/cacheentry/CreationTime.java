package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionUtils.*;
import static org.coconut.test.CollectionUtils.M8;

import java.util.Map;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.coconut.core.AttributeMap;
import org.junit.Test;

public class CreationTime extends AbstractCacheTCKTestBundle {
    static class MyLoader implements CacheLoader<Integer, String> {
        public String load(Integer key, AttributeMap attributes) throws Exception {
            CacheAttributes.setCreationTime(attributes, key + 1);
            return "" + (char) (key + 64);
        }
    }

    void assertPeekAndGet(Map.Entry<Integer, String> entry, long creationTime) {
        assertEquals(creationTime, peekEntry(entry).getCreationTime());
        assertEquals(creationTime, getEntry(entry).getCreationTime());
    }

    /**
     * Tests that timestamp is set for creation date. Furthermore test that creation date
     * is not updated when replacing existing values.
     */
    @Test
    public void testCreationDatePut() {
        clock.setTimestamp(10);
        c = newCache(newConf().setClock(clock));

        put(M1);
        assertPeekAndGet(M1, 10);

        clock.incrementTimestamp();
        put(M1);
        assertPeekAndGet(M1, 10);

        clock.incrementTimestamp();
        c.put(M1.getKey(), M2.getValue());
        assertPeekAndGet(M1, 10);

        put(M2);
        assertPeekAndGet(M2, 12);
    }

    @Test
    public void testCreationDatePutAll() {
        clock.setTimestamp(10);
        c = newCache(newConf().setClock(clock));

        putAll(M1, M2);
        assertPeekAndGet(M1, 10);
        assertPeekAndGet(M2, 10);

        clock.incrementTimestamp();
        putAll(M1, M2);
        assertPeekAndGet(M1, 10);
        assertPeekAndGet(M2, 10);

        putAll(M3, M4);
        assertPeekAndGet(M3, 11);
        assertPeekAndGet(M4, 11);
    }

    @Test
    public void testClear() {
        clock.setTimestamp(10);
        c = newCache(newConf().setClock(clock));

        put(M1);
        assertPeekAndGet(M1, 10);
        
        clock.incrementTimestamp();
        c.clear();
        put(M1);
        assertPeekAndGet(M1, 11);
    }

    /**
     * Tests that creation time can propagate via the attribute map provided to a cache
     * loader.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCreationTimeCacheLoader() {
        c = newCache(newConf().loading().setLoader(new MyLoader()));

        assertGet(M1);
        assertPeekAndGet(M1, 2);

        assertGet(M8);
        assertPeekAndGet(M8, 9);

        getAll(M3, M4);
        assertPeekAndGet(M3, 4);
        assertPeekAndGet(M4, 5);
    }
}
