/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRYEVENT_FILTER;
import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M4;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.policy.Policies;
import org.coconut.cache.policy.paging.LRUPolicy;
import org.coconut.cache.service.event.CacheEntryEvent.ItemCreated;
import org.coconut.cache.service.event.CacheEntryEvent.ItemDeleted;
import org.junit.Test;

public class EventServiceEviction extends AbstractEventTestBundle {

    /**
     * Tests that ItemRemoved events are raised when an item has been evicted because of
     * lack of space in the cache.
     * 
     * @throws Exception
     *             test failed
     */
    @Test
    public void evicted() throws Exception {
        c = newCache(conf.eviction().setPolicy(Policies.newLRU()).setMaximumSize(3).c());
        put(M1);
        put(M2);
        put(M3);
        subscribe(CACHEENTRYEVENT_FILTER);
        put(M4);
        ItemDeleted<?, ?> r = consumeItem(ItemDeleted.class, M1);
        assertFalse(r.hasExpired());

        ItemCreated<?, ?> a = consumeItem(ItemCreated.class, M4);
// assertFalse(a.isLoaded());
    }

    @Test
    public void testRejectPutEntry() {
        RejectEntriesPolicy rep = new RejectEntriesPolicy();
        c = newCache(conf.eviction().setPolicy(rep));

        c.put(1, "A");
        c.put(2, "B");
        subscribe(CACHEENTRYEVENT_FILTER);
        rep.rejectAdd = true;
        put(M3);
        assertSize(2);
    }

    @Test
    public void testRejectReplaceEntry() {
        RejectEntriesPolicy rep = new RejectEntriesPolicy();
        c = newCache(conf.eviction().setPolicy(rep).c());

        c.put(1, "A");
        c.put(2, "B");
        assertSize(2);
        subscribe(CACHEENTRYEVENT_FILTER);
        rep.rejectUpdate = true;
        c.put(2, "C");
        assertEquals(1, c.size());
        ItemDeleted<?, ?> r = consumeItem(ItemDeleted.class, M2);
        assertFalse(r.hasExpired());
        assertFalse(c.containsKey(2));
    }

    @SuppressWarnings( { "unchecked", "serial" })
    static class RejectEntriesPolicy extends LRUPolicy {
        volatile boolean rejectAdd;

        volatile boolean rejectUpdate;

        @Override
        public int add(Object data, AttributeMap ignore) {
            if (!rejectAdd) {
                return super.add(data, ignore);
            } else {
                return -1;
            }
        }

        @Override
        public boolean update(int index, Object newElement, AttributeMap ignore) {
            if (!rejectUpdate) {
                return super.update(index, newElement, ignore);
            } else {
                return false;
            }
        }
    }
}
