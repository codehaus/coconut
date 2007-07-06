package org.coconut.cache.tck.service.expiration;

import java.util.Map;

import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.coconut.filter.CollectionFilters;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;
import org.coconut.test.CollectionUtils;
import org.junit.Test;

public class RemoveAllAndFiltered extends AbstractCacheTCKTestBundle {

    @Test(expected = NullPointerException.class)
    public void testRemoveAllNullPointerException() {
        c = newCache();
        expiration().removeAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveAll2NullPointerException() {
        c = newCache();
        expiration().removeAll(CollectionUtils.keysWithNull);

    }

    @Test
    public void testRemoveAll() {
        c = newCache();
        assertEquals(0, expiration().removeAll(CollectionUtils.asList(2, 3)));

        c = newCache(5);
        assertEquals(2, expiration().removeAll(CollectionUtils.asList(2, 3)));
        assertSize(3);

        c = newCache(5);
        assertEquals(1, expiration().removeAll(CollectionUtils.asList(5, 6)));
        assertSize(4);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveFilteredNullPointerException() {
        expiration().removeFiltered(null);
    }

    @Test
    public void testRemoveAllFiltered() {
        c = newCache();
        assertEquals(0, expiration().removeFiltered(Filters.TRUE));

        c = newCache();
        assertEquals(0, expiration().removeFiltered(Filters.FALSE));

        c = newCache(5);
        assertEquals(5, expiration().removeFiltered(Filters.TRUE));
        assertSize(0);

        c = newCache(5);
        assertEquals(0, expiration().removeFiltered(Filters.FALSE));
        assertSize(5);

        c = newCache(5);
        Filter<Map.Entry<Integer, String>> f = CollectionFilters.anyKeyEquals(2, 4);
        assertEquals(2, expiration().removeFiltered(f));
        assertSize(3);

        c = newCache(5);
        f = CollectionFilters.anyValueEquals("A", "C", "H");
        assertEquals(2, expiration().removeFiltered(f));
        assertSize(3);
    }

    // TODO we should check statistics+ events with a failing filter.
    // some implementations might

}
