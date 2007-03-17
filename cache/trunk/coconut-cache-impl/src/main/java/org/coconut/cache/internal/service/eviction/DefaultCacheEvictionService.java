/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.eviction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.AbstractCacheService;
import org.coconut.cache.internal.service.InternalCacheServiceManager;
import org.coconut.cache.policy.Policies;
import org.coconut.cache.policy.ReplacementPolicy;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheEvictionService<T extends CacheEntry> extends
        AbstractCacheService {
    private final ReplacementPolicy<T> cp;

    private final int maxSize;

    private final int preferableSize;

    private final long maxCapacity;

    private final long preferableCapacity;

    public DefaultCacheEvictionService(InternalCacheServiceManager manager,
            CacheConfiguration<?, ?> conf) {
        super(manager, conf);
        if (conf.eviction().getPolicy() == null) {
            cp = Policies.newLRU();
        } else {
            cp = conf.eviction().getPolicy();
        }
        maxSize = conf.eviction().getMaximumSize();
        maxCapacity = conf.eviction().getMaximumCapacity();
        preferableCapacity = conf.eviction().getPreferableCapacity();
        preferableSize = conf.eviction().getPreferableSize();
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
        if (index < 0) {
            throw new IllegalArgumentException(
                    "index must be a non negative number, was " + index);
        }
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

    public List<T> evict(int count) {
        ArrayList<T> list = new ArrayList<T>();
        while (count-- > 0) {
            T e = evictNext();
            list.add(e);
        }
        return list;
    }

    public List<T> evict(int size, long capacity) {
        ArrayList<T> list = new ArrayList<T>();
        int diffSize = size - maxSize;
        long diffCapacity = capacity - maxCapacity;
        while (diffSize-- > 0 || diffCapacity > 0) {
            T e = evictNext();
            list.add(e);
            diffCapacity -= e.getSize();
        }
        return list;
    }

    public boolean isCapacityBreached(long capacity) {
        return capacity > maxCapacity;
    }

    public boolean isSizeBreached(int size) {
        return size > maxSize;
    }

    public int add(T t) {
        if (maxCapacity == 0) {
            return -1;
        }
        return cp.add(t);
    }

    /**
     * 
     */
    public void clear() {
        cp.clear();
    }
}
