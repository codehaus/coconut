/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import static org.coconut.cache.service.event.CacheEventFilters.CACHEENTRYEVENT_FILTER;

import org.coconut.cache.service.event.CacheEntryEvent.ItemRemoved;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EventTrimmer extends AbstractEventTestBundle {

    @Test
    public void entrySetRemove() throws Exception {
        c = newCache(INCLUDE_ALL_CONFIGURATION, 2);
        subscribe(CACHEENTRYEVENT_FILTER);
        assertSize(2);
        eviction().trimToSize(1);
        assertSize(1);
        ItemRemoved<?, ?> removed = consumeItem(c, ItemRemoved.class);
        assertTrue(removed.getKey().equals(1) || removed.getKey().equals(2));
        assertFalse(removed.hasExpired());
    }
}
