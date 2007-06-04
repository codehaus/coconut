/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.attribute.InternalCacheAttributeService;
import org.coconut.cache.internal.service.service.AbstractInternalCacheService;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingMXBean;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.core.AttributeMap;
import org.coconut.core.Transformer;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheLoadingService<K, V> extends
        AbstractInternalCacheService implements CacheLoadingService<K, V>,
        InternalCacheLoadingService<K, V> {

    private final CacheHelper<K, V> helper;

    private final InternalCacheAttributeService attributeFactory;

    @Override
    public void start(CacheConfiguration<?, ?> configuration,
            Map<Class<?>, Object> serviceMap) {
        serviceMap.put(CacheLoadingService.class, this);
    }
    AbstractCacheLoadingService(InternalCacheAttributeService attributeFactory,
            CacheHelper<K, V> helper) {
        super(CacheLoadingConfiguration.SERVICE_NAME);
        this.attributeFactory = attributeFactory;
        this.helper = helper;
    }

    public abstract boolean canLoad();

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#load(java.lang.Object)
     */
    public final Future<?> forceLoad(K key) {
        return doLoad(key, true);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#forceLoad(java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
    public final Future<?> forceLoad(K key, AttributeMap attributes) {
        return doLoad(key, attributes, true);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#forceLoadAll()
     */
    public final Future<?> forceLoadAll() {
        return filteredLoad(Filters.trueFilter());
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#forceLoadAll(org.coconut.core.AttributeMap)
     */
    public final Future<?> forceLoadAll(AttributeMap attributes) {
        return filteredLoad(Filters.trueFilter(), attributes);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#forceLoadAll(java.util.Collection)
     */
    public final Future<?> forceLoadAll(Collection<? extends K> keys) {
        return doLoadAll(keys, true);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#forceLoadAll(java.util.Map)
     */
    public final Future<?> forceLoadAll(Map<K, AttributeMap> mapsWithAttributes) {
        return doLoadAll(mapsWithAttributes, true);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#load(java.lang.Object)
     */
    public final Future<?> load(K key) {
        return doLoad(key, false);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#load(java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
    public Future<?> load(K key, AttributeMap attributes) {
        return doLoad(key, attributes, false);
    }

    public final Future<?> loadAll(Collection<? extends K> keys) {
        return doLoadAll(keys, false);
    }

    public final Future<?> loadAll(Map<K, AttributeMap> mapsWithAttributes) {
        return doLoadAll(mapsWithAttributes, false);
    }

    public abstract V loadBlocking(K key, AttributeMap attributes);

    public abstract void reloadIfNeeded(CacheEntry<K, V> entry);

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#load(java.lang.Object,
     *      org.coconut.core.AttributeMap)
     */
    private Future<?> doLoad(K key, AttributeMap attributes, boolean force) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        if (force || !helper.isValid(key)) {
            return doLoad(key, attributeFactory.createMap(attributes));
        }
        return null;
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#forceLoad(java.lang.Object)
     */
    private Future<?> doLoad(K key, boolean forceLoad) {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        if (forceLoad || !helper.isValid(key)) {
            return doLoad(key, attributeFactory.createMap());
        }
        return null;
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#loadAll(java.util.Collection)
     */
    private Future<?> doLoadAll(Collection<? extends K> keys, boolean forceLoad) {
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
            return doLoad(map);
        }
        return null;
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#loadAll(java.util.Map)
     */
    private Future<?> doLoadAll(Map<K, AttributeMap> keysWithAttributes, boolean forceLoad) {
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
            return doLoad(map);
        }
        return null;
    }

    abstract Future<?> doLoad(K key, AttributeMap map);

    abstract Future<?> doLoad(Map<? extends K, AttributeMap> keysWithAttributes);

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#filteredLoad(org.coconut.filter.Filter)
     */
    public final Future<?> filteredLoad(Filter<? super CacheEntry<K, V>> filter) {
        if (filter == null) {
            throw new NullPointerException("filter is null");
        }
        Collection<? extends K> keys = helper.filterKeys(filter);
        return forceLoadAll(keys);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#filteredLoad(org.coconut.filter.Filter,
     *      org.coconut.core.AttributeMap)
     */
    public final Future<?> filteredLoad(Filter<? super CacheEntry<K, V>> filter,
            AttributeMap defaultAttributes) {
        if (filter == null) {
            throw new NullPointerException("filter is null");
        } else if (defaultAttributes == null) {
            throw new NullPointerException("defaultAttributes is null");
        }
        Collection<? extends K> keys = helper.filterKeys(filter);
        HashMap<K, AttributeMap> map = new HashMap<K, AttributeMap>();
        for (K key : keys) {
            map.put(key, defaultAttributes);
        }
        return forceLoadAll(map);
    }

    /**
     * @see org.coconut.cache.service.loading.CacheLoadingService#filteredLoad(org.coconut.filter.Filter,
     *      org.coconut.core.Transformer)
     */
    public final Future<?> filteredLoad(Filter<? super CacheEntry<K, V>> filter,
            Transformer<CacheEntry<K, V>, AttributeMap> attributeTransformer) {
        if (filter == null) {
            throw new NullPointerException("filter is null");
        } else if (attributeTransformer == null) {
            throw new NullPointerException("defaultAttributes is null");
        }
        Collection<? extends CacheEntry<K, V>> keys = helper.filter(filter);
        HashMap<K, AttributeMap> map = new HashMap<K, AttributeMap>();
        for (CacheEntry<K, V> entry : keys) {
            AttributeMap atr = attributeTransformer.transform(entry);
            map.put(entry.getKey(), atr);
        }
        return forceLoadAll(map);
    }
}
