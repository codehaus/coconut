/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import static org.coconut.internal.util.XmlUtil.addAndsaveObject;
import static org.coconut.internal.util.XmlUtil.getChild;
import static org.coconut.internal.util.XmlUtil.loadOptional;

import java.util.Arrays;
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
// currentNoMap to specify load attributes???, perhaps specify a
// refreshTransformer??(y)
public class CacheLoadingConfiguration<K, V> extends
		AbstractCacheServiceConfiguration<K, V> {

	private final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.NANOSECONDS;

	public static final String SERVICE_NAME = "loading";

	private final static String LOADER_TAG = "loader";

	private final static String REFRESH_FILTER_TAG = "refresh-filter";

	private final static String REFRESH_INTERVAL_TAG = "refresh-time";

	private long defaultRefreshInterval = Long.MAX_VALUE;

	private Filter<CacheEntry<K, V>> refreshFilter;

	private CacheLoader<? super K, ? extends V> loader;

	public CacheLoadingConfiguration() {
		super(SERVICE_NAME, Arrays.asList(CacheLoadingService.class, CacheLoadingMXBean.class));
	}

	/**
     * Returns the CacheLoader that the cache should use for loading new
     * key-value bindings. If this method returns <code>null</code> no initial
     * loader will be set.
     */
	public CacheLoader<? super K, ? extends V> getLoader() {
		return loader;
	}

	public Filter<CacheEntry<K, V>> getRefreshFilter() {
		return refreshFilter;
	}

	/**
     * Returns the reload interval in the specified timeunit.
     * 
     * @param unit
     *            the unit of time to return the reload interval in
     * @return
     * @see #setReloadInterval(long, TimeUnit)
     */
	public long getDefaultRefreshTime(TimeUnit unit) {
		if (defaultRefreshInterval == Long.MAX_VALUE) {
			return Long.MAX_VALUE;
		} else {
			return unit.convert(defaultRefreshInterval, DEFAULT_TIME_UNIT);
		}
	}

	/**
     * Sets the default refresh interval relative to the last update of the
     * element. For example, if all elements should be refreshed 1 hour after
     * they have been added to cache by default, you might use:
     * 
     * <pre>
     * CacheLoadingConfiguration clc;
     * clc.setDefaultRefreshTime(1, TimeUnit.HOUR);
     * </pre>
     * 
     * @param interval
     *            the i
     * @param unit
     *            the unit of the interval
     * @return this Expiration
     */
	public CacheLoadingConfiguration<K, V> setDefaultRefreshTime(long interval,
			TimeUnit unit) {
		if (unit == null) {
			throw new NullPointerException("unit is null");
		} else if (interval <= 0) {
			throw new IllegalArgumentException("interval must be a positive number");
		}

		if (interval == Long.MAX_VALUE) {
			defaultRefreshInterval = interval;
		} else {
			defaultRefreshInterval = DEFAULT_TIME_UNIT.convert(interval, unit);
		}
		return this;
	}

	/**
     * Sets the loader that should be used for loading new elements into the
     * cache. If the specified loader is <code>null</code> no loader will be
     * used for loading new key-value bindings. All values must then put into
     * the cache by using put or putAll.
     * 
     * @param loader
     *            the loader to set
     * @return the current CacheConfiguration
     * @throws IllegalStateException
     *             if an extended loader has already been set, using
     *             {@link #setExtendedBackend(CacheLoader)}
     */
	public CacheLoadingConfiguration<K, V> setLoader(
			CacheLoader<? super K, ? extends V> loader) {
		this.loader = loader;
		return this;
	}

	/**
     * Sets a function ({@link Filter}) that is used for determining if an
     * element should be reloaded. The filter is checked on each call to
     * {@link org.coconut.cache.Cache#get(Object)),{@link org.coconut.cache.Cache#getAll(Collection)),
     * {@link org.coconut.cache.Cache#getEntry(Object)) if a mapping exist for specified key(s).
     * Furthermore it is called for all entries within the cache on calls to
     * {@link org.coconut.cache.Cache#evict()).
     * <p>
     * 
     * @param filter
     *            the reload filter
     * @return this configuration
     */
	public CacheLoadingConfiguration<K, V> setRefreshFilter(
			Filter<CacheEntry<K, V>> filter) {
		refreshFilter = filter;
		return this;
	}

	/**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#fromXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
	@Override
	public void fromXML(Document doc, Element parent) throws Exception {
		/* Loader */
		this.loader = loadOptional(parent, LOADER_TAG, CacheLoader.class);

		/* Refresh timer */
		Element eTime = getChild(REFRESH_INTERVAL_TAG, parent);
		long time = UnitOfTime.fromElement(eTime, DEFAULT_TIME_UNIT, Long.MAX_VALUE);
		setDefaultRefreshTime(time, DEFAULT_TIME_UNIT);

		/* Refresh Filter */
		refreshFilter = loadOptional(parent, REFRESH_FILTER_TAG, Filter.class);
	}

	/**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#toXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
	@Override
	public void toXML(Document doc, Element parent) {
		/* Cache Loader */
		addAndsaveObject(doc, parent, LOADER_TAG, getResourceBundle(),
				"loading.saveOfLoaderFailed", loader);

		/* Refresh Timer */
		UnitOfTime.toElementCompact(doc, parent, REFRESH_INTERVAL_TAG,
				defaultRefreshInterval, DEFAULT_TIME_UNIT, Long.MAX_VALUE);

		/* Refresh Filter */
		addAndsaveObject(doc, parent, REFRESH_FILTER_TAG, getResourceBundle(),
				"loading.saveOfFilterFailed", refreshFilter);
	}
}
