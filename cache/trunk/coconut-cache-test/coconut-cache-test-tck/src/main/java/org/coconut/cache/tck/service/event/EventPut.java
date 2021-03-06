/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRYEVENT_FILTER;
import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;

import java.util.Collection;

import org.coconut.cache.service.event.CacheEntryEvent.ItemCreated;
import org.coconut.cache.service.event.CacheEntryEvent.ItemUpdated;
import org.coconut.operations.Predicates;
import org.junit.Test;

public class EventPut extends AbstractEventTestBundle {

    /**
     * Tests put
     *
     * @throws Exception
     *             test failed
     */
    @Test
    public void put() {
        init();
        subscribe(CACHEENTRYEVENT_FILTER);

        put(M1);

        ItemCreated<?, ?> added = consumeItem(ItemCreated.class, M1);
        // assertFalse(added.isLoaded());
    }

    @Test
    public void putOverrideExisting() {
        init();
        c.put(M2.getKey(), M3.getValue());
        subscribe(CACHEENTRYEVENT_FILTER);
        put(M2);

        ItemUpdated<?, ?> updated = consumeItem(ItemUpdated.class, M2);
        assertEquals(M3.getValue(), updated.getPreviousValue());
        // assertFalse(updated.isLoaded());
        assertFalse(updated.hasExpired());
    }

    /**
     * Tests that ItemUpdated events are raised when changing the value of an entry in the
     * cache throgh the putAll method.
     *
     * @throws Exception
     *             test failed
     */
    @Test
    public void putAll() throws Exception {
        init();
        subscribe(CACHEENTRYEVENT_FILTER);

        putAll(M1, M2);

        Collection<ItemCreated> added = consumeItems(ItemCreated.class, M1, M2);
// for (ItemAdded i : added) {
// assertFalse(i.isLoaded());
// }

    }

    @Test
    public void putIfAbsent() throws Exception {
        init(1);
        subscribe(CACHEENTRYEVENT_FILTER);
        putIfAbsent(M1);
        putIfAbsent(M2);
        ItemCreated<?, ?> added = consumeItem(ItemCreated.class, M2);
        // assertFalse(added.isLoaded());
    }

    @Test
    public void isCacheables() {
        conf.eviction().setIsCacheableFilter(Predicates.FALSE);
        init();
        subscribe(CACHEENTRYEVENT_FILTER);
        put(M1);
        putAll(M1, M2, M3);
        assertSize(0);
    }

    /**
     * Tests that ItemUpdated events are raised when we try to change the current value to
     * a value that is equal or same as itself.
     *
     * @throws Exception
     *             test failed
     */
    @Test
    public void itemUpdatedEventForSameValuePut() throws Exception {
        init();
        subscribe(CACHEENTRYEVENT_FILTER);

        put(M1);
        ItemCreated<?, ?> added = consumeItem(ItemCreated.class, M1);
        // assertFalse(added.isLoaded());
        put(M1);
        ItemUpdated<?, ?> updated = consumeItem(ItemUpdated.class, M1);
        assertEquals(M1.getValue(), updated.getPreviousValue());
        // assertFalse(updated.isLoaded());
        assertFalse(updated.hasExpired());
    }
}
