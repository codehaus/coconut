/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRYEVENT_FILTER;
import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M3;
import static org.coconut.test.CollectionTestUtil.M4;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.coconut.cache.service.event.CacheEntryEvent.ItemDeleted;
import org.junit.Test;

public class EventRemove extends AbstractEventTestBundle {

    @Test
    public void entrySetRemove() throws Exception {
        init(2);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.entrySet().remove(M1);
        ItemDeleted<?, ?> removed = consumeItem(ItemDeleted.class, M1);
        assertFalse(removed.hasExpired());
    }

    @Test
    public void entrySetRemoveAllEntry() throws Exception {
        init(4);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.entrySet().removeAll(Arrays.asList(M3, M4));
        Collection<ItemDeleted> removed = consumeItems(ItemDeleted.class, M3, M4);
        for (ItemDeleted i : removed) {
            assertFalse(i.hasExpired());
        }
    }

    @Test
    public void entrySetRemoveIterator() throws Exception {
        init(2);
        subscribe(CACHEENTRYEVENT_FILTER);
        Iterator<Map.Entry<Integer, String>> iter = c.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, String> i = iter.next();
            if (i.getKey() == 2) {
                iter.remove();
            }
        }
        ItemDeleted<?, ?> removed = consumeItem(ItemDeleted.class, M2);
        assertFalse(removed.hasExpired());
    }

    @Test
    public void entrySetRetainAllEntry() throws Exception {
        init(4);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.entrySet().retainAll(Arrays.asList(M3, M4));
        Collection<ItemDeleted> removed = consumeItems(ItemDeleted.class, M1, M2);
        for (ItemDeleted i : removed) {
            assertFalse(i.hasExpired());
        }
    }

    @Test
    public void keySetRemove() throws Exception {
        init(2);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.keySet().remove(M1.getKey());
        ItemDeleted<?, ?> removed = consumeItem(ItemDeleted.class, M1);
        assertFalse(removed.hasExpired());
    }

    @Test
    public void keySetRemoveAllEntry() throws Exception {
        init(4);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.keySet().removeAll(Arrays.asList(M3.getKey(), M4.getKey()));
        Collection<ItemDeleted> removed = consumeItems(ItemDeleted.class, M3, M4);
        for (ItemDeleted i : removed) {
            assertFalse(i.hasExpired());
        }
    }

    @Test
    public void keySetRemoveIterator() throws Exception {
        init(2);
        subscribe(CACHEENTRYEVENT_FILTER);
        Iterator<Integer> iter = c.keySet().iterator();
        while (iter.hasNext()) {
            int i = iter.next();
            if (i == 2) {
                iter.remove();
            }
        }
        ItemDeleted<?, ?> removed = consumeItem(ItemDeleted.class, M2);
        assertFalse(removed.hasExpired());
    }

    @Test
    public void keySetRetainAllEntry() throws Exception {
        init(4);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.keySet().retainAll(Arrays.asList(M3.getKey(), M4.getKey()));
        Collection<ItemDeleted> removed = consumeItems(ItemDeleted.class, M1, M2);
        for (ItemDeleted i : removed) {
            assertFalse(i.hasExpired());
        }
    }

    @Test
    public void removeAllEntry() throws Exception {
        init(4);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.removeAll(Arrays.asList(M3.getKey(), M4.getKey()));

        Collection<ItemDeleted> removed = consumeItems(ItemDeleted.class, M3, M4);
        for (ItemDeleted i : removed) {
            assertFalse(i.hasExpired());
        }
    }

    @Test
    public void removeEntry() throws Exception {
        init(2);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.remove(M1.getKey());

        ItemDeleted<?, ?> removed = consumeItem(ItemDeleted.class, M1);
        assertFalse(removed.hasExpired());
    }

    @Test
    public void removeKeyValueEntry() throws Exception {
        init(2);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.remove(M2.getKey(), M2.getValue());

        ItemDeleted<?, ?> removed = consumeItem(ItemDeleted.class, M2);
        assertFalse(removed.hasExpired());
    }

    @Test
    public void removeNonExisting() throws Exception {
        init(2);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.remove(M3.getKey());
        c.remove(M3.getKey(), M3.getValue());
        c.removeAll(Arrays.asList(M3.getKey(), M4.getKey()));
    }

    @Test
    public void valueSetRemove() throws Exception {
        init(2);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.values().remove(M1.getValue());
        ItemDeleted<?, ?> removed = consumeItem(ItemDeleted.class, M1);
        assertFalse(removed.hasExpired());
    }

    @Test
    public void valueSetRemoveAllEntry() throws Exception {
        init(4);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.values().removeAll(Arrays.asList(M3.getValue(), M4.getValue()));
        Collection<ItemDeleted> removed = consumeItems(ItemDeleted.class, M3, M4);
        for (ItemDeleted i : removed) {
            assertFalse(i.hasExpired());
        }
    }

    @Test
    public void valueSetRemoveIterator() throws Exception {
        init(2);
        subscribe(CACHEENTRYEVENT_FILTER);
        Iterator<String> iter = c.values().iterator();
        while (iter.hasNext()) {
            String i = iter.next();
            if (i.equals(M2.getValue())) {
                iter.remove();
            }
        }
        ItemDeleted<?, ?> removed = consumeItem(ItemDeleted.class, M2);
        assertFalse(removed.hasExpired());
    }

    @Test
    public void valueSetRetainAllEntry() throws Exception {
        init(4);
        subscribe(CACHEENTRYEVENT_FILTER);
        c.values().retainAll(Arrays.asList(M3.getValue(), M4.getValue()));
        Collection<ItemDeleted> removed = consumeItems(ItemDeleted.class, M1, M2);
        for (ItemDeleted i : removed) {
            assertFalse(i.hasExpired());
        }
    }
}
