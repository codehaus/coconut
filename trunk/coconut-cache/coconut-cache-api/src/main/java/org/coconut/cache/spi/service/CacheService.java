/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.spi.AbstractCache;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheService<K, V> {
    /**
     * Starts the cache service.
     * 
     * @param cache
     * @param properties
     */
    void start(AbstractCache<K, V> cache, Map<String, Object> properties);

    void shutdown();

    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

    void addTo(ManagedGroup dg);
}
