/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.core.AttributeMap;
import org.coconut.event.EventSubscription;

/**
 * Currently this class only defines one standard exception.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheExceptionHandlers {

    public static <K, V> CacheExceptionHandler<K, V> defaultExceptionHandler() {
        return new DefaultCacheExceptionHandler<K, V>();
    }

    public static class DefaultCacheExceptionHandler<K, V> extends
            CacheExceptionHandler<K, V> {

        @Override
        public boolean eventDeliveryFailed(Cache<K, V> cache, CacheEvent<K, V> event,
                EventSubscription<CacheEvent<K, V>> destination, Throwable cause) {
            getLogger().error(
                    "Could not deliver event (destination " + destination + ", event ="
                            + event + ")", cause);
            return false;
        }

        @Override
        public V loadFailed(Cache<K, V> cache, CacheLoader<? super K, ?> loader, K key,
                AttributeMap map, boolean isGet, Throwable cause) {
            getLogger().error("Could not load value (key =" + key + ")", cause);
            return null;
        }

        @Override
        public void warning(String warning) {
            getLogger().warn(warning);
        }

        @Override
        public void unhandledRuntimeException(RuntimeException t) {
            getLogger().fatal("An unexpected failure occured inside the cache", t);
        }
    }
}
