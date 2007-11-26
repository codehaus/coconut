/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.internal.service.servicemanager.CompositeService;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycleInitializer;
import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;
import org.coconut.predicate.Predicate;

/**
 * An abstract implementation of CacheLoadingService.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class AbstractCacheLoadingService<K, V> extends AbstractCacheLifecycle implements
        InternalCacheLoadingService<K, V>, CompositeService, ManagedLifecycle {
    private final InternalCacheEntryService attributeFactory;

    private final InternalCacheExceptionService<K, V> exceptionHandler;

    private final CacheLoader<? super K, ? extends V> loader;

    private final LoadSupport<K, V> loadSupport;

    private final Predicate<CacheEntry<K, V>> reloadFilter;

    public AbstractCacheLoadingService(CacheLoadingConfiguration<K, V> loadingConfiguration,
            InternalCacheEntryService attributeFactory,
            InternalCacheExceptionService<K, V> exceptionHandler, LoadSupport<K, V> loadSupport) {
        attributeFactory.setTimeToRefreshNs(LoadingUtils
                .getInitialTimeToRefresh(loadingConfiguration));
        this.loader = loadingConfiguration.getLoader();
        reloadFilter = loadingConfiguration.getRefreshPredicate();
        this.loadSupport = loadSupport;
        this.attributeFactory = attributeFactory;
        this.exceptionHandler = exceptionHandler;
    }

    public V loadAndGet(K key) {
        // will and probably should count as a cache miss
        AbstractCacheEntry<K, V> ace = loadBlocking(key);
        return ace == null ? null : ace.getValue();
    }

    public V loadAndGet(K key, AttributeMap attributes) {
        AbstractCacheEntry<K, V> ace = loadBlocking(key, attributes);
        return ace == null ? null : ace.getValue();
    }

    AbstractCacheEntry<K, V> loadBlocking(K key) {
        return loadBlocking(key, AttributeMaps.EMPTY_MAP);
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

    /** {@inheritDoc} */
    public final void forceLoadAll(AttributeMap attributes) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        loadSupport.loadAll(attributes, true);
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public final void forceLoadAll(Map<K, AttributeMap> mapsWithAttributes) {
        if (mapsWithAttributes == null) {
            throw new NullPointerException("mapsWithAttributes is null");
        }
        loadAllAsync(mapsWithAttributes);
    }

    /** {@inheritDoc} */
    public Collection<?> getChildServices() {
        return Arrays.asList(loader, reloadFilter);
    }

    /** {@inheritDoc} */
    public long getDefaultTimeToRefresh(TimeUnit unit) {
        return LoadingUtils.convertNanosToRefreshTime(attributeFactory.getTimeToRefreshNs(),
                unit);
    }

    /** {@inheritDoc} */
    public Predicate<CacheEntry<K, V>> getRefreshPredicate() {
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

    /** {@inheritDoc} */
    public final void loadAll() {
        loadAll(AttributeMaps.EMPTY_MAP);
    }

    /** {@inheritDoc} */
    public final void loadAll(AttributeMap attributes) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        loadSupport.loadAll(attributes, false);
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public final void loadAll(Map<K, AttributeMap> mapsWithAttributes) {
        if (mapsWithAttributes == null) {
            throw new NullPointerException("mapsWithAttributes is null");
        }
        loadSupport.loadAll(mapsWithAttributes);
    }

    /** {@inheritDoc} */
    public void loadAllAsync(Map<K, AttributeMap> mapsWithAttributes) {
        for (Map.Entry<K, AttributeMap> e : mapsWithAttributes.entrySet()) {
            loadAsync(e.getKey(), e.getValue());
        }
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
    public void initialize(CacheLifecycleInitializer cli) {
        if (loader != null) {
            cli.registerService(CacheLoadingService.class, LoadingUtils.wrapService(this));
        }
    }

    /** {@inheritDoc} */
    public void loadAsync(K key, AttributeMap attributes) {
        loadBlocking(key, attributes);// Load blocking as default
    }

    /** {@inheritDoc} */
    public void setDefaultTimeToRefresh(long timeToRefresh, TimeUnit unit) {
        attributeFactory.setTimeToRefreshNs(LoadingUtils.convertRefreshTimeToNanos(timeToRefresh,
                unit));
    }

    /**
     * Returns the {@link InternalCacheExceptionService} configured for this service.
     * 
     * @return the CacheExceptionService configured for this service
     */
    final InternalCacheExceptionService<K, V> getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Returns the {@link CacheLoader} configured for this service.
     * 
     * @return the CacheLoader configured for this service
     */
    final CacheLoader<? super K, ? extends V> getLoader() {
        return loader;
    }

    /**
     * @param key
     *            the key for which a value should be loaded
     * @param attributes
     *            an AttributeMap for the load
     * @param isSynchronous
     *            Whether or not this is synchronous operation (user waits on the result)
     * @return the cache entry that was added to the cache or null if no entry was added
     */
    AbstractCacheEntry<K, V> loadAndAddToCache(K key, AttributeMap attributes, boolean isSynchronous) {
        V v = null;
        if (loader != null) {
            try {
                v = loader.load(key, attributes);
            } catch (Exception e) {
                v = getExceptionHandler().getExceptionHandler().loadFailed(
                        getExceptionHandler().createContext(), loader, key, attributes, e);
            } catch (Error e) {
                e.printStackTrace();
                throw e;
            }
        }
        return loadSupport.valueLoaded(key, v, attributes);
    }
}
