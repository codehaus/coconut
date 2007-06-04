/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.service.AbstractInternalCacheService;
import org.coconut.cache.internal.spi.CacheHelper;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.eviction.CacheEvictionMXBean;
import org.coconut.cache.service.eviction.CacheEvictionService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractEvictionService<K, V, T extends CacheEntry<K, V>> extends
        AbstractInternalCacheService implements InternalCacheEvictionService<K, V, T>,
        CacheEvictionService<K, V>, CacheEvictionMXBean {
    private final CacheHelper<K, V> helper;

    /**
     * 
     */
    public AbstractEvictionService(CacheHelper<K, V> helper) {
        super(CacheEvictionConfiguration.SERVICE_NAME);
        this.helper = helper;
    }

    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionMXBean#getDefaultIdleTimeMs()
     */
    public long getDefaultIdleTimeMs() {
        return getDefaultIdleTime(TimeUnit.MILLISECONDS);
    }

    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionMXBean#setDefaultIdleTimeMs(long)
     */
    public void setDefaultIdleTimeMs(long idleTimeMs) {
        setDefaultIdleTime(idleTimeMs, TimeUnit.MILLISECONDS);
    }

    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionMXBean#trimToCapacity(long)
     */
    public void trimToCapacity(long capacity) {
        helper.trimToCapacity(capacity);
    }

    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionMXBean#trimToSize(int)
     */
    public void trimToSize(int size) {
        helper.trimToSize(size);
    }

    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionService#evict(java.lang.Object)
     */
    public void evict(Object key) {
        helper.evict(key);
    }

    /**
     * @see org.coconut.cache.service.eviction.CacheEvictionService#evictAll(java.util.Collection)
     */
    public void evictAll(Collection<? extends K> keys) {
        helper.evictAll(keys);
    }
}
