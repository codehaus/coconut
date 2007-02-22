/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.CacheServiceManager;
import org.coconut.cache.internal.service.event.DefaultCacheEventService;
import org.coconut.cache.internal.service.eviction.DefaultCacheEvictionService;
import org.coconut.cache.internal.service.expiration.ExpirationCacheService;
import org.coconut.cache.internal.service.expiration.FinalExpirationCacheService;
import org.coconut.cache.internal.service.loading.CacheEntryLoaderService;
import org.coconut.cache.internal.service.management.DefaultCacheManagementService;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.spi.annotations.CacheServiceSupport;
import org.coconut.cache.spi.annotations.CacheSupport;

/**
 * TODO fix loading. fix event bus make cache services mutable
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@CacheSupport(CacheLoadingSupport = true, CacheEntrySupport = true, ExpirationSupport = true)
@ThreadSafe
@CacheServiceSupport( { CacheEventService.class, CacheManagementService.class })
public class SynchronizedCache<K, V> extends SupportedCache<K, V> {
    private final EntryMap<K, V> map = new EntryMap<K, V>(false);

    private final DefaultCacheEventService<K, V> eventService;

    private final DefaultCacheEvictionService<AbstractCacheEntry<K, V>> evictionService;

    private final DefaultCacheStatisticsService<K, V> statistics;

    private final CacheEntryLoaderService<K, V> loading;

    private final ExpirationCacheService<K, V> expiration;

    @SuppressWarnings("unchecked")
    public SynchronizedCache() {
        this((CacheConfiguration) CacheConfiguration.create());
    }

    public SynchronizedCache(CacheConfiguration<K, V> conf) {
        super(conf);
        statistics = getCsm().initialize(DefaultCacheStatisticsService.class);
        expiration = getCsm().initialize(ExpirationCacheService.class);
        loading = getCsm().initialize(CacheEntryLoaderService.class);
        evictionService = getCsm().initialize(DefaultCacheEvictionService.class);
        eventService = getCsm().initialize(DefaultCacheEventService.class);

        // important must be last, because of final value being inlined.
        if (conf.getInitialMap() != null) {
            putAll(conf.getInitialMap());
        }
    }

    /**
     * @param m1_to_m5_map
     */
    public SynchronizedCache(Map<Integer, String> m1_to_m5_map) {
        this();
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doClear()
     */
    @Override
    int doClear() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doEvict()
     */
    @Override
    int doEvict() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doGet(java.lang.Object)
     */
    @Override
    CacheEntry<K, V> doGet(K key) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doPeek(java.lang.Object)
     */
    @Override
    CacheEntry<K, V> doPeek(K key) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doPut(java.lang.Object, java.lang.Object, long, long, boolean)
     */
    @Override
    CacheEntry<K, V> doPut(K key, V value, long expirationTimeMilli, long version, boolean isPutIfAbsent) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doPutAll(java.util.Map, long)
     */
    @Override
    void doPutAll(Map<? extends K, ? extends V> t, long expirationTimeMilli) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doPutEntries(java.util.Collection)
     */
    @Override
    void doPutEntries(Collection<CacheEntry<K, V>> entries) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doPutEntry(org.coconut.cache.CacheEntry, long)
     */
    @Override
    CacheEntry<K, V> doPutEntry(CacheEntry<K, V> entry, long version) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doRemove(java.lang.Object, java.lang.Object)
     */
    @Override
    CacheEntry<K, V> doRemove(Object key, Object value) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#doReplace(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    @Override
    CacheEntry<K, V> doReplace(K key, V oldValue, V newValue) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.cache.defaults.SupportedCache#populateCsm(org.coconut.cache.internal.service.CacheServiceManager, org.coconut.cache.CacheConfiguration)
     */
    @Override
    CacheServiceManager<K, V> populateCsm(CacheServiceManager<K, V> csm, CacheConfiguration<K, V> conf) {
        // TODO Auto-generated method stub
        return null;
    }
}
