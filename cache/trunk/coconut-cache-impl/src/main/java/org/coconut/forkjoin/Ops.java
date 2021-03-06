/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package org.coconut.forkjoin;
import java.util.Comparator;

import org.coconut.operations.Ops.DoubleComparator;
import org.coconut.operations.Ops.DoubleGenerator;
import org.coconut.operations.Ops.DoubleMapper;
import org.coconut.operations.Ops.DoublePredicate;
import org.coconut.operations.Ops.DoubleReducer;
import org.coconut.operations.Ops.IntGenerator;
import org.coconut.operations.Ops.IntReducer;
import org.coconut.operations.Ops.LongComparator;
import org.coconut.operations.Ops.LongGenerator;
import org.coconut.operations.Ops.LongMapper;
import org.coconut.operations.Ops.LongPredicate;
import org.coconut.operations.Ops.LongReducer;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.MapperFromDouble;
import org.coconut.operations.Ops.MapperFromDoubleToLong;
import org.coconut.operations.Ops.MapperFromLong;
import org.coconut.operations.Ops.MapperFromLongToDouble;
import org.coconut.operations.Ops.MapperToDouble;
import org.coconut.operations.Ops.MapperToLong;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Reducer;

/**
 * Interfaces and utilities describing per-element operations used
 * within parallel methods on aggregates. This class provides type
 * names for common operation signatures accepting zero, one or two
 * arguments, and returning zero or one results, for parameterized
 * types, as well as specializations to <tt>long</tt>, and
 * <tt>double</tt>. (Other scalar types like <tt>short</tt> are
 * absent.)
 *
 * <p>In addition to stated signatures, implementations of these
 * interfaces must work safely in parallel. In general, this means
 * methods should operate only on their arguments, and should not rely
 * on ThreadLocals, unsafely published globals, or other unsafe
 * constructions. Additionally, they should not block waiting for
 * synchronization.
 *
 * <p> This class also provides methods returning some commonly used
 * implementations of some of these interfaces
 *
 * <p>This class is normally best used via <tt>import static</tt>.
 */
class Ops {
    private Ops() {} // disable construction

    /**
     * Returns a Comparator for Comparable objects
     */
    public static <T extends Comparable<? super T>> Comparator<T>
                             naturalComparator(Class<T> type) {
        return new Comparator<T>() {
            public int compare(T a, T b) {
                return a.compareTo(b);
            }
        };
    }

    /**
     * Returns a reducer returning the maximum of two Comparable
     * elements, treating null as less than any non-null element.
     */
    public static <T extends Comparable<? super T>> Reducer<T>
                             naturalMaxReducer(Class<T> type) {
        return new Reducer<T>() {
            public T combine(T a, T b) {
                return (a != null &&
                        (b == null || a.compareTo(b) >= 0))? a : b;
            }
        };
    }

    /**
     * Returns a reducer returning the minimum of two Comparable
     * elements, treating null as greater than any non-null element.
     */
    public static <T extends Comparable<? super T>> Reducer<T>
                             naturalMinReducer(Class<T> type) {
        return new Reducer<T>() {
            public T combine(T a, T b) {
                return (a != null &&
                        (b == null || a.compareTo(b) <= 0))? a : b;
            }
        };
    }

    /**
     * Returns a reducer returning the maximum of two elements, using
     * the given comparator, and treating null as less than any
     * non-null element.
     */
    public static <T> Reducer<T> maxReducer
        (final Comparator<? super T> comparator) {
        return new Reducer<T>() {
            public T combine(T a, T b) {
                return (a != null &&
                        (b == null || comparator.compare(a, b) >= 0))? a : b;
            }
        };
    }

    /**
     * Returns a reducer returning the minimum of two elements, using
     * the given comparator, and treating null as greater than any
     * non-null element.
     */
    public static <T> Reducer<T> minReducer
        (final Comparator<? super T> comparator) {
        return new Reducer<T>() {
            public T combine(T a, T b) {
                return (a != null &&
                        (b == null || comparator.compare(a, b) <= 0))? a : b;
            }
        };
    }

    /**
     * Returns a Comparator that casts its arguments as Comparable on
     * each comparison, throwing ClassCastException on failure.
     */
    public static Comparator<Object> castedComparator() {
        return (Comparator<Object>)(RawComparator.cmp);
    }
    static final class RawComparator implements Comparator {
        static final RawComparator cmp = new RawComparator();
        public int compare(Object a, Object b) {
            return ((Comparable)a).compareTo((Comparable)b);
        }
    }

    /**
     * Returns a reducer returning maximum of two values, or
     * <tt>null</tt> if both arguments are null, and that casts
     * its arguments as Comparable on each comparison, throwing
     * ClassCastException on failure.
     */
    public static Reducer<Object> castedMaxReducer() {
        return (Reducer<Object>)RawMaxReducer.max;
    }
    static final class RawMaxReducer implements Reducer {
        static final RawMaxReducer max = new RawMaxReducer();
        public Object combine(Object a, Object b) {
            return (a != null &&
                    (b == null ||
                     ((Comparable)a).compareTo((Comparable)b) >= 0))? a : b;
        }
    }

    /**
     * Returns a reducer returning minimum of two values, or
     * <tt>null</tt> if both arguments are null, and that casts
     * its arguments as Comparable on each comparison, throwing
     * ClassCastException on failure.
     */
    public static Reducer<Object> castedMinReducer() {
        return (Reducer<Object>)RawMinReducer.min;
    }
    static final class RawMinReducer implements Reducer {
        static final RawMinReducer min = new RawMinReducer();
        public Object combine(Object a, Object b) {
            return (a != null &&
                    (b == null ||
                     ((Comparable)a).compareTo((Comparable)b) <= 0))? a : b;
        }
    }


    /**
     * Returns a comparator for doubles relying on natural ordering
     */
    public static DoubleComparator naturalDoubleComparator() {
        return NaturalDoubleComparator.comparator;
    }
    static final class NaturalDoubleComparator
        implements DoubleComparator {
        static final NaturalDoubleComparator comparator = new
            NaturalDoubleComparator();
        public int compare(double a, double b) {
            return Double.compare(a, b);
        }
    }

    /**
     * Returns a reducer returning the maximum of two double elements,
     * using natural comparator
     */
    public static DoubleReducer naturalDoubleMaxReducer() {
        return NaturalDoubleMaxReducer.max;
    }

    static final class NaturalDoubleMaxReducer
        implements DoubleReducer {
        public static final NaturalDoubleMaxReducer max =
            new NaturalDoubleMaxReducer();
        public double combine(double a, double b) { return Math.max(a, b); }
    }

    /**
     * Returns a reducer returning the minimum of two double elements,
     * using natural comparator
     */
    public static DoubleReducer naturalDoubleMinReducer() {
        return NaturalDoubleMinReducer.min;
    }
    static final class NaturalDoubleMinReducer
        implements DoubleReducer {
        public static final NaturalDoubleMinReducer min =
            new NaturalDoubleMinReducer();
        public double combine(double a, double b) { return Math.min(a, b); }
    }

    /**
     * Returns a reducer returning the maximum of two double elements,
     * using the given comparator
     */
    public static DoubleReducer doubleMaxReducer
        (final DoubleComparator comparator) {
        return new DoubleReducer() {
                public double combine(double a, double b) {
                    return (comparator.compare(a, b) >= 0)? a : b;
                }
            };
    }

    /**
     * Returns a reducer returning the minimum of two double elements,
     * using the given comparator
     */
    public static DoubleReducer doubleMinReducer
        (final DoubleComparator comparator) {
        return new DoubleReducer() {
                public double combine(double a, double b) {
                    return (comparator.compare(a, b) <= 0)? a : b;
                }
            };
    }

    /**
     * Returns a comparator for longs relying on natural ordering
     */
    public static LongComparator naturalLongComparator() {
        return NaturalLongComparator.comparator;
    }
    static final class NaturalLongComparator
        implements LongComparator {
        static final NaturalLongComparator comparator = new
            NaturalLongComparator();
        public int compare(long a, long b) {
            return a < b? -1 : ((a > b)? 1 : 0);
        }
    }

    /**
     * Returns a reducer returning the maximum of two long elements,
     * using natural comparator
     */
    public static LongReducer naturalLongMaxReducer() {
        return NaturalLongMaxReducer.max;
    }

    static final class NaturalLongMaxReducer
        implements LongReducer {
        public static final NaturalLongMaxReducer max =
            new NaturalLongMaxReducer();
        public long combine(long a, long b) { return a >= b? a : b; }
    }

    /**
     * A reducer returning the minimum of two long elements,
     * using natural comparator
     */
    public static LongReducer naturalLongMinReducer() {
        return NaturalLongMinReducer.min;
    }
    static final class NaturalLongMinReducer
        implements LongReducer {
        public static final NaturalLongMinReducer min =
            new NaturalLongMinReducer();
        public long combine(long a, long b) { return a <= b? a : b; }
    }

    /**
     * Returns a reducer returning the maximum of two long elements,
     * using the given comparator
     */
    public static LongReducer longMaxReducer
        (final LongComparator comparator) {
        return new LongReducer() {
                public long combine(long a, long b) {
                    return (comparator.compare(a, b) >= 0)? a : b;
                }
            };
    }

    /**
     * Returns a reducer returning the minimum of two long elements,
     * using the given comparator
     */
    public static LongReducer longMinReducer
        (final LongComparator comparator) {
        return new LongReducer() {
                public long combine(long a, long b) {
                    return (comparator.compare(a, b) <= 0)? a : b;
                }
            };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T,U,V> Mapper<T,V> compoundMapper
        (final Mapper<? super T, ? extends U> first,
         final Mapper<? super U, ? extends V> second) {
        return new Mapper<T,V>() {
            public final V map(T t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T,V> Mapper<T,V> compoundMapper
        (final MapperToDouble<? super T> first,
         final MapperFromDouble<? extends V> second) {
        return new Mapper<T,V>() {
            public final V map(T t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T,V> Mapper<T,V> compoundMapper
        (final MapperToLong<? super T> first,
         final MapperFromLong<? extends V> second) {
        return new Mapper<T,V>() {
            public final V map(T t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T,V> MapperFromDouble<V> compoundMapper
        (final MapperFromDouble<? extends T> first,
         final Mapper<? super T,? extends V> second) {
        return new MapperFromDouble<V>() {
            public final V map(double t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T,V> MapperFromLong<V> compoundMapper
        (final MapperFromLong<? extends T> first,
         final Mapper<? super T,? extends V> second) {
        return new MapperFromLong<V>() {
            public final V map(long t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T,U> MapperToDouble<T> compoundMapper
        (final Mapper<? super T, ? extends U> first,
         final MapperToDouble<? super U> second) {
        return new MapperToDouble<T>() {
            public final double map(T t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T,U> MapperToLong<T> compoundMapper
        (final Mapper<? super T, ? extends U> first,
         final MapperToLong<? super U> second) {
        return new MapperToLong<T>() {
            public final long map(T t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T> MapperToDouble<T> compoundMapper
        (final MapperToDouble<? super T> first,
         final DoubleMapper second) {
        return new MapperToDouble<T>() {
            public final double map(T t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T> MapperToLong<T> compoundMapper
        (final MapperToDouble<? super T> first,
         final MapperFromDoubleToLong second) {
        return new MapperToLong<T>() {
            public final long map(T t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T> MapperToLong<T> compoundMapper
        (final MapperToLong<? super T> first,
         final LongMapper second) {
        return new MapperToLong<T>() {
            public final long map(T t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T> MapperToDouble<T> compoundMapper
        (final MapperToLong<? super T> first,
         final MapperFromLongToDouble second) {
        return new MapperToDouble<T>() {
            public final double map(T t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static DoubleMapper compoundMapper
        (final DoubleMapper first,
         final DoubleMapper second) {
        return new DoubleMapper() {
            public final double map(double t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static MapperFromDoubleToLong compoundMapper
        (final DoubleMapper first,
         final MapperFromDoubleToLong second) {
        return new MapperFromDoubleToLong() {
            public final long map(double t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static MapperFromDoubleToLong compoundMapper
        (final MapperFromDoubleToLong first,
         final LongMapper second) {
        return new MapperFromDoubleToLong() {
            public final long map(double t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T> MapperFromDouble<T> compoundMapper
        (final MapperFromDoubleToLong first,
         final MapperFromLong<? extends T> second) {
        return new MapperFromDouble<T>() {
            public final T map(double t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T> MapperFromLong<T> compoundMapper
        (final MapperFromLongToDouble first,
         final MapperFromDouble<? extends T> second) {
        return new MapperFromLong<T>() {
            public final T map(long t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static MapperFromLongToDouble compoundMapper
        (final LongMapper first,
         final MapperFromLongToDouble second) {
        return new MapperFromLongToDouble() {
            public final double map(long t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static MapperFromLongToDouble compoundMapper
        (final MapperFromLongToDouble first,
         final DoubleMapper second) {
        return new MapperFromLongToDouble() {
            public final double map(long t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T> MapperFromDouble<T> compoundMapper
        (final DoubleMapper first,
         final MapperFromDouble<? extends T> second) {
        return new MapperFromDouble<T>() {
            public final T map(double t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T> MapperFromLong<T> compoundMapper
        (final LongMapper first,
         final MapperFromLong<? extends T> second) {
        return new MapperFromLong<T>() {
            public final T map(long t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T> DoubleMapper compoundMapper
        (final MapperFromDouble<? extends T> first,
         final MapperToDouble<? super T>  second) {
        return new DoubleMapper() {
            public final double map(double t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T> MapperFromLongToDouble compoundMapper
        (final MapperFromLong<? extends T> first,
         final MapperToDouble<? super T>  second) {
        return new MapperFromLongToDouble() {
            public final double map(long t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T> MapperFromDoubleToLong compoundMapper
        (final MapperFromDouble<? extends T> first,
         final MapperToLong<? super T>  second) {
        return new MapperFromDoubleToLong() {
            public final long map(double t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static <T> LongMapper compoundMapper
        (final MapperFromLong<? extends T> first,
         final MapperToLong<? super T>  second) {
        return new LongMapper() {
            public final long map(long t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static LongMapper compoundMapper
        (final LongMapper first,
         final LongMapper second) {
        return new LongMapper() {
            public final long map(long t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static DoubleMapper compoundMapper
        (final MapperFromDoubleToLong first,
         final MapperFromLongToDouble second) {
        return new DoubleMapper() {
            public final double map(double t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a composite mapper that applies a second mapper to the results
     * of applying the first one
     */
    public static LongMapper compoundMapper
        (final MapperFromLongToDouble first,
         final MapperFromDoubleToLong second) {
        return new LongMapper() {
            public final long map(long t) { return second.map(first.map(t)); }
        };
    }

    /**
     * Returns a predicate evaluating to the negation of its contained predicate
     */
    public static <T> Predicate<T> notPredicate
        (final Predicate<T> pred) {
        return new Predicate<T>() {
            public final boolean evaluate(T x) { return !pred.evaluate(x); }
        };
    }

    /**
     * Returns a predicate evaluating to the negation of its contained predicate
     */
    public static DoublePredicate notPredicate
        (final DoublePredicate pred) {
        return new DoublePredicate() {
                public final boolean evaluate(double x) { return !pred.evaluate(x); }
            };
    }

    /**
     * Returns a predicate evaluating to the negation of its contained predicate
     */
    public static LongPredicate notPredicate
        (final LongPredicate pred) {
        return new LongPredicate() {
                public final boolean evaluate(long x) { return !pred.evaluate(x); }
            };
    }

    /**
     * Returns a predicate evaluating to the conjunction of its contained predicates
     */
    public static <S, T extends S> Predicate<T> andPredicate
        (final Predicate<S> first,
         final Predicate<? super T> second) {
        return new Predicate<T>() {
            public final boolean evaluate(T x) {
                return first.evaluate(x) && second.evaluate(x);
            }
        };
    }

    /**
     * Returns a predicate evaluating to the disjuntion of its contained predicates
     */
    public static <S, T extends S> Predicate<T> orPredicate
        (final Predicate<S> first,
         final Predicate<? super T> second) {
        return new Predicate<T>() {
            public final boolean evaluate(T x) {
                return first.evaluate(x) || second.evaluate(x);
            }
        };
    }

    /**
     * Returns a predicate evaluating to the conjunction of its contained predicates
     */
    public static DoublePredicate andPredicate
        (final DoublePredicate first,
         final DoublePredicate second) {
        return new DoublePredicate() {
            public final boolean evaluate(double x) {
                return first.evaluate(x) && second.evaluate(x);
            }
        };
    }

    /**
     * Returns a predicate evaluating to the disjuntion of its contained predicates
     */
    public static DoublePredicate orPredicate
        (final DoublePredicate first,
         final DoublePredicate second) {
        return new DoublePredicate() {
            public final boolean evaluate(double x) {
                return first.evaluate(x) || second.evaluate(x);
            }
        };
    }


    /**
     * Returns a predicate evaluating to the conjunction of its contained predicates
     */
    public static LongPredicate andPredicate
        (final LongPredicate first,
         final LongPredicate second) {
        return new LongPredicate() {
            public final boolean evaluate(long x) {
                return first.evaluate(x) && second.evaluate(x);
            }
        };
    }

    /**
     * Returns a predicate evaluating to the disjuntion of its contained predicates
     */
    public static LongPredicate orPredicate
        (final LongPredicate first,
         final LongPredicate second) {
        return new LongPredicate() {
            public final boolean evaluate(long x) {
                return first.evaluate(x) || second.evaluate(x);
            }
        };
    }

    /**
     * Returns a predicate evaluating to true if its argument is non-null
     */
    public static  Predicate<Object> isNonNullPredicate() {
        return IsNonNullPredicate.predicate;
    }
    static final class IsNonNullPredicate implements Predicate<Object> {
        static final IsNonNullPredicate predicate =
            new IsNonNullPredicate();
        public final boolean evaluate(Object x) {
            return x != null;
        }
    }

    /**
     * Returns a predicate evaluating to true if its argument is null
     */
    public static  Predicate<Object> isNullPredicate() {
        return IsNullPredicate.predicate;
    }
    static final class IsNullPredicate implements Predicate<Object> {
        static final IsNullPredicate predicate =
            new IsNullPredicate();
        public final boolean evaluate(Object x) {
            return x != null;
        }
    }

    /**
     * Returns a predicate evaluating to true if its argument is an instance
     * of (see {@link Class#isInstance} the given type (class).
     */
    public static Predicate<Object> instanceofPredicate(final Class type) {
        return new Predicate<Object>() {
            public final boolean evaluate(Object x) {
                return type.isInstance(x);
            }
        };
    }


    /**
     * Returns a predicate evaluating to true if its argument is assignable
     * from (see {@link Class#isAssignableFrom} the given type (class).
     */
    public static Predicate<Object> isAssignablePredicate(final Class type) {
        return new Predicate<Object>() {
            public final boolean evaluate(Object x) {
                return type.isAssignableFrom(x.getClass());
            }
        };
    }

    /**
     * Returns a reducer that adds two double elements
     */
    public static DoubleReducer doubleAdder() { return DoubleAdder.adder; }
    static final class DoubleAdder implements DoubleReducer {
        static final DoubleAdder adder = new DoubleAdder();
        public double combine(double a, double b) { return a + b; }
    }

    /**
     * Returns a reducer that adds two long elements
     */
    public static LongReducer longAdder() { return LongAdder.adder; }
    static final class LongAdder implements LongReducer {
        static final LongAdder adder = new LongAdder();
        public long combine(long a, long b) { return a + b; }
    }

    /**
     * Returns a reducer that adds two int elements
     */
    public static IntReducer intAdder() { return IntAdder.adder; }
    static final class IntAdder implements IntReducer {
        static final IntAdder adder = new IntAdder();
        public int combine(int a, int b) { return a + b; }
    }

    /**
     * Returns a generator producing uniform random values between
     * zero and one, with the same properties as {@link
     * java.util.Random#nextDouble} but operating independently across
     * ForkJoinWorkerThreads and usable only within forkjoin
     * computations.
     */
    public static DoubleGenerator doubleRandom() {
        return DoubleRandomGenerator.generator;
    }
    static final class DoubleRandomGenerator implements DoubleGenerator {
        static final DoubleRandomGenerator generator =
            new DoubleRandomGenerator();
        public double generate() {
            return ForkJoinWorkerThread.nextRandomDouble();
        }
    }

    /**
     * Returns a generator producing uniform random values between
     * zero and the given bound, with the same properties as {@link
     * java.util.Random#nextDouble} but operating independently across
     * ForkJoinWorkerThreads and usable only within forkjoin
     * computations.
     * @param bound the upper bound (exclusive) of generated values
     */
    public static DoubleGenerator doubleRandom(double bound) {
        return new DoubleBoundedRandomGenerator(bound);
    }
    static final class DoubleBoundedRandomGenerator implements DoubleGenerator {
        final double bound;
        DoubleBoundedRandomGenerator(double bound) { this.bound = bound; }
        public double generate() {
            return ForkJoinWorkerThread.nextRandomDouble() * bound;
        }
    }

    /**
     * Returns a generator producing uniform random values between the
     * given least value (inclusive) and bound (exclusive), operating
     * independently across ForkJoinWorkerThreads and usable only
     * within forkjoin computations.
     * @param least the least value returned
     * @param bound the upper bound (exclusive) of generated values
     */
    public static DoubleGenerator doubleRandom(double least, double bound) {
        return new DoubleIntervalRandomGenerator(least, bound);
    }
    static final class DoubleIntervalRandomGenerator implements DoubleGenerator {
        final double least;
        final double range;
        DoubleIntervalRandomGenerator(double least, double bound) {
            this.least = least; this.range = bound - least;
        }
        public double generate() {
            return ForkJoinWorkerThread.nextRandomDouble() * range + least;
        }
    }

    /**
     * Returns a generator producing uniform random values with the
     * same properties as {@link java.util.Random#nextLong} but
     * operating independently across ForkJoinWorkerThreads and usable
     * only within forkjoin computations.
     */
    public static LongGenerator longRandom() {
        return LongRandomGenerator.generator;
    }
    static final class LongRandomGenerator implements LongGenerator {
        static final LongRandomGenerator generator =
            new LongRandomGenerator();
        public long generate() {
            return ForkJoinWorkerThread.nextRandomLong();
        }
    }

    /**
     * Returns a generator producing uniform random values with the
     * same properties as {@link java.util.Random#nextInt(int)} but
     * operating independently across ForkJoinWorkerThreads and usable
     * only within forkjoin computations.
     * @param bound the upper bound (exclusive) of generated values
     */
    public static LongGenerator longRandom(long bound) {
        if (bound <= 0)
            throw new IllegalArgumentException();
        return new LongBoundedRandomGenerator(bound);
    }
    static final class LongBoundedRandomGenerator implements LongGenerator {
        final long bound;
        LongBoundedRandomGenerator(long bound) { this.bound = bound; }
        public long generate() {
            return ForkJoinWorkerThread.nextRandomLong(bound);
        }
    }

    /**
     * Returns a generator producing uniform random values between the
     * given least value (inclusive) and bound (exclusive), operating
     * independently across ForkJoinWorkerThreads and usable only
     * within forkjoin computations.
     * @param least the least value returned
     * @param bound the upper bound (exclusive) of generated values
     */
    public static LongGenerator longRandom(long least, long bound) {
        if (least >= bound)
            throw new IllegalArgumentException();
        return new LongIntervalRandomGenerator(least, bound);
    }
    static final class LongIntervalRandomGenerator implements LongGenerator {
        final long least;
        final long range;
        LongIntervalRandomGenerator(long least, long bound) {
            this.least = least; this.range = bound - least;
        }
        public long generate() {
            return ForkJoinWorkerThread.nextRandomLong(range) + least;
        }
    }

    /**
     * Returns a generator producing uniform random values with the
     * same properties as {@link java.util.Random#nextInt} but
     * operating independently across ForkJoinWorkerThreads and usable
     * only within forkjoin computations.
     */
    public static IntGenerator intRandom() {
        return IntRandomGenerator.generator;
    }
    static final class IntRandomGenerator implements IntGenerator {
        static final IntRandomGenerator generator =
            new IntRandomGenerator();
        public int generate() {
            return ForkJoinWorkerThread.nextRandomInt();
        }
    }

    /**
     * Returns a generator producing uniform random values with the
     * same properties as {@link java.util.Random#nextInt(int)} but
     * operating independently across ForkJoinWorkerThreads and usable
     * only within forkjoin computations.
     * @param bound the upper bound (exclusive) of generated values
     */
    public static IntGenerator intRandom(int bound) {
        if (bound <= 0)
            throw new IllegalArgumentException();
        return new IntBoundedRandomGenerator(bound);
    }
    static final class IntBoundedRandomGenerator implements IntGenerator {
        final int bound;
        IntBoundedRandomGenerator(int bound) { this.bound = bound; }
        public int generate() {
            return ForkJoinWorkerThread.nextRandomInt(bound);
        }
    }

    /**
     * Returns a generator producing uniform random values between the
     * given least value (inclusive) and bound (exclusive), operating
     * independently across ForkJoinWorkerThreads and usable only
     * within forkjoin computations.
     * @param least the least value returned
     * @param bound the upper bound (exclusive) of generated values
     */
    public static IntGenerator intRandom(int least, int bound) {
        if (least >= bound)
            throw new IllegalArgumentException();
        return new IntIntervalRandomGenerator(least, bound);
    }
    static final class IntIntervalRandomGenerator implements IntGenerator {
        final int least;
        final int range;
        IntIntervalRandomGenerator(int least, int bound) {
            this.least = least; this.range = bound - least;
        }
        public int generate() {
            return ForkJoinWorkerThread.nextRandomInt(range) + least;
        }
    }


}
