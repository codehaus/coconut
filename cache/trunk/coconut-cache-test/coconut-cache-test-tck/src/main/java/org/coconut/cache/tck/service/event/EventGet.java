/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRYEVENT_FILTER;
import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRY_ADDED_FILTER;
import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M4;
import static org.coconut.test.CollectionTestUtil.M6;

import java.util.Collection;

import org.coconut.cache.service.event.CacheEntryEvent.ItemCreated;
import org.coconut.cache.service.event.CacheEntryEvent.ItemDeleted;
import org.coconut.cache.service.event.CacheEntryEvent.ItemUpdated;
import org.coconut.cache.test.util.CacheEntryFilter;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.junit.Test;

public class EventGet extends AbstractEventTestBundle {

    /**
     * Tests that the get method does not fire any events.
     *
     * @throws Exception
     *             test failed
     */
    @Test
    public void get() throws Exception {
        c = newCache(2);
        subscribe(CACHEENTRYEVENT_FILTER);
        assertGet(M1);
        assertGet(M2);
        assertNullGet(M3);
    }

    @Test
    public void getAll2WithCacheLoader() throws Exception {
        c = newCache(conf.loading().setLoader(new IntegerToStringLoader()).c(), 0);
        subscribe(CACHEENTRY_ADDED_FILTER);

        assertEquals(M1.getValue(), get(M1));
        consumeItem(ItemCreated.class, M1);

        getAll(M2, M3);
        Collection col = consumeItems(c, 2);
        assertTrue(col.contains(EventServiceLoading.itemAdded(c, M2.getKey(), M2.getValue())));
        assertTrue(col.contains(EventServiceLoading.itemAdded(c, M3.getKey(), M3.getValue())));

        getAll(M4, M6); // M6 does not cause loading
        consumeItem(ItemCreated.class, M4);
    }

    @Test
    public void getAllWithCacheLoader() throws Exception {
        c = newCache(conf.loading().setLoader(new IntegerToStringLoader()).c(), 0);
        subscribe(CACHEENTRYEVENT_FILTER);
        assertEquals(M1.getValue(), get(M1));
        consumeItem(ItemCreated.class, M1);
        getAll(M1, M3);
        consumeItem(ItemCreated.class, M3);
    }

    /**
     * Tests that the get method fires an ItemAdded when an entry is loaded through a
     * CacheLoader.
     *
     * @throws Exception
     *             test failed
     */
    @Test
    public void getEntryWithCacheLoader() throws Exception {
        c = newCache(conf.loading().setLoader(new IntegerToStringLoader()), 2);
        subscribe(CACHEENTRYEVENT_FILTER);
        assertGetEntry(M1);
        assertGetEntry(M2);
        assertGetEntry(M3);
        ItemCreated<?, ?> added = consumeItem(ItemCreated.class, M3);
        // assertTrue(added.isLoaded());
    }

    /**
     * Tests that the get method does not fire any events.
     *
     * @throws Exception
     *             test failed
     */
    @Test
    public void getExpired() throws Exception {
        CacheEntryFilter f = new CacheEntryFilter();
        c = newCache(conf.expiration().setExpirationFilter(f).c(), 2);
        subscribe(CACHEENTRYEVENT_FILTER);
        assertGet(M1);
        assertGet(M2);
        f.setAccept(true);
        assertNullGet(M2);
        ItemDeleted<?, ?> removed = consumeItem(ItemDeleted.class, M2);
        assertTrue(removed.hasExpired());
    }

    /**
     * Tests that the get method fires an ItemAdded when an entry is loaded through a
     * CacheLoader.
     *
     * @throws Exception
     *             test failed
     */
    // @Test
    public void getExpiredWithCacheLoader() throws Exception {
        CacheEntryFilter f = new CacheEntryFilter();
        IntegerToStringLoader loader = new IntegerToStringLoader();
        c = newCache(conf.loading().setLoader(loader).c().expiration().setExpirationFilter(f), 2);
        subscribe(CACHEENTRYEVENT_FILTER);
        assertGet(M1);
        assertGet(M2);
        f.setAccept(true);
        assertGet(M2);
        ItemUpdated<?, ?> added = consumeItem(ItemUpdated.class, M2);
        // assertTrue(added.isLoaded());
        assertTrue(added.hasExpired());
    }

    @Test
    public void getExpiredWithCacheLoaderNullLoad() throws Exception {
        CacheEntryFilter f = new CacheEntryFilter();
        IntegerToStringLoader loader = new IntegerToStringLoader();
        loader.setDoReturnNull(true);
        c = newCache(conf.loading().setLoader(loader).c().expiration().setExpirationFilter(f), 2);
        subscribe(CACHEENTRYEVENT_FILTER);
        assertGet(M1);
        assertGet(M2);
        f.setAccept(true);
        assertNullGet(M2);
        ItemDeleted<?, ?> removed = consumeItem(ItemDeleted.class, M2);
        assertTrue(removed.hasExpired());
    }

    /**
     * Tests that the get method fires an ItemAdded when an entry is loaded through a
     * CacheLoader.
     *
     * @throws Exception
     *             test failed
     */
    @Test
    public void getWithCacheLoader() throws Exception {
        c = newCache(conf.loading().setLoader(new IntegerToStringLoader()).c(), 2);
        subscribe(CACHEENTRYEVENT_FILTER);
        assertGet(M1);
        assertGet(M2);
        assertGet(M3);
        ItemCreated<?, ?> added = consumeItem(ItemCreated.class, M3);
        // assertTrue(added.isLoaded());
    }

    /**
     * Tests that the get method fires an ItemAdded when an entry is loaded through a
     * CacheLoader.
     *
     * @throws Exception
     *             test failed
     */
    @Test
    public void getWithCacheLoaderNullLoad() throws Exception {
        IntegerToStringLoader loader = new IntegerToStringLoader();
        loader.setDoReturnNull(true);
        c = newCache(conf.loading().setLoader(loader).c(), 2);
        subscribe(CACHEENTRYEVENT_FILTER);
        assertGet(M1);
        assertGet(M2);
        assertNullGet(M3);
    }

    /**
     * Tests that the get method fires an ItemAdded when an entry is loaded through a
     * CacheLoader.
     *
     * @throws Exception
     *             test failed
     */
    @Test
    public void peekWithCacheLoader() throws Exception {
        c = newCache(conf.loading().setLoader(new IntegerToStringLoader()).c(), 2);
        subscribe(CACHEENTRYEVENT_FILTER);
        assertPeek(M1);
        assertPeek(M2);
        assertNullPeek(M3);
        assertNull(c.peekEntry(M3.getKey()));
    }
}
