/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M6;

import java.util.Map;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.coconut.core.AttributeMap;
import org.junit.Test;

public class Cost extends AbstractCacheTCKTestBundle {
    static class MyLoader implements CacheLoader<Integer, String> {
        private int totalCount;

        public String load(Integer key, AttributeMap attributes) throws Exception {
            CacheAttributes.setCost(attributes, key + 0.5 + totalCount);
            totalCount++;
            return "" + (char) (key + 64);
        }
    }

    
    void assertPeekAndGet(Map.Entry<Integer, String> entry, double cost) {
        assertEquals(cost, peekEntry(entry).getCost());
        assertEquals(cost, getEntry(entry).getCost());
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
        assertPeekAndGet(M1, 1.5);

        assertGet(M3);
        assertPeekAndGet(M3, 4.5);

        assertGet(M6);
        assertPeekAndGet(M6, 8.5);

        c.clear();
        assertGet(M1);
        assertPeekAndGet(M1, 4.5);
    }

}
