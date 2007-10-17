/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.M3;
import static org.coconut.test.CollectionUtils.M4;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.event.CacheEventFilters;
import org.coconut.cache.service.event.CacheEntryEvent.ItemAdded;
import org.coconut.cache.service.event.CacheEntryEvent.ItemUpdated;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@SuppressWarnings("unchecked")
public class EventServiceLoading extends AbstractEventTestBundle {

    IntegerToStringLoader loader;

    @Before
    public void setup() {
        CacheConfiguration conf = CacheConfiguration.create();
        conf.setClock(clock);
        conf.event().setEnabled(true);
        loader = new IntegerToStringLoader();
        conf.loading().setLoader(loader);
        c = newCache(conf);
    }



    @Test
    public void forceLoad() throws Exception {
        subscribe(CacheEventFilters.CACHEENTRYEVENT_FILTER);
        loading().forceLoad(1);
        consumeItem(ItemAdded.class, M1);
        loader.incBase();
        loading().forceLoad(1);
        consumeItem(ItemUpdated.class, M1.getKey(), M2.getValue());
        loading().forceLoadAll(Arrays.asList(1, 2));

        // the order might vary
        consumeItem(ItemUpdated.class, M1.getKey(), M2.getValue());
        consumeItem(ItemAdded.class, M2.getKey(), M3.getValue());

        loader.incBase();
        loading().forceLoadAll();
        consumeItem(ItemUpdated.class, M1.getKey(), M3.getValue());
        consumeItem(ItemUpdated.class, M2.getKey(), M4.getValue());
       
    }

    @Test
    public void load() throws Exception {
        subscribe(CacheEventFilters.CACHEENTRYEVENT_FILTER);
        loading().load(1);
        consumeItem(ItemAdded.class, M1);
        loader.incBase();
        loading().load(1);// does not trigger load
        loading().loadAll(Arrays.asList(1, 2));
        consumeItem(ItemAdded.class, M2.getKey(), M3.getValue());
        loader.incBase();
        loading().loadAll();
    }
    

    @Test
    public void loadRefresh() throws Exception {
        subscribe(CacheEventFilters.CACHEENTRYEVENT_FILTER);
        loading().setDefaultTimeToRefresh(1, TimeUnit.MILLISECONDS);
        loading().load(1);
        consumeItem(ItemAdded.class, M1);
        incTime(1);
        loader.incBase();
        loading().loadAll();
        consumeItem(ItemUpdated.class, M1.getKey(), M2.getValue());
    }

}
