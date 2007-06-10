package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRY_UPDATED_FILTER;
import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;

import org.coconut.cache.service.event.CacheEntryEvent.ItemUpdated;
import org.junit.Test;

public class EventEntryUpdated extends AbstractEventTestBundle{

    /**
     * Tests that ItemUpdated events are raised when changing the value of an entry in the
     * cache through the put method.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemUpdatedPut() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 0);
        subscribe(CACHEENTRY_UPDATED_FILTER);

        c.put(M2.getKey(), M1.getValue());
        put(M2);

        ItemUpdated<?, ?> updated = consumeItem(ItemUpdated.class, M2);
        assertEquals(M1.getValue(), updated.getPreviousValue());
    }

    /**
     * Tests that ItemUpdated events are raised when changing the value of an entry in the
     * cache throgh the putAll method.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemUpdatedPutAll() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 0);
        subscribe(CACHEENTRY_UPDATED_FILTER);

        c.put(M2.getKey(), M1.getValue());
        putAll(M1, M2);

        ItemUpdated<?, ?> updated = consumeItem(ItemUpdated.class, M2);
        assertEquals(M1.getValue(), updated.getPreviousValue());
    }

    /**
     * Tests that ItemUpdated events are not raised when try to change the current value
     * to a value that is equal to itself.
     * 
     * @throws Exception
     *             test failed
     */
    public void itemUpdatedNoEventsForSameValuePut() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 0);
        subscribe(CACHEENTRY_UPDATED_FILTER);

        put(M1);
        put(M1);

        putAll(M1, M2);
    }
}
