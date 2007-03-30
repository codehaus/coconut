/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.joinpoint;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class NoOpAfterCacheOperation implements InternalCacheOperation {

    /**
     * @see org.coconut.cache.internal.service.joinpoint.InternalCacheOperation#needElementsAfterClear()
     */
    public boolean needElementsAfterClear() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.AfterCacheOperation#afterCacheClear(org.coconut.cache.Cache, long, int, long, java.util.Collection)
     */
    public void afterCacheClear(Cache cache, long started, int previousSize, long previousCapacity, Collection entries) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.AfterCacheOperation#afterCacheEvict(org.coconut.cache.Cache, long, int, int, long, long, java.util.Collection, java.util.Collection)
     */
    public void afterCacheEvict(Cache cache, long started, int size, int previousSize, long capacity, long previousCapacity, Collection evicted, Collection expired) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.AfterCacheOperation#afterGet(org.coconut.cache.Cache, long, java.util.Collection, java.lang.Object, org.coconut.cache.CacheEntry, org.coconut.cache.CacheEntry, boolean)
     */
    public void afterGet(Cache cache, long started, Collection evictedEntries, Object key, CacheEntry prev, CacheEntry newEntry, boolean isExpired) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.AfterCacheOperation#afterPut(org.coconut.cache.Cache, long, java.util.Collection, org.coconut.cache.CacheEntry, org.coconut.cache.CacheEntry)
     */
    public void afterPut(Cache cache, long started, Collection evictedEntries, CacheEntry oldEntry, CacheEntry newEntry) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.AfterCacheOperation#afterPutAll(org.coconut.cache.Cache, long, java.util.Collection, java.util.Collection, java.util.Collection)
     */
    public void afterPutAll(Cache cache, long started, Collection evictedEntries, Collection prev, Collection added) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.AfterCacheOperation#afterRemove(org.coconut.cache.Cache, long, org.coconut.cache.CacheEntry)
     */
    public void afterRemove(Cache cache, long started, CacheEntry entry) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.AfterCacheOperation#afterReplace(org.coconut.cache.Cache, long, java.util.Collection, org.coconut.cache.CacheEntry, org.coconut.cache.CacheEntry)
     */
    public void afterReplace(Cache cache, long started, Collection evictedEntries, CacheEntry oldEntry, CacheEntry newEntry) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.AfterCacheOperation#afterTrimToSize(org.coconut.cache.Cache, long, java.util.Collection)
     */
    public void afterTrimToSize(Cache cache, long started, Collection evictedEntries) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.BeforeCacheOperation#beforeCacheClear(org.coconut.cache.Cache)
     */
    public long beforeCacheClear(Cache cache) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.BeforeCacheOperation#beforeCacheEvict(org.coconut.cache.Cache)
     */
    public long beforeCacheEvict(Cache cache) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.BeforeCacheOperation#beforeGet(org.coconut.cache.Cache, java.lang.Object)
     */
    public long beforeGet(Cache cache, Object key) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.BeforeCacheOperation#beforePut(org.coconut.cache.Cache, org.coconut.cache.CacheEntry)
     */
    public long beforePut(Cache cache, CacheEntry entry) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.BeforeCacheOperation#beforePut(org.coconut.cache.Cache, java.lang.Object, java.lang.Object)
     */
    public long beforePut(Cache cache, Object key, Object value) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.BeforeCacheOperation#beforePutAll(org.coconut.cache.Cache, java.util.Collection)
     */
    public long beforePutAll(Cache cache, Collection added) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.BeforeCacheOperation#beforePutAll(org.coconut.cache.Cache, java.util.Map)
     */
    public long beforePutAll(Cache cache, Map map) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.BeforeCacheOperation#beforeRemove(org.coconut.cache.Cache, java.lang.Object)
     */
    public long beforeRemove(Cache cache, Object key) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.BeforeCacheOperation#beforeReplace(org.coconut.cache.Cache, java.lang.Object, java.lang.Object, java.lang.Object)
     */
    public long beforeReplace(Cache cache, Object key, Object oldValue, Object newValue) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.internal.service.joinpoint.BeforeCacheOperation#beforeTrimToSize(org.coconut.cache.Cache)
     */
    public long beforeTrimToSize(Cache cache) {
        // TODO Auto-generated method stub
        return 0;
    }

}
