/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.internal.service.CacheServiceManager;
import org.coconut.cache.internal.service.event.DefaultCacheEventService;
import org.coconut.cache.internal.service.eviction.DefaultCacheEvictionService;
import org.coconut.cache.internal.service.expiration.FinalExpirationCacheService;
import org.coconut.cache.internal.service.loading.CacheEntryLoaderService;
import org.coconut.cache.internal.service.management.DefaultCacheManagementService;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.management.CacheManagementService;
import org.coconut.cache.spi.annotations.CacheServiceSupport;
import org.coconut.cache.spi.annotations.CacheSupport;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@CacheSupport(CacheLoadingSupport = true, CacheEntrySupport = true, ExpirationSupport = true)
@ThreadSafe
@CacheServiceSupport( { CacheEventService.class, CacheManagementService.class })
public class SynchronizedCache<K, V> extends UnsynchronizedCache<K, V> {
    
    @SuppressWarnings("unchecked")
    protected CacheServiceManager<K, V> populateCsm(CacheServiceManager<K, V> csm,
            CacheConfiguration<K, V> conf) {
        csm.addService(DefaultCacheStatisticsService.class);
        csm.addService(DefaultCacheEvictionService.class);
        csm.addService(FinalExpirationCacheService.class);
        csm.addService(CacheEntryLoaderService.class);
        csm.addService(DefaultCacheManagementService.class);
        csm.addService(DefaultCacheEventService.class);
        return csm;
    }

    }
