package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRYEVENT_FILTER;
import static org.coconut.test.CollectionUtils.M1;

import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved;
import org.coconut.test.MockTestCase;
import org.junit.Test;

public class EventServiceBus extends AbstractEventTestBundle {

    @Test
    public void entrySetRemove() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        subscribe(CACHEENTRYEVENT_FILTER);
        // event().unsubscribeAll()
        c.entrySet().remove(M1);
        ItemRemoved<?, ?> removed = consumeItem(ItemRemoved.class, M1);
        assertFalse(removed.hasExpired());
    }

    @Test
    public void unsubscribeShutdown() {
        c = newCache(newConf().event().setEnabled(true));
        prestart();
        c.shutdown();
        event().unsubscribeAll();// should not fail
    }

    @Test
    public void getSubscribersShutdown() {
        c = newCache(newConf().event().setEnabled(true));
        prestart();
        c.shutdown();
        event().getSubscribers();
    }

    @Test(expected = IllegalStateException.class)
    public void offer() {
        c = newCache(newConf().event().setEnabled(true));
        prestart();
        c.shutdown();
        event().offer(MockTestCase.mockDummy(CacheEvent.class));
    }

}
