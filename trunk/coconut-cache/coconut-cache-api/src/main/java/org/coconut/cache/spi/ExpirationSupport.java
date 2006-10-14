/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.concurrent.TimeUnit;

import org.coconut.apm.ApmGroup;
import org.coconut.apm.spi.annotation.ManagedAttribute;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheConfiguration.ExpirationStrategy;
import org.coconut.core.Clock;
import org.coconut.filter.Filter;
import org.coconut.internal.util.tabular.TabularFormatter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class ExpirationSupport<K, V> {
    final Clock clock;

    private final ExpirationStrategy expirationStrategy;

    ExpirationSupport(CacheConfiguration<K, V> conf) {
        clock = conf.getClock();
        expirationStrategy = conf.expiration().getStrategy();
    }

    public ExpirationStrategy getExpirationStrategy() {
        return expirationStrategy;
    }

    @ManagedAttribute(defaultValue = "Default Expiration Nano", description = "The default expiration time of cache entries in nanoseconds")
    public long getDefaultExpirationNanoTime() {
        return innerGetDefaultExpirationNanoTime();
    }

    @ManagedAttribute(defaultValue = "Default Expiration", description = "The default expiration time of the cache")
    public String getDefaultExpiration() {
        long d = getDefaultExpirationNanoTime();
        if (d == Cache.NEVER_EXPIRE) {
            return "Never expire";
        }
        return TabularFormatter.formatTime2(d);
    }

    @ManagedAttribute(defaultValue = "Default Refresh-Ahead", description = "The default refresh ahead for the cache")
    public String getDefaultRefreshAhead() {
        long d = getDefaultRefreshNanoTime();
        if (d < 0) {
            return "Never refresh ahead";
        } else if (d == 0) {
            return "Never refresh ahead, but load asynchronusly on evict()";
        } else {
            return TabularFormatter.formatTime2(d);
        }
    }

    abstract long innerGetDefaultExpirationNanoTime();

    @ManagedAttribute(defaultValue = "Default Refresh Time", description = "The default expiration time of cache entries in nanoseconds")
    public long getDefaultRefreshNanoTime() {
        return innerGetDefaultRefreshNanoTime();
    }

    abstract long innerGetDefaultRefreshNanoTime();

    public boolean evictRemove(Cache<K, V> cache, CacheEntry<K, V> entry) {
        if (isExpired(entry)) {
            if (getDefaultRefreshNanoTime() >= 0) {
                cache.loadAsync(entry.getKey());
            } else {
                return true;
            }
        } else if (needsRefresh(entry)) {
            cache.loadAsync(entry.getKey());
        }
        return false;
    }

    public boolean doStrictAndLoad(Cache<K, V> cache, CacheEntry<K, V> entry) {
        boolean strictLoading = false;
        if (getExpirationStrategy() == ExpirationStrategy.LAZY) {
            if (needsRefresh(entry) || isExpired(entry)) {
                cache.loadAsync(entry.getKey());
            }
        } else if (getExpirationStrategy() == ExpirationStrategy.STRICT) {
            strictLoading = isExpired(entry);
            if (!strictLoading && needsRefresh(entry)) {
                cache.loadAsync(entry.getKey());
            }
        }
        return strictLoading;
    }

    public long getDeadline(long timeout, TimeUnit unit) {
        if (timeout == Cache.NEVER_EXPIRE) {
            return Long.MAX_VALUE;
        } else if (timeout == Cache.DEFAULT_EXPIRATION) {
            return clock.getDeadlineFromNow(getDefaultExpirationNanoTime(),
                    TimeUnit.NANOSECONDS);
        } else {
            return clock.getDeadlineFromNow(timeout, unit);
        }
    }

    public abstract Filter<CacheEntry<K, V>> getExpirationFilter();

    public abstract Filter<CacheEntry<K, V>> getRefreshFilter();

    public static <K, V> ExpirationSupport<K, V> newFinal(CacheConfiguration<K, V> conf) {
        return new FinalExpirationSupport<K, V>(conf);
    }

    public long getExpirationTimeFromLoaded(CacheEntry<K, V> entry) {
        long e = entry.getExpirationTime();
        return e == 0 ? getDefaultExpirationNanoTime() : e;
    }

    /**
     * @see org.coconut.apm.Apm#configureJMX(org.coconut.apm.spi.JMXConfigurator)
     */
    public void addTo(ApmGroup dg) {
        ApmGroup m = dg.addGroup("Expiration").add(this);
        Filter f = getExpirationFilter();
        m.setDescription("Management of Expiration settings for the cache");
        if (f != null) {
            m.add(f);
        }
        if (f != null) {
            m.add(f);
        }
    }

    public boolean isExpired(CacheEntry<K, V> entry) {
        Filter<CacheEntry<K, V>> filter = getExpirationFilter();
        if (filter != null && filter.accept(entry)) {
            return true;
        }
        long expTime = entry.getExpirationTime();
        return expTime == Cache.NEVER_EXPIRE ? false : clock.hasExpired(expTime);
    }

    public boolean needsRefresh(CacheEntry<K, V> entry) {
        // create test for never expire
        long refreshExpirationTime = getDefaultRefreshNanoTime();
        if (refreshExpirationTime < 0 || entry.getExpirationTime() == Cache.NEVER_EXPIRE) {
            return false;
        }
        Filter<CacheEntry<K, V>> filter = getRefreshFilter();
        if (filter != null && filter.accept(entry)) {
            return true;
        }
        long refTime = entry.getExpirationTime() - refreshExpirationTime;
        return clock.hasExpired(refTime);
    }

    public static final class FinalExpirationSupport<K, V> extends
            ExpirationSupport<K, V> {
        private final Filter<CacheEntry<K, V>> expireFilter;

        private final Filter<CacheEntry<K, V>> refreshFilter;

        private final long defaultExpirationTime;

        private final long refreshExpirationTime;

        FinalExpirationSupport(CacheConfiguration<K, V> conf) {
            super(conf);
            defaultExpirationTime = conf.expiration().getDefaultTimeout(
                    TimeUnit.NANOSECONDS);
            refreshExpirationTime = conf.expiration().getRefreshWindow(
                    TimeUnit.NANOSECONDS);
            expireFilter = conf.expiration().getFilter();
            refreshFilter = conf.expiration().getPreExpirationFilter();
        }

        long innerGetDefaultExpirationNanoTime() {
            return defaultExpirationTime;
        }

        long innerGetDefaultRefreshNanoTime() {
            return refreshExpirationTime;
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

    }
}
