/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheConfiguration.ExpirationStrategy;
import org.coconut.core.Clock;
import org.coconut.filter.Filter;

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

    public abstract long getDefaultExpirationNanoTime();

    public abstract long getDefaultRefreshNanoTime();

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

    public abstract boolean isExpired(CacheEntry<K, V> entry);

    public abstract boolean needsRefresh(CacheEntry<K, V> entry);

    public static <K, V> ExpirationSupport<K, V> newFinal(CacheConfiguration<K, V> conf) {
        return new FinalExpirationSupport<K, V>(conf);
    }

    public long getExpirationTimeFromLoaded(CacheEntry<K, V> entry) {
        long e = entry.getExpirationTime();
        return e == 0 ? getDefaultExpirationNanoTime() : e;
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

        public boolean isExpired(CacheEntry<K, V> entry) {
            if (expireFilter != null && expireFilter.accept(entry)) {
                return true;
            }
            long expTime = entry.getExpirationTime();
            return expTime == Cache.NEVER_EXPIRE ? false : clock.hasExpired(expTime);
        }

        public boolean needsRefresh(CacheEntry<K, V> entry) {
            // create test for never expire
            if (refreshExpirationTime < 0
                    || entry.getExpirationTime() == Cache.NEVER_EXPIRE) {
                return false;
            }
            if (refreshFilter != null && refreshFilter.accept(entry)) {
                return true;
            }
            long refTime = entry.getExpirationTime() - refreshExpirationTime;
            return clock.hasExpired(refTime);
        }

        public long getDefaultExpirationNanoTime() {
            return defaultExpirationTime;
        }

        public long getDefaultRefreshNanoTime() {
            return refreshExpirationTime;
        }
    }
}
