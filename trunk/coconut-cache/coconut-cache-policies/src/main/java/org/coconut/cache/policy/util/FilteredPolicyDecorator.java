/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy.util;

import org.coconut.cache.policy.CostSizeObject;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.filter.Filter;

/**
 * This replacement policy decorator can be used to easily reject certain types
 * of elements.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class FilteredPolicyDecorator<T> extends PolicyDecorator<T> {

    /**
     * This replacement decorates another cache policy rejecting any documents
     * larger than a certain threshold size.
     * <p>
     * This policy is based on a ACM SIGCOMM '96 paper: <a
     * href="http://ei.cs.vt.edu/~succeed/96sigcomm/">Removal Policies in
     * Network Caches for World-Wide Web Documents</a> by Stephen Williams et
     * al. However, this version is generalized for any replacement policies not
     * just LRU.
     * <p>
     * If no cache policy is specified in the constructors a LRU replacement
     * policy is being wrapped (as in the original paper).
     */
    public static <T extends CostSizeObject> ReplacementPolicy<T> sizeRejector(
            ReplacementPolicy<T> policy, long size) {
        return new FilteredPolicyDecorator<T>(policy, new SizeFilter<T>(size));
    }

    public static <T extends CostSizeObject> ReplacementPolicy<T> costRejector(
            ReplacementPolicy<T> policy, double minimumCost) {
        return new FilteredPolicyDecorator<T>(policy, new CostFilter<T>(minimumCost));
    }

    private final Filter<T> filter;

    /**
     * @param policy
     */
    public FilteredPolicyDecorator(ReplacementPolicy<T> policy, Filter<T> filter) {
        super(policy);
        if (filter == null) {
            throw new NullPointerException("filter is null");
        }
        this.filter = filter;
    }

    /**
     * @see org.coconut.cache.policy.util.ReplacementPolicyDecorator#add(java.lang.Object)
     */
    @Override
    public int add(T data) {
        if (filter.accept(data)) {
            return super.add(data);
        } else {
            return -1;
        }
    }

    public Filter<T> getFilter() {
        return filter;
    }

    /**
     * @see org.coconut.cache.policy.util.ReplacementPolicyDecorator#update(int,
     *      java.lang.Object)
     */
    @Override
    public boolean update(int index, T newElement) {
        if (filter.accept(newElement)) {
            return super.update(index, newElement);
        } else {
            remove(index);
            return false;
        }
    }

    static class CostFilter<T extends CostSizeObject> implements Filter<T> {
        private final double minimumCost;

        /**
         * @param threshold
         */
        public CostFilter(final double minimumCost) {
            this.minimumCost = minimumCost;
        }

        /**
         * @see org.coconut.filter.Filter#accept(java.lang.Object)
         */
        public boolean accept(T element) {
            return element.getCost() >= minimumCost;
        }
    }

    static class SizeFilter<T extends CostSizeObject> implements Filter<T> {
        private final long threshold;

        /**
         * @param threshold
         */
        public SizeFilter(final long threshold) {
            if (threshold <= 0) {
                throw new IllegalArgumentException(
                        "threshold must be a positive number, was " + threshold);
            }
            this.threshold = threshold;
        }

        /**
         * @see org.coconut.filter.Filter#accept(java.lang.Object)
         */
        public boolean accept(T element) {
            return element.getSize() <= threshold;
        }
    }
}
