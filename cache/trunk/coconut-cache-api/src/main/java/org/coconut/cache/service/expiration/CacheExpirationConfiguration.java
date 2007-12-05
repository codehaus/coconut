/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.expiration;

import static org.coconut.internal.util.XmlUtil.addTypedElement;
import static org.coconut.internal.util.XmlUtil.getChild;
import static org.coconut.internal.util.XmlUtil.loadChildObject;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.util.XmlUtil;
import org.coconut.operations.Ops.Predicate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to configure the expiration service prior to usage.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class CacheExpirationConfiguration<K, V> extends
        AbstractCacheServiceConfiguration<K, V> {

    /** The name of this service. */
    public final static String SERVICE_NAME = "Expiration";

    /** The XML default time to live tag. */
    private final static String DEFAULT_TIMEOUT_TAG = "default-timetolive";

    /** The XML expiration filter tag. */
    private final static String EXPIRATION_FILTER_TAG = "filter";

    /** The default TimeUnit uses in this service. */
    private final static TimeUnit DEFAULT_TIME_UNIT = TimeUnit.NANOSECONDS;

    /** The default settings, used when xml-serializing this configuration. */
    private final static CacheExpirationConfiguration<?, ?> DEFAULT = new CacheExpirationConfiguration<Object, Object>();

    /** The default time to live. */
    private long defaultTimeToLive;

    /** The expiration filter. */
    private Predicate<CacheEntry<K, V>> expirationFilter;

    /**
     * Creates a new CacheExpirationConfiguration.
     */
    @SuppressWarnings("unchecked")
    public CacheExpirationConfiguration() {
        super(SERVICE_NAME);
    }

    /**
     * Returns the default configured time to live in the specified time unit.
     * 
     * @param unit
     *            the time unit to return the time to live in
     * @return the default configured time to live
     * @see #setDefaultTimeToLive(long, TimeUnit)
     */
    public long getDefaultTimeToLive(TimeUnit unit) {
        if (defaultTimeToLive == CacheExpirationService.NEVER_EXPIRE) {
            return CacheExpirationService.NEVER_EXPIRE;
        } else {
            return unit.convert(defaultTimeToLive, DEFAULT_TIME_UNIT);
        }
    }

    /**
     * Returns the configured expiration filter or <tt>null</tt> if no filter has been
     * configured.
     * 
     * @return the configured expiration filter or null if no filter has been configured
     * @see #setExpirationFilter(Predicate)
     */
    public Predicate<CacheEntry<K, V>> getExpirationFilter() {
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
     * @return this configuration
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
     * time based expiration check to see if an element has expired. If no filter has been
     * set items are expired according to their registered expiration time. If an
     * expiration filter is set cache entries are first checked against that filter then
     * against the time based expiration times.
     * 
     * @param filter
     *            the filter to use for checking expired elements
     * @return this configuration
     */
    public CacheExpirationConfiguration<K, V> setExpirationFilter(
            Predicate<CacheEntry<K, V>> filter) {
        expirationFilter = filter;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    protected void fromXML(Element base) throws Exception {
        /* Expiration timeout */
        Element eTime = getChild(DEFAULT_TIMEOUT_TAG, base);
        long time = XmlUtil.elementTimeUnitRead(eTime, DEFAULT_TIME_UNIT,
                CacheExpirationService.NEVER_EXPIRE);
        setDefaultTimeToLive(time, DEFAULT_TIME_UNIT);

        /* Expiration Filter */
        expirationFilter = loadChildObject(base, EXPIRATION_FILTER_TAG, Predicate.class);
    }

    /** {@inheritDoc} */
    @Override
    protected void toXML(Document doc, Element parent) throws Exception {
        /* Expiration Timer */
        XmlUtil.elementTimeUnitAdd(doc, parent, DEFAULT_TIMEOUT_TAG, defaultTimeToLive,
                DEFAULT_TIME_UNIT, DEFAULT.defaultTimeToLive);

        /* Filter */
        addTypedElement(doc, parent, EXPIRATION_FILTER_TAG, getResourceBundle(),getClass(),
                "saveOfExpirationFilterFailed", expirationFilter);
    }
}
