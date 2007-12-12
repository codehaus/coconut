/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import java.io.Serializable;

import org.coconut.operations.Ops.LongPredicate;
import org.coconut.operations.Ops.MapperToLong;
import org.coconut.operations.Ops.Predicate;

/**
 * Various implementations of {@link Ops.LongPredicate}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class LongPredicates {

    /** A LongPredicate that always evaluates to <code>false</code>. */
    public static final LongPredicate FALSE = new FalseLongPredicate();

    /** A LongPredicate that always evaluates to <code>true</code>. */
    public static final LongPredicate TRUE = new TrueLongPredicate();

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private LongPredicates() {}

    // /CLOVER:ON

    /**
     * Creates a LongPredicate that performs a logical AND on two supplied predicates. The
     * returned predicate uses short-circuit evaluation (or minimal evaluation). That is,
     * if the specified left side predicate evaluates to <code>false</code> the right
     * side predicate will not be evaluated. More formally
     * 
     * <pre>
     * left.evaluate(element) &amp;&amp; right.evaluate(element);
     * </pre>
     * 
     * <p>
     * If both of the supplied predicates are serializable the returned predicate will
     * also be serializable.
     * 
     * @param left
     *            the left side LongPredicate
     * @param right
     *            the right side LongPredicate
     * @return the newly created LongPredicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static LongPredicate and(LongPredicate left, LongPredicate right) {
        return new AndLongPredicate(left, right);
    }

    public static LongPredicate between(long left, long right) {
        return and(greaterThenOrEquals(left), lessThenOrEquals(right));
    }

    /**
     * Returns a predicate that accepts any long that is equal to the value specified.
     * 
     * @param equalsTo
     *            the value of the equals predicate
     * @return a predicate that accepts any long that is equal to the value specified
     */
    public static EqualsToLongPredicate equalsTo(long equalsTo) {
        return new EqualsToLongPredicate(equalsTo);
    }

    public static GreaterThenLongPredicate greaterThen(long greaterThen) {
        return new GreaterThenLongPredicate(greaterThen);
    }

    public static GreaterThenOrEqualsLongPredicate greaterThenOrEquals(long greaterThenOrEquals) {
        return new GreaterThenOrEqualsLongPredicate(greaterThenOrEquals);
    }

    public static LessThenLongPredicate lessThen(long lessThen) {
        return new LessThenLongPredicate(lessThen);
    }

    public static LessThenOrEqualsLongPredicate lessThenOrEquals(long lessThenOrEquals) {
        return new LessThenOrEqualsLongPredicate(lessThenOrEquals);
    }

    public static <T> Predicate<T> mapAndEvaluate(MapperToLong<T> mapper, LongPredicate predicate) {
        return new MapToLongAndEvaluatePredicate<T>(mapper, predicate);
    }

    /**
     * Creates a LongPredicate that performs a logical logical NOT on the supplied
     * LongPredicate. More formally
     * 
     * <pre>
     * !predicate.evaluate(value);
     * </pre>
     * 
     * <p>
     * If the specified predicate is serializable the returned predicate will also be
     * serializable.
     * 
     * @param predicate
     *            the predicate to negate
     * @return the newly created LongPredicate
     * @throws NullPointerException
     *             if the specified predicate is <code>null</code>
     */
    public static NotLongPredicate not(LongPredicate predicate) {
        return new NotLongPredicate(predicate);
    }

    public static class EqualsToLongPredicate implements LongPredicate, Serializable {

        /** The value to compare with. */
        private final long equalsTo;

        public EqualsToLongPredicate(long equalsTo) {
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

        /**
         * @return the value we are comparing with
         */
        public long getEqualsTo() {
            return equalsTo;
        }
    }

    public static class GreaterThenLongPredicate implements LongPredicate, Serializable {
        /** The value to compare with. */
        private final long greaterThen;

        public GreaterThenLongPredicate(long greaterThen) {
            this.greaterThen = greaterThen;
        }

        public boolean evaluate(long t) {
            return greaterThen < t;
        }

        public long getGreaterThen() {
            return greaterThen;
        }
    }

    public static class GreaterThenOrEqualsLongPredicate implements LongPredicate, Serializable {
        /** The value to compare with. */
        private final long greaterThenOrEquals;

        public GreaterThenOrEqualsLongPredicate(long greaterThenOrEquals) {
            this.greaterThenOrEquals = greaterThenOrEquals;
        }

        public boolean evaluate(long t) {
            return greaterThenOrEquals <= t;
        }

        public long getGreaterThenOrEquals() {
            return greaterThenOrEquals;
        }
    }

    public static class LessThenLongPredicate implements LongPredicate, Serializable {
        /** The value to compare with. */
        private final long lessThen;

        public LessThenLongPredicate(long lessThen) {
            this.lessThen = lessThen;
        }

        public boolean evaluate(long t) {
            return lessThen > t;
        }

        public long getLessThen() {
            return lessThen;
        }
    }

    public static class LessThenOrEqualsLongPredicate implements LongPredicate, Serializable {
        /** The value to compare with. */
        private final long lessThenOrEquals;

        public LessThenOrEqualsLongPredicate(long lessThenOrEquals) {
            this.lessThenOrEquals = lessThenOrEquals;
        }

        public boolean evaluate(long t) {
            return lessThenOrEquals >= t;
        }

        public long getLessThenOrEquals() {
            return lessThenOrEquals;
        }
    }

    /**
     * A LongPredicate that evaluates to true iff the Predicate used for constructing
     * evaluates to <code>false</code>.
     */
    public final static class NotLongPredicate implements LongPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -5117781730584740429L;

        /** The LongPredicate to negate. */
        private final LongPredicate predicate;

        /**
         * Creates a new NotLongPredicate.
         * 
         * @param predicate
         *            the predicate to negate.
         * @throws NullPointerException
         *             if the specified predicate is <code>null</code>
         */
        public NotLongPredicate(LongPredicate predicate) {
            if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
        }

        /**
         * Returns a boolean representing the logical NOT value of the supplied
         * LongPredicate.
         * 
         * @param element
         *            the element to test
         * @return the logical NOT of the supplied LongPredicate
         */
        public boolean evaluate(long element) {
            return !predicate.evaluate(element);
        }

        /**
         * Returns the predicate that is being negated.
         * 
         * @return the predicate that is being negated.
         */
        public LongPredicate getPredicate() {
            return predicate;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "!(" + predicate + ")";
        }
    }

    /**
     * A LongPredicate that performs a logical exclusive AND on two supplied predicates.
     */
    final static class AndLongPredicate implements LongPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 6981902451700512606L;

        /** The left side operand. */
        private final LongPredicate left;

        /** The right side operand. */
        private final LongPredicate right;

        /**
         * Creates a new <code>AndLongPredicate</code>.
         * 
         * @param left
         *            the left side LongPredicate
         * @param right
         *            the right side LongPredicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        public AndLongPredicate(LongPredicate left, LongPredicate right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean evaluate(long element) {
            return left.evaluate(element) && right.evaluate(element);
        }

        /**
         * Returns the left side LongPredicate.
         * 
         * @return the left side LongPredicate.
         */
        public LongPredicate getLeftPredicate() {
            return left;
        }

        /**
         * Returns the right side LongPredicate.
         * 
         * @return the right side LongPredicate.
         */
        public LongPredicate getRightPredicate() {
            return right;
        }

        /** {@inheritDoc} */
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }

    /**
     * A LongPredicate that always evaluates to <tt>false</tt>. Use {@link #FALSE} to
     * get an instance of this LongPredicate.
     * 
     * @see TrueLongPredicate
     */
    final static class FalseLongPredicate implements LongPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -3048464662394104180L;

        /** Creates a new FalseLongPredicate. */
        private FalseLongPredicate() {}

        /** {@inheritDoc} */
        public boolean evaluate(long value) {
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return Boolean.FALSE.toString();
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return FALSE;
        }
    }

    /**
     * A Predicate that first applies the specified mapper to the argument before
     * evaluating the specified LongPredicate.
     */
    final static class MapToLongAndEvaluatePredicate<T> implements Predicate<T>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -6292758840373110577L;

        /** The mapper that maps the element being tested to a long. */
        private final MapperToLong<T> mapper;

        /** The predicate to test against. */
        private final LongPredicate predicate;

        /**
         * Creates a new MapToLongAndEvaluatePredicate.
         * 
         * @param mapper
         *            the mapper used to first map the argument
         * @param predicate
         *            the predicate used to evaluate the mapped argument
         */
        public MapToLongAndEvaluatePredicate(final MapperToLong<T> mapper, LongPredicate predicate) {
            if (mapper == null) {
                throw new NullPointerException("mapper is null");
            } else if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
            this.mapper = mapper;
        }

        /** {@inheritDoc} */
        public boolean evaluate(T element) {
            return predicate.evaluate(mapper.map(element));
        }

        /**
         * Returns the mapper that will map the object to a long before applying the
         * predicate on it.
         * 
         * @return the mapper that will map the object to a long before applying the
         *         predicate on it
         */
        public MapperToLong<T> getMapper() {
            return mapper;
        }

        /**
         * Returns the Predicate we are testing against.
         * 
         * @return the Predicate we are testing against.
         */
        public LongPredicate getPredicate() {
            return predicate;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "convert " + mapper;
        }
    }

    /**
     * A LongPredicate that always evaluates to <tt>true</tt>. Use {@link #TRUE} to get
     * an instance of this LongPredicate.
     * 
     * @see FalseLongPredicate
     */
    final static class TrueLongPredicate implements LongPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3258129137502925875L;

        /** Creates a new TrueLongPredicate. */
        private TrueLongPredicate() {}

        /** {@inheritDoc} */
        public boolean evaluate(long value) {
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return Boolean.TRUE.toString();
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return TRUE;
        }
    }
}
