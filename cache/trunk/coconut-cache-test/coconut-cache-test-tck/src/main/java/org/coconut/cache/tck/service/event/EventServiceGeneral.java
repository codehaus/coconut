package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRY_ACCESSED_FILTER;
import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.event.EventSubscription;
import org.junit.Test;

public class EventServiceGeneral extends AbstractEventTestBundle{

    @Test
    public void testNotEnabled() {
        CacheConfiguration<?, ?> conf = CacheConfiguration.create();
        c = newCache(conf);
        assertFalse(c.hasService(CacheEventService.class));
        assertFalse(c.getAllServices().containsKey(CacheEventService.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotEnabledCE() {
        CacheConfiguration<?, ?> conf = CacheConfiguration.create();
        c = newCache(conf);
        c.getService(CacheEventService.class);
    }

    @Test
    public void testEnabled() {
        CacheConfiguration<?, ?> conf = CacheConfiguration.create();
        conf.event().setEnabled(true);
        c = newCache(conf);
        assertTrue(c.hasService(CacheEventService.class));
        assertTrue(c.getAllServices().containsKey(CacheEventService.class));
        Object cs = c.getAllServices().get(CacheEventService.class);
        assertSame(cs, c.getService(CacheEventService.class));
    }
    @Test
    public void testUnsubscribe() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 0);
        EventSubscription<?> s = subscribe(CACHEENTRY_ACCESSED_FILTER);
        c.get(M1.getKey());
        assertEquals(1, getPendingEvents());
        s.unsubscribe();
        c.get(M2.getKey());
        assertEquals(1, getPendingEvents());
        consumeItem(); // take event
    }
}
