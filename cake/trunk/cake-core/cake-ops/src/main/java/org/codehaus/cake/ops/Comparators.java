/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.ops;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;

import org.codehaus.cake.ops.Ops.DoubleComparator;
import org.codehaus.cake.ops.Ops.IntComparator;
import org.codehaus.cake.ops.Ops.LongComparator;
import org.codehaus.cake.ops.Ops.ObjectToLong;
import org.codehaus.cake.ops.Ops.Op;

/**
 * Various implementations of {@link Comparator}, {@link LongComparator}, {@link DoubleComparator}
 * and {@link IntComparator}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Comparators.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class Comparators {

    /**
     * A Comparator for Comparable.objects using their <i>natural ordering</i>. The comparator is
     * Serializable.
     */
    public static final Comparator NATURAL_COMPARATOR = new NaturalComparator();

    /**
     * A comparator that imposes the reverse of the <i>natural ordering</i>. This comparator is
     * Serializable.
     */
    public static final Comparator NATURAL_REVERSE_COMPARATOR = Collections.reverseOrder();

    public static final Comparator NULL_GREATEST_ORDER = new NullGreatestOrderPredicate();

    /**
     * A Comparator for Comparable.objects. The comparator is Serializable.
     */
    public static final Comparator NULL_LEAST_ORDER = new NullLeastOrderPredicate();

    /** Cannot instantiate. */
    private Comparators() {}

    public static <T> Comparator<T> mappedComparator(ObjectToLong<? super T> mapper) {
        throw new UnsupportedOperationException();
    }

    public static <T> Comparator<T> mappedComparator(ObjectToLong<? super T> mapper,
            LongComparator comparator) {
        throw new UnsupportedOperationException();
    }

    public static <T, U extends Comparable<? super U>> Comparator<T> mappedComparator(
            Op<? super T, U> mapper) {
        return mappedComparator(mapper, NATURAL_COMPARATOR);
    }

    public static <T, U> Comparator<T> mappedComparator(Op<? super T, U> mapper,
            Comparator<? super U> comparator) {
        return new MappedComparator<T, U>(comparator, mapper);
    }

    /**
     * Returns a Comparator that use the objects natural comparator. The returned comparator is
     * serializable.
     * <p>
     * This example illustrates the type-safe way to obtain a natural comparator:
     * 
     * <pre>
     * Comparator&lt;String&gt; s = Comparators.naturalComparator();
     * </pre>
     * 
     * Implementation note: Implementations of this method need not create a separate
     * <tt>comparator</tt> object for each call. Using this method is likely to have comparable
     * cost to using the like-named field. (Unlike this method, the field does not provide type
     * safety.)
     * 
     * @return a comparator for Comparable.objects
     * @param <T>
     *            the type of elements accepted by the comparator
     */
    public static <T extends Comparable> Comparator<T> naturalComparator() {
        return NATURAL_COMPARATOR;
    }

    public static <T extends Comparable> Comparator<T> nullGreatestOrder() {
        return NULL_GREATEST_ORDER;
    }

    public static <T extends Comparable> Comparator<T> nullGreatestOrder(Comparator<T> comparator) {
        return new NullGreatestOrderComparatorPredicate<T>(comparator);
    }

    public static <T extends Comparable> Comparator<T> nullLeastOrder() {
        return NULL_LEAST_ORDER;
    }

    public static <T extends Comparable> Comparator<T> nullLeastOrder(Comparator<T> comparator) {
        return new NullLeastOrderComparatorPredicate<T>(comparator);
    }

    /**
     * Returns a comparator that imposes the reverse of the <i>natural ordering</i> on a collection
     * of objects that implement the <tt>Comparable</tt> interface. (The natural ordering is the
     * ordering imposed by the objects' own <tt>compareTo</tt> method.) This enables a simple
     * idiom for sorting (or maintaining) collections (or arrays) of objects that implement the
     * <tt>Comparable</tt> interface in reverse-natural-order.
     * <p>
     * The returned comparator is serializable.
     * 
     * @return a comparator that imposes the reverse of the <i>natural ordering</i> on a collection
     *         of objects that implement the <tt>Comparable</tt> interface.
     * @param <T>
     *            the Comparable types accepted by the Comparator
     * @see Comparable
     */
    public static <T extends Comparable> Comparator<T> reverseOrder() {
        return NATURAL_REVERSE_COMPARATOR;
    }

    /**
     * Creates a comparator that imposes the reverse ordering of the specified comparator.
     * <p>
     * The returned comparator is serializable (assuming the specified comparator is also
     * serializable).
     * 
     * @param comparator
     *            the Comparator to reverse
     * @return a comparator that imposes the reverse ordering of the specified comparator.
     * @param <T>
     *            the Comparable types accepted by the Comparator
     */
    public static <T> Comparator<T> reverseOrder(Comparator<T> comparator) {
        if (comparator == null) {
            throw new NullPointerException("comparator is null");
        }
        return Collections.reverseOrder(comparator);
    }

    /** A Comparator for Comparable.objects. */
    static final class MappedComparator<T, U> implements Comparator<T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -5405101414861263699L;

        private final Comparator<? super U> comparator;

        private final Op<? super T, U> mapper;

        MappedComparator(Comparator<? super U> comparator, Op<? super T, U> mapper) {
            if (mapper == null) {
                throw new NullPointerException("mapper is null");
            } else if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.mapper = mapper;
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public int compare(T a, T b) {
            U ua = mapper.op(a);
            U ub = mapper.op(b);
            return comparator.compare(ua, ub);
        }
    }

    /** A Comparator for Comparable.objects. */
    static final class NaturalComparator<T extends Comparable<? super T>> implements Comparator<T>,
            Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 949691819933412722L;

        /** {@inheritDoc} */
        public int compare(T a, T b) {
            return a.compareTo(b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return NATURAL_COMPARATOR;
        }
    }

    static final class NullGreatestOrderComparatorPredicate<T> implements Comparator<T>,
            Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -5918068122775742745L;

        private final Comparator<T> comparator;

        NullGreatestOrderComparatorPredicate(Comparator<T> comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public int compare(T a, T b) {
            return a == null ? b == null ? 0 : 1 : b == null ? -1 : comparator.compare(a, b);
        }
    }

    static final class NullGreatestOrderPredicate<T extends Comparable<? super T>> implements
            Comparator<T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 4313874045537757310L;

        /** {@inheritDoc} */
        public int compare(T a, T b) {
            return a == null ? b == null ? 0 : 1 : b == null ? -1 : a.compareTo(b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return NULL_GREATEST_ORDER;
        }
    }

    static final class NullLeastOrderComparatorPredicate<T> implements Comparator<T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -5918068122775742745L;

        private final Comparator<T> comparator;

        NullLeastOrderComparatorPredicate(Comparator<T> comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public int compare(T a, T b) {
            return a == null ? b == null ? 0 : -1 : b == null ? 1 : comparator.compare(a, b);
        }
    }

    static final class NullLeastOrderPredicate<T extends Comparable<? super T>> implements
            Comparator<T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -5791305305191186665L;

        /** {@inheritDoc} */
        public int compare(T a, T b) {
            return a == null ? b == null ? 0 : -1 : b == null ? 1 : a.compareTo(b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return NULL_LEAST_ORDER;
        }
    }
}
