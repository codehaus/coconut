/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.expiration;

import static org.coconut.internal.util.XmlUtil.addAndsaveObject;
import static org.coconut.internal.util.XmlUtil.getChild;
import static org.coconut.internal.util.XmlUtil.loadOptional;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.filter.Filter;
import org.coconut.internal.util.UnitOfTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to configure the expiration service prior to usage.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheExpirationConfiguration<K, V> extends
        AbstractCacheServiceConfiguration<K, V> {

    /** The name of this service. */
    public final static String SERVICE_NAME = "Expiration";

    private final static String DEFAULT_TIMEOUT_TAG = "default-timetolive";

    private final static String EXPIRATION_FILTER_TAG = "filter";

    private final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.NANOSECONDS;

    /** The default settings, used when xml-serializing this configuration */
    private final static CacheExpirationConfiguration<?, ?> DEFAULT = new CacheExpirationConfiguration<Object, Object>();

    private long defaultTimeToLive;

    private Filter<CacheEntry<K, V>> expirationFilter;

    /**
     * Creates a new CacheExpirationConfiguration
     */
    @SuppressWarnings("unchecked")
    public CacheExpirationConfiguration() {
        super(SERVICE_NAME);
    }

    public long getDefaultTimeToLive(TimeUnit unit) {
        if (defaultTimeToLive == CacheExpirationService.NEVER_EXPIRE) {
            return CacheExpirationService.NEVER_EXPIRE;
        } else {
            return unit.convert(defaultTimeToLive, DEFAULT_TIME_UNIT);
        }
    }

    /**
     * Returns the specified expiration filter or <tt>null</tt> if no filter has been
     * specified.
     */
    public Filter<CacheEntry<K, V>> getExpirationFilter() {
        return expirationFilter;
    }

    /**
     * Sets the default expiration time for elements added to the cache. Elements added
     * using the {@link Cache#put(Object, Object)} will expire at
     * <tt>time_of_insert + default_expiration_time</tt>. The default expiration is
     * infinite, that is elements never expires..
     * 
     * @param timeToLive
     *            the default time to live for elements added to the cache, must be
     *            positive
     * @param unit
     *            the time unit of the duration argument
     * @throws IllegalArgumentException
     *             if specified timeToLive is a non positive number
     * @throws NullPointerException
     *             if the specified unit is <tt>null</tt>
     */
    public CacheExpirationConfiguration<K, V> setDefaultTimeToLive(long timeToLive,
            TimeUnit unit) {
        if (timeToLive < 0) {
            throw new IllegalArgumentException("timeToLive must be greather then 0, was "
                    + timeToLive);
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        if (timeToLive == CacheExpirationService.NEVER_EXPIRE) {
            defaultTimeToLive = CacheExpirationService.NEVER_EXPIRE;
            // don't convert relative to time unit
        } else {
            defaultTimeToLive = DEFAULT_TIME_UNIT.convert(timeToLive, unit);
        }
        return this;
    }

    /**
     * Sets a specific expiration filter that can be used in <tt>addition</tt> to the
     * time based expiration filter to check if items has expired. If no filter has been
     * set items are expired according to their registered expiration time. If an
     * expiration filter is set cache entries are first checked against that filter then
     * against the time based expiration times.
     */
    public CacheExpirationConfiguration<K, V> setExpirationFilter(
            Filter<CacheEntry<K, V>> filter) {
        expirationFilter = filter;
        return this;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#fromXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void fromXML(Element base) throws Exception {
        /* Expiration timeout */
        Element eTime = getChild(DEFAULT_TIMEOUT_TAG, base);
        long time = UnitOfTime.fromElement(eTime, DEFAULT_TIME_UNIT,
                CacheExpirationService.NEVER_EXPIRE);
        setDefaultTimeToLive(time, DEFAULT_TIME_UNIT);

        /* Expiration Filter */
        expirationFilter = loadOptional(base, EXPIRATION_FILTER_TAG, Filter.class);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#toXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void toXML(Document doc, Element parent) throws Exception {
        /* Expiration Timer */
        UnitOfTime.toElementCompact(doc, parent, DEFAULT_TIMEOUT_TAG, defaultTimeToLive,
                DEFAULT_TIME_UNIT, DEFAULT.defaultTimeToLive);

        /* Filter */
        addAndsaveObject(doc, parent, EXPIRATION_FILTER_TAG, getResourceBundle(),
                "expiration.saveOfExpirationFilterFailed", expirationFilter);
    }
}
