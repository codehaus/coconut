/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.integration;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRY_ADDED_FILTER;
import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;
import static org.coconut.test.CollectionUtils.M6;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.event.CacheEventConfiguration;
import org.coconut.cache.service.event.CacheEventFilters;
import org.coconut.cache.service.event.CacheEntryEvent.ItemAccessed;
import org.coconut.cache.service.event.CacheEntryEvent.ItemAdded;
import org.coconut.cache.tck.service.event.AbstractEventTestBundle;
import org.coconut.cache.tck.util.IntegerToStringLoader;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EventBusLoading extends AbstractEventTestBundle {

    @Before
    public void setup() {
        CacheConfiguration conf = CacheConfiguration.create();
        conf.addService(CacheEventConfiguration.class);
        conf.serviceLoading().setLoader(new IntegerToStringLoader());
        c = newCache(conf);
    }
    
    /**
     * This test checks that when requesting a non-existing entry with a cache
     * loader defined. First an accessed failed event will be posted then
     * followed by an itemadded event.
     */
    @Test
    public void itemAccessedThenAddedByLoading() throws Exception {
        subscribe(CacheEventFilters.CACHEENTRYEVENT_FILTER);
        assertEquals(M1.getValue(), get(M1));
        ItemAccessed<Integer, String> event = consumeItem(ItemAccessed.class, M1);
        assertFalse(event.isHit());
        consumeItem(ItemAdded.class, M1);

        getAll(M1, M3);
        event = consumeItem(ItemAccessed.class, M1);
        assertTrue(event.isHit());
        event = consumeItem(ItemAccessed.class, M3);
        assertFalse(event.isHit());
        consumeItem(ItemAdded.class, M3);
    }

    @Test
    public void itemAddedByLoaded() throws Exception {
        subscribe(CACHEENTRY_ADDED_FILTER);

        assertEquals(M1.getValue(), get(M1));
        consumeItem(ItemAdded.class, M1);

        getAll(M2, M3);
        consumeItem(ItemAdded.class, M2);
        consumeItem(ItemAdded.class, M3);

        getAll(M4, M6); // M6 does not cause loading
        consumeItem(ItemAdded.class, M4);
    }
}
