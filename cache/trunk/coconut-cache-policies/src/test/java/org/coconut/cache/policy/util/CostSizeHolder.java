/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.util;

import org.coconut.cache.policy.PolicyObject;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CostSizeHolder<T> implements PolicyObject {
    private final double cost;

    private final long size;

    private final T data;

    private final long hits;

    public double getCost() {
        return cost;
    }

    public T getData() {
        return data;
    }

    public long getSize() {
        return size;
    }

    public CostSizeHolder(final T data, final long size, final double cost) {
        this.cost = cost;
        this.size = size;
        this.data = data;
        this.hits = CostSizeHolder.DEFAULT_HITS;
    }

    public static <T> CostSizeHolder add(T data, long size, double cost) {
        return new CostSizeHolder<T>(data, size, cost);
    }

    public static <T> CostSizeHolder add(T data) {
        return new CostSizeHolder<T>(data, PolicyObject.DEFAULT_SIZE,
                PolicyObject.DEFAULT_COST);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof CostSizeHolder) && equalsObject((CostSizeHolder) obj);
    }

    public boolean equalsObject(CostSizeHolder<T> obj) {
        return cost == obj.cost && size == obj.size && data.equals(obj.data);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * @see org.coconut.cache.policy.CostSizeObject#getHits()
     */
    public long getHits() {
        return hits;
    }
}
