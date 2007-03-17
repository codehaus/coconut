/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service;

import java.util.Map;

import org.coconut.cache.spi.AbstractCache;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface InternalCacheService<K, V> {
    /**
     * Starts the cache service.
     * 
     * @param cache
     * @param properties
     */
    void start(AbstractCache<K, V> cache, Map<String, Object> properties) throws Exception;

    void shutdown(Runnable shutdownCallback) throws Exception;

    void addTo(ManagedGroup dg);
}
