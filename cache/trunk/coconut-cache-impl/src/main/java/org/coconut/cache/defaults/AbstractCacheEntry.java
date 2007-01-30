/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.defaults.EntryMap;
import org.coconut.cache.defaults.SupportedCache;
import org.coconut.cache.spi.CacheErrorHandler;
import org.coconut.cache.util.DefaultCacheEntry;
import org.coconut.core.Clock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
abstract class AbstractCacheEntry<K, V> implements CacheEntry<K, V> {
    interface EntryFactory<K, V> {
        AbstractCacheEntry<K, V> createNew(SupportedCache<K, V> cache, K key, V value,
                double cost, long creationTime, long expirationTime, long hits,
                long lastAccessTime, long lastUpdateTime, long size, long version);
    }

    static final EntryFactory UNSYNC = new UnsynchronizedEntryFactory();

    static final class UnsynchronizedEntryFactory<K, V> implements EntryFactory<K, V> {

        /**
         * @see org.coconut.cache.defaults.memory.AbstractCacheEntry.EntryFactory#createNew(org.coconut.cache.defaults.memory.SupportedCache,
         *      java.lang.Object, java.lang.Object, double, long, long, long,
         *      long, long, long, long)
         */
        public AbstractCacheEntry<K, V> createNew(SupportedCache<K, V> cache, K key,
                V value, double cost, long creationTime, long expirationTime, long hits,
                long lastAccessTime, long lastUpdateTime, long size, long version) {
            return new AbstractCacheEntry.UnsynchronizedCacheEntry<K, V>(cache, key,
                    value, cost, creationTime, expirationTime, hits, lastAccessTime,
                    cache.getClock().timestamp(), size, version);
        }

    }

    static <K, V> double getCost(CacheEntry<K, V> takeFrom,
            CacheErrorHandler<K, V> errorHandler) {
        double cost = takeFrom == null ? CacheEntry.DEFAULT_COST : takeFrom.getCost();
        if (Double.isNaN(cost)) {
            errorHandler
                    .warning("An entry with an invalid cost (cost = NaN) was added for key = "
                            + takeFrom.getKey());
            cost = CacheEntry.DEFAULT_COST;
        }
        return cost;
    }

    static <K, V> long getCreationTime(CacheEntry<K, V> takeFrom,
            CacheEntry<K, V> existing, Clock clock, CacheErrorHandler<K, V> errorHandler) {
        if (takeFrom != null
                && takeFrom.getCreationTime() != DefaultCacheEntry.DEFAULT_CREATION_TIME) {
            long time = takeFrom.getCreationTime();
            if (time > 0) {
                return time;
            }
            errorHandler
                    .warning("'Must specify a positive creation time was creationtime= "
                            + time + " for key = " + takeFrom.getKey());
        }
        return existing == null ? clock.timestamp() : existing.getCreationTime();
    }

    static <K, V> long getExpirationTime(CacheEntry<K, V> takeFrom, long expirationTime,
            long cacheDefaultExpirationTimeMS, Clock clock,
            CacheErrorHandler<K, V> errorHandler) {
        long time = takeFrom == null ? expirationTime : takeFrom.getExpirationTime();
        if (time < 0) {
            errorHandler
                    .warning("'Must specify a positive expirationTime was expirationTime= "
                            + time + " for key = " + takeFrom.getKey());
            time = Cache.DEFAULT_EXPIRATION;
        }
        if (time == Cache.DEFAULT_EXPIRATION) {
            if (cacheDefaultExpirationTimeMS == Cache.NEVER_EXPIRE) {
                return Cache.NEVER_EXPIRE;
            } else {
                return clock.getDeadlineFromNow(cacheDefaultExpirationTimeMS,
                        TimeUnit.MILLISECONDS);
            }
        } else if (time == Cache.NEVER_EXPIRE) {
            return Long.MAX_VALUE;
        } else {
            return takeFrom == null ? clock.getDeadlineFromNow(expirationTime,
                    TimeUnit.MILLISECONDS) : time;
        }
    }

    static <K, V> long getHits(CacheEntry<K, V> takeFrom, CacheEntry<K, V> existing,
            CacheErrorHandler<K, V> errorHandler) {
        if (takeFrom != null && takeFrom.getHits() != DefaultCacheEntry.DEFAULT_HIT_COUNT) {
            long hits = takeFrom.getHits();
            if (hits >= 0) {
                return hits;
            }
            errorHandler.warning("'Must specify a positive hitcount was hitcount= "
                    + hits + " for key = " + takeFrom.getKey());
        }
        return existing == null ? 0 : existing.getHits();
    }

    static <K, V> long getLastAccessTime(CacheEntry<K, V> takeFrom,
            CacheEntry<K, V> existing, Clock clock, CacheErrorHandler<K, V> errorHandler) {
        if (takeFrom != null
                && takeFrom.getLastAccessTime() != DefaultCacheEntry.DEFAULT_LAST_ACCESS_TIME) {
            long time = takeFrom.getLastAccessTime();
            if (time > 0) {
                return time;
            }
            errorHandler
                    .warning("'Must specify a positive lastaccess time was lastaccesstime= "
                            + time + " for key = " + takeFrom.getKey());
        }
        return existing == null ? 0 : existing.getLastAccessTime();
    }

    static <K, V> long getSize(CacheEntry<K, V> takeFrom,
            CacheErrorHandler<K, V> errorHandler) {
        long size = takeFrom == null ? CacheEntry.DEFAULT_SIZE : takeFrom.getSize();
        if (size < 0) {
            errorHandler.warning("An entry with negative size (size = " + size
                    + ") was added for key = " + takeFrom.getKey());
            size = CacheEntry.DEFAULT_SIZE;
        }
        return size;
    }

    static <K, V> long getVersion(CacheEntry<K, V> takeFrom, CacheEntry<K, V> existing,
            CacheErrorHandler<K, V> errorHandler) {
        if (takeFrom != null
                && takeFrom.getVersion() != DefaultCacheEntry.DEFAULT_VERSION) {
            long version = takeFrom.getVersion();
            if (version > 0) {
                return version;
            }
            errorHandler.warning("'Must specify a positive version was version= "
                    + version + " for key = " + takeFrom.getKey());
        }
        return existing == null ? 1 : existing.getVersion() + 1;
    }

    private final double cost;

    private final long creationTime;

    private final int hash;

    private final K key;

    private final long lastUpdateTime;

    /** the index in cache policy, is -1 if not used or initialized. */
    private int policyIndex = -1;

    private final long size;

    private final V value;

    private final long version;

    AbstractCacheEntry<K, V> next;

    /**
     * @param key
     * @param value
     */
    AbstractCacheEntry(K key, V value, double cost, long creationTime,
            long lastUpdateTime, long size, long version) {
        this.hash = EntryMap.hash(key.hashCode());
        this.key = key;
        this.value = value;
        this.cost = cost;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.size = size;
        this.version = version;

    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Map.Entry))
            return false;
        Map.Entry e = (Map.Entry) o;
        Object k1 = getKey();
        Object k2 = e.getKey();
        // we keep null checks, might later want to use Map.Entry instead of
        // Entry to compare with
        if (k1 == k2 || (k1 != null && k1.equals(k2))) {
            Object v1 = value;
            Object v2 = e.getValue();
            if (v1 == v2 || (v1 != null && v1.equals(v2)))
                return true;
        }
        return false;
    }

    /**
     * @return the cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * @return the creationTime
     */
    public long getCreationTime() {
        return creationTime;
    }

    public K getKey() {
        return key;
    }

    /**
     * @return the lastUpdateTime
     */
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    public V getValue() {
        return value;
    }

    /**
     * @return the version
     */
    public long getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        return key.hashCode() ^ value.hashCode();
    }

    public V setValue(V v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return getKey() + "=" + getValue() + " (ExpirationTime= " + getExpirationTime()
                + ")";
    }

    protected void entryRemoved() {

    }

    abstract void accessed();

    int getHash() {
        return hash;
    }

    AbstractCacheEntry<K, V> getNext() {
        return next;
    }

    int getPolicyIndex() {
        return policyIndex;
    }

    void increment() {
        setHits(getHits() + 1);
    }

    abstract void setExpirationTime(long time);

    abstract void setHits(long hits);

    void setNext(AbstractCacheEntry<K, V> entry) {
        next = entry;
    }

    void setPolicyIndex(int index) {
        this.policyIndex = index;
    }

    static <K, V> AbstractCacheEntry<K, V> newEntry(
            SupportedCache<K, V> cache, CacheEntry<K, V> entry,
            AbstractCacheEntry<K, V> existing, K key, V value, long expirationTimeMilli,
            boolean isExpired) {
        K k = entry == null ? key : entry.getKey();
        V v = entry == null ? value : entry.getValue();
        double cost = getCost(entry, cache.getErrorHandler());
        long creationTime = getCreationTime(entry, existing, cache.getClock(), cache
                .getErrorHandler());
        long size = getSize(entry, cache.getErrorHandler());
        long expTime = getExpirationTime(entry, expirationTimeMilli, cache
                .getExpirationSupport().getDefaultExpirationTime(), cache.getClock(),
                cache.getErrorHandler());
        long lastAccessTime = getLastAccessTime(entry, existing, cache.getClock(), cache
                .getErrorHandler());
        long hits = getHits(entry, existing, cache.getErrorHandler());
        long version = getVersion(entry, entry, cache.getErrorHandler());
        AbstractCacheEntry<K, V> me = cache.getEntryFactory().createNew(cache, k, v, cost, creationTime,
                expTime, hits, lastAccessTime, cache.getClock().timestamp(), size,
                version);
        if (existing != null) {
            me.setPolicyIndex(existing.getPolicyIndex());
        }
        return me;
    }

    static class UnsynchronizedCacheEntry<K, V> extends AbstractCacheEntry<K, V> {

        private final SupportedCache<K, V> cache;

        long expirationTime;

        long hits;

        long lastAccessTime;

        /** the index in cache policy, is -1 if not used or initialized. */
        int policyIndex = -1;

        UnsynchronizedCacheEntry(SupportedCache<K, V> cache, K key, V value, double cost,
                long creationTime, long expirationTime, long hits, long lastAccessTime,
                long lastUpdateTime, long size, long version) {
            super(key, value, cost, creationTime, lastUpdateTime, size, version);
            this.cache = cache;
            this.expirationTime = expirationTime;
            this.hits = hits;
            this.lastAccessTime = lastAccessTime;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getExpirationTime()
         */
        public long getExpirationTime() {
            return expirationTime;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getHits()
         */
        public long getHits() {
            return hits;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getLastAccessTime()
         */
        public long getLastAccessTime() {
            return lastAccessTime;
        }

        void accessed() {
            lastAccessTime = cache.getClock().timestamp();
            hits++;
        }

        /**
         * @param expirationTime
         *            the expirationTime to set
         */
        void setExpirationTime(long expirationTime) {
            this.expirationTime = expirationTime;
        }

        /**
         * @param hits
         *            the hits to set
         */
        void setHits(long hits) {
            this.hits = hits;
        }

        /**
         * @param lastAccessTime
         *            the lastAccessTime to set
         */
        void setLastAccessTime(long lastAccessTime) {
            this.lastAccessTime = lastAccessTime;
        }
    }

    static class VolatileCacheEntry<K, V> extends AbstractCacheEntry<K, V> {

        private SupportedCache<K, V> cache;

        private volatile long expirationTime;

        private volatile long hits;

        private volatile long lastAccessedTime;

        /**
         * @param key
         * @param value
         * @param cost
         * @param creationTime
         * @param lastUpdateTime
         * @param size
         * @param version
         */
        public VolatileCacheEntry(SupportedCache<K, V> cache, K key, V value,
                double cost, long creationTime, long lastUpdateTime, long size,
                long version) {
            super(key, value, cost, creationTime, lastUpdateTime, size, version);

        }

        /**
         * @see org.coconut.cache.CacheEntry#getExpirationTime()
         */
        public long getExpirationTime() {
            return expirationTime;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getHits()
         */
        public long getHits() {
            return hits;
        }

        /**
         * @see org.coconut.cache.CacheEntry#getLastAccessTime()
         */
        public long getLastAccessTime() {
            return lastAccessedTime;
        }

        void accessed() {
            lastAccessedTime = cache.getClock().timestamp();
        }

        /**
         * @see org.coconut.cache.defaults.memory.CacheEntryImpl#setExpirationTime(long)
         */
        @Override
        void setExpirationTime(long time) {
            this.expirationTime = time;
        }

        /**
         * @see org.coconut.cache.defaults.memory.CacheEntryImpl#setHits(long)
         */
        @Override
        void setHits(long hits) {
            this.hits = hits;
        }
    }
}
