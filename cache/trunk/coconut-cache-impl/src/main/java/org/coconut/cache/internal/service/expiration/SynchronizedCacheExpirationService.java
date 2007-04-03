/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.attribute.InternalCacheAttributeService;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.core.Clock;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SynchronizedCacheExpirationService<K, V> extends
        DefaultCacheExpirationService<K, V> {

    private final Object mutex;

    /**
     * @param cache
     * @param conf
     * @param clock
     * @param errorHandler
     */
    public SynchronizedCacheExpirationService(CacheHelper<K, V> helper,
            CacheExpirationConfiguration<K, V> conf, Clock clock,
            CacheExceptionHandler<K, V> errorHandler,
            InternalCacheAttributeService attributeFactory) {
        super(helper, conf, clock, errorHandler, attributeFactory);
        mutex = helper.getMutex();
    }

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

    /**
     * @see org.coconut.cache.internal.service.expiration.AbstractCacheExpirationService#getFilter()
     */
    @Override
    public String getFilterAsString() {
        //this is needed because we call f.toString();
        synchronized (mutex) {
            return super.getFilterAsString();
        }
    }

}
