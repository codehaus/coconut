/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRYEVENT_FILTER;
import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;

import org.coconut.cache.policy.Policies;
import org.coconut.cache.service.event.CacheEntryEvent.ItemAdded;
import org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved;
import org.junit.Test;

public class EventServiceEviction extends AbstractEventTestBundle{

    /**
     * Tests that ItemRemoved events are raised when an item has been evicted because of
     * lack of space in the cache.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void evicted() throws Exception {
        c = newCache(newConf().eviction().setPolicy(Policies.newLRU()).setMaximumSize(3)
                .c().event().setEnabled(true));
        put(M1);
        put(M2);
        put(M3);
        subscribe(CACHEENTRYEVENT_FILTER);
        put(M4);
        ItemRemoved<?, ?> r = consumeItem(ItemRemoved.class, M1);
        assertFalse(r.hasExpired());

        ItemAdded<?, ?> a = consumeItem(ItemAdded.class, M4);
        assertFalse(a.isLoaded());
    }
}
