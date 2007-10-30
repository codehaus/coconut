package org.coconut.cache.internal.service.loading;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntryFactoryService;
import org.coconut.cache.internal.service.exceptionhandling.CacheExceptionService;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
import org.coconut.filter.Filter;
import org.coconut.management.ManagedGroup;

public abstract class AbstractCacheLoadingService<K, V> extends AbstractCacheLifecycle
        implements CacheLoadingService<K, V>, InternalCacheLoadingService<K, V> {
    private final AbstractCacheEntryFactoryService<K, V> attributeFactory;

    private final CacheExceptionService<K, V> exceptionHandler;

    private final CacheLoader<? super K, ? extends V> loader;

    private final LoadSupport<K, V> loadSupport;

    private final Filter<CacheEntry<K, V>> reloadFilter;

    public AbstractCacheLoadingService(
            CacheLoadingConfiguration<K, V> loadingConfiguration,
            AbstractCacheEntryFactoryService attributeFactory,
            CacheExceptionService<K, V> exceptionHandler, LoadSupport<K, V> loadSupport) {
        this.loader = loadingConfiguration.getLoader();
        reloadFilter = loadingConfiguration.getRefreshFilter();
        this.loadSupport = loadSupport;
        this.attributeFactory = attributeFactory;
        this.exceptionHandler = exceptionHandler;
    }

    /** {@inheritDoc} */
    public final void forceLoad(K key) {
        forceLoad(key, AttributeMaps.EMPTY_MAP);
    }

    /** {@inheritDoc} */
    public final void forceLoad(K key, AttributeMap attributes) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        loadAsync(key, attributes);
    }

    /** {@inheritDoc} */
    public final void forceLoadAll() {
        forceLoadAll(AttributeMaps.EMPTY_MAP);
    }

    public final void forceLoadAll(AttributeMap attributes) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        loadSupport.loadAll(attributes, true);
    }

    public final void forceLoadAll(Collection<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException("keys is null");
        }
        HashMap<K, AttributeMap> map = new HashMap<K, AttributeMap>();
        for (K key : keys) {
            if (key == null) {
                throw new NullPointerException("Collection contains a null key");
            }
            map.put(key, AttributeMaps.EMPTY_MAP);
        }
        loadAllAsync(map);
    }

    public final void forceLoadAll(Map<K, AttributeMap> mapsWithAttributes) {
        if (mapsWithAttributes == null) {
            throw new NullPointerException("mapsWithAttributes is null");
        }
        loadAllAsync(mapsWithAttributes);
    }

    public Collection<?> getChildServices() {
        return Arrays.asList(loader, reloadFilter);
    }

    /** {@inheritDoc} */
    public long getDefaultTimeToRefresh(TimeUnit unit) {
        return LoadingUtils.convertNanosToRefreshTime(attributeFactory.update()
                .getTimeToRefreshNanos(), unit);
    }

    public Filter<CacheEntry<K, V>> getRefreshFilter() {
        return reloadFilter;
    }

    /** {@inheritDoc} */
    public final void load(K key) {
        load(key, AttributeMaps.EMPTY_MAP);
    }

    /** {@inheritDoc} */
    public final void load(K key, AttributeMap attributes) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        loadSupport.load(key, attributes);
    }

    public final void loadAll() {
        loadSupport.loadAll(AttributeMaps.EMPTY_MAP, false);
    }

    public final void loadAll(AttributeMap attributes) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        loadSupport.loadAll(attributes, false);
    }

    public final void loadAll(Collection<? extends K> keys) {
        if (keys == null) {
            throw new NullPointerException("keys is null");
        }
        HashMap<K, AttributeMap> map = new HashMap<K, AttributeMap>();
        for (K key : keys) {
            if (key == null) {
                throw new NullPointerException("Collection contains a null key");
            }
            map.put(key, AttributeMaps.EMPTY_MAP);
        }
        loadSupport.loadAll(map);
    }

    public final void loadAll(Map<K, AttributeMap> mapsWithAttributes) {
        if (mapsWithAttributes == null) {
            throw new NullPointerException("mapsWithAttributes is null");
        }
        loadSupport.loadAll(mapsWithAttributes);
    }

    /** {@inheritDoc} */
    public void manage(ManagedGroup parent) {
        if (loader != null) {
            ManagedGroup g = parent.addChild(CacheLoadingConfiguration.SERVICE_NAME,
                    "Cache Loading attributes and operations");
            g.add(LoadingUtils.wrapMXBean(this));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void registerServices(Map<Class<?>, Object> serviceMap) {
        if (loader != null) {
            serviceMap.put(CacheLoadingService.class, LoadingUtils.wrapService(this));
        }
    }

    /** {@inheritDoc} */
    public void setDefaultTimeToRefresh(long timeToRefresh, TimeUnit unit) {
        attributeFactory.update().setTimeToFreshNanos(
                LoadingUtils.convertRefreshTimeToNanos(timeToRefresh, unit));
    }

    public void loadAllAsync(Map<K, AttributeMap> mapsWithAttributes) {
        for (Map.Entry<K, AttributeMap> e : mapsWithAttributes.entrySet()) {
            loadAsync(e.getKey(), e.getValue());
        }
    }

    final V doLoad(CacheLoader<? super K, ? extends V> loader, K key,
            AttributeMap attributes, boolean isSynchronous) {
        V v = null;
        if (loader != null) {
            try {
                v = loader.load(key, attributes);
            } catch (Exception e) {
                v = getExceptionHandler().getExceptionHandler()
                        .loadFailed(getExceptionHandler().createContext(), loader, key,
                                attributes, e);
            }
        }
        return v;
    }

    final CacheExceptionService<K, V> getExceptionHandler() {
        return exceptionHandler;
    }

    final CacheLoader<? super K, ? extends V> getLoader() {
        return loader;
    }

    AbstractCacheEntry<K, V> loadAndAddToCache(K key, AttributeMap attributes,
            boolean isSynchronous) {
        V v = doLoad(loader, key, attributes, false);
        return loadSupport.valueLoaded(key, v, attributes);

    }
}
