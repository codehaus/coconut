/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.AbstractCacheService;
import org.coconut.cache.internal.service.InternalCacheServiceManager;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.core.Clock;
import org.coconut.filter.Filter;
import org.coconut.internal.util.tabular.TabularFormatter;
import org.coconut.management.ManagedGroup;
import org.coconut.management.annotation.ManagedAttribute;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class ExpirationCacheService<K, V> extends AbstractCacheService<K, V>
        implements CacheExpirationService<K, V> {
    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#expire(org.coconut.cache.CacheEntry)
     */
    public boolean expire(CacheEntry<?, ?> entry) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#expireKey(java.lang.Object)
     */
    public boolean expireKey(Object key) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#put(java.lang.Object,
     *      java.lang.Object, long, java.util.concurrent.TimeUnit)
     */
    public V put(K key, V value, long timeout, TimeUnit unit) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#putAll(java.util.Map,
     *      long, java.util.concurrent.TimeUnit)
     */
    public void putAll(Map<? extends K, ? extends V> t, long timeout, TimeUnit unit) {
        // TODO Auto-generated method stub

    }

    private final Clock clock;

    public ExpirationCacheService(InternalCacheServiceManager manager,
            CacheConfiguration<K, V> conf) {
        super(manager, conf);
        clock = conf.getClock();
    }

    /**
     * @see org.coconut.apm.Apm#configureJMX(org.coconut.apm.spi.JMXConfigurator)
     */
    public void addTo(ManagedGroup dg) {
        ManagedGroup m = dg.addNewGroup("Expiration",
                "Management of Expiration settings for the cache");
        m.add(this);
        Filter f = getExpirationFilter();
        if (f != null) {
            m.add(f);
        }
        f = getRefreshFilter();
        if (f != null) {
            m.add(f);
        }
    }

    public boolean evictRemove(Cache<K, V> cache, CacheEntry<K, V> entry) {
        if (isExpired(entry)) {
            if (getDefaultRefreshTime() >= 0) {
                // TODO What if cache doesn't support async load
                if (loadingService != null) {
                    loadingService.load(entry.getKey());
                }
                return true;
            } else {
                return true;
            }
        } else if (needsRefresh(entry) && loadingService != null) {
            loadingService.load(entry.getKey());
        }
        return false;
    }

    // public long getDeadline(long timeout, TimeUnit unit) {
    // if (timeout == Cache.NEVER_EXPIRE) {
    // return Long.MAX_VALUE;
    // } else if (timeout == Cache.DEFAULT_EXPIRATION) {
    // long d = getDefaultExpirationTime();
    // if (d == Cache.NEVER_EXPIRE) {
    // return Cache.NEVER_EXPIRE;
    // } else {
    // return clock.getDeadlineFromNow(d, TimeUnit.MILLISECONDS);
    // }
    // } else {
    // return clock.getDeadlineFromNow(timeout, unit);
    // }
    // }

    @ManagedAttribute(defaultValue = "Default Expiration Description", description = "The default expiration time of the cache")
    public String getDefaultExpirationDescription() {
        long d = getDefaultExpirationTime();
        if (d == CacheExpirationService.NEVER_EXPIRE) {
            return "Never expire";
        }
        return TabularFormatter.formatTime2(d, TimeUnit.MILLISECONDS);
    }

    @ManagedAttribute(defaultValue = "Default Expiration", description = "The default expiration time of cache entries in milliseconds")
    public long getDefaultExpirationTime() {
        return innerGetDefaultExpirationMsTime();
    }

    @ManagedAttribute(defaultValue = "Default Refresh-Time Description", description = "The default refresh ahead for the cache")
    public String getDefaultRefreshAheadDescription() {
        long d = getDefaultRefreshTime();
        if (d < 0) {
            return "Never refresh ahead";
        } else if (d == 0) {
            return "Never refresh ahead, but load asynchronusly on evict()";
        } else {
            return TabularFormatter.formatTime2(d, TimeUnit.MILLISECONDS);
        }
    }

    @ManagedAttribute(defaultValue = "Default Refresh-Time", description = "The default expiration time of cache entries in milliseconds")
    public long getDefaultRefreshTime() {
        return innerGetDefaultRefreshMsTime();
    }

    public abstract Filter<CacheEntry<K, V>> getExpirationFilter();

    public long getExpirationTimeFromLoaded(CacheEntry<K, V> entry) {
        long e = entry.getExpirationTime();
        return e == 0 ? getDefaultExpirationTime() : e;
    }

    public abstract Filter<CacheEntry<K, V>> getRefreshFilter();

    public boolean isExpired(CacheEntry<K, V> entry) {
        Filter<CacheEntry<K, V>> filter = getExpirationFilter();
        if (filter != null && filter.accept(entry)) {
            return true;
        }
        long expTime = entry.getExpirationTime();
        return expTime == CacheExpirationService.NEVER_EXPIRE ? false : clock
                .isPassed(expTime);
    }

    public boolean needsRefresh(CacheEntry<K, V> entry) {
        // create test for never expire
        long refreshAheadTime = getDefaultRefreshTime();
        if (refreshAheadTime < 0
                || entry.getExpirationTime() == CacheExpirationService.NEVER_EXPIRE) {
            return false;
        }
        Filter<CacheEntry<K, V>> filter = getRefreshFilter();
        if (filter != null && filter.accept(entry)) {
            return true;
        }
        long refTime = entry.getExpirationTime() - refreshAheadTime;
        return clock.isPassed(refTime);
    }

    protected abstract long innerGetDefaultExpirationMsTime();

    protected abstract long innerGetDefaultRefreshMsTime();

    private volatile CacheLoadingService<K, V> loadingService;

    /**
     * @see org.coconut.cache.internal.service.AbstractCacheService#doStart(org.coconut.cache.spi.AbstractCache,
     *      java.util.Map)
     */
    @Override
    protected void doStart(AbstractCache<K, V> cache, Map<String, Object> properties)
            throws Exception {
        loadingService = cache.getService(CacheLoadingService.class);
        super.doStart(cache, properties);
    }

}
