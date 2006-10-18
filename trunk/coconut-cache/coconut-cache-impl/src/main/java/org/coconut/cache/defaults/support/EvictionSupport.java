/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults.support;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.policy.ReplacementPolicy;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EvictionSupport<T> {
    private final ReplacementPolicy<T> cp;

    private final int maxCapacity;

    public EvictionSupport(CacheConfiguration<?, ?> conf) {
        cp = conf.eviction().getPolicy();
        maxCapacity = conf.eviction().getMaximumCapacity();
        if (maxCapacity != Integer.MAX_VALUE && cp == null) {
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

    public boolean isCapacityReached(int size) {
        return size >= maxCapacity;
    }

    public int add(T t) {
        return cp.add(t);
    }
}
