/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import net.jcip.annotations.NotThreadSafe;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheParallelService;
import org.coconut.cache.internal.CacheInternals;
import org.coconut.cache.internal.InternalCacheFactory;
import org.coconut.cache.internal.UnsynchronizedInternalCache;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.memorystore.MemoryStoreService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.spi.CacheServiceSupport;

/**
 * An unsynchronized cache implementation.
 * <p>
 * If multiple threads access this cache concurrently, and at least one of the threads
 * modifies the cache structurally, it <i>must</i> be synchronized externally. (A
 * structural modification is any operation that adds, deletes or changes one or more
 * mappings.) This is typically accomplished by synchronizing on some object that
 * naturally encapsulates the cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 */
@NotThreadSafe
@CacheServiceSupport( { CacheEventService.class, MemoryStoreService.class,
        CacheExpirationService.class, CacheLoadingService.class, CacheParallelService.class,
        CacheServiceManagerService.class, CacheStatisticsService.class })
public class UnsynchronizedCache<K, V> extends AbstractCache<K, V> {
    /** The default factory. */
    private final static InternalCacheFactory F = CacheInternals.DEFAULT_UNSYNCHRONIZED_CACHE;

    /** Creates a new UnsynchronizedCache with a default configuration. */
    public UnsynchronizedCache() {
        super(F);
    }

    /**
     * Creates a new UnsynchronizedCache from the specified configuration.
     * 
     * @param conf
     *            the configuration to create the cache from
     * @throws NullPointerException
     *             if the specified configuration is <code>null</code>
     */
    public UnsynchronizedCache(CacheConfiguration<K, V> conf) {
        super(F, conf);
    }

    @Override
    public void prestart() {
        ((UnsynchronizedInternalCache) cache).prestart();
    }
}
