/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

/*
 * Written by Doug Lea with assistance from members of JCP JSR-166 Expert Group and
 * released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.operations;

/**
 * Interfaces and utilities describing per-element operations used within parallel methods
 * on aggregates. This class provides type names for common operation signatures accepting
 * zero, one or two arguments, and returning zero or one results, for parameterized types,
 * as well as specializations to <tt>int</tt>, <tt>long</tt>, and <tt>double</tt>.
 * (Lesser used types like <tt>short</tt> are absent.)
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @version $Id: AttributeMaps.java 472 2007-11-19 09:34:26Z kasper $
 */
public final class Ops {

    /**
     * An object with a function accepting pairs of objects, one of type T and one of type
     * U, returning those of type V.
     */
    public static interface Combiner<T, U, V> {
        public V combine(T t, U u);
    }

    /**
     * A Comparator for doubles.
     */
    public static interface DoubleComparator {
        public int compare(double x, double y);
    }

    /** A generator of doubles. */
    public static interface DoubleGenerator {
        /**
         * Generates a double.
         *
         * @return the generated double
         */
        public double generate();
    }

    /** A predicate accepting a double argument. */
    public static interface DoublePredicate {
        public boolean evaluate(double t);
    }

    /** A procedure accepting a double. */
    public static interface DoubleProcedure {
        public void apply(double t);
    }

    /** A reducer accepting and returning doubles. */
    public static interface DoubleReducer {
        public double combine(double u, double v);
    }

    /** A relationalPredicate accepting double arguments. */
    public static interface DoubleRelationalPredicate {
        public boolean evaluate(double t, double u);
    }

    /**
     * A generator (builder) of objects of type T that takes no arguments.
     */
    public static interface Generator<T> {
        /**
         * Generates an object of Type T.
         *
         * @return the generated object
         */
        public T generate();
    }

    /**
     * A Comparator for ints.
     */
    public static interface IntComparator {
        public int compare(int x, int y);
    }

    /** A generator of ints. */
    public static interface IntGenerator {
        /**
         * Generates a int.
         *
         * @return the generated int
         */
        public int generate();
    }

    /** A predicate accepting an int. */
    public static interface IntPredicate {
        public boolean evaluate(int t);
    }

    /** A procedure accepting an int. */
    public static interface IntProcedure {
        public void apply(int t);
    }

    /** A reducer accepting and returning ints. */
    public static interface IntReducer {
        public int combine(int u, int v);
    }

    /** A relationalPredicate accepting int arguments. */
    public static interface IntRelationalPredicate {
        public boolean evaluate(int t, int u);
    }

    /**
     * A Comparator for longs.
     */
    public static interface LongComparator {
        public int compare(long x, long y);
    }

    /** A generator of longs. */
    public static interface LongGenerator {
        /**
         * Generates a long.
         *
         * @return the generated long
         */
        public long generate();
    }

    /** A predicate accepting a long argument. */
    public static interface LongPredicate {
        public boolean evaluate(long t);
    }

    /** A procedure accepting a long. */
    public static interface LongProcedure {
        public void apply(long t);
    }

    /** A reducer accepting and returning longs. */
    public static interface LongReducer {
        public long combine(long u, long v);
    }

    /** A relationalPredicate accepting long arguments. */
    public static interface LongRelationalPredicate {
        public boolean evaluate(long t, long u);
    }

    /**
     * An object with a function accepting objects of type T and returning those of type
     * U.
     */
    public static interface Mapper<T, U> {
        public U map(T t);
    }

    /**
     * A mapper accepting a double.
     */
    public static interface MapperFromDouble<T> {
        public T map(double t);
    }

    /**
     * A mapper accepting a double argument and returning a double.
     */
    public static interface DoubleMapper {
        public double map(double t);
    }

    /**
     * A mapper accepting a double argument and returning an int.
     */
    public static interface MapperFromDoubleToInt {
        public int map(double t);
    }

    /**
     * A mapper accepting a double argument and returning a long.
     */
    public static interface MapperFromDoubleToLong {
        public long map(double t);
    }

    /**
     * A mapper accepting an int.
     */
    public static interface MapperFromInt<T> {
        public T map(int t);
    }

    /**
     * A mapper accepting an int argument and returning a double.
     */
    public static interface MapperFromIntToDouble {
        public double map(int t);
    }

    /** A map accepting an int and returning an int. */
    public static interface MapperFromIntToInt {
        public int map(int u);
    }

    /**
     * A mapper accepting an int argument and returning a long.
     */
    public static interface MapperFromIntToLong {
        public long map(int t);
    }

    /**
     * A mapper accepting a long argument.
     */
    public static interface MapperFromLong<T> {
        public T map(long t);
    }

    /**
     * A mapper accepting a long argument and returning a double.
     */
    public static interface MapperFromLongToDouble {
        public double map(long t);
    }

    /**
     * A mapper accepting a long argument and returning an int.
     */
    public static interface MapperFromLongToInt {
        public int map(long t);
    }

    /**
     * A mapper accepting a long argument and returning a long.
     */
    public static interface LongMapper {
        public long map(long t);
    }

    /**
     * A mapper returning a double.
     */
    public static interface MapperToDouble<T> {
        public double map(T t);
    }

    /**
     * A mapper returning an int.
     */
    public static interface MapperToInt<T> {
        public int map(T t);
    }

    /**
     * A mapper returning a long.
     */
    public static interface MapperToLong<T> {
        public long map(T t);
    }

    /**
     * An object with boolean method of one argument.
     */
    public static interface Predicate<T> {
        public boolean evaluate(T t);
    }

    /**
     * An object with a method of one argument that does not return a result.
     */
    public static interface Procedure<T> {
        public void apply(T t);
    }

    /**
     * A specialized combiner that is associative and accepts pairs of objects of the same
     * type and returning one of the same type. Like for example, an addition operation, a
     * Reducer must be (left) associative: combine(a, combine(b, c)) should have the same
     * result as combine(conbine(a, b), c).
     */
    public static interface Reducer<T> extends Combiner<T, T, T> {
        public T combine(T t, T v);
    }

    /**
     * An object with boolean method of two arguments.
     */
    public static interface RelationalPredicate<T, U> {
        public boolean evaluate(T t, U u);
    }

}
