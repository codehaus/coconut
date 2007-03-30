/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheErrorHandler;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.core.Clock;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SynchronizedCacheExpirationService<K, V> extends
        DefaultCacheExpirationService<K, V> {

    /**
     * @param cache
     * @param conf
     * @param clock
     * @param errorHandler
     */
    public SynchronizedCacheExpirationService(Cache<K, V> cache,
            CacheExpirationConfiguration<K, V> conf, Clock clock,
            CacheErrorHandler<K, V> errorHandler) {
        super(cache, conf, clock, errorHandler);
        mutex = cache;
    }

    private final Object mutex;

    /**
     * @see org.coconut.cache.internal.service.expiration.DefaultCacheExpirationService#getDefaultTimeout(java.util.concurrent.TimeUnit)
     */
    @Override
    public long getDefaultTimeToLive(TimeUnit unit) {
        synchronized (mutex) {
            return super.getDefaultTimeToLive(unit);
        }
    }

    /**
     * @see org.coconut.cache.internal.service.expiration.DefaultCacheExpirationService#getExpirationFilter()
     */
    @Override
    public Filter<CacheEntry<K, V>> getExpirationFilter() {
        synchronized (mutex) {
            return super.getExpirationFilter();
        }
    }

    /**
     * @see org.coconut.cache.internal.service.expiration.DefaultCacheExpirationService#setDefaultTimeout(long,
     *      java.util.concurrent.TimeUnit)
     */
    @Override
    public void setDefaultTimeToLive(long duration, TimeUnit unit) {
        synchronized (mutex) {
            super.setDefaultTimeToLive(duration, unit);
        }
    }

    /**
     * @see org.coconut.cache.internal.service.expiration.DefaultCacheExpirationService#setExpirationFilter(org.coconut.filter.Filter)
     */
    @Override
    public void setExpirationFilter(Filter<CacheEntry<K, V>> filter) {
        synchronized (mutex) {
            super.setExpirationFilter(filter);
        }
    }

}
