/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.InternalCacheSupport;
import org.coconut.cache.internal.service.attribute.InternalCacheAttributeService;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
import org.coconut.core.Transformer;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache containing this service
 * @param <V>
 *            the type of mapped values
 */
public abstract class AbstractCacheLoadingService<K, V> extends AbstractCacheLifecycle
        implements CacheLoadingService<K, V>, InternalCacheLoadingService<K, V> {

    private final InternalCacheAttributeService attributeFactory;

    private final InternalCacheSupport<K, V> helper;

    AbstractCacheLoadingService(InternalCacheAttributeService attributeFactory,
            InternalCacheSupport<K, V> helper) {
        super(CacheLoadingConfiguration.SERVICE_NAME);
        this.attributeFactory = attributeFactory;
        this.helper = helper;
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#getDefaultTimeToLive(java.util.concurrent.TimeUnit)
     */
    public long getDefaultTimeToRefresh(TimeUnit unit) {
        return LoadingUtils.convertNanosToRefreshTime(attributeFactory.update()
                .getTimeToRefreshNanos(), unit);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#setDefaultTimeToLive(long,
     *      java.util.concurrent.TimeUnit)
     */
    public void setDefaultTimeToRefresh(long timeToRefresh, TimeUnit unit) {
        attributeFactory.update().setTimeToFreshNanos(
                LoadingUtils.convertRefreshTimeToNanos(timeToRefresh, unit));
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#load(java.lang.Object)
     */
    public final void forceLoad(K key) {
        doLoad(key, true);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#forceLoad(java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
    public final void forceLoad(K key, AttributeMap attributes) {
        doLoad(key, attributes, true);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#forceLoadAll(java.util.Collection)
     */
    public final void forceLoadAll(Collection<? extends K> keys) {
        doLoadAll(keys, true);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#forceLoadAll(java.util.Map)
     */
    public final void forceLoadAll(Map<K, AttributeMap> mapsWithAttributes) {
        doLoadAll(mapsWithAttributes, true);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#load(java.lang.Object)
     */
    public final void load(K key) {
        doLoad(key, false);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#load(java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
    public void load(K key, AttributeMap attributes) {
        doLoad(key, attributes, false);
    }

    public final void loadAll(Collection<? extends K> keys) {
        doLoadAll(keys, false);
    }

    public final void loadAll(Map<K, AttributeMap> mapsWithAttributes) {
        doLoadAll(mapsWithAttributes, false);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#forceLoadAll()
     */
    public final void forceLoadAll() {
        forceLoadAll(AttributeMaps.EMPTY_MAP);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#load(java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
    private void doLoad(K key, AttributeMap attributes, boolean force) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        if (force || !helper.isValid(key)) {
            doLoad(key, attributeFactory.createMap(attributes));
        }
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#forceLoad(java.lang.Object)
     */
    private void doLoad(K key, boolean forceLoad) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        if (forceLoad || !helper.isValid(key)) {
            doLoad(key, attributeFactory.createMap());
        }
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#loadAll(java.util.Collection)
     */
    private void doLoadAll(Collection<? extends K> keys, boolean forceLoad) {
        if (keys == null) {
            throw new NullPointerException("keys is null");
        }
        HashMap<K, AttributeMap> map = new HashMap<K, AttributeMap>();
        for (K key : keys) {
            if (key == null) {
                throw new NullPointerException("Collection of keys contained a null");
            }
            if (forceLoad || !helper.isValid(key)) {
                map.put(key, attributeFactory.createMap());
            }
        }
        if (map.size() > 0) {
            doLoad(map);
        }
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#loadAll(java.util.Map)
     */
    private void doLoadAll(Map<K, AttributeMap> keysWithAttributes, boolean forceLoad) {
        if (keysWithAttributes == null) {
            throw new NullPointerException("keysWithAttributes is null");
        }
        HashMap<K, AttributeMap> map = new HashMap<K, AttributeMap>();
        for (Map.Entry<K, AttributeMap> entry : keysWithAttributes.entrySet()) {
            K key = entry.getKey();
            AttributeMap am = entry.getValue();
            if (key == null) {
                throw new NullPointerException("Map contains a null key");
            } else if (am == null) {
                throw new NullPointerException("Map contains a null AttributeMap");
            }
            if (forceLoad || !helper.isValid(key)) {
                map.put(key, attributeFactory.createMap(am));
            }
        }
        if (map.size() > 0) {
            doLoad(map);
        }
    }

    abstract void doLoad(K key, AttributeMap map);

    abstract void doLoad(Map<? extends K, AttributeMap> keysWithAttributes);
}
