/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.internal.service.InternalCacheService;
import org.coconut.cache.service.expiration.CacheExpirationMXBean;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;
import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheExpirationService<K, V> implements
        InternalCacheService, InternalCacheExpirationService<K, V>,
        CacheExpirationMXBean, CacheExpirationService<K, V> {

    boolean registerForManagement() {
        return false;
    }
    public void addServices(Map<Class, Object> map) {
        map.put(CacheExpirationService.class,
                new InternalCacheExpirationUtils.DelegatedCacheExpirationService<K, V>(
                        this));
        if (registerForManagement()) {
            map.put(CacheExpirationMXBean.class, null);
        }
    }

    public void addTo(ManagedGroup dg) {
        ManagedGroup m = dg.addNewGroup("Expiration",
                "Controls expiration of items in the cache", true);
        m.add(this);
    }

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
    // ignore
    }

    /**
     * @see org.coconut.cache.internal.service.InternalCacheService#shutdown(org.coconut.cache.internal.service.ShutdownCallback)
     */
    public void shutdown(Executor callback) throws Exception {
    // ignore
    }

}
