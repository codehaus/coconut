package org.coconut.cache.internal.service.loading;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheException;
import org.coconut.cache.spi.CacheSPI;

class LoadableFutureTask<K, V> extends FutureTask<CacheEntry<K, V>> {

    LoadableFutureTask(InternalCacheLoadingService<K, V> loaderService, K key, AttributeMap attributes) {
        super(createLoadCallable(loaderService, key, attributes));
    }

    CacheEntry<K, V> getBlocking() {
        run();
        try {
            return get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else if (e.getCause() instanceof Error) {
                throw (Error) e.getCause();
            } else {
                throw new CacheException(CacheSPI.HIGHLY_IRREGULAR_MSG, e);
            }
        }
    }

    /**
     * Creates a new Callable that will call the specified loader service to load a new
     * AbstractCacheEntry.
     *
     * @param loaderService
     *            the loading service to load from
     * @param key
     *            the key that should be loaded
     * @param attributes
     *            a map of attributes that should be loaded
     * @return the newly created callable
     * @param <K>
     *            the type of keys maintained by this service
     * @param <V>
     *            the type of mapped values
     */
    static <K, V> Callable<CacheEntry<K, V>> createLoadCallable(
            InternalCacheLoadingService<K, V> loaderService, K key, AttributeMap attributes) {
        return new LoadValueCallable<K, V>(loaderService, key, attributes);
    }

    /**
     * A LoadValueCallable is used to asynchronously load a value into the cache.
     */
    static class LoadValueCallable<K, V> implements Callable<CacheEntry<K, V>> {

        /** The attribute map that should be passed to the cache loader. */
        private final AttributeMap attributes;

        /** The key to load. */
        private final K key;

        /** The loading service to load the value from. */
        private final InternalCacheLoadingService<K, V> loadingService;

        /**
         * Creates a new LoadValueCallable.
         *
         * @param loadingService
         *            the loading service to load the value from
         * @param key
         *            the key to load
         * @param attributes
         *            the attribute map that should be passed to the cache loader
         * @throws NullPointerException
         *             if the specified loading service, key or attribute map is
         *             <code>null</code>
         */
        LoadValueCallable(InternalCacheLoadingService<K, V> loadingService, K key,
                AttributeMap attributes) {
            if (loadingService == null) {
                throw new NullPointerException("loadingService is null");
            } else if (key == null) {
                throw new NullPointerException("key is null");
            } else if (attributes == null) {
                throw new NullPointerException("attributes is null");
            }
            this.key = key;
            this.loadingService = loadingService;
            this.attributes = attributes;
        }

        /** {@inheritDoc} */
        public CacheEntry<K, V> call() {
            return loadingService.loadAndAddToCache(key, attributes, false);
        }

        /**
         * Returns the key whose corresponding value should be loaded.
         *
         * @return the key whose corresponding value should be loaded
         */
        public K getKey() {
            return key;
        }

        /**
         * Returns the attribute map that should be passed to the cache loader.
         *
         * @return the attribute map that should be passed to the cache loader
         */
        public AttributeMap getAttributes() {
            return attributes;
        }
    }
}
