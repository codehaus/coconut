package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M6;

import java.util.Map;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.coconut.core.AttributeMap;
import org.junit.Test;

/**
 * Tests the size attribute of {@link CacheEntry}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Size extends AbstractCacheTCKTestBundle {

    static class MyLoader implements CacheLoader<Integer, String> {
        private int totalCount;

        public String load(Integer key, AttributeMap attributes) throws Exception {
            CacheAttributes.setSize(attributes, key + 1 + totalCount);
            totalCount++;
            return "" + (char) (key + 64);
        }
    }

    void assertPeekAndGet(Map.Entry<Integer, String> entry, long size) {
        assertEquals(size, peekEntry(entry).getSize());
        assertEquals(size, getEntry(entry).getSize());
        // TODO Enable when cachenetry.attributes is working
        // assertEquals(modificationTime,
        // CacheAttributes.getLastUpdateTime(getEntry(entry)
        // .getAttributes()));
    }

    /**
     * Tests default size of 1.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void put() {
        c = newCache();
        put(M1);
        assertPeekAndGet(M1, 1);
        putAll(M1, M2);
        assertPeekAndGet(M1, 1);
        assertPeekAndGet(M2, 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void loaded() {
        c = newCache(newConf().loading().setLoader(new MyLoader()));
        assertGet(M1);
        assertPeekAndGet(M1, 2);

        assertGet(M3);
        assertPeekAndGet(M3, 5);

        assertGet(M6);
        assertPeekAndGet(M6, 9);

        c.clear();
        assertGet(M1);
        assertPeekAndGet(M1, 5);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void cacheCapacity() {
        c = newCache(newConf().loading().setLoader(new MyLoader()));
        assertGet(M1);
        assertEquals(2, c.getCapacity());
        assertGet(M3);
        assertEquals(7, c.getCapacity());
        remove(M1);
        assertEquals(5, c.getCapacity());
        assertGet(M6);
        assertEquals(14, c.getCapacity());
        assertEquals(2, c.size());// element size unaffected
        c.clear();
        assertEquals(0, c.getCapacity());

        c = newCache();
        put(M1);
        assertEquals(1, c.getCapacity());
        putAll(M1, M2);
        assertEquals(2, c.getCapacity());
        c.clear();
        assertEquals(0, c.getCapacity());
    }
}
