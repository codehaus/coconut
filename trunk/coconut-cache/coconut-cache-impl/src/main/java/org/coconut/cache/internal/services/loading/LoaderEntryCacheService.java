/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.services.loading;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.AbstractCacheService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LoaderEntryCacheService<K, V> extends AbstractCacheService<K, V> {
    /**
     * @param conf
     */
    public LoaderEntryCacheService(CacheConfiguration<K, V> conf) {
        super(conf);
    }

    private final ConcurrentHashMap<K, Future<V>> loads = new ConcurrentHashMap<K, Future<V>>();

    void cancelAll(boolean interrupt) {
        for (Iterator<Map.Entry<K, Future<V>>> iter = loads.entrySet().iterator(); iter
                .hasNext();) {
            Map.Entry<K, Future<V>> element = iter.next();
            element.getValue().cancel(interrupt);
            iter.remove();
        }
    }
}
