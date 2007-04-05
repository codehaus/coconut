/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

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
public class CacheLoadingConfiguration<K, V> extends
        AbstractCacheServiceConfiguration<K, V> {

    public static final String SERVICE_NAME = "loading";

    private final static String LOADER_TAG = "loader";

    private final static String REFRESH_FILTER_TAG = "refresh-filter";

    private final static String REFRESH_INTERVAL_TAG = "refresh-interval";

    private long defaultRefreshInterval = -1;

    private Filter<CacheEntry<K, V>> refreshFilter;

    private CacheLoader<? super K, ? extends V> loader;

    public CacheLoadingConfiguration() {
        super(SERVICE_NAME, CacheLoadingService.class);
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
    public long getRefreshInterval(TimeUnit unit) {
        if (defaultRefreshInterval > 0) {
            return unit.convert(defaultRefreshInterval, TimeUnit.NANOSECONDS);
        } else {
            return defaultRefreshInterval;
        }
    }

    /**
     * Sets the default reload interval relative to the last update of the
     * element. For example, if all elements should be refreshed 1 hour after
     * they have been added or last updated. Use
     * 
     * <pre>
     * CacheLoadingConfiguration clc;
     * clc.setReloadInterval(60 * 60, TimeUnit.SECONDS);
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
        }
        if (interval <= 0) {
            defaultRefreshInterval = interval;
        } else {
            defaultRefreshInterval = unit.toNanos(interval);
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
    // currentNoMap to specify load attributes???, perhaps specify a
    // refreshTransformer??(y)
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
    protected void fromXML(Document doc, Element parent) throws Exception {
        Element loaderE = getChild(LOADER_TAG, parent);
        if (loaderE != null) {
            CacheLoader loader = loadObject(loaderE, CacheLoader.class);
            setLoader(loader);
        }

        /* Refresh timer */
        Element refreshInterval = getChild(REFRESH_INTERVAL_TAG, parent);
        if (refreshInterval != null) {
            long timeout = UnitOfTime.fromElement(refreshInterval, TimeUnit.MILLISECONDS);
            setDefaultRefreshTime(timeout, TimeUnit.MILLISECONDS);
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
    protected void toXML(Document doc, Element parent) {
        if (loader != null) {
            Element e = add(doc, LOADER_TAG, parent);
            saveObject(doc, e, "backend.cannotPersistLoader", loader);
        }

        /* Refresh Timer */
        long refresh = getRefreshInterval(TimeUnit.MILLISECONDS);
        if (refresh > 0) {
            UnitOfTime.toElementCompact(add(doc, REFRESH_INTERVAL_TAG, parent), refresh,
                    TimeUnit.MILLISECONDS);
        }

        /* Refresh Filter */
        Filter refreshFilter = getRefreshFilter();
        if (refreshFilter != null) {
            saveObject(doc, add(doc, REFRESH_FILTER_TAG, parent),
                    "expiration.cannotPersistRefreshFilter", refreshFilter);
        }
    }
}
