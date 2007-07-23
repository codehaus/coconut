/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRY_REMOVED_FILTER;
import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M5;
import static org.coconut.test.CollectionUtils.M6;

import org.coconut.cache.policy.Policies;
import org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved;
import org.junit.Test;

public class EventEntryRemoved extends AbstractEventTestBundle{



    @Test
    public void itemRemoved() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        subscribe(CACHEENTRY_REMOVED_FILTER);

        remove(M3); // not contained in the cache -> no events
        assertQueueEmpty();
        remove(M1);
        ItemRemoved<?, ?> r = consumeItem(ItemRemoved.class, M1);
        assertEquals(M1.getKey(), r.getKey());
        assertFalse(r.hasExpired());
    }

    /**
     * Tests that ItemRemoved events are raised when an item has been evicted because of
     * lack of space in the cache.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemRemovedEvicted() throws Exception {
        c = newCache(newConf().eviction().setPolicy(Policies.newLRU()).setMaximumSize(3)
                .c().event().setEnabled(true).c());
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
}
