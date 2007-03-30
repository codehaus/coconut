/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import org.coconut.cache.Cache;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SynchronizedCacheEvictionService extends DefaultCacheEvictionService {
    private final Cache mutex;

    public SynchronizedCacheEvictionService(Cache c, CacheEvictionConfiguration conf) {
        super(conf);
        this.mutex = c;
    }

    /**
     * @see org.coconut.cache.internal.service.eviction.DefaultCacheEvictionService#getMaximumCapacity()
     */
    @Override
    public long getMaximumCapacity() {
        synchronized (mutex) {
            return super.getMaximumCapacity();
        }
    }

    /**
     * @see org.coconut.cache.internal.service.eviction.DefaultCacheEvictionService#getMaximumSize()
     */
    @Override
    public int getMaximumSize() {
        synchronized (mutex) {
            return super.getMaximumSize();
        }
    }

    /**
     * @see org.coconut.cache.internal.service.eviction.DefaultCacheEvictionService#setMaximumCapacity(long)
     */
    @Override
    public void setMaximumCapacity(long size) {
        synchronized (mutex) {
            super.setMaximumCapacity(size);
        }
    }

    /**
     * @see org.coconut.cache.internal.service.eviction.DefaultCacheEvictionService#setMaximumSize(int)
     */
    @Override
    public void setMaximumSize(int size) {
        synchronized (mutex) {
            super.setMaximumSize(size);
        }
    }

    /**
     * @see org.coconut.cache.internal.service.eviction.DefaultCacheEvictionService#getPreferableCapacity()
     */
    @Override
    public long getPreferableCapacity() {
        synchronized (mutex) {
            return super.getPreferableCapacity();
        }
    }

    /**
     * @see org.coconut.cache.internal.service.eviction.DefaultCacheEvictionService#getPreferableSize()
     */
    @Override
    public int getPreferableSize() {
        synchronized (mutex) {
            return super.getPreferableSize();
        }
    }

    /**
     * @see org.coconut.cache.internal.service.eviction.DefaultCacheEvictionService#setPreferableCapacity(long)
     */
    @Override
    public void setPreferableCapacity(long size) {
        synchronized (mutex) {
            super.setPreferableCapacity(size);
        }
    }

    /**
     * @see org.coconut.cache.internal.service.eviction.DefaultCacheEvictionService#setPreferableSize(int)
     */
    @Override
    public void setPreferableSize(int size) {
        synchronized (mutex) {
            super.setPreferableSize(size);
        }
    }
}
