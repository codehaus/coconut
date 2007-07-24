/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import org.coconut.cache.Cache;
import org.coconut.cache.internal.service.CacheHelper;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SynchronizedCacheEvictionService<K,V> extends UnsynchronizedCacheEvictionService {
    private final Cache mutex;

    public SynchronizedCacheEvictionService(Cache c, CacheEvictionConfiguration conf,CacheHelper<K, V> helper) {
        super(conf,helper);
        this.mutex = c;
    }

    /** {@inheritDoc} */
    @Override
    public long getMaximumCapacity() {
        synchronized (mutex) {
            return super.getMaximumCapacity();
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getMaximumSize() {
        synchronized (mutex) {
            return super.getMaximumSize();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setMaximumCapacity(long size) {
        synchronized (mutex) {
            super.setMaximumCapacity(size);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setMaximumSize(int size) {
        synchronized (mutex) {
            super.setMaximumSize(size);
        }
    }

    /** {@inheritDoc} */
    @Override
    public long getPreferableCapacity() {
        synchronized (mutex) {
            return super.getPreferableCapacity();
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getPreferableSize() {
        synchronized (mutex) {
            return super.getPreferableSize();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setPreferableCapacity(long size) {
        synchronized (mutex) {
            super.setPreferableCapacity(size);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setPreferableSize(int size) {
        synchronized (mutex) {
            super.setPreferableSize(size);
        }
    }
}
