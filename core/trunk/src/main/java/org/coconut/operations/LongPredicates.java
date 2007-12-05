/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import java.io.Serializable;

import org.coconut.operations.Ops.Predicate;

/**
 * Various implementations of {@link Ops.LongPredicate}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class LongPredicates {
    // /CLOVER:OFF
    /** Cannot instantiate. */
    private LongPredicates() {}

    /**
     * Returns a predicate that accepts any long that is equal to the value specified.
     * 
     * @param equalsTo
     *            the value of the equals predicate
     * @return a predicate that accepts any long that is equal to the value specified
     */
    public static EqualsToPredicate equalsTo(long equalsTo) {
        return new EqualsToPredicate(equalsTo);
    }

    public static GreaterThenPredicate greaterThen(long greaterThen) {
        return new GreaterThenPredicate(greaterThen);
    }

    public static GreaterThenOrEqualsPredicate greaterThenOrEquals(long greaterThenOrEquals) {
        return new GreaterThenOrEqualsPredicate(greaterThenOrEquals);
    }

    // /CLOVER:ON
    public static LessThenPredicate lessThen(long lessThen) {
        return new LessThenPredicate(lessThen);
    }

    public static LessThenOrEqualsPredicate lessThenOrEquals(long lessThenOrEquals) {
        return new LessThenOrEqualsPredicate(lessThenOrEquals);
    }

    public static <T> Predicate<T> mapperPredicate(final Ops.MapperToLong<T> mapper,
            Ops.LongPredicate predicate) {
        return new MapperPredicate<T>(mapper, predicate);
    }

    public static class EqualsToPredicate implements Ops.LongPredicate, Serializable {

        /** The value to compare with. */
        private final long equalsTo;

        public EqualsToPredicate(long equalsTo) {
            this.equalsTo = equalsTo;
        }

        /**
         * Returns <code>true</code> if the specified value is equal to the value that
         * was used when constructing this predicate, otherwise <code>false</code>.
         * 
         * @param t
         *            the value to compare with
         * @return <code>true</code> if the specified value is equal to the value that
         *         was used when constructing this predicate, otherwise <code>false</code>.
         */
        public boolean evaluate(long t) {
            return equalsTo == t;
        }

        public long getEqualsTo() {
            return equalsTo;
        }
    }

    public static class GreaterThenOrEqualsPredicate implements Ops.LongPredicate, Serializable {
        /** The value to compare with. */
        private final long greaterThenOrEquals;

        public GreaterThenOrEqualsPredicate(long greaterThenOrEquals) {
            this.greaterThenOrEquals = greaterThenOrEquals;
        }

        public boolean evaluate(long t) {
            return greaterThenOrEquals <= t;
        }

        public long getGreaterThenOrEquals() {
            return greaterThenOrEquals;
        }
    }

    public static class GreaterThenPredicate implements Ops.LongPredicate, Serializable {
        /** The value to compare with. */
        private final long greaterThen;

        public GreaterThenPredicate(long greaterThen) {
            this.greaterThen = greaterThen;
        }

        public boolean evaluate(long t) {
            return greaterThen < t;
        }

        public long getGreaterThen() {
            return greaterThen;
        }
    }

    public static class LessThenOrEqualsPredicate implements Ops.LongPredicate, Serializable {
        /** The value to compare with. */
        private final long lessThenOrEquals;

        public LessThenOrEqualsPredicate(long lessThenOrEquals) {
            this.lessThenOrEquals = lessThenOrEquals;
        }

        public boolean evaluate(long t) {
            return lessThenOrEquals >= t;
        }

        public long getLessThenOrEquals() {
            return lessThenOrEquals;
        }
    }

    public static class LessThenPredicate implements Ops.LongPredicate, Serializable {
        /** The value to compare with. */
        private final long lessThen;

        public LessThenPredicate(long lessThen) {
            this.lessThen = lessThen;
        }

        public boolean evaluate(long t) {
            return lessThen > t;
        }

        public long getLessThen() {
            return lessThen;
        }
    }

    /**
     */
    final static class MapperPredicate<T> implements Predicate<T>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -6292758840373110577L;

        /** The object to compare with. */
        private final Ops.MapperToLong<T> mapper;

        /** The predicate to test against. */
        private final Ops.LongPredicate predicate;

        public MapperPredicate(final Ops.MapperToLong<T> mapper, Ops.LongPredicate predicate) {
            if (mapper == null) {
                throw new NullPointerException("mapper is null");
            } else if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
            this.mapper = mapper;
        }

        /**
         * Accepts all elements that are {@link Object#equals equal} to the specified
         * object.
         * 
         * @param element
         *            the element to test against.
         * @return <code>true</code> if the predicate accepts the element;
         *         <code>false</code> otherwise.
         */
        public boolean evaluate(T element) {
            return predicate.evaluate(mapper.map(element));
        }

        /**
         * Returns the mapper that will map the object before applying the predicate on
         * it.
         * 
         * @return the mapper that will map the object before applying the predicate on it
         */
        public Ops.MapperToLong<T> getMapper() {
            return mapper;
        }

        /**
         * Returns the Predicate we are testing against.
         * 
         * @return the Predicate we are testing against.
         */
        public Ops.LongPredicate getPredicate() {
            return predicate;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "convert " + mapper;
        }
    }

}
