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
        public void handleError(CacheExceptionContext<K, V> context, Error cause) {
            context.defaultLogger().fatal("An unexpected failure occured inside the cache", cause);
            throw cause;
        }

        @Override
        public void handleException(CacheExceptionContext<K, V> context, Exception cause) {}

        @Override
        public void handleRuntimeException(CacheExceptionContext<K, V> context,
                RuntimeException cause) {
            context.defaultLogger().fatal("An unexpected failure occured inside the cache", cause);
        }

        @Override
        public void warning(CacheExceptionContext<K, V> context, String warning) {
            context.defaultLogger().warn(warning);
        }

        @Override
        public boolean eventDeliveryFailed(CacheExceptionContext<K, V> context,
                CacheEvent<K, V> event, EventSubscription<CacheEvent<K, V>> destination,
                RuntimeException cause) {
            context.defaultLogger().error(
                    "Could not deliver event (destination " + destination + ", event ="
                            + event + ")", cause);
            return false;
        }

        @Override
        public V loadFailed(CacheExceptionContext<K, V> context,
                CacheLoader<? super K, ?> loader, K key, AttributeMap map, boolean isGet,
                Exception cause) {
            context.defaultLogger().error("Could not load value (key =" + key + ")",
                    cause);
            return null;
        }
    }
}
