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
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEntryEvent;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEventFilters;
import org.coconut.cache.service.event.CacheEntryEvent.ItemCreated;
import org.coconut.cache.service.event.CacheEntryEvent.ItemUpdated;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.operations.Predicates;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
public class EventServiceLoading extends AbstractEventTestBundle {

    IntegerToStringLoader loader;

    @Before
    public void setupLoading() {
        loader = new IntegerToStringLoader();
        conf.loading().setLoader(loader);
        init();
    }

    @Test
    public void forceLoad() throws Exception {
        subscribe(CacheEventFilters.CACHEENTRYEVENT_FILTER);
        loading().forceLoad(1);
        awaitAllLoads();
        ItemCreated added = consumeItem(ItemCreated.class, M1);
        // assertTrue(added.isLoaded());
        loader.incBase();
        loading().forceLoad(1);
        awaitAllLoads();
        consumeItem(ItemUpdated.class, M1.getKey(), M2.getValue());
        loading().forceLoadAll(Arrays.asList(1, 2));
        awaitAllLoads();
        // The order may wary
        Collection<CacheEvent> ce = consumeItems(c, 2);
        assertTrue(ce.contains(itemAdded(c, M2.getKey(), M3.getValue())));
        assertTrue(ce.contains(itemUpdated(c, M1.getKey(), M2.getValue(), M2.getValue())));

        loader.incBase();
        loading().forceLoadAll();
        awaitAllLoads();
        ce = consumeItems(c, 2);
        assertTrue(ce.contains(itemUpdated(c, M1.getKey(), M3.getValue(), M2.getValue())));
        assertTrue(ce.contains(itemUpdated(c, M2.getKey(), M4.getValue(), M3.getValue())));
    }

    static Object itemAdded(final Cache c, final Integer key, final String value) {
        return new Object() {
            @Override
            public boolean equals(Object obj) {
                if (obj instanceof CacheEntryEvent.ItemCreated) {
                    CacheEntryEvent.ItemCreated event = (ItemCreated) obj;
                    return event.getCache().equals(c) && event.getKey().equals(key)
                            && event.getValue().equals(value);
                }
                return false;
            }
        };
    }

    static Object itemUpdated(final Cache c, final Integer key, final String value,
            final String previousValue) {
        return new Object() {
            @Override
            public boolean equals(Object obj) {
                if (obj instanceof CacheEntryEvent.ItemUpdated) {
                    CacheEntryEvent.ItemUpdated event = (ItemUpdated) obj;
                    return event.getCache().equals(c) && event.getKey().equals(key)
                            && event.getValue().equals(value)
                            && event.getPreviousValue().equals(previousValue);
                }
                return false;
            }
        };
    }

    @Test
    public void load() throws Exception {
        subscribe(CacheEventFilters.CACHEENTRYEVENT_FILTER);
        loading().load(1);
        awaitAllLoads();
        consumeItem(ItemCreated.class, M1);
        loader.incBase();
        loading().load(1);// does not trigger load
        awaitAllLoads();
        loading().loadAll(Arrays.asList(1, 2));
        awaitAllLoads();
        consumeItem(ItemCreated.class, M2.getKey(), M3.getValue());
        loader.incBase();
        loading().loadAll();
        awaitAllLoads();
    }

    @Test
    public void isCacheables() {
        conf.loading().setLoader(loader);
        conf.eviction().setIsCacheableFilter(Predicates.FALSE);
        c = newCache(conf);
        subscribe(CACHEENTRYEVENT_FILTER);
        loading().load(1);
        awaitAllLoads();
        assertSize(0);
    }

    @Test
    public void loadRefresh() throws Exception {
        subscribe(CacheEventFilters.CACHEENTRYEVENT_FILTER);
        loading().setDefaultTimeToRefresh(1, TimeUnit.MILLISECONDS);
        loading().load(1);
        awaitAllLoads();
        consumeItem(ItemCreated.class, M1);
        incTime(1);
        loader.incBase();
        loading().loadAll();
        awaitAllLoads();
        consumeItem(ItemUpdated.class, M1.getKey(), M2.getValue());
    }

}
