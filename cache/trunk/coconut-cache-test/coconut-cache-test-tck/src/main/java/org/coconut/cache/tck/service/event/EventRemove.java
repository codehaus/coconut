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
import org.junit.Test;

public class EventRemove extends AbstractEventTestBundle {

    @Test
    public void removeNonExisting() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.remove(M3.getKey());
        c.remove(M3.getKey(), M3.getValue());
        c.removeAll(Arrays.asList(M3.getKey(), M4.getKey()));
    }

    @Test
    public void removeEntry() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.remove(M1.getKey());

        ItemRemoved<?, ?> removed = consumeItem(ItemRemoved.class, M1);
        assertFalse(removed.hasExpired());
    }

    @Test
    public void removeKeyValueEntry() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.remove(M2.getKey(), M2.getValue());
      
        ItemRemoved<?, ?> removed = consumeItem(ItemRemoved.class, M2);
        assertFalse(removed.hasExpired());
    }

    @Test
    public void removeAllEntry() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 4);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.removeAll(Arrays.asList(M3.getKey(), M4.getKey()));

        Collection<ItemRemoved> removed = consumeItems(ItemRemoved.class, M3, M4);
        for (ItemRemoved i : removed) {
            assertFalse(i.hasExpired());
        }
    }
}
