/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults;

import org.coconut.cache.internal.service.OlfInternalCacheServiceManager;
import org.coconut.cache.internal.service.event.DefaultCacheEventService;
import org.coconut.cache.internal.service.eviction.DefaultCacheEvictionService;
import org.coconut.cache.internal.service.expiration.UnsynchronizedCacheExpirationService;
import org.coconut.cache.internal.service.loading.DefaultCacheLoaderService;
import org.coconut.cache.internal.service.management.DefaultCacheManagementService;
import org.coconut.cache.internal.service.statistics.DefaultCacheStatisticsService;
import org.coconut.cache.internal.service.threading.NoThreadingCacheService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
final class Defaults {

    static <K, V> void initializeUnsynchronizedCache(
            OlfInternalCacheServiceManager<K, V> serviceManager) {
        serviceManager.registerServiceImplementations(DefaultCacheStatisticsService.class);
        serviceManager.registerServiceImplementations(DefaultCacheEvictionService.class);
        serviceManager.registerServiceImplementations(UnsynchronizedCacheExpirationService.class);
        serviceManager.registerServiceImplementations(DefaultCacheLoaderService.class);
        serviceManager.registerServiceImplementations(DefaultCacheManagementService.class);
        serviceManager.registerServiceImplementations(DefaultCacheEventService.class);
        serviceManager.registerServiceImplementations(NoThreadingCacheService.class);
    }
}
