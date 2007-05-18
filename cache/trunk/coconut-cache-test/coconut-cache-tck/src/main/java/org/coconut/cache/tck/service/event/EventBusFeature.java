/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRY_ACCESSED_FILTER;
import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRY_ADDED_FILTER;
import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRY_REMOVED_FILTER;
import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRY_UPDATED_FILTER;
import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.M6;
import static org.coconut.test.CollectionUtils.M8;
import static org.coconut.test.CollectionUtils.M9;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.policy.Policies;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEventConfiguration;
import org.coconut.cache.service.event.CacheEventFilters;
import org.coconut.cache.service.event.CacheEntryEvent.ItemAccessed;
import org.coconut.cache.service.event.CacheEntryEvent.ItemAdded;
import org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved;
import org.coconut.cache.service.event.CacheEntryEvent.ItemUpdated;
import org.coconut.event.EventSubscription;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class EventBusFeature extends AbstractEventTestBundle {

    
    @Before
    public void setup() {
        CacheConfiguration conf = CacheConfiguration.create();
        conf.event();
        c = newCache(conf);
        c2 = newCache(conf, 2);
        c0=newCache(conf,0);
    }

    // /**
    // * Tests that the cache support the getEventBus method.
    // */
    // @Test
    // public void testEventBus() {
    // c = c0; // test c0
    // assertNotNull(c.getEventBus());
    // assertEquals(0, c.getEventBus().getSubscribers().size());
    // c.getEventBus().unsubscribeAll();
    // assertEquals(0, c.getEventBus().getSubscribers().size());
    // }

    /**
     * Tests that peek does not raise any events.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemAccessedPeek() throws Exception {
        c = c2; // test c2;
        subscribe(CACHEENTRY_ACCESSED_FILTER);

        peek(M1);
        peek(M3);
    }

    /**
     * Tests that ItemAccessed events are posted when accessing items in the
     * cache using get.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemAccessedGet() throws Exception {
        c = c2; // test c2;
        subscribe(CACHEENTRY_ACCESSED_FILTER);

        get(M1); // hit
        ItemAccessed<Integer, String> event = consumeItem(ItemAccessed.class, M1);
        assertTrue(event.isHit());

        get(M9); // miss
        event = consumeItem(ItemAccessed.class, M9.getKey(), null);
        assertFalse(event.isHit());
    }

    /**
     * Tests that ItemAccessed events are posted when accessing items in the
     * cache using getAll.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemAccessedGetAll() throws Exception {
        c = c2; // test c2;
        subscribe(CACHEENTRY_ACCESSED_FILTER);

        getAll(M1, M2, M8, M9);
        // TODO what about order is it allowed to be 4-3-2-1 instead of 1-2-3-4?
        ItemAccessed<Integer, String> event = consumeItem(ItemAccessed.class, M1);
        assertTrue(event.isHit());
        event = consumeItem(ItemAccessed.class, M2);
        assertTrue(event.isHit());
        event = consumeItem(ItemAccessed.class, M8.getKey(), null);
        assertFalse(event.isHit());
        event = consumeItem(ItemAccessed.class, M9.getKey(), null);
        assertFalse(event.isHit());
    }

    /**
     * Tests that ItemAdded events are raised when adding elements to the cache
     * with the put method.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemAddedPut() throws Exception {
        c = c2;
        subscribe(CACHEENTRY_ADDED_FILTER);

        put(M3);
        consumeItem(ItemAdded.class, M3);
    }

    /**
     * Tests that ItemAdded events are raised when adding elements to the cache
     * with the putAll method.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemAddedPutAll() throws Exception {
        c = c2;
        subscribe(CACHEENTRY_ADDED_FILTER);

        putAll(M4, M5);
        consumeItems(ItemAdded.class, M4, M5);
    }

    /**
     * Tests that ItemUpdated events are raised when changing the value of an
     * entry in the cache through the put method.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemUpdatedPut() throws Exception {
        c = c0;
        subscribe(CACHEENTRY_UPDATED_FILTER);

        c.put(M2.getKey(), M1.getValue());
        put(M2);

        ItemUpdated<?, ?> updated = consumeItem(ItemUpdated.class, M2);
        assertEquals(M1.getValue(), updated.getPreviousValue());
    }

    /**
     * Tests that ItemUpdated events are raised when changing the value of an
     * entry in the cache throgh the putAll method.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemUpdatedPutAll() throws Exception {
        c = c0;
        subscribe(CACHEENTRY_UPDATED_FILTER);

        c.put(M2.getKey(), M1.getValue());
        putAll(M1, M2);

        ItemUpdated<?, ?> updated = consumeItem(ItemUpdated.class, M2);
        assertEquals(M1.getValue(), updated.getPreviousValue());
    }

    /**
     * Tests that ItemUpdated events are not raised when try to change the
     * current value to a value that is equal to itself.
     * 
     * @throws Exception
     *             test failed
     */
    public void itemUpdatedNoEventsForSameValuePut() throws Exception {
        c = c0;
        subscribe(CACHEENTRY_UPDATED_FILTER);

        put(M1);
        put(M1);

        putAll(M1, M2);
    }

    @Test
    public void itemRemoved() throws Exception {
        c = c2; // working cache is c2
        subscribe(CACHEENTRY_REMOVED_FILTER);

        remove(M3); // not contained in the cache -> no events
        remove(M1);
        ItemRemoved<?, ?> r = consumeItem(ItemRemoved.class, M1);
        assertFalse(r.hasExpired());
    }

    /**
     * Tests that ItemRemoved events are raised when an item has been evicted
     * because of lack of space in the cache.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemRemovedEvicted() throws Exception {
        c = newCache(newConf().eviction().setPolicy(Policies.newLRU()).setMaximumSize(3)
                .c());
        subscribe(CACHEENTRY_REMOVED_FILTER);
        putAll(M1, M2, M3);
        get(M2);
        get(M3);

        put(M4);
        ItemRemoved<?, ?> r = consumeItem(ItemRemoved.class, M1);
        assertFalse(r.hasExpired());

        putAll(M5, M6);
        r = consumeItem(ItemRemoved.class, M2);
        assertFalse(r.hasExpired());
        r = consumeItem(ItemRemoved.class, M3);
        assertFalse(r.hasExpired());
    }

    // TODO testExpiredNoLoadingStrict
    public void testCleared() throws Exception {
        assertNotNull(subscribe(CacheEventFilters.CACHE_CLEARED_FILTER));
        c2.put(1, "B"); // sequenceid=1
        c2.put(5, "F"); // sequenceid=2
        c2.clear();
        CacheEvent.CacheCleared<?, ?> cleared = consumeItem(c2,
                CacheEvent.CacheCleared.class);
        assertEquals(2, cleared.getPreviousSize());
    }

    @Test
    public void testUnsubscribe() throws Exception {
        c = c0;
        EventSubscription<?> s = subscribe(CACHEENTRY_ACCESSED_FILTER);
        c.get(M1.getKey());
        assertEquals(1, getPendingEvents());
        s.unsubscribe();
        c.get(M2.getKey());
        assertEquals(1, getPendingEvents());
        consumeItem(); // take event
    }
}
