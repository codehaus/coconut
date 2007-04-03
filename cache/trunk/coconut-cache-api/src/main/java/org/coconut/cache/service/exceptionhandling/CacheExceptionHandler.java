/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import java.util.HashMap;
import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.core.AttributeMap;
import org.coconut.event.EventSubscription;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheExceptionHandler<K, V> {

    public Map<K, V> loadAllFailed(final CacheLoader<? super K, ?> loader,
            Map<? extends K, AttributeMap> keysWithAttributes, boolean isAsynchronous,
            Throwable cause) {
        return null;
    }

    public Map<K, V> loadAllFailed(Cache<K, V> cache, CacheLoader<? super K, ?> loader,
            Map<K, AttributeMap> keysWithAttributes, boolean isAsync, Throwable cause) {
        HashMap<K, V> map = new HashMap<K, V>();
        for (Map.Entry<K, AttributeMap> e : keysWithAttributes.entrySet()) {
            K key = e.getKey();
            AttributeMap aMap = e.getValue();
            map.put(key, loadFailed(cache, loader, key, aMap, isAsync, cause));
        }
        return map;
    }

    public V loadFailed(Cache<K, V> cache, CacheLoader<? super K, ?> loader, K key,
            AttributeMap map, boolean isAsync, Throwable cause) {
        return null;
    }

    public void eventDeliveryFailed(Cache<K, V> cache, CacheEvent<K, V> event,
            EventSubscription<CacheEvent<K, V>> destination, Throwable cause) {}

    public final void unhandledRuntimeException(RuntimeException t) {}

    public void configurationChanged(Cache<?, ?> cache, String title) {}

    public void warning(String warning) {}

}
