/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.exceptionhandling;

import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.servicemanager.CacheLifecycle;

/**
 * An exception service available as an internal service at runtime.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public interface InternalCacheExceptionService<K, V> {

    void fatal(String msg);

    void fatal(String msg, Throwable cause);

    void initializationFailed(CacheConfiguration<K, V> configuration, CacheLifecycle service,
            RuntimeException cause);

    void initialize(Cache<K, V> cache, CacheConfiguration<K, V> conf);

    void checkExceptions(boolean failIfShutdown);

    V loadFailed(Throwable cause, CacheLoader<? super K, ?> loader, K key, AttributeMap map);

    boolean startupFailed();

    void serviceManagerShutdownFailed(Throwable cause, CacheLifecycle lifecycle);

    void startFailed(Throwable cause, CacheConfiguration<K, V> configuration, Object service);

    void terminated(Map<? extends CacheLifecycle, RuntimeException> terminationFailures);

    void warning(String warning);
}
