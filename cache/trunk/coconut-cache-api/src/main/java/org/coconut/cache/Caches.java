/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;
import org.coconut.internal.util.CollectionUtils;

/**
 * Factory and utility methods for for creating different types of {@link Cache Caches}
 * and {@link CacheLoader CacheLoaders}. Furthermore there are a number of small utility
 * functions concerning general cache usage.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheServices.java 469 2007-11-17 14:32:25Z kasper $
 */
public final class Caches {

    final static Cache EMPTY_CACHE = new EmptyCache();

    final static NoServices NO_SERVICES = new NoServices();

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Caches() {}

    // /CLOVER:ON

    public static <K, V> Cache<K, V> emptyCache() {
        return EMPTY_CACHE;
    }

    /**
     * Returns a Runnable that when executed will call the clear method on the specified
     * cache.
     * <p>
     * The following example shows how this can be used to clear the cache every hour.
     * 
     * <pre>
     * Cache c;
     * ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
     * ses.scheduleAtFixedRate(Caches.runClear(c), 0, 60 * 60, TimeUnit.SECONDS);
     * </pre>
     * 
     * @param cache
     *            the cache on which to call evict
     * @return a runnable where invocation of the run method will clear the specified
     *         cache
     * @throws NullPointerException
     *             if the specified cache is <tt>null</tt>.
     */
    public static Runnable runClear(Cache<?, ?> cache) {
        return new ClearRunnable(cache);
    }

    /**
     * A runnable used for calling clear on a cache.
     */
    static class ClearRunnable implements Runnable {

        /** The cache to call clear on. */
        private final Cache<?, ?> cache;

        /**
         * Creates a new ClearRunnable.
         * 
         * @param cache
         *            the cache to call clear on
         */
        ClearRunnable(Cache<?, ?> cache) {
            if (cache == null) {
                throw new NullPointerException("cache is null");
            }
            this.cache = cache;
        }

        /** {@inheritDoc} */
        public void run() {
            cache.clear();
        }
    }

    static class EmptyCache<K, V> extends AbstractMap<K, V> implements Cache<K, V>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -5245003832315997155L;

        /** {@inheritDoc} */
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            unit.sleep(timeout);
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public Set<java.util.Map.Entry<K, V>> entrySet() {
            return Collections.EMPTY_MAP.entrySet();
        }

        /** {@inheritDoc} */
        public Map<K, V> getAll(Collection<? extends K> keys) {
            CollectionUtils.checkCollectionForNulls(keys);
            Map<K, V> result = new HashMap<K, V>(keys.size());
            for (K key : keys) {
                result.put(key, null);
            }
            return result;
        }

        /** {@inheritDoc} */
        public CacheEntry<K, V> getEntry(K key) {
            return null;
        }

        /** {@inheritDoc} */
        public String getName() {
            return "emptymap";
        }

        /** {@inheritDoc} */
        public <T> T getService(Class<T> serviceType) {
            return NO_SERVICES.getService(serviceType);
        }

        /** {@inheritDoc} */
        public long getVolume() {
            return 0;
        }

        /** {@inheritDoc} */
        public boolean isShutdown() {
            return false;
        }

        /** {@inheritDoc} */
        public boolean isStarted() {
            return false;
        }

        /** {@inheritDoc} */
        public boolean isTerminated() {
            return false;
        }

        /** {@inheritDoc} */
        public V peek(K key) {
            return null;
        }

        /** {@inheritDoc} */
        public CacheEntry<K, V> peekEntry(K key) {
            return null;
        }

        /** {@inheritDoc} */
        public V putIfAbsent(K key, V value) {
            throw new UnsupportedOperationException();
        }

        /** {@inheritDoc} */
        public boolean remove(Object key, Object value) {
            return false;
        }

        /** {@inheritDoc} */
        public void removeAll(Collection<? extends K> keys) {
         
        }

        /** {@inheritDoc} */
        public V replace(K key, V value) {
            throw new UnsupportedOperationException();//??
        }

        /** {@inheritDoc} */
        public boolean replace(K key, V oldValue, V newValue) {
            return false;
        }

        public CacheServices<K, V> services() {
            return new CacheServices<K, V>(this);
        }

        /** {@inheritDoc} */
        public void shutdown() {
          
        }

        /** {@inheritDoc} */
        public void shutdownNow() {
          
        }
        
        /** @return Preserves singleton property */
        private Object readResolve() {
            return EMPTY_CACHE;
        }
    }

    static class NoServices implements CacheServiceManagerService, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -5065001507844515260L;

        private final Map services = Collections.singletonMap(CacheServiceManagerService.class,
                this);

        /** {@inheritDoc} */
        public Map<Class<?>, Object> getAllServices() {
            return services;
        }

        /** {@inheritDoc} */
        public <T> T getService(Class<T> serviceType) {
            if (serviceType == null) {
                throw new NullPointerException("serviceType is null");
            }
            T t = (T) getAllServices().get(serviceType);
            if (t == null) {
                throw new IllegalArgumentException("Unknown service " + serviceType);
            }
            return t;
        }

        /** {@inheritDoc} */
        public boolean hasService(Class<?> serviceType) {
            return getAllServices().containsKey(serviceType);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return NO_SERVICES;
        }
    }
}
