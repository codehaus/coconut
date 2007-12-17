/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.core.Logger;

/**
 * This class should define a number of standard {@link CacheExceptionHandler}s. However,
 * currently is only defines one {@link DefaultLoggingExceptionHandler}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class CacheExceptionHandlers {

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private CacheExceptionHandlers() {}

    // /CLOVER:ON

    /**
     * Creates a new instance of {@link DefaultLoggingExceptionHandler}.
     * 
     * @return a new instance of DefaultLoggingExceptionHandler
     * @param <K>
     *            the type of keys maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> DefaultLoggingExceptionHandler<K, V> defaultLoggingExceptionHandler() {
        return new DefaultLoggingExceptionHandler<K, V>();
    }

    /**
     * An implementation of {@link CacheExceptionHandler} that logs all exceptions to the
     * logger defined accordingly to
     * {@link CacheExceptionHandlingConfiguration#setExceptionLogger(org.coconut.core.Logger)}.
     */
    public static class DefaultLoggingExceptionHandler<K, V> extends CacheExceptionHandler<K, V> {

        /** {@inheritDoc} */
        public void serviceManagerInitializationFailed(Logger logger,
                CacheConfiguration<K, V> configuration, String cacheName,
                Class<? extends Cache> cacheType, CacheLifecycle service, RuntimeException cause) {
        }

//        /** {@inheritDoc} */
//        public void handleError(CacheExceptionContext<K, V> context, Error cause) {
//            context.defaultLogger().fatal("An unexpected error occured inside the cache", cause);
//            throw cause;
//        }
//
//        /** {@inheritDoc} */
//        public void handleException(CacheExceptionContext<K, V> context, Exception cause) {
//            context.defaultLogger().error("An exception occured inside the cache", cause);
//        }
//
//        /** {@inheritDoc} */
//        public void handleRuntimeException(CacheExceptionContext<K, V> context,
//                RuntimeException cause) {
//            context.defaultLogger().fatal("An unexpected failure occured inside the cache", cause);
//        }

        /** {@inheritDoc} */
        @Override
        public void handleWarning(CacheExceptionContext<K, V> context) {
            context.defaultLogger().warn(context.getMessage());
        }

        /** {@inheritDoc} */
        @Override
        public V loadingLoadValueFailed(CacheExceptionContext<K, V> context,
                CacheLoader<? super K, ?> loader, K key, AttributeMap map) {
            context.defaultLogger().error(context.getMessage(), context.getCause());
            return super.loadingLoadValueFailed(context, loader, key, map);
        }
    }
}
