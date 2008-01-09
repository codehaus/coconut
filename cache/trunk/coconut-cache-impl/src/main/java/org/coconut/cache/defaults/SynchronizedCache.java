/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.CacheInternals;
import org.coconut.cache.internal.InternalCacheFactory;
import org.coconut.cache.internal.SynchronizedInternalCache;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.service.memorystore.MemoryStoreService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.cache.service.statistics.CacheStatisticsService;
import org.coconut.cache.service.worker.CacheWorkerService;
import org.coconut.cache.spi.CacheServiceSupport;

/**
 * An synchronized cache implementation.
 * <p>
 * It is imperative that the user manually synchronize on the cache when iterating over
 * any of its collection views:
 * 
 * <pre>
 *  Cache c = new SynchronizedCache();
 *      ...
 *  Set s = c.keySet();  // Needn't be in synchronized block
 *      ...
 *  synchronized(c) {  // Synchronizing on c, not s!
 *      Iterator i = s.iterator(); // Must be in synchronized block
 *      while (i.hasNext())
 *          foo(i.next());
 *  }
 * </pre>
 * 
 * Failure to follow this advice may result in non-deterministic behavior.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by this cache
 * @param <V>
 *            the type of mapped values
 */
@ThreadSafe
@CacheServiceSupport( { CacheEventService.class, MemoryStoreService.class,
        CacheExpirationService.class, CacheLoadingService.class, CacheManagementService.class,
        CacheServiceManagerService.class, CacheStatisticsService.class, CacheWorkerService.class })
public class SynchronizedCache<K, V> extends AbstractCache<K, V> {
    /** The default factory. */
    private final static InternalCacheFactory FACTORY = CacheInternals.DEFAULT_SYNCHRONIZED_CACHE;

    /**
     * Creates a new UnsynchronizedCache with a default configuration.
     */
    public SynchronizedCache() {
        super(FACTORY);
    }

    /**
     * Creates a new UnsynchronizedCache from the specified configuration.
     * 
     * @param conf
     *            the configuration to create the cache from
     * @throws NullPointerException
     *             if the specified configuration is <code>null</code>
     */
    public SynchronizedCache(CacheConfiguration<K, V> conf) {
        super(FACTORY, conf);
    }

    @Override
    public void prestart() {
        ((SynchronizedInternalCache) cache).prestart();
    }

    @Override
    public synchronized String toString() {
        return super.toString();
    }
}
