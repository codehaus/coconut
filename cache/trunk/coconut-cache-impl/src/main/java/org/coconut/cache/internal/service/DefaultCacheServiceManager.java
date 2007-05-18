/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.lifecycle.CacheManagerService;
import org.coconut.cache.spi.CacheService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheServiceManager implements CacheManagerService {

    public static DefaultCacheServiceManager getInstance(Cache c, CacheConfiguration conf) {
        return null;
    }

    /**
     * @see org.coconut.cache.service.servicemanager.CacheManagerService#attach(org.coconut.cache.spi.CacheService)
     */
    public void attach(CacheService lifecycle) {
        throw new UnsupportedOperationException();
    }
}
