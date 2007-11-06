/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import static org.coconut.internal.util.XmlUtil.addAndsaveObject;
import static org.coconut.internal.util.XmlUtil.getChild;
import static org.coconut.internal.util.XmlUtil.loadOptional;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.filter.Predicate;
import org.coconut.internal.util.UnitOfTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to configure the loading service prior to usage.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class CacheLoadingConfiguration<K, V> extends
        AbstractCacheServiceConfiguration<K, V> {

    /** The name of this service. */
    public static final String SERVICE_NAME = "loading";

    /** The XML tag for the cache loader. */
    private final static String LOADER_TAG = "loader";

    /** The XML tag for the refresh filter. */
    private final static String REFRESH_FILTER_TAG = "refresh-filter";

    /** The XML tag for the refresh interval. */
    private final static String REFRESH_INTERVAL_TAG = "default-time-to-refresh";

    /** The default time to refresh. */
    private long defaultTimeToRefresh;

    /** The cache loader. */
    private CacheLoader<? super K, ? extends V> loader;

    /** The refresh filter. */
    private Predicate<CacheEntry<K, V>> refreshFilter;

    /**
     * Creates a new CacheLoadingConfiguration.
     */
    public CacheLoadingConfiguration() {
        super(SERVICE_NAME);
    }

    /**
     * Returns the refresh interval in the specified timeunit.
     * 
     * @param unit
     *            the unit of time to return the reload interval in
     * @return the refresh interval in the specified timeunit.
     * @see #setDefaultTimeToRefresh(long, TimeUnit)
     */
    public long getDefaultTimeToRefresh(TimeUnit unit) {
        if (defaultTimeToRefresh == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        } else {
            return unit.convert(defaultTimeToRefresh, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * Returns the CacheLoader that the cache should use for loading cache elements.
     * 
     * @return the configured cache loader for the cache
     * @see #setLoader(CacheLoader)
     */
    public CacheLoader<? super K, ? extends V> getLoader() {
        return loader;
    }

    /**
     * Returns the configured refresh filter.
     * 
     * @return the configured refresh filter
     * @see #setRefreshFilter(Predicate)
     */
    public Predicate<CacheEntry<K, V>> getRefreshFilter() {
        return refreshFilter;
    }

    /**
     * Sets the default refresh interval relative to the last update of the element. For
     * example, if all elements should be refreshed 1 hour after they have been added to
     * cache by default, one might use:
     * 
     * <pre>
     * CacheLoadingConfiguration clc;
     * clc.setDefaultRefreshTime(1, TimeUnit.HOUR);
     * </pre>
     * 
     * @param interval
     *            the interval between refreshes
     * @param unit
     *            the time unit of the interval
     * @return this configuration
     * @throws NullPointerException
     *             if the specified unit is <tt>null</tt>
     * @throws IllegalArgumentException
     *             if the specified interval is negative
     */
    public CacheLoadingConfiguration<K, V> setDefaultTimeToRefresh(long interval,
            TimeUnit unit) {
        if (unit == null) {
            throw new NullPointerException("unit is null");
        } else if (interval < 0) {
            throw new IllegalArgumentException("interval must be a non negative number");
        }
        if (interval == Long.MAX_VALUE) {
            defaultTimeToRefresh = interval;
        } else {
            defaultTimeToRefresh = TimeUnit.NANOSECONDS.convert(interval, unit);
        }
        return this;
    }

    /**
     * Sets the cache loader that should be used for loading new elements into the cache.
     * If the specified loader is <code>null</code> no loader will be used for loading
     * new key-value bindings. All values must then put into the cache by using on of the
     * caches {@link Cache#put(Object, Object)} methods.
     * 
     * @param cacheLoader
     *            the cache loader to set
     * @return the current CacheConfiguration
     */
    public CacheLoadingConfiguration<K, V> setLoader(
            CacheLoader<? super K, ? extends V> cacheLoader) {
        this.loader = cacheLoader;
        return this;
    }

    /**
     * Sets a function ({@link Predicate}) that is used for determining if an element
     * should be refreshed. The filter is checked on each call to
     * {@link org.coconut.cache.Cache#get(Object)},{@link org.coconut.cache.Cache#getAll(Collection)},
     * {@link org.coconut.cache.Cache#getEntry(Object)} if a mapping exist for specified
     * key(s). Furthermore it is called for all entries within the cache on calls to
     * {@link org.coconut.cache.Cache#evict()}.
     * <p>
     * 
     * @param filter
     *            the reload filter
     * @return this configuration
     */
    public CacheLoadingConfiguration<K, V> setRefreshFilter(
            Predicate<CacheEntry<K, V>> filter) {
        refreshFilter = filter;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    protected void fromXML(Element parent) throws Exception {
        /* Loader */
        this.loader = loadOptional(parent, LOADER_TAG, CacheLoader.class);

        /* Refresh timer */
        Element eTime = getChild(REFRESH_INTERVAL_TAG, parent);
        long time = UnitOfTime.fromElement(eTime, TimeUnit.NANOSECONDS, Long.MAX_VALUE);
        setDefaultTimeToRefresh(time, TimeUnit.NANOSECONDS);

        /* Refresh Filter */
        refreshFilter = loadOptional(parent, REFRESH_FILTER_TAG, Predicate.class);
    }

    /** {@inheritDoc} */
    @Override
    protected void toXML(Document doc, Element parent) {
        /* Cache Loader */
        addAndsaveObject(doc, parent, LOADER_TAG, getResourceBundle(),
                "loading.saveOfLoaderFailed", loader);

        /* Refresh Timer */
        UnitOfTime.toElementCompact(doc, parent, REFRESH_INTERVAL_TAG,
                defaultTimeToRefresh, TimeUnit.NANOSECONDS, 0);

        /* Refresh Filter */
        addAndsaveObject(doc, parent, REFRESH_FILTER_TAG, getResourceBundle(),
                "loading.saveOfFilterFailed", refreshFilter);
    }
}
