/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy.util;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.core.AttributeMap;

/**
 * This replacement policy decorator can be used to easily reject certain types of
 * elements.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <T>
 *            the type of data maintained by the policy
 */
public class FilteredPolicyDecorator<T> extends PolicyDecorator<T> {

    /** The policy filter we are decorating. */
    private final AttributedFilter<T> filter;

    /**
     * Creates a new FilteredPolicyDecorator from the specified replacement policy and
     * policy filter.
     * 
     * @param policy
     *            the replacement to decorate
     * @param filter
     *            the policy filter that decides if the specified element should be
     *            accepted
     * @throws NullPointerException
     *             if the specified policy or filter is <code>null</code>
     */
    public FilteredPolicyDecorator(ReplacementPolicy<T> policy, AttributedFilter<T> filter) {
        super(policy);
        if (filter == null) {
            throw new NullPointerException("filter is null");
        }
        this.filter = filter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int add(T data, AttributeMap attributes) {
        if (filter.accept(data, attributes)) {
            return super.add(data);
        } else {
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(int index, T newElement, AttributeMap attributes) {
        if (filter.accept(newElement, attributes)) {
            return super.update(index, newElement);
        } else {
            remove(index);
            return false;
        }
    }

    /**
     * Returns the attributed filter that is being used.
     * 
     * @return the attributed filter that is being used
     */
    protected AttributedFilter<T> getFilter() {
        return filter;
    }

    /**
     * @param <T>
     *            the type of elements accepted by this filter
     */
    static class MinimumCostFilter<T> implements AttributedFilter<T> {
        /** The minimum cost of an element that will be accepted. */
        private final double minimumCost;

        /**
         * Creates a new MinimumCostFilter.
         * 
         * @param minimumCost
         *            the minimum cost of an element that will be accepted by this filter
         */
        public MinimumCostFilter(final double minimumCost) {
            this.minimumCost = minimumCost;
        }

        /**
         * {@inheritDoc}
         */
        public boolean accept(T t, AttributeMap attributes) {
            double cost = CacheAttributes.getCost(attributes);
            return cost >= minimumCost;
        }
    }

    /**
     * @param <T>
     *            the type of elements accepted by this filter
     */
    static class MaximumSizeFilter<T> implements AttributedFilter<T> {
        /** The maximum size of an element that will be accepted. */
        private final long threshold;

        /**
         * Creates a new MaximumSizeFilter.
         * 
         * @param maximumSize
         *            the maximum size of an element that will be accepted by this filter
         */
        public MaximumSizeFilter(final long maximumSize) {
            if (maximumSize <= 0) {
                throw new IllegalArgumentException(
                        "threshold must be a positive number, was " + maximumSize);
            }
            this.threshold = maximumSize;
        }

        /**
         * {@inheritDoc}
         */
        public boolean accept(T t, AttributeMap attributes) {
            long size = CacheAttributes.getSize(attributes);
            return size <= threshold;
        }
    }

    /**
     * This method return a replacement policy that decorates another replacement policy
     * rejecting any elements whose retrievel cost is less then a specified minimum cost.
     * 
     * @param policy
     *            the replacement policy to wrap
     * @param minimumCost
     *            the minimum cost of an element that will be accepted by the returned
     *            replacement policy
     * @return a replacement policy that will reject any elements that are larger then the
     *         specified maximum size
     * @param <T>
     *            the type of elements accepted by the replacement policy
     */
    public static <T> ReplacementPolicy<T> costRejector(ReplacementPolicy<T> policy,
            double minimumCost) {
        return new FilteredPolicyDecorator<T>(policy, new MinimumCostFilter(minimumCost));
    }

    /**
     * This method return a replacement policy that decorates another replacement policy
     * rejecting any elements whose size is larger than the specified maximum size.
     * <p>
     * This policy is based on a ACM SIGCOMM '96 paper: <a
     * href="http://ei.cs.vt.edu/~succeed/96sigcomm/">Removal Policies in Network Caches
     * for World-Wide Web Documents</a> by Stephen Williams et al. However, this version
     * is generalized for any replacement policies not just LRU.
     * 
     * @param policy
     *            the replacement policy to wrap
     * @param maximumSize
     *            the maximum size of any element that will be accepted by the returned
     *            policy
     * @return a replacement policy that will reject any elements that are larger then the
     *         specified maximum size
     * @param <T>
     *            the type of elements accepted by the replacement policy
     */
    public static <T> ReplacementPolicy<T> sizeRejector(ReplacementPolicy<T> policy,
            long maximumSize) {
        return new FilteredPolicyDecorator<T>(policy, new MaximumSizeFilter<T>(
                maximumSize));
    }
}
