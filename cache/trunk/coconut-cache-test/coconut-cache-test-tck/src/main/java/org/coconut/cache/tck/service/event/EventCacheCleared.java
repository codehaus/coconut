/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEventFilters;
import org.junit.Test;

public class EventCacheCleared extends AbstractEventTestBundle {

    @Test
    public void testCleared() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        assertNotNull(subscribe(CacheEventFilters.CACHE_CLEARED_FILTER));
        c.put(1, "B");
        c.put(5, "F");
        c.clear();
        CacheEvent.CacheCleared<?, ?> cleared = consumeItem(c,
                CacheEvent.CacheCleared.class);
        assertEquals(3, cleared.getPreviousSize());
    }

    @Test
    public void keySetCleared() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        assertNotNull(subscribe(CacheEventFilters.CACHE_CLEARED_FILTER));
        c.put(1, "B");
        c.put(5, "F");
        c.keySet().clear();
        CacheEvent.CacheCleared<?, ?> cleared = consumeItem(c,
                CacheEvent.CacheCleared.class);
        assertEquals(3, cleared.getPreviousSize());
    }

    @Test
    public void entrySetCleared() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        assertNotNull(subscribe(CacheEventFilters.CACHE_CLEARED_FILTER));
        c.put(1, "B");
        c.put(5, "F");
        c.entrySet().clear();
        CacheEvent.CacheCleared<?, ?> cleared = consumeItem(c,
                CacheEvent.CacheCleared.class);
        assertEquals(3, cleared.getPreviousSize());
    }

    @Test
    public void valueSetCleared() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        assertNotNull(subscribe(CacheEventFilters.CACHE_CLEARED_FILTER));
        c.put(1, "B");
        c.put(5, "F");
        c.values().clear();
        CacheEvent.CacheCleared<?, ?> cleared = consumeItem(c,
                CacheEvent.CacheCleared.class);
        assertEquals(3, cleared.getPreviousSize());
    }

    @Test
    public void testClearedRemoved() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        c.put(1, "B");
        c.put(5, "F");
        assertNotNull(subscribe(CacheEventFilters.CACHEEVENT_FILTER));
        c.clear();
        CacheEvent.CacheCleared<?, ?> cleared = consumeItem(c,
                CacheEvent.CacheCleared.class);
        assertEquals(3, cleared.getPreviousSize());
        assertEquals(3, cleared.getPreviousVolume());
    }
}
