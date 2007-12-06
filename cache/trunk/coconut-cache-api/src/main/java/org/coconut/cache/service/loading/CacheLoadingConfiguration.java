/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import static org.coconut.internal.util.XmlUtil.addTypedElement;
import static org.coconut.internal.util.XmlUtil.getChild;
import static org.coconut.internal.util.XmlUtil.loadChildObject;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.internal.util.XmlUtil;
import org.coconut.operations.Ops.Predicate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to configure the loading service prior to usage.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class CacheLoadingConfiguration<K, V> extends AbstractCacheServiceConfiguration<K, V> {

    /** The name of this service. */
    public static final String SERVICE_NAME = "loading";

    /** The XML tag for the cache loader. */
    private final static String LOADER_TAG = "loader";

    /** The XML tag for the refresh predicate. */
    private final static String REFRESH_PREDICATE_TAG = "refresh-predicate";

    /** The XML tag for the refresh interval. */
    private final static String REFRESH_INTERVAL_TAG = "default-time-to-refresh";

    /** The default time to refresh. */
    private long defaultTimeToRefresh;

    /** The cache loader. */
    private CacheLoader<? super K, ? extends V> loader;

    /** The refresh predicate. */
    private Predicate<CacheEntry<K, V>> refreshPredicate;

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
     * Returns the configured refresh predicate.
     * 
     * @return the configured refresh predicate
     * @see #setRefreshPredicate(Predicate)
     */
    public Predicate<CacheEntry<K, V>> getRefreshPredicate() {
        return refreshPredicate;
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
    public CacheLoadingConfiguration<K, V> setDefaultTimeToRefresh(long interval, TimeUnit unit) {
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
     * new key-value bindings. And the {@link CacheLoadingService} will not be available
     * at runtime. All values must then put into the cache by
     * {@link Cache#put(Object, Object)}, {@link Cache#putAll(java.util.Map)} or some of
     * the other put operations.
     * 
     * @param cacheLoader
     *            the cache loader to set
     * @return the current CacheConfiguration
     */
    public CacheLoadingConfiguration<K, V> setLoader(CacheLoader<? super K, ? extends V> cacheLoader) {
        this.loader = cacheLoader;
        return this;
    }

    /**
     * Sets a function ({@link Predicate}) that is used for determining if an element
     * should be refreshed. The predicate is checked on calls to the various load methods
     * in {@link CacheLoadingService}.
     * <p>
     * Some cache implementations might also check the predicate on calls to
     * {@link org.coconut.cache.Cache#get(Object)},{@link org.coconut.cache.Cache#getAll(Collection)},
     * {@link org.coconut.cache.Cache#getEntry(Object)}, but this is not required.
     * 
     * @param predicate
     *            the reload predicate
     * @return this configuration
     * @see #getRefreshPredicate()
     */
    public CacheLoadingConfiguration<K, V> setRefreshPredicate(Predicate<CacheEntry<K, V>> predicate) {
        refreshPredicate = predicate;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    protected void fromXML(Element parent) throws Exception {
        /* Loader */
        this.loader = loadChildObject(parent, LOADER_TAG, CacheLoader.class);

        /* Refresh timer */
        Element eTime = getChild(REFRESH_INTERVAL_TAG, parent);
        long time = XmlUtil.elementTimeUnitRead(eTime, TimeUnit.NANOSECONDS, Long.MAX_VALUE);
        setDefaultTimeToRefresh(time, TimeUnit.NANOSECONDS);

        /* Refresh Filter */
        refreshPredicate = loadChildObject(parent, REFRESH_PREDICATE_TAG, Predicate.class);
    }

    /** {@inheritDoc} */
    @Override
    protected void toXML(Document doc, Element parent) {
        /* Cache Loader */
        addTypedElement(doc, parent, LOADER_TAG, getResourceBundle(), getClass(),
                "saveOfLoaderFailed", loader);

        /* Refresh Timer */
        XmlUtil.elementTimeUnitAdd(doc, parent, REFRESH_INTERVAL_TAG, defaultTimeToRefresh,
                TimeUnit.NANOSECONDS, 0);

        /* Refresh Predicate */
        addTypedElement(doc, parent, REFRESH_PREDICATE_TAG, getResourceBundle(), getClass(),
                "saveOfFilterFailed", refreshPredicate);
    }
}
