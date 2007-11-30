/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy;

import java.io.Serializable;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.common.CostAttribute;
import org.coconut.attribute.common.SizeAttribute;
import org.coconut.predicate.Predicate;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class IsCacheables {
    public static final IsCacheable REJECT_ALL = new RejectAll();

    public static final IsCacheable ACCEPT_ALL = new AcceptAll();

    public static final <K, V> IsCacheable<K, V> rejectAll() {
        return REJECT_ALL;
    }

    public static final <K, V> IsCacheable<K, V> acceptAll() {
        return ACCEPT_ALL;
    }
    public static final <K, V> IsCacheable<K, V> fromValuePredicate(Predicate<? super V> predicate) {
        return new ValuePredicate<K, V>(predicate);
    }
    public static final <K, V> IsCacheable<K, V> fromKeyPredicate(Predicate<? super K> predicate) {
        return new KeyPredicate<K, V>(predicate);
    }

    public static final <K, V> IsCacheable<K, V> minimumCost(double minimumCost) {
        return new MinimumCost<K, V>(minimumCost);
    }

    public static final <K, V> IsCacheable<K, V> maximumSize(long maximumSize) {
        return new MaximumSize<K, V>(maximumSize);
    }

    /**
     * @param <T>
     *            the type of elements accepted by this filter
     */
    static class RejectAll<K, V> implements IsCacheable<K, V>, Serializable {

        /** {@inheritDoc} */
        public boolean isCacheable(K key, V value, AttributeMap attributes) {
            return false;
        }
    }

    /**
     * @param <T>
     *            the type of elements accepted by this filter
     */
    static class AcceptAll<K, V> implements IsCacheable<K, V>, Serializable {
        /** {@inheritDoc} */
        public boolean isCacheable(K key, V value, AttributeMap attributes) {
            return true;
        }
    }

    /**
     * @param <T>
     *            the type of elements accepted by this filter
     */
    static class MaximumSize<K, V> implements IsCacheable<K, V>, Serializable {
        /** The maximum size of an element that will be accepted. */
        private final long threshold;

        /**
         * Creates a new MaximumSize.
         * 
         * @param maximumSize
         *            the maximum size of an element that will be accepted by this filter
         */
        public MaximumSize(final long maximumSize) {
            if (!SizeAttribute.INSTANCE.isValid(maximumSize)) {
                throw new IllegalArgumentException("threshold must be a positive number, was "
                        + maximumSize);
            }
            this.threshold = maximumSize;
        }

        public boolean isCacheable(K key, V value, AttributeMap attributes) {
            long size = SizeAttribute.get(attributes);
            return size <= threshold;
        }
    }
    /**
     * @param <T>
     *            the type of elements accepted by this filter
     */
    static class ValuePredicate<K, V> implements IsCacheable<K, V>, Serializable {
        /** The minimum cost of an element that will be accepted. */
        private final Predicate<? super V> predicate;

        /**
         * Creates a new KeyPredicate.
         * 
         * @param Predicate
         *            the key predicate
         */
        public ValuePredicate(final Predicate<? super V> predicate) {
            if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
        }

        /** {@inheritDoc} */
        public boolean isCacheable(K key, V value, AttributeMap attributes) {
            return predicate.evaluate(value);
        }
    }
    /**
     * @param <T>
     *            the type of elements accepted by this filter
     */
    static class KeyPredicate<K, V> implements IsCacheable<K, V>, Serializable {
        /** The minimum cost of an element that will be accepted. */
        private final Predicate<? super K> predicate;

        /**
         * Creates a new KeyPredicate.
         * 
         * @param Predicate
         *            the key predicate
         */
        public KeyPredicate(final Predicate<? super K> predicate) {
            if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
        }

        /** {@inheritDoc} */
        public boolean isCacheable(K key, V value, AttributeMap attributes) {
            return predicate.evaluate(key);
        }
    }

    /**
     * @param <T>
     *            the type of elements accepted by this filter
     */
    static class MinimumCost<K, V> implements IsCacheable<K, V>, Serializable {
        /** The minimum cost of an element that will be accepted. */
        private final double minimumCost;

        /**
         * Creates a new MinimumCost.
         * 
         * @param minimumCost
         *            the minimum cost of an element that will be accepted by this filter
         */
        public MinimumCost(final double minimumCost) {
            this.minimumCost = minimumCost;
        }

        /** {@inheritDoc} */
        public boolean isCacheable(K key, V value, AttributeMap attributes) {
            double cost = CostAttribute.get(attributes);
            return cost >= minimumCost;
        }
    }
}
