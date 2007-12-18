/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import java.io.Serializable;
import java.util.Comparator;

import org.coconut.operations.Ops.DoubleComparator;
import org.coconut.operations.Ops.DoubleReducer;
import org.coconut.operations.Ops.IntComparator;
import org.coconut.operations.Ops.IntReducer;
import org.coconut.operations.Ops.LongComparator;
import org.coconut.operations.Ops.LongReducer;
import org.coconut.operations.Ops.Reducer;

public class Reducers {

    public static final DoubleReducer DOUBLE_ADDER_REDUCER = new DoubleAdder();

    public static final DoubleReducer DOUBLE_MAX_REDUCER = new NaturalDoubleMaxReducer();

    public static final DoubleReducer DOUBLE_MIN_REDUCER = new NaturalDoubleMinReducer();

    public static final IntReducer INT_ADDER_REDUCER = new IntAdder();

    public static final IntReducer INT_MAX_REDUCER = new NaturalIntMaxReducer();

    public static final IntReducer INT_MIN_REDUCER = new NaturalIntMinReducer();

    public static final LongReducer LONG_ADDER_REDUCER = new LongAdder();

    public static final LongReducer LONG_MAX_REDUCER = new NaturalLongMaxReducer();

    public static final LongReducer LONG_MIN_REDUCER = new NaturalLongMinReducer();

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Reducers() {}

    // /CLOVER:ON
    /**
     * A reducer returning the maximum of two double elements, using the given comparator
     */
    public static final class DoubleMaxReducer implements DoubleReducer {
        final DoubleComparator comparator;

        /**
         * Creates a DoubleMaxReducer using the given comparator
         */
        public DoubleMaxReducer(DoubleComparator comparator) {
            this.comparator = comparator;
        }

        public double combine(double a, double b) {
            return (comparator.compare(a, b) >= 0) ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two double elements, using the given comparator
     */
    public static final class DoubleMinReducer implements DoubleReducer {
        final DoubleComparator comparator;

        /**
         * Creates a DoubleMinReducer using the given comparator
         */
        public DoubleMinReducer(DoubleComparator comparator) {
            this.comparator = comparator;
        }

        public double combine(double a, double b) {
            return (comparator.compare(a, b) <= 0) ? a : b;
        }
    }

    /**
     * A reducer returning the maximum of two int elements, using the given comparator
     */
    public static final class IntMaxReducer implements IntReducer {
        final IntComparator comparator;

        /**
         * Creates a IntMaxReducer using the given comparator
         */
        public IntMaxReducer(IntComparator comparator) {
            this.comparator = comparator;
        }

        public int combine(int a, int b) {
            return (comparator.compare(a, b) >= 0) ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two int elements, using the given comparator
     */
    public static final class IntMinReducer implements IntReducer {
        final IntComparator comparator;

        /**
         * Creates a IntMinReducer using the given comparator
         */
        public IntMinReducer(IntComparator comparator) {
            this.comparator = comparator;
        }

        public int combine(int a, int b) {
            return (comparator.compare(a, b) <= 0) ? a : b;
        }
    }

    /**
     * A reducer returning the maximum of two long elements, using the given comparator
     */
    public static final class LongMaxReducer implements LongReducer {
        final LongComparator comparator;

        /**
         * Creates a LongMaxReducer using the given comparator
         */
        public LongMaxReducer(LongComparator comparator) {
            this.comparator = comparator;
        }

        public long combine(long a, long b) {
            return (comparator.compare(a, b) >= 0) ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two long elements, using the given comparator
     */
    public static final class LongMinReducer implements LongReducer {
        final LongComparator comparator;

        /**
         * Creates a LongMinReducer using the given comparator
         */
        public LongMinReducer(LongComparator comparator) {
            this.comparator = comparator;
        }

        public long combine(long a, long b) {
            return (comparator.compare(a, b) <= 0) ? a : b;
        }
    }

    /**
     * A reducer returning the maximum of two elements, using the given comparator, and
     * treating null as less than any non-null element.
     */
    public static final class MaxReducer<T> implements Reducer<T> {
        private final Comparator<? super T> comparator;

        public MaxReducer(Comparator<? super T> comparator) {
            this.comparator = comparator;
        }

        public T combine(T a, T b) {
            return (a != null && (b == null || comparator.compare(a, b) >= 0)) ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two elements, using the given comparator, and
     * treating null as greater than any non-null element.
     */
    public static final class MinReducer<T> implements Reducer<T> {
        private final Comparator<? super T> comparator;

        public MinReducer(Comparator<? super T> comparator) {
            this.comparator = comparator;
        }

        public T combine(T a, T b) {
            return (a != null && (b == null || comparator.compare(a, b) <= 0)) ? a : b;
        }
    }

    /**
     * A reducer returning the maximum of two Comparable elements, treating null as less
     * than any non-null element.
     */
    public static final class NaturalMaxReducer<T extends Comparable<? super T>> implements
            Reducer<T> {
        /**
         * Creates a NaturalMaxReducer for the given element type
         * 
         * @param type
         *            the type
         */
        NaturalMaxReducer(Class<T> type) {}

        public T combine(T a, T b) {
            return (a != null && (b == null || a.compareTo(b) >= 0)) ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two Comparable elements, treating null as less
     * than any non-null element.
     */
    public static final class NaturalMinReducer<T extends Comparable<? super T>> implements
            Reducer<T> {
        /**
         * Creates a NaturalMinReducer for the given element type
         * 
         * @param type
         *            the type
         */
        NaturalMinReducer(Class<T> type) {}

        public T combine(T a, T b) {
            return (a != null && (b == null || a.compareTo(b) <= 0)) ? a : b;
        }
    }

    /**
     * A reducer that adds two double elements
     */
    static final class DoubleAdder implements DoubleReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -5291652398158425686L;

        /** {@inheritDoc} */
        public double combine(double a, double b) {
            return a + b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return DOUBLE_ADDER_REDUCER;
        }
    }

    /**
     * A reducer that adds two int elements
     */
    static final class IntAdder implements IntReducer, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -8988998991050744720L;

        /** {@inheritDoc} */
        public int combine(int a, int b) {
            return a + b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return INT_ADDER_REDUCER;
        }
    }

    /**
     * A reducer that adds two double elements
     */
    static final class LongAdder implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 3153032156748016348L;

        /** {@inheritDoc} */
        public long combine(long a, long b) {
            return a + b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return LONG_ADDER_REDUCER;
        }
    }

    /**
     * A reducer returning the maximum of two double elements, using natural comparator
     */
    static final class NaturalDoubleMaxReducer implements DoubleReducer, Serializable {

        /** NaturalDoubleMaxReducer */
        private static final long serialVersionUID = -5902864811727900806L;

        /** {@inheritDoc} */
        public double combine(double a, double b) {
            return Math.max(a, b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return DOUBLE_MAX_REDUCER;
        }
    }

    /**
     * A reducer returning the minimum of two double elements, using natural comparator
     */
    static final class NaturalDoubleMinReducer implements DoubleReducer, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 9005140841348156699L;

        /** {@inheritDoc} */
        public double combine(double a, double b) {
            return Math.min(a, b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return DOUBLE_MIN_REDUCER;
        }
    }

    /**
     * A reducer returning the maximum of two int elements, using natural comparator
     */
    static final class NaturalIntMaxReducer implements IntReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 9077782496369337326L;

        /** {@inheritDoc} */
        public int combine(int a, int b) {
            return a >= b ? a : b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return INT_MAX_REDUCER;
        }
    }

    /**
     * A reducer returning the minimum of two int elements, using natural comparator
     */
    static final class NaturalIntMinReducer implements IntReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -6976117010912842877L;

        /** {@inheritDoc} */
        public int combine(int a, int b) {
            return a <= b ? a : b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return INT_MIN_REDUCER;
        }
    }

    /**
     * A reducer returning the maximum of two long elements, using natural comparator
     */
    static final class NaturalLongMaxReducer implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6933457108004180230L;

        /** {@inheritDoc} */
        public long combine(long a, long b) {
            return a >= b ? a : b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return LONG_MAX_REDUCER;
        }
    }

    /**
     * A reducer returning the minimum of two long elements, using natural comparator
     */
    static final class NaturalLongMinReducer implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -7592565629449709106L;

        /** {@inheritDoc} */
        public long combine(long a, long b) {
            return a <= b ? a : b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return LONG_MIN_REDUCER;
        }
    }
}
