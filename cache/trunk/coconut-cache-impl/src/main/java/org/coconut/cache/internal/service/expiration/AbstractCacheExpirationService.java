/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.expiration.CacheExpirationMXBean;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.core.AttributeMap;
import org.coconut.filter.Filter;
import org.coconut.management.annotation.ManagedAttribute;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheExpirationService<K, V> implements
        CacheExpirationMXBean, CacheExpirationService<K, V> {

    

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationMXBean#getDefaultExpirationMs()
     */
    @ManagedAttribute(defaultValue = "Default TimeToLive", description = "The default time to live for cache entries in milliseconds")
    public long getDefaultTimeToLiveMs() {
        return getDefaultTimeToLive(TimeUnit.MILLISECONDS);
    }

    /**
     * @see org.coconut.cache.service.expiration.CacheExpirationMXBean#setDefaultTimeout(long)
     */
    public void setDefaultTimeToLiveMs(long timeToLiveMs) {
        setDefaultTimeToLive(timeToLiveMs, TimeUnit.MILLISECONDS);
    }

    public abstract boolean isExpired(CacheEntry<K, V> entry);

    public abstract long getExpirationTime(K key, V value, AttributeMap attributes);

}
