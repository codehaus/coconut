/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy;

import java.io.Serializable;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.common.CostAttribute;
import org.coconut.attribute.common.SizeAttribute;
import org.coconut.operations.LongPredicates;
import org.coconut.operations.Ops.Predicate;

/**
 * Various implementations of {@link IsCacheable}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class IsCacheables {

    /** An IsCacheable that accepts all elements. */
    public static final IsCacheable ACCEPT_ALL = new AcceptAll();

    /** An IsCacheable that rejects all elements. */
    public static final IsCacheable REJECT_ALL = new RejectAll();

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private IsCacheables() {}

    // /CLOVER:ON

    /**
     * Returns an {@link IsCacheables} that accepts any argument.
     * 
     * @param <K>
     *            the type of keys that are being cached
     * @param <V>
     *            the type of values that are being cached
     * @return an {@link IsCacheables} that accepts any argument
     */
    public static final <K, V> IsCacheable<K, V> acceptAll() {
        return ACCEPT_ALL;
    }

    /**
     * Returns an {@link IsCacheables} that accepts any argument where the AttributeMap is
     * accepted by the specified predicate.
     * 
     * @param predicate
     *            the predicate to test the attribute map with
     * @return an {@link IsCacheables} that accepts any argument where the AttributeMap is
     *         accepted by the specified predicate
     * @param <K>
     *            the type of keys that are being cached
     * @param <V>
     *            the type of values that are being cached
     */
    public static final <K, V> IsCacheable<K, V> fromAttributeMapPredicate(
            Predicate<AttributeMap> predicate) {
        return new AttributeMapPredicate<K, V>(predicate);
    }

    /**
     * Returns an {@link IsCacheables} that accepts any argument where the key is accepted
     * by the specified predicate.
     * 
     * @param predicate
     *            the predicate to test the key with
     * @return an {@link IsCacheables} that accepts any argument where the key is accepted
     *         by the specified predicate
     * @param <K>
     *            the type of keys that are being cached
     * @param <V>
     *            the type of values that are being cached
     */
    public static final <K, V> IsCacheable<K, V> fromKeyPredicate(Predicate<? super K> predicate) {
        return new KeyPredicate<K, V>(predicate);
    }

    /**
     * Returns an {@link IsCacheables} that accepts any argument where the value is
     * accepted by the specified predicate.
     * 
     * @param predicate
     *            the predicate to test the value with
     * @return an {@link IsCacheables} that accepts any argument where the value is
     *         accepted by the specified predicate
     * @param <K>
     *            the type of keys that are being cached
     * @param <V>
     *            the type of values that are being cached
     */
    public static final <K, V> IsCacheable<K, V> fromValuePredicate(Predicate<? super V> predicate) {
        return new ValuePredicate<K, V>(predicate);
    }

    /**
     * This method return a replacement policy that decorates another replacement policy
     * rejecting any elements whose size is larger than the specified maximum size.
     * <p>
     * This can be used implement policies such the ones outlined in the ACM SIGCOMM '96
     * paper: <a href="http://ei.cs.vt.edu/~succeed/96sigcomm/">Removal Policies in
     * Network Caches for World-Wide Web Documents</a> by Stephen Williams et al.
     * However, this version is generalized for any replacement policies not just LRU.
     * 
     * @param maximumSize
     *            the maximum size of any element that will be accepted by the returned
     *            policy
     * @return a replacement policy that will reject any elements that are larger then the
     *         specified maximum size
     * @param <K>
     *            the type of keys that are being cached
     * @param <V>
     *            the type of values that are being cached
     */
    public static final <K, V> IsCacheable<K, V> maximumSize(long maximumSize) {
        SizeAttribute.INSTANCE.checkValid(maximumSize);
        return new AttributeMapPredicate<K, V>(LongPredicates.mapperPredicate(
                SizeAttribute.INSTANCE.mapToLong(), LongPredicates.lessThenOrEquals(maximumSize)));
    }

    /**
     * This method return a replacement policy that decorates another replacement policy
     * rejecting any elements whose retrievel cost is less then a specified minimum cost.
     * 
     * @param minimumCost
     *            the minimum cost of an element that will be accepted by the returned
     *            replacement policy
     * @return a replacement policy that will reject any elements that are larger then the
     *         specified maximum size
     * @param <K>
     *            the type of keys that are being cached
     * @param <V>
     *            the type of values that are being cached
     */
    public static final <K, V> IsCacheable<K, V> minimumCost(double minimumCost) {
        return new MinimumCost<K, V>(minimumCost);
    }

    /**
     * Returns an {@link IsCacheables} that rejects all arguments.
     * 
     * @param <K>
     *            the type of keys that are being cached
     * @param <V>
     *            the type of values that are being cached
     * @return an {@link IsCacheables} that accepts any argument
     */
    public static final <K, V> IsCacheable<K, V> rejectAll() {
        return REJECT_ALL;
    }

    /**
     * @param <K>
     *            the type of keys that are being cached
     * @param <V>
     *            the type of values that are being cached
     */
    static class AcceptAll<K, V> implements IsCacheable<K, V>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -7594333684624926263L;

        /** {@inheritDoc} */
        public boolean isCacheable(K key, V value, AttributeMap attributes) {
            return true;
        }
    }

    /**
     * @param <K>
     *            the type of keys that are being cached
     * @param <V>
     *            the type of values that are being cached
     */
    static class AttributeMapPredicate<K, V> implements IsCacheable<K, V>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 7985164051605463107L;

        /** The minimum cost of an element that will be accepted. */
        private final Predicate<AttributeMap> predicate;

        /**
         * Creates a new AttributeMapPredicate.
         * 
         * @param predicate
         *            the predicate to test the attribute map with
         */
        public AttributeMapPredicate(Predicate<AttributeMap> predicate) {
            if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
        }

        /** {@inheritDoc} */
        public boolean isCacheable(K key, V value, AttributeMap attributes) {
            return predicate.evaluate(attributes);
        }
    }

    /**
     * @param <K>
     *            the type of keys that are being cached
     * @param <V>
     *            the type of values that are being cached
     */
    static class KeyPredicate<K, V> implements IsCacheable<K, V>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 5046395272186446797L;

        /** The minimum cost of an element that will be accepted. */
        private final Predicate<? super K> predicate;

        /**
         * Creates a new KeyPredicate.
         * 
         * @param predicate
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
     * @param <K>
     *            the type of keys that are being cached
     * @param <V>
     *            the type of values that are being cached
     */
    static class MinimumCost<K, V> implements IsCacheable<K, V>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 7985164051605463107L;

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
            return minimumCost <= cost;
        }
    }

    /**
     * An {@link IsCacheable} implementation that rejects all.
     * 
     * @param <K>
     *            the type of keys that are being cached
     * @param <V>
     *            the type of values that are being cached
     */
    static class RejectAll<K, V> implements IsCacheable<K, V>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -3317413702565532922L;

        /** {@inheritDoc} */
        public boolean isCacheable(K key, V value, AttributeMap attributes) {
            return false;
        }
    }

    /**
     * @param <K>
     *            the type of keys that are being cached
     * @param <V>
     *            the type of values that are being cached
     */
    static class ValuePredicate<K, V> implements IsCacheable<K, V>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8315484330603656664L;

        /** The minimum cost of an element that will be accepted. */
        private final Predicate<? super V> predicate;

        /**
         * Creates a new KeyPredicate.
         * 
         * @param predicate
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
}
