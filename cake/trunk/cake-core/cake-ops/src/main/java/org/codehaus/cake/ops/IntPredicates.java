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
 * Various implementations of {@link IntPredicate}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: IntPredicates.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class IntPredicates {

    /** A IntPredicate that always evaluates to <code>false</code>. */
    public static final IntPredicate FALSE = new FalseIntPredicate();

    /** A IntPredicate that always evaluates to <code>true</code>. */
    public static final IntPredicate TRUE = new TrueIntPredicate();

    ///CLOVER:OFF
    /** Cannot instantiate. */
    private IntPredicates() {}
    ///CLOVER:ON
    
    /**
     * Creates a IntPredicate that performs a logical AND on two supplied predicates. The
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
     *            the left side IntPredicate
     * @param right
     *            the right side IntPredicate
     * @return the newly created IntPredicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static IntPredicate and(IntPredicate left, IntPredicate right) {
        return new AndIntPredicate(left, right);
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
    public static IntPredicate equalsTo(int element) {
        return new EqualsToIntPredicate(element);
    }
    
    /**
     * Creates a IntPredicate that evaluates to <code>true</code> if the element being
     * tested is greater then the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created IntPredicate
     */
    public static IntPredicate greaterThen(int element) {
        return new GreaterThenIntPredicate(element);
    }

    /**
     * Creates a IntPredicate that evaluates to <code>true</code> if the element being
     * tested is greater then or equals to the element being used to construct the
     * predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created IntPredicate
     */
    public static IntPredicate greaterThenOrEquals(int element) {
        return new GreaterThenOrEqualsIntPredicate(element);
    }

    /**
     * Creates a IntPredicate that evaluates to <code>true</code> if the element being
     * tested is less then the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created IntPredicate
     */
    public static IntPredicate lessThen(int element) {
        return new LessThenIntPredicate(element);
    }

    /**
     * Creates a IntPredicate that evaluates to <code>true</code> if the element being
     * tested is less then or equals to the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created IntPredicate
     */
    public static IntPredicate lessThenOrEquals(int element) {
        return new LessThenOrEqualsIntPredicate(element);
    }
    
    /**
     * Creates a IntPredicate that performs a logical logical NOT on the supplied
     * IntPredicate. More formally
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
     * @return the newly created IntPredicate
     * @throws NullPointerException
     *             if the specified predicate is <code>null</code>
     */
    public static IntPredicate not(IntPredicate predicate) {
        return new NotIntPredicate(predicate);
    }

    /**
     * Creates a IntPredicate that performs a logical OR on two supplied predicates. The
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
     *            the left side IntPredicate
     * @param right
     *            the right side IntPredicate
     * @return the newly created IntPredicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static IntPredicate or(IntPredicate left, IntPredicate right) {
        return new OrIntPredicate(left, right);
    }
    
    /**
     * A IntPredicate that performs a logical exclusive AND on two supplied predicates.
     */
    static final class AndIntPredicate implements IntPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 6981902451700512606L;

        /** The left side operand. */
        private final IntPredicate left;

        /** The right side operand. */
        private final IntPredicate right;

        /**
         * Creates a new <code>AndIntPredicate</code>.
         *
         * @param left
         *            the left side IntPredicate
         * @param right
         *            the right side IntPredicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        AndIntPredicate(IntPredicate left, IntPredicate right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean op(int element) {
            return left.op(element) && right.op(element);
        }

        /**
         * Returns the left side IntPredicate.
         *
         * @return the left side IntPredicate.
         */
        public IntPredicate getLeft() {
            return left;
        }

        /**
         * Returns the right side IntPredicate.
         *
         * @return the right side IntPredicate.
         */
        public IntPredicate getRight() {
            return right;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }
    
    static class EqualsToIntPredicate implements IntPredicate, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 8220487572042162409L;

        /** The value to compare with. */
        private final int equalsTo;

        EqualsToIntPredicate(int equalsTo) {
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
        public boolean op(int t) {
            return equalsTo == t;
        }

        /**
         * @return the value we are comparing with
         */
        public int getEqualsTo() {
            return equalsTo;
        }
    }
    
     /**
     * a IntPredicate that always evaluates to <tt>false</tt>. Use {@link #FALSE} to
     * get an instance of this IntPredicate.
     *
     * @see TrueIntPredicate
     */
    static final class FalseIntPredicate implements IntPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -3048464662394104180L;

        /** Creates a new FalseIntPredicate. */
        FalseIntPredicate() {}

        /** {@inheritDoc} */
        public boolean op(int value) {
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
    
    static class GreaterThenIntPredicate implements IntPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 7198592614364500859L;

        /** The value to compare with. */
        private final int greaterThen;

        GreaterThenIntPredicate(int greaterThen) {
            this.greaterThen = greaterThen;
        }

        /** {@inheritDoc} */
        public boolean op(int t) {
            return greaterThen < t;
        }

        public int getGreaterThen() {
            return greaterThen;
        }
    }
    
    static class GreaterThenOrEqualsIntPredicate implements IntPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -4681995097900012563L;

        /** The value to compare with. */
        private final int greaterThenOrEquals;

        GreaterThenOrEqualsIntPredicate(int greaterThenOrEquals) {
            this.greaterThenOrEquals = greaterThenOrEquals;
        }

        /** {@inheritDoc} */
        public boolean op(int t) {
            return greaterThenOrEquals <= t;
        }

        public int getGreaterThenOrEquals() {
            return greaterThenOrEquals;
        }
    }
    
    static class LessThenIntPredicate implements IntPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -9180606923416408020L;

        /** The value to compare with. */
        private final int lessThen;

        LessThenIntPredicate(int lessThen) {
            this.lessThen = lessThen;
        }

        /** {@inheritDoc} */
        public boolean op(int t) {
            return lessThen > t;
        }

        public int getLessThen() {
            return lessThen;
        }
    }

    static class LessThenOrEqualsIntPredicate implements IntPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 8711220473905545122L;

        /** The value to compare with. */
        private final int lessThenOrEquals;

        LessThenOrEqualsIntPredicate(int lessThenOrEquals) {
            this.lessThenOrEquals = lessThenOrEquals;
        }

        /** {@inheritDoc} */
        public boolean op(int t) {
            return lessThenOrEquals >= t;
        }

        public int getLessThenOrEquals() {
            return lessThenOrEquals;
        }
    }
    /**
     * A IntPredicate that evaluates to true iff the Predicate used for constructing
     * evaluates to <code>false</code>.
     */
    static final class NotIntPredicate implements IntPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -5117781730584740429L;

        /** The IntPredicate to negate. */
        private final IntPredicate predicate;

        /**
         * Creates a new NotIntPredicate.
         *
         * @param predicate
         *            the predicate to negate.
         * @throws NullPointerException
         *             if the specified predicate is <code>null</code>
         */
        NotIntPredicate(IntPredicate predicate) {
            if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
        }

        /**
         * Returns a boolean representing the logical NOT value of the supplied
         * IntPredicate.
         *
         * @param element
         *            the element to test
         * @return the logical NOT of the supplied IntPredicate
         */
        public boolean op(int element) {
            return !predicate.op(element);
        }

        /**
         * Returns the predicate that is being negated.
         *
         * @return the predicate that is being negated.
         */
        public IntPredicate getPredicate() {
            return predicate;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "!(" + predicate + ")";
        }
    }

    /**
     * A IntPredicate that performs a logical exclusive OR on two supplied predicates.
     */
    static final class OrIntPredicate implements IntPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 747277162607915666L;

        /** The left side operand. */
        private final IntPredicate left;

        /** The right side operand. */
        private final IntPredicate right;

        /**
         * Creates a new <code>OrIntPredicate</code>.
         *
         * @param left
         *            the left side IntPredicate
         * @param right
         *            the right side IntPredicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        OrIntPredicate(IntPredicate left, IntPredicate right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean op(int element) {
            return left.op(element) || right.op(element);
        }

        /**
         * Returns the left side IntPredicate.
         *
         * @return the left side IntPredicate.
         */
        public IntPredicate getLeft() {
            return left;
        }

        /**
         * Returns the right side IntPredicate.
         *
         * @return the right side IntPredicate.
         */
        public IntPredicate getRight() {
            return right;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }
    
    /**
     * A IntPredicate that always evaluates to <tt>true</tt>. Use {@link #TRUE} to get
     * an instance of this IntPredicate.
     *
     * @see FalseIntPredicate
     */
    static final class TrueIntPredicate implements IntPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3258129137502925875L;

        /** Creates a new TrueIntPredicate. */
        TrueIntPredicate() {}

        /** {@inheritDoc} */
        public boolean op(int value) {
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