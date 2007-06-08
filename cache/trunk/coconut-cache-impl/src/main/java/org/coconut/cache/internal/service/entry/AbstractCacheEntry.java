/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import java.util.Map;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.exceptionhandling.AbstractCacheExceptionHandler;
import org.coconut.core.Clock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheEntry<K, V> implements CacheEntry<K, V> {

    public static final long DEFAULT_HIT_COUNT = -1;
    public static final long DEFAULT_LAST_ACCESS_TIME = 0;
    static <K, V> long getHits(CacheEntry<K, V> takeFrom, CacheEntry<K, V> existing,
            AbstractCacheExceptionHandler<K, V> errorHandler) {
        if (takeFrom != null && takeFrom.getHits() !=  DEFAULT_HIT_COUNT) {
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
            CacheEntry<K, V> existing, Clock clock, AbstractCacheExceptionHandler<K, V> errorHandler) {
        if (takeFrom != null
                && takeFrom.getLastAccessTime() != DEFAULT_LAST_ACCESS_TIME) {
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


    //
    // static <K, V> long getVersion(CacheEntry<K, V> takeFrom, CacheEntry<K, V>
    // existing,
    // CacheErrorHandler<K, V> errorHandler) {
    // if (takeFrom != null
    // && takeFrom.getVersion() != DefaultCacheEntry.DEFAULT_VERSION) {
    // long version = takeFrom.getVersion();
    // if (version > 0) {
    // return version;
    // }
    // errorHandler.warning("'Must specify a positive version was version= "
    // + version + " for key = " + takeFrom.getKey());
    // }
    // return existing == null ? 1 : existing.getVersion() + 1;
    // }

    private final double cost;

    private final long creationTime;

    private final int hash;

    private final K key;

    private final long lastUpdateTime;

    /** the index in cache policy, is -1 if not used or initialized. */
    private int policyIndex = -1;

    private final long size;

    private final V value;

    AbstractCacheEntry<K, V> next;

    /**
     * @param key
     * @param value
     */
    AbstractCacheEntry(K key, V value, double cost, long creationTime,
            long lastUpdateTime, long size) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        this.hash = EntryMap.hash(key.hashCode());
        this.key = key;
        this.value = value;
        this.cost = cost;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.size = size;
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


    @Override
    public int hashCode() {
        return key.hashCode() ^ value.hashCode();
    }

    public V setValue(V v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return getKey() + "=" + getValue() + " (policyIndex= " + getPolicyIndex() + ")";
    }

    public void entryRemoved() {

    }

    public abstract void accessed();

    public int getHash() {
        return hash;
    }

    public AbstractCacheEntry<K, V> getNext() {
        return next;
    }

    public int getPolicyIndex() {
        return policyIndex;
    }

    public void incrementHits() {
        setHits(getHits() + 1);
    }

    public abstract void setExpirationTime(long time);

    public abstract void setHits(long hits);

    public void setNext(AbstractCacheEntry<K, V> entry) {
        next = entry;
    }

    public void setPolicyIndex(int index) {
        this.policyIndex = index;
    }

//    static <K, V> AbstractCacheEntry<K, V> newEntry(EntryFactory<K, V> f,
//            SupportedCache<K, V> cache, AbstractCacheExpirationService<K, V> service,
//            CacheEntry<K, V> entry, AbstractCacheEntry<K, V> existing, K key, V value,
//            long expirationTimeMilli, boolean isExpired3) {
//        K k = entry == null ? key : entry.getKey();
//        V v = entry == null ? value : entry.getValue();
//        if (v == null) {
//            throw new NullPointerException("value is null");
//        }
//        double cost = getCost(entry, cache.getErrorHandler());
//        long creationTime =0;
//            
//            
//            //getCreationTime(entry, existing, cache.getClock(), cache
//            //    .getErrorHandler());
//        long size = getSize(entry, cache.getErrorHandler());
//        long expTime =0;
//            
//            //getExpirationTime(entry, expirationTimeMilli, service
//            //    .getDefaultTimeToLiveMs(), cache.getClock(), cache.getErrorHandler());
//        long lastAccessTime = getLastAccessTime(entry, existing, cache.getClock(), cache
//                .getErrorHandler());
//        long hits = getHits(entry, existing, cache.getErrorHandler());
//        long version = cache.getNextVersion();
//        AbstractCacheEntry<K, V> me = f.createNew(cache, k, v, cost, creationTime,
//                expTime, hits, lastAccessTime, cache.getClock().timestamp(), size,
//                version);
//
//        return me;
//    }




}
