/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.InternalCacheServiceManager;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class MutableVolatileExpirationSupport<K, V> extends ExpirationCacheService<K, V> {
    private volatile long defaultExpirationTime;

    private final Filter<CacheEntry<K, V>> expireFilter;

    private volatile long refreshExpirationTime;

    private final Filter<CacheEntry<K, V>> refreshFilter;

    public MutableVolatileExpirationSupport(InternalCacheServiceManager manager,
            CacheConfiguration<K, V> conf) {
        super(manager, conf);
        defaultExpirationTime = conf.serviceExpiration().getDefaultTimeout(TimeUnit.NANOSECONDS);
        refreshExpirationTime = conf.serviceExpiration()
                .getRefreshInterval(TimeUnit.NANOSECONDS);
        expireFilter = conf.serviceExpiration().getFilter();
        refreshFilter = conf.serviceExpiration().getRefreshFilter();
    }

    /**
     * @see org.coconut.cache.spi.ExpirationSupport#getExpirationFilter()
     */
    @Override
    public Filter<CacheEntry<K, V>> getExpirationFilter() {
        return expireFilter;
    }

    /**
     * @see org.coconut.cache.spi.ExpirationSupport#getRefreshFilter()
     */
    @Override
    public Filter<CacheEntry<K, V>> getRefreshFilter() {
        return refreshFilter;
    }

    public void setDefaultRefreshTime(long timeMs) {
        // we use validation rutine from CacheConfiguration
        // a bit slow, but this method is not called to often
        CacheConfiguration<K, V> conf = CacheConfiguration.create();
        conf.serviceExpiration().setRefreshInterval(timeMs, TimeUnit.MILLISECONDS);
        refreshExpirationTime = conf.serviceExpiration().getRefreshInterval(
                TimeUnit.MILLISECONDS);
    }

    public void setDefaultExpirationTime(long timeMs) {
        // we use validation rutine from CacheConfiguration
        // a bit slow, but this method is not called to often
        CacheConfiguration<K, V> conf = CacheConfiguration.create();
        conf.serviceExpiration().setDefaultTimeout(timeMs, TimeUnit.MILLISECONDS);
        refreshExpirationTime = conf.serviceExpiration()
                .getDefaultTimeout(TimeUnit.MILLISECONDS);
    }

    @Override
    protected long innerGetDefaultExpirationMsTime() {
        return defaultExpirationTime;
    }

    @Override
    protected long innerGetDefaultRefreshMsTime() {
        return refreshExpirationTime;
    }

}
