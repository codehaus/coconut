/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.test.CollectionTestUtil.M1;

import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.event.CacheEntryEvent.ItemAdded;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.operations.Predicates;
import org.junit.Before;
import org.junit.Test;

public class EventCacheStarted extends AbstractEventTestBundle {
    @Before
    public void setupEvent() {
        conf.serviceManager().add(new Subscriber());
    }

    @Test
    public void started() throws Exception {
        c = newCache(conf);
        put(M1);
        consumeItem(c, CacheEvent.CacheStarted.class);// start event is first
        consumeItem(ItemAdded.class, M1);
    }

    @Test
    public void startedDisabled() throws Exception {
        anythingBut(CacheEvent.CacheStarted.class);

        c = newCache(conf);
        put(M1);
        consumeItem(ItemAdded.class, M1);// no start event posted
    }

    public class Subscriber extends AbstractCacheLifecycle {

        @Override
        public void start(CacheServiceManagerService serviceManager) {
            CacheEventService ces = serviceManager.getService(CacheEventService.class);
            assertNotNull(subscribe(ces, Predicates.TRUE));
        }

    }
}
