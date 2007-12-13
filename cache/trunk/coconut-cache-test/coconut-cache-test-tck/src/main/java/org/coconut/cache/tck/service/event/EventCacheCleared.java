/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.test.CollectionTestUtil.M1;

import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEventFilters;
import org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved;
import org.junit.Test;

public class EventCacheCleared extends AbstractEventTestBundle {

    @Test
    public void testCleared() throws Exception {
        setCache(2);
        assertNotNull(subscribe(CacheEventFilters.CACHE_CLEARED_FILTER));
        c.put(1, "B");
        c.put(5, "F");
        c.clear();
        CacheEvent.CacheCleared<?, ?> cleared = consumeItem(c, CacheEvent.CacheCleared.class);
        assertEquals(3, cleared.getPreviousSize());
    }

    @Test
    public void cleared2() throws Exception {
        setCache();
        assertNotNull(subscribe(CacheEventFilters.CACHEENTRY_REMOVED_FILTER));
        put(M1);
        c.clear();
        consumeItem(ItemRemoved.class, M1);
    }

    @Test
    public void noClearEvent0() {
        setCache();
        assertNotNull(subscribe(CacheEventFilters.CACHE_CLEARED_FILTER));
        c.clear();
    }

    @Test
    public void noClearEvent0_() throws Exception {
        setCache();
        assertNotNull(subscribe(CacheEventFilters.CACHE_CLEARED_FILTER));
        c.put(1, "B");
        c.clear();
        consumeItem(c, CacheEvent.CacheCleared.class);
        c.clear();
    }

    @Test
    public void keySetCleared() throws Exception {
        setCache(2);
        assertNotNull(subscribe(CacheEventFilters.CACHE_CLEARED_FILTER));
        c.put(1, "B");
        c.put(5, "F");
        c.keySet().clear();
        CacheEvent.CacheCleared<?, ?> cleared = consumeItem(c, CacheEvent.CacheCleared.class);
        assertEquals(3, cleared.getPreviousSize());
    }

    @Test
    public void entrySetCleared() throws Exception {
        setCache(2);
        assertNotNull(subscribe(CacheEventFilters.CACHE_CLEARED_FILTER));
        c.put(1, "B");
        c.put(5, "F");
        c.entrySet().clear();
        CacheEvent.CacheCleared<?, ?> cleared = consumeItem(c, CacheEvent.CacheCleared.class);
        assertEquals(3, cleared.getPreviousSize());
    }

    @Test
    public void valueSetCleared() throws Exception {
        setCache(2);
        assertNotNull(subscribe(CacheEventFilters.CACHE_CLEARED_FILTER));
        c.put(1, "B");
        c.put(5, "F");
        c.values().clear();
        CacheEvent.CacheCleared<?, ?> cleared = consumeItem(c, CacheEvent.CacheCleared.class);
        assertEquals(3, cleared.getPreviousSize());
    }

    // @Test
    public void testClearedRemoved() throws Exception {
        setCache(2);
        c.put(1, "B");
        c.put(5, "F");
        assertNotNull(subscribe(CacheEventFilters.CACHEEVENT_FILTER));
        c.clear();
        CacheEvent.CacheCleared<?, ?> cleared = consumeItem(c, CacheEvent.CacheCleared.class);
        assertEquals(3, cleared.getPreviousSize());
        assertEquals(3, cleared.getPreviousVolume());
    }
}
