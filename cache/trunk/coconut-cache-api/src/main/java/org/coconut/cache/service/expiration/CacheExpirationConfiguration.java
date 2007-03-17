/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.filter.Filter;
import org.coconut.internal.util.UnitOfTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheExpirationConfiguration<K, V> extends
        AbstractCacheServiceConfiguration<K, V> {

    private final static String DEFAULT_TIMEOUT_TAG = "default-timeout";

    private final static String EXPIRATION_FILTER_TAG = "filter";

    private final static String EXPIRATION_TAG = "expiration";

    private final static String REFRESH_FILTER_TAG = "refresh-filter";

    private final static String REFRESH_INTERVAL_TAG = "refresh-timer";

    private long defaultExpirationRefreshDuration = -1;

    private long defaultExpirationTimeoutDuration = CacheExpirationService.NEVER_EXPIRE;

    private Filter<CacheEntry<K, V>> expirationFilter;

    private Filter<CacheEntry<K, V>> expirationRefreshFilter;

    /**
     * @param tag
     * @param c
     */
    public CacheExpirationConfiguration() {
        super(EXPIRATION_TAG, CacheExpirationService.class);
    }

    public long getDefaultTimeout(TimeUnit unit) {
        if (defaultExpirationTimeoutDuration == CacheExpirationService.NEVER_EXPIRE) {
            // don't convert relative to time unit
            return defaultExpirationTimeoutDuration;
        } else {
            return unit.convert(defaultExpirationTimeoutDuration, TimeUnit.NANOSECONDS);
        }
    }

    public Filter<CacheEntry<K, V>> getFilter() {
        return expirationFilter;
    }

    public Filter<CacheEntry<K, V>> getRefreshFilter() {
        return expirationRefreshFilter;
    }

    /**
     * Returns the refresh interval in the specified timeunit.
     * 
     * @param unit
     *            the unit of time to return the refresh interval in
     * @return
     */
    public long getRefreshInterval(TimeUnit unit) {
        if (defaultExpirationRefreshDuration > 0) {
            return unit.convert(defaultExpirationRefreshDuration, TimeUnit.NANOSECONDS);
        } else {
            return defaultExpirationRefreshDuration;
        }
    }

    /**
     * Sets the default expiration time for elements added to the cache.
     * Elements added using the {@link Cache#put(Object, Object)} will expire at
     * <tt>time_of_insert + default_expiration_time</tt>. Expired elements
     * are handled accordingly to the {@link ExpirationStrategy} set for the
     * cache using {@link #setFilter(Filter)}. The default expiration is
     * infinite, that is elements never expires..
     * 
     * @param duration
     *            the default timeout for elements added to the cache, must be
     *            positive
     * @param unit
     *            the time unit of the duration argument
     */
    public CacheExpirationConfiguration setDefaultTimeout(long duration, TimeUnit unit) {
        if (duration <= 0) {
            throw new IllegalArgumentException("duration must be greather then 0, was "
                    + duration);
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        if (duration == CacheExpirationService.NEVER_EXPIRE) {
            defaultExpirationTimeoutDuration = CacheExpirationService.NEVER_EXPIRE;
            // don't convert relative to time unit
        } else {
            defaultExpirationTimeoutDuration = unit.toNanos(duration);
        }
        return this;
    }

    /**
     * Sets a specific expiration that can be used in <tt>addition</tt> to the
     * time based expiration filter to check if items has expired. If no filter
     * has been set items are expired according to their registered expiration
     * time. If an expiration filter is set cache entries are first checked
     * against that filter then against the time based expiration times.
     */
    public CacheExpirationConfiguration setFilter(Filter<CacheEntry<K, V>> filter) {
        expirationFilter = filter;
        return this;
    }

    public CacheExpirationConfiguration setRefreshFilter(Filter<CacheEntry<K, V>> filter) {
        expirationRefreshFilter = filter;
        return this;
    }

    /**
     * Sets the default refresh interval. Setting of the refresh window only
     * makes sense if an asynchronously loader has been specified. -1
     * 
     * @param interval
     *            the i
     * @param unit
     *            the unit of the interval
     * @return this Expiration
     */
    public CacheExpirationConfiguration setRefreshInterval(long interval, TimeUnit unit) {
        if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        if (interval <= 0) {
            defaultExpirationRefreshDuration = interval;
        } else {
            defaultExpirationRefreshDuration = unit.toNanos(interval);
        }
        return this;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#fromXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void fromXML(Document doc, Element parent) throws Exception {
        /* Expiration timeout */
        Element defaultTimeout = getChild(DEFAULT_TIMEOUT_TAG, parent);
        if (defaultTimeout != null) {
            long timeout = UnitOfTime.fromElement(defaultTimeout, TimeUnit.MILLISECONDS);
            setDefaultTimeout(timeout, TimeUnit.MILLISECONDS);
        }
        /* Expiration Filter */
        Element filter = getChild(EXPIRATION_FILTER_TAG, parent);
        if (filter != null) {
            Filter f = loadObject(filter, Filter.class);
            setFilter(f);
        }
        /* Refresh timer */
        Element refreshInterval = getChild(REFRESH_INTERVAL_TAG, parent);
        if (refreshInterval != null) {
            long timeout = UnitOfTime.fromElement(refreshInterval, TimeUnit.MILLISECONDS);
            setRefreshInterval(timeout, TimeUnit.MILLISECONDS);
        }

        /* Refresh Filter */
        Element refreshFilter = getChild(REFRESH_FILTER_TAG, parent);
        if (refreshFilter != null) {
            Filter f = loadObject(refreshFilter, Filter.class);
            setRefreshFilter(f);
        }
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#toXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void toXML(Document doc, Element base) throws Exception {

        /* Expiration Timeout */
        long timeout = getDefaultTimeout(TimeUnit.MILLISECONDS);
        if (timeout != CacheExpirationService.NEVER_EXPIRE) {
            UnitOfTime.toElementCompact(add(doc, DEFAULT_TIMEOUT_TAG, base), timeout,
                    TimeUnit.MILLISECONDS);
        }

        /* Expiration Filter */
        Filter filter = getFilter();
        if (filter != null) {
            super.saveObject(doc, add(doc, EXPIRATION_FILTER_TAG, base),
                    "expiration.cannotPersistFilter", filter);
        }

        /* Refresh Timer */
        long refresh = getRefreshInterval(TimeUnit.MILLISECONDS);
        if (refresh > 0) {
            UnitOfTime.toElementCompact(add(doc, REFRESH_INTERVAL_TAG, base),
                    refresh, TimeUnit.MILLISECONDS);
        }

        /* Refresh Filter */
        Filter refreshFilter = getRefreshFilter();
        if (refreshFilter != null) {
            super.saveObject(doc, add(doc, REFRESH_FILTER_TAG, base),
                    "expiration.cannotPersistRefreshFilter", refreshFilter);
        }
    }
}
