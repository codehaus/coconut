/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.expiration;

import static org.coconut.internal.util.XmlUtil.add;
import static org.coconut.internal.util.XmlUtil.getChild;
import static org.coconut.internal.util.XmlUtil.loadObject;
import static org.coconut.internal.util.XmlUtil.saveObject;

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

	private final static CacheExpirationConfiguration DEFAULT = new CacheExpirationConfiguration();

	private final static String DEFAULT_TIMEOUT_TAG = "default-timetolive";

	private final static String EXPIRATION_FILTER_TAG = "filter";

	public final static String SERVICE_NAME = "expiration";

	private long defaultTimeToLive = CacheExpirationService.NEVER_EXPIRE;

	private Filter<CacheEntry<K, V>> expirationFilter;

	/**
     * Creates a new CacheExpirationConfiguration
     */
	public CacheExpirationConfiguration() {
		super(SERVICE_NAME, CacheExpirationService.class);
	}

	public long getDefaultTimeToLive(TimeUnit unit) {
		if (defaultTimeToLive == CacheExpirationService.NEVER_EXPIRE) {
			return CacheExpirationService.NEVER_EXPIRE;
		} else {
			return unit.convert(defaultTimeToLive, TimeUnit.NANOSECONDS);
		}
	}

	/**
     * Returns the specified expiration filter or <tt>null</tt> if no filter
     * has been specified.
     */
	public Filter<CacheEntry<K, V>> getExpirationFilter() {
		return expirationFilter;
	}

	/**
     * Sets the default expiration time for elements added to the cache.
     * Elements added using the {@link Cache#put(Object, Object)} will expire at
     * <tt>time_of_insert + default_expiration_time</tt>. Expired elements
     * are handled accordingly to the {@link ExpirationStrategy} set for the
     * cache using {@link #setFilter(Filter)}. The default expiration is
     * infinite, that is elements never expires..
     * 
     * @param timeToLive
     *            the default time to live for elements added to the cache, must
     *            be positive
     * @param unit
     *            the time unit of the duration argument
     */
	public CacheExpirationConfiguration<K, V> setDefaultTimeToLive(long timeToLive,
			TimeUnit unit) {
		if (timeToLive <= 0) {
			throw new IllegalArgumentException("timeToLive must be greather then 0, was "
					+ timeToLive);
		} else if (unit == null) {
			throw new NullPointerException("unit is null");
		}
		if (timeToLive == CacheExpirationService.NEVER_EXPIRE) {
			defaultTimeToLive = CacheExpirationService.NEVER_EXPIRE;
			// don't convert relative to time unit
		} else {
			defaultTimeToLive = unit.toNanos(timeToLive);
		}
		return this;
	}

	/**
     * Sets a specific expiration filter that can be used in <tt>addition</tt>
     * to the time based expiration filter to check if items has expired. If no
     * filter has been set items are expired according to their registered
     * expiration time. If an expiration filter is set cache entries are first
     * checked against that filter then against the time based expiration times.
     */
	public CacheExpirationConfiguration setExpirationFilter(
			Filter<CacheEntry<K, V>> filter) {
		expirationFilter = filter;
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
			setDefaultTimeToLive(timeout, TimeUnit.MILLISECONDS);
		}
		/* Expiration Filter */
		setExpirationFilter(loadObject(getChild(EXPIRATION_FILTER_TAG, parent),
				Filter.class));
	}

	/**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#toXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
	@Override
	protected void toXML(Document doc, Element base) throws Exception {
		/* Expiration Timeout */
		long timeout = getDefaultTimeToLive(TimeUnit.MILLISECONDS);
		if (timeout != CacheExpirationService.NEVER_EXPIRE) {
			UnitOfTime.toElementCompact(add(doc, DEFAULT_TIMEOUT_TAG, base), timeout,
					TimeUnit.MILLISECONDS);
		}
		/* Expiration Filter */
		saveObject(doc, base, EXPIRATION_FILTER_TAG, "expiration.cannotPersistFilter",
				getExpirationFilter(), DEFAULT.getExpirationFilter());
	}

	public static void main(String[] args) {
		CacheExpirationConfiguration conf = new CacheExpirationConfiguration();
	}
}
