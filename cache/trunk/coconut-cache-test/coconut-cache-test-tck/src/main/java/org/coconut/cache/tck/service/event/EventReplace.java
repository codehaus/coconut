/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRYEVENT_FILTER;
import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;

import java.util.Arrays;
import java.util.Collection;

import org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved;
import org.coconut.cache.service.event.CacheEntryEvent.ItemUpdated;
import org.junit.Test;

public class EventReplace extends AbstractEventTestBundle {

    @Test
    public void noEvents() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        subscribe(CACHEENTRYEVENT_FILTER);
        replace(M3);
        c.replace(M2.getKey(), M3.getValue(), M2.getValue());
    }

    @Test
    public void replace() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 0);
        c.put(M2.getKey(), M3.getValue());
        subscribe(CACHEENTRYEVENT_FILTER);
        replace(M2);

        ItemUpdated<?, ?> updated = consumeItem(ItemUpdated.class, M2);
        assertEquals(M3.getValue(), updated.getPreviousValue());
    //    assertFalse(updated.isLoaded());
        assertFalse(updated.hasExpired());
    }

    @Test
    public void replace3() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 0);
        c.put(M2.getKey(), M3.getValue());
        subscribe(CACHEENTRYEVENT_FILTER);
        replace(M2.getKey(), M3.getValue(), M2.getValue());

        ItemUpdated<?, ?> updated = consumeItem(ItemUpdated.class, M2);
        assertEquals(M3.getValue(), updated.getPreviousValue());
 //       assertFalse(updated.isLoaded());
        assertFalse(updated.hasExpired());
    }
}
