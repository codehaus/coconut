/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.servicemanager.CacheService;
import org.coconut.cache.service.servicemanager.CacheServiceManagerService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheServiceManager implements CacheServiceManagerService {

    public static DefaultCacheServiceManager getInstance(Cache<?, ?> c,
            CacheConfiguration<?, ?> conf) {
        return null;
    }

    /**
     * @see org.coconut.cache.service.servicemanager.CacheServiceManagerService#registerService(org.coconut.cache.service.servicemanager.CacheService)
     */
    public <T extends CacheService> T registerService(T lifecycle) {
        throw new UnsupportedOperationException();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) {
        return false;
    }

    public boolean isShutdown() {
        return false;
    }

    public boolean isTerminated() {
        return false;
    }

    public void shutdown() {}
}
