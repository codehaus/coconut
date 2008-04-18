/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://cake.codehaus.org/LICENSE
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.ops;

import java.io.Serializable;
import static org.codehaus.cake.ops.Ops.*;
/**
 * Various implementations of {@link LongPredicate}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: LongPredicates.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class LongPredicates {

    /** A LongPredicate that always evaluates to <code>false</code>. */
    public static final LongPredicate FALSE = new FalseLongPredicate();

    /** A LongPredicate that always evaluates to <code>true</code>. */
    public static final LongPredicate TRUE = new TrueLongPredicate();

    /** Cannot instantiate. */
    private LongPredicates() {}
    
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
    
    /**
     * Creates a predicate that accepts any value that is equal to the value specified.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the value of the equals predicate
     * @return a predicate that accepts any value that is equal to the value specified
     */
    public static LongPredicate equalsTo(long element) {
        return new EqualsToLongPredicate(element);
    }
    
    /**
     * Creates a LongPredicate that evaluates to <code>true</code> if the element being
     * tested is greater then the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created LongPredicate
     */
    public static LongPredicate greaterThen(long element) {
        return new GreaterThenLongPredicate(element);
    }

    /**
     * Creates a LongPredicate that evaluates to <code>true</code> if the element being
     * tested is greater then or equals to the element being used to construct the
     * predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created LongPredicate
     */
    public static LongPredicate greaterThenOrEquals(long element) {
        return new GreaterThenOrEqualsLongPredicate(element);
    }

    /**
     * Creates a LongPredicate that evaluates to <code>true</code> if the element being
     * tested is less then the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created LongPredicate
     */
    public static LongPredicate lessThen(long element) {
        return new LessThenLongPredicate(element);
    }

    /**
     * Creates a LongPredicate that evaluates to <code>true</code> if the element being
     * tested is less then or equals to the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created LongPredicate
     */
    public static LongPredicate lessThenOrEquals(long element) {
        return new LessThenOrEqualsLongPredicate(element);
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
    public static LongPredicate not(LongPredicate predicate) {
        return new NotLongPredicate(predicate);
    }

    /**
     * Creates a LongPredicate that performs a logical OR on two supplied predicates. The
     * returned predicate uses short-circuit evaluation (or minimal evaluation). That is,
     * if the specified left side predicate evaluates to <code>true</code> the right
     * side predicate will not be evaluated. More formally
     *
     * <pre>
     * left.evaluate(element) || right.evaluate(element);
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
    public static LongPredicate or(LongPredicate left, LongPredicate right) {
        return new OrLongPredicate(left, right);
    }
    
    /**
     * A LongPredicate that performs a logical exclusive AND on two supplied predicates.
     */
    static final class AndLongPredicate implements LongPredicate, Serializable {

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
        AndLongPredicate(LongPredicate left, LongPredicate right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean op(long element) {
            return left.op(element) && right.op(element);
        }

        /**
         * Returns the left side LongPredicate.
         *
         * @return the left side LongPredicate.
         */
        public LongPredicate getLeft() {
            return left;
        }

        /**
         * Returns the right side LongPredicate.
         *
         * @return the right side LongPredicate.
         */
        public LongPredicate getRight() {
            return right;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }
    
    static class EqualsToLongPredicate implements LongPredicate, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 8220487572042162409L;

        /** The value to compare with. */
        private final long equalsTo;

        EqualsToLongPredicate(long equalsTo) {
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
        public boolean op(long t) {
            return equalsTo == t;
        }

        /**
         * @return the value we are comparing with
         */
        public long getEqualsTo() {
            return equalsTo;
        }
    }
    
     /**
     * a LongPredicate that always evaluates to <tt>false</tt>. Use {@link #FALSE} to
     * get an instance of this LongPredicate.
     *
     * @see TrueLongPredicate
     */
    static final class FalseLongPredicate implements LongPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -3048464662394104180L;

        /** Creates a new FalseLongPredicate. */
        FalseLongPredicate() {}

        /** {@inheritDoc} */
        public boolean op(long value) {
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
    
    static class GreaterThenLongPredicate implements LongPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 7198592614364500859L;

        /** The value to compare with. */
        private final long greaterThen;

        GreaterThenLongPredicate(long greaterThen) {
            this.greaterThen = greaterThen;
        }

        /** {@inheritDoc} */
        public boolean op(long t) {
            return greaterThen < t;
        }

        public long getGreaterThen() {
            return greaterThen;
        }
    }
    
    static class GreaterThenOrEqualsLongPredicate implements LongPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -4681995097900012563L;

        /** The value to compare with. */
        private final long greaterThenOrEquals;

        GreaterThenOrEqualsLongPredicate(long greaterThenOrEquals) {
            this.greaterThenOrEquals = greaterThenOrEquals;
        }

        /** {@inheritDoc} */
        public boolean op(long t) {
            return greaterThenOrEquals <= t;
        }

        public long getGreaterThenOrEquals() {
            return greaterThenOrEquals;
        }
    }
    
    static class LessThenLongPredicate implements LongPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -9180606923416408020L;

        /** The value to compare with. */
        private final long lessThen;

        LessThenLongPredicate(long lessThen) {
            this.lessThen = lessThen;
        }

        /** {@inheritDoc} */
        public boolean op(long t) {
            return lessThen > t;
        }

        public long getLessThen() {
            return lessThen;
        }
    }

    static class LessThenOrEqualsLongPredicate implements LongPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 8711220473905545122L;

        /** The value to compare with. */
        private final long lessThenOrEquals;

        LessThenOrEqualsLongPredicate(long lessThenOrEquals) {
            this.lessThenOrEquals = lessThenOrEquals;
        }

        /** {@inheritDoc} */
        public boolean op(long t) {
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
    static final class NotLongPredicate implements LongPredicate, Serializable {

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
        NotLongPredicate(LongPredicate predicate) {
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
        public boolean op(long element) {
            return !predicate.op(element);
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
     * A LongPredicate that performs a logical exclusive OR on two supplied predicates.
     */
    static final class OrLongPredicate implements LongPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 747277162607915666L;

        /** The left side operand. */
        private final LongPredicate left;

        /** The right side operand. */
        private final LongPredicate right;

        /**
         * Creates a new <code>OrLongPredicate</code>.
         *
         * @param left
         *            the left side LongPredicate
         * @param right
         *            the right side LongPredicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        OrLongPredicate(LongPredicate left, LongPredicate right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean op(long element) {
            return left.op(element) || right.op(element);
        }

        /**
         * Returns the left side LongPredicate.
         *
         * @return the left side LongPredicate.
         */
        public LongPredicate getLeft() {
            return left;
        }

        /**
         * Returns the right side LongPredicate.
         *
         * @return the right side LongPredicate.
         */
        public LongPredicate getRight() {
            return right;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }
    
    /**
     * A LongPredicate that always evaluates to <tt>true</tt>. Use {@link #TRUE} to get
     * an instance of this LongPredicate.
     *
     * @see FalseLongPredicate
     */
    static final class TrueLongPredicate implements LongPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3258129137502925875L;

        /** Creates a new TrueLongPredicate. */
        TrueLongPredicate() {}

        /** {@inheritDoc} */
        public boolean op(long value) {
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