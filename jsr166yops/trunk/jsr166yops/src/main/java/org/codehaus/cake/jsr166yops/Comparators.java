/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.jsr166yops;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;

import jsr166y.forkjoin.Ops.DoubleComparator;
import jsr166y.forkjoin.Ops.IntComparator;
import jsr166y.forkjoin.Ops.LongComparator;
import jsr166y.forkjoin.Ops.Op;

/**
 * Various implementations of {@link Comparator}, {@link LongComparator},
 * {@link DoubleComparator} and {@link IntComparator}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class Comparators {

    /**
     * A comparator for doubles relying on natural ordering. The comparator is
     * Serializable.
     */
    public static final DoubleComparator DOUBLE_COMPARATOR = new NaturalDoubleComparator();

    /**
     * A comparator that imposes the reverse of the <i>natural ordering</i> on doubles.
     * The comparator is Serializable.
     */
    public static final DoubleComparator DOUBLE_REVERSE_COMPARATOR = new NaturalDoubleReverseComparator();

    /**
     * A comparator for ints relying on natural ordering. The comparator is Serializable.
     */
    public static final IntComparator INT_COMPARATOR = new NaturalIntComparator();

    /**
     * A comparator that imposes the reverse of the <i>natural ordering</i> on ints. The
     * comparator is Serializable.
     */
    public static final IntComparator INT_REVERSE_COMPARATOR = new NaturalIntReverseComparator();

    /**
     * A comparator for longs relying on natural ordering. The comparator is Serializable.
     */
    public static final LongComparator LONG_COMPARATOR = new NaturalLongComparator();

    /**
     * A comparator that imposes the reverse of the <i>natural ordering</i> on longs. The
     * comparator is Serializable.
     */
    public static final LongComparator LONG_REVERSE_COMPARATOR = new NaturalLongReverseComparator();

    /**
     * A Comparator for Comparable.objects using their <i>natural ordering</i>. The
     * comparator is Serializable.
     */
    public static final Comparator NATURAL_COMPARATOR = new NaturalComparator();

    /**
     * A comparator that imposes the reverse of the <i>natural ordering</i>. This
     * comparator is Serializable.
     */
    public static final Comparator NATURAL_REVERSE_COMPARATOR = Collections.reverseOrder();

    public static final Comparator NULL_GREATEST_ORDER = new NullGreatestOrderPredicate();

    /**
     * A Comparator for Comparable.objects. The comparator is Serializable.
     */
    public static final Comparator NULL_LEAST_ORDER = new NullLeastOrderPredicate();

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Comparators() {}

    // /CLOVER:ON

    public static <T, U extends Comparable<? super U>> Comparator<T> mappedComparator(
            Op<? super T, U> mapper) {
        return mappedComparator(mapper, NATURAL_COMPARATOR);
    }

    public static <T, U> Comparator<T> mappedComparator(Op<? super T, U> mapper,
            Comparator<? super U> comparator) {
        return new MappedComparator<T, U>(comparator, mapper);
    }

    /**
     * Returns a Comparator that use the objects natural comparator. The returned
     * comparator is serializable.
     * <p>
     * This example illustrates the type-safe way to obtain a natural comparator:
     *
     * <pre>
     * Comparator&lt;String&gt; s = Comparators.naturalComparator();
     * </pre>
     *
     * Implementation note: Implementations of this method need not create a separate
     * <tt>comparator</tt> object for each call. Using this method is likely to have
     * comparable cost to using the like-named field. (Unlike this method, the field does
     * not provide type safety.)
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
     * Returns a comparator that imposes the reverse of the <i>natural ordering</i> on a
     * collection of objects that implement the <tt>Comparable</tt> interface. (The
     * natural ordering is the ordering imposed by the objects' own <tt>compareTo</tt>
     * method.) This enables a simple idiom for sorting (or maintaining) collections (or
     * arrays) of objects that implement the <tt>Comparable</tt> interface in
     * reverse-natural-order.
     * <p>
     * The returned comparator is serializable.
     *
     * @return a comparator that imposes the reverse of the <i>natural ordering</i> on a
     *         collection of objects that implement the <tt>Comparable</tt> interface.
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

    /**
     * Creates a comparator that imposes the reverse ordering of the specified comparator.
     * <p>
     * The returned comparator is serializable (assuming the specified comparator is also
     * serializable).
     *
     * @param comparator
     *            the DoubleComparator to reverse
     * @return a comparator that imposes the reverse ordering of the specified comparator.
     */
    public static DoubleComparator reverseOrder(DoubleComparator comparator) {
        return new ReverseDoubleComparator(comparator);
    }

    /**
     * Creates a comparator that imposes the reverse ordering of the specified comparator.
     * <p>
     * The returned comparator is serializable (assuming the specified comparator is also
     * serializable).
     *
     * @param comparator
     *            the IntComparator to reverse
     * @return a comparator that imposes the reverse ordering of the specified comparator.
     */
    public static IntComparator reverseOrder(IntComparator comparator) {
        return new ReverseIntComparator(comparator);
    }

    /**
     * Creates a comparator that imposes the reverse ordering of the specified comparator.
     * <p>
     * The returned comparator is serializable (assuming the specified comparator is also
     * serializable).
     *
     * @param comparator
     *            the LongComparator to reverse
     * @return a comparator that imposes the reverse ordering of the specified comparator.
     */
    public static LongComparator reverseOrder(LongComparator comparator) {
        return new ReverseLongComparator(comparator);
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

    /** A comparator for doubles relying on natural ordering. */
    static final class NaturalDoubleComparator implements DoubleComparator, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 8763765406476535022L;

        /** {@inheritDoc} */
        public int compare(double a, double b) {
            return Double.compare(a, b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return DOUBLE_COMPARATOR;
        }
    }

    /** A comparator for doubles relying on natural ordering. */
    static final class NaturalDoubleReverseComparator implements DoubleComparator, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -7289505884757339069L;

        /** {@inheritDoc} */
        public int compare(double a, double b) {
            return -Double.compare(a, b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return DOUBLE_REVERSE_COMPARATOR;
        }
    }

    /** A comparator for ints relying on natural ordering. */
    static final class NaturalIntComparator implements IntComparator, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -937141628195281323L;

        /** {@inheritDoc} */
        public int compare(int a, int b) {
            return a < b ? -1 : a > b ? 1 : 0;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return INT_COMPARATOR;
        }
    }

    /** A comparator for ints relying on natural ordering. */
    static final class NaturalIntReverseComparator implements IntComparator, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -94614177221491910L;

        /** {@inheritDoc} */
        public int compare(int a, int b) {
            return a < b ? 1 : a > b ? -1 : 0;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return INT_REVERSE_COMPARATOR;
        }
    }

    /** A comparator for longs relying on natural ordering. */
    static final class NaturalLongComparator implements LongComparator, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 948562823440773841L;

        /** {@inheritDoc} */
        public int compare(long a, long b) {
            return a < b ? -1 : a > b ? 1 : 0;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return LONG_COMPARATOR;
        }
    }

    /** A comparator for longs relying on natural ordering. */
    static final class NaturalLongReverseComparator implements LongComparator, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -5519000744406211184L;

        /** {@inheritDoc} */
        public int compare(long a, long b) {
            return a < b ? 1 : a > b ? -1 : 0;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return LONG_REVERSE_COMPARATOR;
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

    /** A comparator that reserves the result of another DoubleComparator. */
    static final class ReverseDoubleComparator implements DoubleComparator, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 1585665469031127321L;

        /** The DoubleComparator to reverse. */
        private final DoubleComparator comparator;

        /**
         * Creates a new ReverseDoubleComparator.
         *
         * @param comparator
         *            the comparator to reverse
         */
        ReverseDoubleComparator(DoubleComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public int compare(double a, double b) {
            return -comparator.compare(a, b);
        }
    }

    /** A comparator that reserves the result of another LongComparator. */
    static final class ReverseIntComparator implements IntComparator, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 4013898636057466724L;

        /** The IntComparator to reverse. */
        private final IntComparator comparator;

        /**
         * Creates a new ReverseIntComparator.
         *
         * @param comparator
         *            the comparator to reverse
         */
        ReverseIntComparator(IntComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public int compare(int a, int b) {
            return -comparator.compare(a, b);
        }
    }

    /** A comparator that reserves the result of another LongComparator. */
    static final class ReverseLongComparator implements LongComparator, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -3870535703169514045L;

        /** The LongComparator to reverse. */
        private final LongComparator comparator;

        /**
         * Creates a new ReverseLongComparator.
         *
         * @param comparator
         *            the comparator to reverse
         */
        ReverseLongComparator(LongComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public int compare(long a, long b) {
            return -comparator.compare(a, b);
        }
    }
}
