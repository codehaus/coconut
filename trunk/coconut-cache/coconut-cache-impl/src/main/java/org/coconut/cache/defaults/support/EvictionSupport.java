/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.policy.ReplacementPolicy;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EvictionSupport<T extends CacheEntry> {
    private final ReplacementPolicy<T> cp;

    private final int maxSize;

    private final int preferableSize;

    private final long maxCapacity;

    private final long preferableCapacity;

    public EvictionSupport(CacheConfiguration<?, ?> conf) {
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

    public boolean isEnabled() {
        return cp != null;
    }

    public List<T> evict(int size, long capacity) {
        int diffSize = maxSize - size;
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
        long diffCapacity = maxCapacity - capacity;
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
        return cp.add(t);
    }
}
