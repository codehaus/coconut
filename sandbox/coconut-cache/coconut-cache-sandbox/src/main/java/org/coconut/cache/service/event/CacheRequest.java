/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.event;

import org.coconut.cache.Cache;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheRequest<K, V> {

    /**
     * Returns the cache from where this event should be processed, or
     * <tt>null</tt> if the target is....
     * 
     * @return the cache from where this event originated or <code>null</code>
     *         if the cache is not available (for example, if the event is
     *         handled in another JVM than it was raised in)
     */
    Cache<K, V> getCache();

    /**
     * Returns a unique name that can be used to identify the
     * <tt>type<tt> of the event. This is usual a display friendly name.
     * 
     * @return a unique name that can be used to identify the type of the event
     */
    String getName();

    public interface ClearCache {

    }

    public interface EvictCache {

    }

    public interface ShutdownCache {

    }
}
