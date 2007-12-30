/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M6;

import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.common.SizeAttribute;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.loading.AbstractCacheLoader;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.junit.Test;

/**
 * Tests the size attribute of {@link CacheEntry}.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class Size extends AbstractCacheTCKTest {

    @SuppressWarnings("unchecked")
    @Test
    public void cacheCapacity() {
        init(conf.loading().setLoader(new MyLoader()));
        assertGet(M1);
        assertEquals(2, c.getVolume());
        assertGet(M3);
        assertEquals(7, c.getVolume());
        remove(M1);
        assertEquals(5, c.getVolume());
        assertGet(M6);
        assertEquals(14, c.getVolume());
        assertSize(2);// element size unaffected
        c.clear();
        assertEquals(0, c.getVolume());

        init();
        put(M1);
        assertEquals(1, c.getVolume());
        putAll(M1, M2);
        assertEquals(2, c.getVolume());
        c.clear();
        assertEquals(0, c.getVolume());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void loaded() {
        init(conf.loading().setLoader(new MyLoader()));
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

    /**
     * Tests default size of 1.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void put() {
        put(M1);
        assertPeekAndGet(M1, 1);
        putAll(M1, M2);
        assertPeekAndGet(M1, 1);
        assertPeekAndGet(M2, 1);
    }

    void assertPeekAndGet(Map.Entry<Integer, String> entry, long size) {
        assertEquals(size, peekEntry(entry).getSize());
        assertEquals(size, getEntry(entry).getSize());

        assertEquals(size, SizeAttribute.get(peekAttributes(entry)));
        assertEquals(size, peekAttributes(entry).getLong(SizeAttribute.INSTANCE));
        assertEquals(size, peekAttributes(entry).getLong(SizeAttribute.INSTANCE, size - 1));
        assertEquals(size, peekAttributes(entry).get(SizeAttribute.INSTANCE));
        assertEquals(size, peekAttributes(entry).get(SizeAttribute.INSTANCE, size - 1));

        assertEquals(size, SizeAttribute.get(getAttributes(entry)));
        assertEquals(size, getAttributes(entry).getLong(SizeAttribute.INSTANCE));
        assertEquals(size, getAttributes(entry).getLong(SizeAttribute.INSTANCE, size - 1));
        assertEquals(size, getAttributes(entry).get(SizeAttribute.INSTANCE));
        assertEquals(size, getAttributes(entry).get(SizeAttribute.INSTANCE, size - 1));
    }

    static class MyLoader extends AbstractCacheLoader<Integer, String> {
        private int totalCount;

        public String load(Integer key, AttributeMap attributes) throws Exception {
            SizeAttribute.set(attributes, key + 1 + totalCount);
            totalCount++;
            return "" + (char) (key + 64);
        }
    }
}
