/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRY_ACCESSED_FILTER;
import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M8;
import static org.coconut.test.CollectionUtils.M9;

import org.coconut.cache.service.event.CacheEntryEvent.ItemAccessed;
import org.junit.Test;

public class EventEntryGet extends AbstractEventTestBundle {
    /**
     * Tests that peek does not raise any events.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemAccessedPeek() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        subscribe(CACHEENTRY_ACCESSED_FILTER);

        peek(M1);
        peek(M3);
    }
    


    /**
     * Tests that ItemAccessed events are posted when accessing items in the cache using
     * get.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemAccessedGet() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        subscribe(CACHEENTRY_ACCESSED_FILTER);

        get(M1); // hit
        ItemAccessed<Integer, String> event = consumeItem(ItemAccessed.class, M1);
        assertTrue(event.isHit());

        get(M9); // miss
        event = consumeItem(ItemAccessed.class, M9.getKey(), null);
        assertFalse(event.isHit());
    }

    /**
     * Tests that ItemAccessed events are posted when accessing items in the cache using
     * getAll.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemAccessedGetAll() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
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
}
