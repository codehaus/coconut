/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.cacheentry;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M4;
import static org.coconut.test.CollectionTestUtil.M5;
import static org.coconut.test.CollectionTestUtil.M8;

import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.common.DateModifiedAttribute;
import org.coconut.cache.service.loading.AbstractCacheLoader;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.junit.Test;

public class LastUpdatedTime extends AbstractCacheTCKTest {
    static class MyLoader extends AbstractCacheLoader<Integer, String> {
        public String load(Integer key, AttributeMap attributes) throws Exception {
            DateModifiedAttribute.INSTANCE.setLong(attributes, key + 1);
            return "" + (char) (key + 64);
        }
    }

    void assertPeekAndGet(Map.Entry<Integer, String> entry, long modificationTime) {
        assertEquals(modificationTime, peekEntry(entry).getLastUpdateTime());
        assertEquals(modificationTime, getEntry(entry).getLastUpdateTime());
        // TODO Enable when cachenetry.attributes is working
        // assertEquals(modificationTime,
        // CacheAttributes.getLastUpdateTime(getEntry(entry)
        // .getAttributes()));
    }

    /**
     * Tests that timestamp is set for modification date. This also tests that put of the
     * same values constitutes a new put ie modification time is updated.
     */
    @Test
    public void put() {
        clock.setTimestamp(10);

        put(M1);
        assertPeekAndGet(M1, 10);

        clock.incrementTimestamp();
        put(M1);
        assertPeekAndGet(M1, 11);

        clock.incrementTimestamp();
        c.put(M1.getKey(), M2.getValue());
        assertPeekAndGet(M1, 12);

        put(M2);
        assertPeekAndGet(M2, 12);
    }

    /**
     * as {@link #put()} except that we use {@link Map#putAll(Map)} instead
     */
    @Test
    public void putAll() {
        clock.setTimestamp(10);

        putAll(M1, M2, M3);
        assertPeekAndGet(M1, 10);
        assertPeekAndGet(M2, 10);
        assertPeekAndGet(M3, 10);

        clock.incrementTimestamp();
        putAll(M1, M2);
        assertPeekAndGet(M1, 11);
        assertPeekAndGet(M2, 11);
        assertPeekAndGet(M3, 10);

        putAll(M3, M4);
        assertPeekAndGet(M3, 11);
        assertPeekAndGet(M4, 11);
    }

    /**
     * as {@link #put()} except that we use
     * {@link java.util.ConcurrentMap#putIfAbsent(Map)} instead
     */
    @Test
    public void putIfAbsent() {
        clock.setTimestamp(10);

        putIfAbsent(M1);
        assertPeekAndGet(M1, 10);

        clock.incrementTimestamp();
        putIfAbsent(M1);
        assertPeekAndGet(M1, 10);

        clock.incrementTimestamp();
        putIfAbsent(M1.getKey(), M2.getValue());
        assertPeekAndGet(M1, 10);

        putIfAbsent(M2);
        assertPeekAndGet(M2, 12);
    }

    /**
     * as {@link #put()} except that we use
     * {@link java.util.ConcurrentMap#putIfAbsent(Map)} instead
     */
    @Test
    public void replace() {
        clock.setTimestamp(10);

        put(M1);
        assertPeekAndGet(M1, 10);

        clock.incrementTimestamp();
        replace(M1);
        assertPeekAndGet(M1, 11);

        clock.incrementTimestamp();
        replace(M1.getKey(), M2.getValue());
        assertPeekAndGet(M1, 12);

        clock.incrementTimestamp();
        replace(M1.getKey(), M2.getValue(), M3.getValue());
        assertPeekAndGet(M1, 13);

        replace(M1.getKey(), M5.getValue(), M4.getValue());
        assertPeekAndGet(M1, 13);

    }

    /**
     * Tests that remove will force new elements to have a new timestamp.
     */
    @Test
    public void remove() {
        clock.setTimestamp(10);

        put(M1);
        assertPeekAndGet(M1, 10);

        clock.incrementTimestamp();
        remove(M1);
        put(M1);
        assertPeekAndGet(M1, 11);
    }

    /**
     * Tests that clear will force new elements to have a new timestamp.
     */
    @Test
    public void clear() {
        clock.setTimestamp(10);

        put(M1);
        assertPeekAndGet(M1, 10);

        clock.incrementTimestamp();
        c.clear();
        put(M1);
        assertPeekAndGet(M1, 11);
    }

    /**
     * Tests that timestamp is set for creation date when loading values
     */
    @Test
    public void loadedNoAttribute() {
        clock.setTimestamp(10);
        init(conf.loading().setLoader(new IntegerToStringLoader()));
        get(M1);
        assertEquals(10l, getEntry(M1).getLastUpdateTime());
    }

    /**
     * Tests that creation time can propagate via the attribute map provided to a cache
     * loader.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void loadedAttribute() {
        init(conf.loading().setLoader(new MyLoader()));

        assertGet(M1);
        assertPeekAndGet(M1, 2);

        assertGet(M8);
        assertPeekAndGet(M8, 9);

        getAll(M3, M4);
        assertPeekAndGet(M3, 4);
        assertPeekAndGet(M4, 5);
    }

}
