/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.test.CollectionUtils.M1;

import java.util.Map;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.event.CacheEntryEvent.ItemAdded;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.predicate.Predicates;
import org.junit.Before;
import org.junit.Test;

public class EventCacheStarted extends AbstractEventTestBundle {
    CacheConfiguration conf;

    @Before
    public void setupConf() {
        conf = CacheConfiguration.create();
        conf.serviceManager().add(new Subscriber());
        conf.event().setEnabled(true).include(CacheEvent.class);
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
        conf.event().exclude(CacheEvent.CacheStarted.class);

        c = newCache(conf);
        put(M1);
        consumeItem(ItemAdded.class, M1);// no start event posted
    }

    public class Subscriber extends AbstractCacheLifecycle {

        @Override
        public void start(Map<Class<?>, Object> allServices) {
            CacheEventService ces = (CacheEventService) allServices.get(CacheEventService.class);
            assertNotNull(subscribe(ces, Predicates.TRUE));
        }

    }
}
