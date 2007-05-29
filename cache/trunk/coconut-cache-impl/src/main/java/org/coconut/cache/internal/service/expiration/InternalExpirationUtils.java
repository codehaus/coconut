/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.service.DummyCacheService;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.core.AttributeMap;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class InternalExpirationUtils {

    public static final InternalExpirationService DUMMY = new NoCacheExpirationService();

    static <K, V> CacheExpirationService<K, V> wrap(
            CacheExpirationService<K, V> service) {
        return new DelegatedCacheExpirationService<K, V>(service);
    }

    static final class NoCacheExpirationService<K, V> extends DummyCacheService implements
            InternalExpirationService<K, V> {

        /**
		 * @param name
		 */
		public NoCacheExpirationService() {
			super(CacheExpirationConfiguration.SERVICE_NAME);
		}

		/**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#getDefaultTimeout(java.util.concurrent.TimeUnit)
         */
        public long getDefaultTimeToLive(TimeUnit unit) {
            return CacheExpirationService.NEVER_EXPIRE;
        }

        /**
         * @see org.coconut.cache.internal.service.expiration.InternalCacheExpirationService#getExpirationTime(java.lang.Object,
         *      java.lang.Object,
         *      org.coconut.cache.service.loading.AttributeMap)
         */
        public long innerGetExpirationTime(K key, V value, AttributeMap attributes) {
            return CacheExpirationService.NEVER_EXPIRE;
        }

        /**
         * @see org.coconut.cache.internal.service.expiration.InternalCacheExpirationService#isExpired(org.coconut.cache.CacheEntry)
         */
        public boolean innerIsExpired(CacheEntry<K, V> entry) {
            return false;
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#put(java.lang.Object,
         *      java.lang.Object, long, java.util.concurrent.TimeUnit)
         */
        public V put(K key, V value, long expirationTime) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#putAll(java.util.Map,
         *      long, java.util.concurrent.TimeUnit)
         */
        public void putAll(Map<? extends K, ? extends V> t, long timeout) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#setDefaultTimeout(long,
         *      java.util.concurrent.TimeUnit)
         */
        public void setDefaultTimeToLive(long duration, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#setExpirationFilter(org.coconut.filter.Filter)
         */
        public void setExpirationFilter(Filter<CacheEntry<K, V>> filter) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#expire(java.lang.Object)
         */
        public boolean expire(K key) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#expireAll(java.util.Collection)
         */
        public int expireAll(Collection<? extends K> keys) {
            throw new UnsupportedOperationException();
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#expireAll(org.coconut.filter.Filter)
         */
        public int expireAll(Filter<? extends CacheEntry<K, V>> filter) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * A wrapper class that exposes only the ExecutorService methods of an
     * implementation.
     */
    static class DelegatedCacheExpirationService<K, V> implements
            CacheExpirationService<K, V> {
        private final CacheExpirationService<K, V> service;

        DelegatedCacheExpirationService(CacheExpirationService<K, V> service) {
            this.service = service;
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#expireAll()
         */
        public int removeAll() {
            return service.removeAll();
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#expireAll(java.util.Collection)
         */
        public int removeAll(Collection<? extends K> keys) {
            return service.removeAll(keys);
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#expireAll(org.coconut.filter.Filter)
         */
        public int removeAll(Filter<? extends CacheEntry<K, V>> filter) {
            return service.removeAll(filter);
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#getDefaultTimeToLive(java.util.concurrent.TimeUnit)
         */
        public long getDefaultTimeToLive(TimeUnit unit) {
            return service.getDefaultTimeToLive(unit);
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#getExpirationFilter()
         */
        public Filter<CacheEntry<K, V>> getExpirationFilter() {
            return service.getExpirationFilter();
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#put(java.lang.Object,
         *      java.lang.Object, long, java.util.concurrent.TimeUnit)
         */
        public V put(K key, V value, long expirationTime, TimeUnit unit) {
            return service.put(key, value, expirationTime, unit);
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#putAll(java.util.Map,
         *      long, java.util.concurrent.TimeUnit)
         */
        public void putAll(Map<? extends K, ? extends V> t, long timeout, TimeUnit unit) {
            service.putAll(t, timeout, unit);
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#setDefaultTimeToLive(long,
         *      java.util.concurrent.TimeUnit)
         */
        public void setDefaultTimeToLive(long timeToLive, TimeUnit unit) {
            service.setDefaultTimeToLive(timeToLive, unit);
        }

        /**
         * @see org.coconut.cache.service.expiration.CacheExpirationService#setExpirationFilter(org.coconut.filter.Filter)
         */
        public void setExpirationFilter(Filter<CacheEntry<K, V>> filter) {
            service.setExpirationFilter(filter);
        }
    }
}
