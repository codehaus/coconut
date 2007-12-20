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

/**
 * Various implementations of {@link Reducer}, {@link DoubleReducer}, {@link IntReducer}
 * and {@link LongReducer}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class Reducers {

    /** A reducer that adds two double elements. */
    public static final DoubleReducer DOUBLE_ADDER_REDUCER = new DoubleAdder();

    /** A reducer returning the maximum of two double elements, using natural comparator. */
    public static final DoubleReducer DOUBLE_MAX_REDUCER = new NaturalDoubleMaxReducer();

    /** A reducer returning the minimum of two double elements, using natural comparator. */
    public static final DoubleReducer DOUBLE_MIN_REDUCER = new NaturalDoubleMinReducer();

    /** A reducer that adds two int elements. */
    public static final IntReducer INT_ADDER_REDUCER = new IntAdder();

    /** A reducer returning the maximum of two int elements, using natural comparator. */
    public static final IntReducer INT_MAX_REDUCER = new NaturalIntMaxReducer();

    /** A reducer returning the minimum of two int elements, using natural comparator. */
    public static final IntReducer INT_MIN_REDUCER = new NaturalIntMinReducer();

    /** A reducer that adds two double elements. */
    public static final LongReducer LONG_ADDER_REDUCER = new LongAdder();

    /** A reducer returning the maximum of two long elements, using natural comparator. */
    public static final LongReducer LONG_MAX_REDUCER = new NaturalLongMaxReducer();

    /** A reducer returning the minimum of two long elements, using natural comparator. */
    public static final LongReducer LONG_MIN_REDUCER = new NaturalLongMinReducer();

    /**
     * A reducer returning the maximum of two Comparable elements, treating null as less
     * than any non-null element.
     */
    public static final Reducer MAX_REDUCER = new NaturalMaxReducer();

    /**
     * A reducer returning the minimum of two Comparable elements, treating null as less
     * than any non-null element.
     */
    public static final Reducer MIN_REDUCER = new NaturalMinReducer();

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Reducers() {}

    // /CLOVER:ON

    public static DoubleReducer doubleMaxReducer(DoubleComparator comparator) {
        return new DoubleMaxReducer(comparator);
    }

    public static DoubleReducer doubleMinReducer(DoubleComparator comparator) {
        return new DoubleMinReducer(comparator);
    }

    public static IntReducer intMaxReducer(IntComparator comparator) {
        return new IntMaxReducer(comparator);
    }

    public static IntReducer intMinReducer(IntComparator comparator) {
        return new IntMinReducer(comparator);
    }

    public static LongReducer longMaxReducer(LongComparator comparator) {
        return new LongMaxReducer(comparator);
    }

    public static LongReducer longMinReducer(LongComparator comparator) {
        return new LongMinReducer(comparator);
    }

    /**
     * Creates a max reducer.
     */
    public static <T extends Comparable<? super T>> Reducer<T> maxReducer() {
        return MAX_REDUCER;
    }

    public static <T> Reducer<T> maxReducer(Comparator<? super T> comparator) {
        return new MaxReducer(comparator);
    }

    /**
     * Creates a max reducer.
     */
    public static <T extends Comparable<? super T>> Reducer<T> minReducer() {
        return MIN_REDUCER;
    }

    public static <T> Reducer<T> minReducer(Comparator<? super T> comparator) {
        return new MinReducer(comparator);
    }

    /** A reducer that adds two double elements. */
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
     * A reducer returning the maximum of two double elements, using the given comparator
     */
    static final class DoubleMaxReducer implements DoubleReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 2065097741025480432L;

        /** DoubleComparator used when reducing. */
        private final DoubleComparator comparator;

        /**
         * Creates a DoubleMaxReducer using the given comparator
         */
        DoubleMaxReducer(DoubleComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public double combine(double a, double b) {
            return (comparator.compare(a, b) >= 0) ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two double elements, using the given comparator
     */
    static final class DoubleMinReducer implements DoubleReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6109941145562459503L;

        /** DoubleComparator used when reducing. */
        private final DoubleComparator comparator;

        /**
         * Creates a DoubleMinReducer using the given comparator
         */
        DoubleMinReducer(DoubleComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        public double combine(double a, double b) {
            return (comparator.compare(a, b) <= 0) ? a : b;
        }
    }

    /** A reducer that adds two int elements. */
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
     * A reducer returning the maximum of two int elements, using the given comparator
     */
    static final class IntMaxReducer implements IntReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 5555681020470083072L;

        /** IntComparator used when reducing. */
        final IntComparator comparator;

        /**
         * Creates a IntMaxReducer using the given comparator
         */
        IntMaxReducer(IntComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public int combine(int a, int b) {
            return (comparator.compare(a, b) >= 0) ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two int elements, using the given comparator
     */
    static final class IntMinReducer implements IntReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -1965332824437951121L;

        /** IntComparator used when reducing. */
        final IntComparator comparator;

        /**
         * Creates a IntMinReducer using the given comparator
         */
        IntMinReducer(IntComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public int combine(int a, int b) {
            return (comparator.compare(a, b) <= 0) ? a : b;
        }
    }

    /** A reducer that adds two double elements. */
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
     * A reducer returning the maximum of two long elements, using the given comparator
     */
    static final class LongMaxReducer implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -3079323426451124529L;

        /** LongComparator used when reducing. */
        final LongComparator comparator;

        /**
         * Creates a LongMaxReducer using the given comparator
         */
        LongMaxReducer(LongComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public long combine(long a, long b) {
            return (comparator.compare(a, b) >= 0) ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two long elements, using the given comparator
     */
    static final class LongMinReducer implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -4447554784861499048L;

        /** LongComparator used when reducing. */
        private final LongComparator comparator;

        /**
         * Creates a LongMinReducer using the given comparator
         */
        LongMinReducer(LongComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public long combine(long a, long b) {
            return (comparator.compare(a, b) <= 0) ? a : b;
        }
    }

    /**
     * A reducer returning the maximum of two elements, using the given comparator, and
     * treating null as less than any non-null element.
     */
    static final class MaxReducer<T> implements Reducer<T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -5456282180421493663L;

        /** Comparator used when reducing. */
        private final Comparator<? super T> comparator;

        MaxReducer(Comparator<? super T> comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public T combine(T a, T b) {
            return (a != null && (b == null || comparator.compare(a, b) >= 0)) ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two elements, using the given comparator, and
     * treating null as greater than any non-null element.
     */
    static final class MinReducer<T> implements Reducer<T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 223676769245813970L;

        /** Comparator used when reducing. */
        private final Comparator<? super T> comparator;

        MinReducer(Comparator<? super T> comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public T combine(T a, T b) {
            return (a != null && (b == null || comparator.compare(a, b) <= 0)) ? a : b;
        }
    }

    /** A reducer returning the maximum of two double elements, using natural comparator. */
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

    /** A reducer returning the minimum of two double elements, using natural comparator. */
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

    /** A reducer returning the maximum of two int elements, using natural comparator. */
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

    /** A reducer returning the minimum of two int elements, using natural comparator. */
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

    /** A reducer returning the maximum of two long elements, using natural comparator. */
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

    /** A reducer returning the minimum of two long elements, using natural comparator. */
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

    /**
     * A reducer returning the maximum of two Comparable elements, treating null as less
     * than any non-null element.
     */
    static final class NaturalMaxReducer<T extends Comparable<? super T>> implements Reducer<T>,
            Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 5079675958818175983L;

        /** {@inheritDoc} */
        public T combine(T a, T b) {
            return (a != null && (b == null || a.compareTo(b) >= 0)) ? a : b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MAX_REDUCER;
        }
    }

    /**
     * A reducer returning the minimum of two Comparable elements, treating null as less
     * than any non-null element.
     */
    static final class NaturalMinReducer<T extends Comparable<? super T>> implements Reducer<T>,
            Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -6750364835779757657L;

        /** {@inheritDoc} */
        public T combine(T a, T b) {
            return (a != null && (b == null || a.compareTo(b) <= 0)) ? a : b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MIN_REDUCER;
        }
    }
}
