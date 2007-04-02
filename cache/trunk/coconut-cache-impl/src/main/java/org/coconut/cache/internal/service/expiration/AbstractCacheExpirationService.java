/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.internal.service.InternalCacheService;
import org.coconut.cache.internal.service.ShutdownCallback;
import org.coconut.cache.service.expiration.CacheExpirationMXBean;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;
import org.coconut.management.annotation.ManagedAttribute;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheExpirationService<K, V> implements
        InternalCacheService, InternalCacheExpirationService<K, V>,
        CacheExpirationMXBean, CacheExpirationService<K, V> {

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationMXBean#getFilter()
     */
    public String getFilterAsString() {
        Filter f = getExpirationFilter();
        if (f == null) {
            return "No Filter defined";
        } else {
            return f.toString();
        }
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationService#expireAll()
     */
    public int expireAll() {
        return expireAll((Filter) Filters.trueFilter());
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationMXBean#getDefaultExpirationMs()
     */
    public long getDefaultTimeToLiveMs() {
        return getDefaultTimeToLive(TimeUnit.MILLISECONDS);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationMXBean#setDefaultTimeout(long)
     */
    public void setDefaultTimeToLiveMs(long timeToLiveMs) {
        setDefaultTimeToLive(timeToLiveMs, TimeUnit.MILLISECONDS);
    }

    /**
     * @see org.coconut.cache.internal.service.InternalCacheService#doStart()
     */
    public void doStart() throws Exception {
        //ignore
    }

    /**
     * @see org.coconut.cache.internal.service.InternalCacheService#shutdown(org.coconut.cache.internal.service.ShutdownCallback)
     */
    public void shutdown(ShutdownCallback callback) throws Exception {
       //ignore
    }

}
