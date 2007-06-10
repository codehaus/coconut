package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRY_ADDED_FILTER;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;

import org.coconut.cache.service.event.CacheEntryEvent.ItemAdded;
import org.junit.Test;

public class EventEntryAdded extends AbstractEventTestBundle {

    /**
     * Tests that ItemAdded events are raised when adding elements to the cache with the
     * put method.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemAddedPut() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        subscribe(CACHEENTRY_ADDED_FILTER);

        put(M3);
        consumeItem(ItemAdded.class, M3);
    }

    /**
     * Tests that ItemAdded events are raised when adding elements to the cache with the
     * putAll method.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemAddedPutAll() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        subscribe(CACHEENTRY_ADDED_FILTER);

        putAll(M4, M5);
        consumeItems(ItemAdded.class, M4, M5);
    }
}
