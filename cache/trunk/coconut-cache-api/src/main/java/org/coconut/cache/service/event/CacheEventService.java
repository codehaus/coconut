/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.event;

import org.coconut.event.EventBus;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheEventService<K, V> {

    /**
     * Returns the {@link org.coconut.event.bus.EventBus} attached to this cache
     * (optional operation). The event bus can be used for getting notications
     * about various {@link CacheEvent events} that is being raised internally
     * in the cache.
     * 
     * @throws UnsupportedOperationException
     *             if the cache does not support notifications of events in the
     *             cache.
     * @see CacheEvent
     * @see CacheItemEvent
     */
    EventBus<CacheEvent<K, V>> getEventBus();
}
