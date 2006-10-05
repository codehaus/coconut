/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.eventbus;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.M6;
import static org.coconut.test.CollectionUtils.M7;
import static org.coconut.test.CollectionUtils.M8;
import static org.coconut.test.CollectionUtils.M9;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEvent;
import org.coconut.cache.CacheEntryEvent;
import org.coconut.cache.CacheEntryEvent.ItemAccessed;
import org.coconut.cache.CacheEntryEvent.ItemAdded;
import org.coconut.cache.CacheEntryEvent.ItemRemoved;
import org.coconut.cache.CacheEntryEvent.ItemUpdated;
import org.coconut.cache.policy.Policies;
import org.coconut.cache.tck.util.IntegerToStringLoader;
import org.coconut.event.EventSubscription;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class EventBusFeature extends AbstractEventTestBundle {

    /**
     * Tests that the cache support the getEventBus method.
     */
    @Test
    public void testEventBus() {
        c = c0; // test c0
        assertNotNull(c.getEventBus());
        assertEquals(0, c.getEventBus().getSubscribers().size());
        c.getEventBus().unsubscribeAll();
        assertEquals(0, c.getEventBus().getSubscribers().size());
    }

    /**
     * Tests that peek does not raise any events.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemAccessedPeek() throws Exception {
        c = c2; // test c2;
        subscribe(ItemAccessed.FILTER);

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
        subscribe(ItemAccessed.FILTER);

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
        subscribe(ItemAccessed.FILTER);

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
        subscribe(ItemAdded.FILTER);

        put(M3);
        consumeItem(ItemAdded.class, M3);

        put(M6, 1, TimeUnit.SECONDS);
        consumeItem(ItemAdded.class, M6);
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
        subscribe(ItemAdded.FILTER);

        putAll(M4, M5);
        consumeItems(ItemAdded.class, M4, M5);

        putAll(1, TimeUnit.SECONDS, M7, M8, M9);
        consumeItems(ItemAdded.class, M7, M8, M9);
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
        subscribe(ItemUpdated.FILTER);

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
        subscribe(ItemUpdated.FILTER);

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
        subscribe(ItemUpdated.FILTER);

        put(M1);
        put(M1);

        putAll(M1, M2);
    }

    @Test
    public void itemRemoved() throws Exception {
        c = c2; // working cache is c2
        subscribe(ItemRemoved.FILTER);

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
        c = newCache(newConf().eviction().setPolicy(Policies.newLRU()).setMaximumCapacity(3).c());
        subscribe(ItemRemoved.FILTER);
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

    @Test
    public void itemRemovedExpired() throws Exception {
        c = newCache(newConf().setClock(clock));
        subscribe(ItemRemoved.FILTER);

        put(M1, 1, TimeUnit.NANOSECONDS);
        putAll(3, TimeUnit.NANOSECONDS, M2, M3);

        clock.incrementRelativeTime(2);
        c.evict();
        assertEquals(2, c.size());

        ItemRemoved<?, ?> r = consumeItem(ItemRemoved.class, M1);
        assertTrue(r.hasExpired());

        clock.incrementRelativeTime(2);
        c.evict();

        r = consumeItem(ItemRemoved.class, M2);
        assertTrue(r.hasExpired());
        r = consumeItem(ItemRemoved.class, M3);
        assertTrue(r.hasExpired());
    }

    /**
     * This test checks that when requesting a non-existing entry with a cache
     * loader defined. First an accessed failed event will be posted then
     * followed by an itemadded event.
     */
    @Test
    public void itemAccessedThenAddedByLoading() throws Exception {
        c = loadableEmptyCache;
        subscribe(CacheEntryEvent.ITEM_FILTER);
        assertEquals(M1.getValue(), get(M1));
        ItemAccessed<Integer, String> event = consumeItem(ItemAccessed.class, M1);
        assertFalse(event.isHit());
        consumeItem(ItemAdded.class, M1);

        getAll(M1, M3);
        event = consumeItem(ItemAccessed.class, M1);
        assertTrue(event.isHit());
        event = consumeItem(ItemAccessed.class, M3);
        assertFalse(event.isHit());
        consumeItem(ItemAdded.class, M3);
    }

    @Test
    public void itemAddedByLoaded() throws Exception {
        c = loadableEmptyCache;
        subscribe(ItemAdded.FILTER);

        assertEquals(M1.getValue(), get(M1));
        consumeItem(ItemAdded.class, M1);

        getAll(M2, M3);
        consumeItem(ItemAdded.class, M2);
        consumeItem(ItemAdded.class, M3);

        getAll(M4, M6); // M6 does not cause loading
        consumeItem(ItemAdded.class, M4);
    }

    @Test
    public void itemUpdatedExpiredWithLoading() throws Exception {
        c = newCache(newConf().setClock(clock).backend().setLoader(new IntegerToStringLoader()).c());
        subscribe(ItemUpdated.FILTER);
        put(M1, 1, TimeUnit.NANOSECONDS);
        putAll(3, TimeUnit.NANOSECONDS, M2, M3);

        clock.incrementRelativeTime(2);
        c.evict();
        // TODO evict loads new values???
        // most caches will probably have background loading.
        // evict will behave as loadAll()??????
        // in this way we can support both

        // assertEquals(3, c.size());
        // ItemRemoved<?, ?> r = consumeItem(c, ItemRemoved.class, M1);
        // assertTrue(r.hasExpired());
        //
        // clock.incrementRelativeTime(2);
        // c.evict();
        // r = consumeItem(c, ItemRemoved.class, M2);
        // assertTrue(r.hasExpired());
        // r = consumeItem(c, ItemRemoved.class, M3);
        // assertTrue(r.hasExpired());

    }

    // TODO testExpiredNoLoadingStrict
    public void testCleared() throws Exception {
        assertNotNull(subscribe(CacheEvent.CacheCleared.FILTER));
        c2.put(1, "B"); // sequenceid=1
        c2.put(5, "F"); // sequenceid=2
        c2.clear();
        CacheEvent.CacheCleared<?, ?> cleared = consumeItem(c2, CacheEvent.CacheCleared.class);
        assertEquals(2, cleared.getPreviousSize());
    }

    public void testStatisticsReset() throws Exception {
        assertNotNull(subscribe(CacheEvent.CacheStatisticsReset.FILTER));
        c2.put(1, "B"); // sequenceid=1
        c2.get(0);
        c2.get(1);
        c2.resetStatistics();
        CacheEvent.CacheStatisticsReset<?, ?> cleared = consumeItem(c2,
                CacheEvent.CacheStatisticsReset.class);
        assertEquals(0.5, cleared.getPreviousHitStat().getHitRatio(), 0.00001);
        assertEquals(1, cleared.getPreviousHitStat().getNumberOfHits());
        assertEquals(1, cleared.getPreviousHitStat().getNumberOfMisses());
    }

    @Test
    public void testUnsubscribe() throws Exception {
        c = c0;
        EventSubscription<?> s = subscribe(ItemAccessed.FILTER);
        c.get(M1.getKey());
        assertEquals(1, getPendingEvents());
        s.cancel();
        c.get(M2.getKey());
        assertEquals(1, getPendingEvents());
        consumeItem(); // take event
    }
}
