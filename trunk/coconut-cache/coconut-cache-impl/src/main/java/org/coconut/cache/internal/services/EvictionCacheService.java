/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.spi.service.AbstractCacheService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EvictionCacheService<T extends CacheEntry> extends AbstractCacheService {
    private final ReplacementPolicy<T> cp;

    private final int maxSize;

    private final int preferableSize;

    private final long maxCapacity;

    private final long preferableCapacity;

    public EvictionCacheService(CacheConfiguration<?, ?> conf) {
        super(conf);
        cp = conf.eviction().getPolicy();
        maxSize = conf.eviction().getMaximumSize();
        maxCapacity = conf.eviction().getMaximumCapacity();
        preferableCapacity = conf.eviction().getPreferableCapacity();
        preferableSize = conf.eviction().getPreferableSize();
        if (maxSize != Integer.MAX_VALUE && cp == null) {
            throw new IllegalArgumentException("Must define a cache policy");
        }
    }

    public T evictNext() {
        return cp.evictNext();
    }

    public void remove(int index) {
        if (cp != null) {
            cp.remove(index);
        }
    }

    public void touch(int index) {
        if (cp != null) {
            cp.touch(index);
        }
    }
    
    public boolean replace(int index, T t) {
        return cp.update(index, t);
    }
    public boolean isEnabled() {
        return cp != null;
    }

    public List<T> evict(int size, long capacity) {
        int diffSize = size - maxSize;
        if (diffSize > 0) {
            if (diffSize == 1) {
                return Collections.singletonList(evictNext());
            } else {
                ArrayList<T> list = new ArrayList<T>(diffSize);
                for (int i = 0; i < diffSize; i++) {
                    list.add(evictNext());
                }
                return list;
            }
        }
        long diffCapacity = capacity - maxCapacity;
        
        if (diffCapacity > 0) {
            ArrayList list = new ArrayList();
            while (diffCapacity > 0) {
                CacheEntry e = evictNext();
                diffCapacity -= e.getSize();
                list.add(e);
            }
            return list;
        }
        return Collections.emptyList();
    }

    public boolean maxSizeReached(int size) {
        int maxSize = this.maxSize;
        return size >= maxSize;
    }

    public boolean maxCapacityReached(long capacity) {
        return maxCapacity >= capacity;
    }

    public boolean isCapacityReached(int size) {
        return size >= maxSize;
    }

    public int add(T t) {
        if (maxCapacity == 0) {
            return -1;
        }
        return cp.add(t);
    }
}
