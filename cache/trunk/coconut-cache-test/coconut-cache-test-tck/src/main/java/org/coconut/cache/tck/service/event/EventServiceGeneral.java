/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRY_ADDED_FILTER;
import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.event.bus.EventSubscription;
import org.junit.Test;

public class EventServiceGeneral extends AbstractEventTestBundle{

    @Test
    public void testNotEnabled() {
        CacheConfiguration<?, ?> conf = CacheConfiguration.create();
        c = newCache(conf);
        assertFalse(services().hasService(CacheEventService.class));
        assertFalse(services().getAllServices().containsKey(CacheEventService.class));
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
        assertTrue(services().hasService(CacheEventService.class));
        assertTrue(services().getAllServices().containsKey(CacheEventService.class));
        Object cs = services().getAllServices().get(CacheEventService.class);
        assertSame(cs, c.getService(CacheEventService.class));
    }
    @Test
    public void testUnsubscribe() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 0);
        EventSubscription<?> s = subscribe(CACHEENTRY_ADDED_FILTER);
        put(M1);
        assertEquals(1, getPendingEvents());
        s.unsubscribe();
        put(M2);
        assertEquals(1, getPendingEvents());
        consumeItem(); // take event
    }
}
