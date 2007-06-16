package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M6;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.coconut.core.AttributeMap;
import org.junit.Test;

public class SizeOfElement extends AbstractCacheTCKTestBundle {

    static class MyLoader implements CacheLoader<Integer, String> {
        private int totalCount;

        public String load(Integer key, AttributeMap attributes) throws Exception {
            CacheAttributes.setSize(attributes, key + 1 + totalCount);
            System.out.println("loaded " + key);
            totalCount++;
            return "" + (char) (key + 64);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testElementSizeFromCacheLoader() {
        c = newCache(newConf().loading().setLoader(new MyLoader()));
        assertGet(M1);
        assertEquals(2, getEntry(M1).getSize());
        
        assertGet(M3);
        assertEquals(5, getEntry(M3).getSize());
        
        assertGet(M6);
        assertEquals(11, getEntry(M6).getSize());
        c.clear();
        assertGet(M1);
        assertEquals(8, getEntry(M1).getSize());
    }

    @SuppressWarnings("unchecked")
    //@Test
    public void testCacheSizeFromCacheLoader() {
        c = newCache(newConf().loading().setLoader(new MyLoader()));
        assertGet(M1);
        assertEquals(2, c.getCapacity());
        assertGet(M3);
        assertEquals(7, c.getCapacity());
        remove(M1);
        assertEquals(5, c.getCapacity());
        assertGet(M6);
        assertEquals(14, c.getCapacity());
        c.clear();
        assertEquals(0, c.getCapacity());
    }
}
