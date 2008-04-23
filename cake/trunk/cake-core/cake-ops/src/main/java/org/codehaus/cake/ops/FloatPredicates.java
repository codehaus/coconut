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

import org.codehaus.cake.ops.Ops.FloatPredicate;

/**
 * Various implementations of {@link FloatPredicate}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: FloatPredicates.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class FloatPredicates {

    /** A FloatPredicate that always evaluates to <code>false</code>. */
    public static final FloatPredicate FALSE = new FalseFloatPredicate();

    /** A FloatPredicate that always evaluates to <code>true</code>. */
    public static final FloatPredicate TRUE = new TrueFloatPredicate();

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private FloatPredicates() {}

    // /CLOVER:ON

    /**
     * Creates a FloatPredicate that performs a logical AND on two supplied predicates. The returned
     * predicate uses short-circuit evaluation (or minimal evaluation). That is, if the specified
     * left side predicate evaluates to <code>false</code> the right side predicate will not be
     * evaluated. More formally
     * 
     * <pre>
     * left.evaluate(element) &amp;&amp; right.evaluate(element);
     * </pre>
     * 
     * <p>
     * If both of the supplied predicates are serializable the returned predicate will also be
     * serializable.
     * 
     * @param left
     *            the left side FloatPredicate
     * @param right
     *            the right side FloatPredicate
     * @return the newly created FloatPredicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static FloatPredicate and(FloatPredicate left, FloatPredicate right) {
        return new AndFloatPredicate(left, right);
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
    public static FloatPredicate equalsTo(float element) {
        return new EqualsToFloatPredicate(element);
    }

    /**
     * Creates a FloatPredicate that evaluates to <code>true</code> if the element being tested is
     * greater then the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     * 
     * @param element
     *            the element to compare with
     * @return the newly created FloatPredicate
     */
    public static FloatPredicate greaterThen(float element) {
        return new GreaterThenFloatPredicate(element);
    }

    /**
     * Creates a FloatPredicate that evaluates to <code>true</code> if the element being tested is
     * greater then or equals to the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     * 
     * @param element
     *            the element to compare with
     * @return the newly created FloatPredicate
     */
    public static FloatPredicate greaterThenOrEquals(float element) {
        return new GreaterThenOrEqualsFloatPredicate(element);
    }

    /**
     * Creates a FloatPredicate that evaluates to <code>true</code> if the element being tested is
     * less then the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     * 
     * @param element
     *            the element to compare with
     * @return the newly created FloatPredicate
     */
    public static FloatPredicate lessThen(float element) {
        return new LessThenFloatPredicate(element);
    }

    /**
     * Creates a FloatPredicate that evaluates to <code>true</code> if the element being tested is
     * less then or equals to the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     * 
     * @param element
     *            the element to compare with
     * @return the newly created FloatPredicate
     */
    public static FloatPredicate lessThenOrEquals(float element) {
        return new LessThenOrEqualsFloatPredicate(element);
    }

    /**
     * Creates a FloatPredicate that performs a logical logical NOT on the supplied FloatPredicate.
     * More formally
     * 
     * <pre>
     * !predicate.evaluate(value);
     * </pre>
     * 
     * <p>
     * If the specified predicate is serializable the returned predicate will also be serializable.
     * 
     * @param predicate
     *            the predicate to negate
     * @return the newly created FloatPredicate
     * @throws NullPointerException
     *             if the specified predicate is <code>null</code>
     */
    public static FloatPredicate not(FloatPredicate predicate) {
        return new NotFloatPredicate(predicate);
    }

    /**
     * Creates a FloatPredicate that performs a logical OR on two supplied predicates. The returned
     * predicate uses short-circuit evaluation (or minimal evaluation). That is, if the specified
     * left side predicate evaluates to <code>true</code> the right side predicate will not be
     * evaluated. More formally
     * 
     * <pre>
     * left.evaluate(element) || right.evaluate(element);
     * </pre>
     * 
     * <p>
     * If both of the supplied predicates are serializable the returned predicate will also be
     * serializable.
     * 
     * @param left
     *            the left side FloatPredicate
     * @param right
     *            the right side FloatPredicate
     * @return the newly created FloatPredicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static FloatPredicate or(FloatPredicate left, FloatPredicate right) {
        return new OrFloatPredicate(left, right);
    }

    /**
     * A FloatPredicate that performs a logical exclusive AND on two supplied predicates.
     */
    static final class AndFloatPredicate implements FloatPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 6981902451700512606L;

        /** The left side operand. */
        private final FloatPredicate left;

        /** The right side operand. */
        private final FloatPredicate right;

        /**
         * Creates a new <code>AndFloatPredicate</code>.
         * 
         * @param left
         *            the left side FloatPredicate
         * @param right
         *            the right side FloatPredicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        AndFloatPredicate(FloatPredicate left, FloatPredicate right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /**
         * Returns the left side FloatPredicate.
         * 
         * @return the left side FloatPredicate.
         */
        public FloatPredicate getLeft() {
            return left;
        }

        /**
         * Returns the right side FloatPredicate.
         * 
         * @return the right side FloatPredicate.
         */
        public FloatPredicate getRight() {
            return right;
        }

        /** {@inheritDoc} */
        public boolean op(float element) {
            return left.op(element) && right.op(element);
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }

    static class EqualsToFloatPredicate implements FloatPredicate, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 8220487572042162409L;

        /** The value to compare with. */
        private final float equalsTo;

        EqualsToFloatPredicate(float equalsTo) {
            this.equalsTo = equalsTo;
        }

        /**
         * @return the value we are comparing with
         */
        public float getEqualsTo() {
            return equalsTo;
        }

        /**
         * Returns <code>true</code> if the specified value is equal to the value that was used
         * when constructing this predicate, otherwise <code>false</code>.
         * 
         * @param t
         *            the value to compare with
         * @return <code>true</code> if the specified value is equal to the value that was used
         *         when constructing this predicate, otherwise <code>false</code>.
         */
        public boolean op(float t) {
            return equalsTo == t;
        }
    }

    /**
     * a FloatPredicate that always evaluates to <tt>false</tt>. Use {@link #FALSE} to get an
     * instance of this FloatPredicate.
     * 
     * @see TrueFloatPredicate
     */
    static final class FalseFloatPredicate implements FloatPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -3048464662394104180L;

        /** Creates a new FalseFloatPredicate. */
        FalseFloatPredicate() {}

        /** {@inheritDoc} */
        public boolean op(float value) {
            return false;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return FALSE;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return Boolean.FALSE.toString();
        }
    }

    static class GreaterThenFloatPredicate implements FloatPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 7198592614364500859L;

        /** The value to compare with. */
        private final float greaterThen;

        GreaterThenFloatPredicate(float greaterThen) {
            this.greaterThen = greaterThen;
        }

        public float getGreaterThen() {
            return greaterThen;
        }

        /** {@inheritDoc} */
        public boolean op(float t) {
            return greaterThen < t;
        }
    }

    static class GreaterThenOrEqualsFloatPredicate implements FloatPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -4681995097900012563L;

        /** The value to compare with. */
        private final float greaterThenOrEquals;

        GreaterThenOrEqualsFloatPredicate(float greaterThenOrEquals) {
            this.greaterThenOrEquals = greaterThenOrEquals;
        }

        public float getGreaterThenOrEquals() {
            return greaterThenOrEquals;
        }

        /** {@inheritDoc} */
        public boolean op(float t) {
            return greaterThenOrEquals <= t;
        }
    }

    static class LessThenFloatPredicate implements FloatPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -9180606923416408020L;

        /** The value to compare with. */
        private final float lessThen;

        LessThenFloatPredicate(float lessThen) {
            this.lessThen = lessThen;
        }

        public float getLessThen() {
            return lessThen;
        }

        /** {@inheritDoc} */
        public boolean op(float t) {
            return lessThen > t;
        }
    }

    static class LessThenOrEqualsFloatPredicate implements FloatPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 8711220473905545122L;

        /** The value to compare with. */
        private final float lessThenOrEquals;

        LessThenOrEqualsFloatPredicate(float lessThenOrEquals) {
            this.lessThenOrEquals = lessThenOrEquals;
        }

        public float getLessThenOrEquals() {
            return lessThenOrEquals;
        }

        /** {@inheritDoc} */
        public boolean op(float t) {
            return lessThenOrEquals >= t;
        }
    }

    /**
     * A FloatPredicate that evaluates to true iff the Predicate used for constructing evaluates to
     * <code>false</code>.
     */
    static final class NotFloatPredicate implements FloatPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -5117781730584740429L;

        /** The FloatPredicate to negate. */
        private final FloatPredicate predicate;

        /**
         * Creates a new NotFloatPredicate.
         * 
         * @param predicate
         *            the predicate to negate.
         * @throws NullPointerException
         *             if the specified predicate is <code>null</code>
         */
        NotFloatPredicate(FloatPredicate predicate) {
            if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
        }

        /**
         * Returns the predicate that is being negated.
         * 
         * @return the predicate that is being negated.
         */
        public FloatPredicate getPredicate() {
            return predicate;
        }

        /**
         * Returns a boolean representing the logical NOT value of the supplied FloatPredicate.
         * 
         * @param element
         *            the element to test
         * @return the logical NOT of the supplied FloatPredicate
         */
        public boolean op(float element) {
            return !predicate.op(element);
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "!(" + predicate + ")";
        }
    }

    /**
     * A FloatPredicate that performs a logical exclusive OR on two supplied predicates.
     */
    static final class OrFloatPredicate implements FloatPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 747277162607915666L;

        /** The left side operand. */
        private final FloatPredicate left;

        /** The right side operand. */
        private final FloatPredicate right;

        /**
         * Creates a new <code>OrFloatPredicate</code>.
         * 
         * @param left
         *            the left side FloatPredicate
         * @param right
         *            the right side FloatPredicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        OrFloatPredicate(FloatPredicate left, FloatPredicate right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /**
         * Returns the left side FloatPredicate.
         * 
         * @return the left side FloatPredicate.
         */
        public FloatPredicate getLeft() {
            return left;
        }

        /**
         * Returns the right side FloatPredicate.
         * 
         * @return the right side FloatPredicate.
         */
        public FloatPredicate getRight() {
            return right;
        }

        /** {@inheritDoc} */
        public boolean op(float element) {
            return left.op(element) || right.op(element);
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }

    /**
     * A FloatPredicate that always evaluates to <tt>true</tt>. Use {@link #TRUE} to get an
     * instance of this FloatPredicate.
     * 
     * @see FalseFloatPredicate
     */
    static final class TrueFloatPredicate implements FloatPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3258129137502925875L;

        /** Creates a new TrueFloatPredicate. */
        TrueFloatPredicate() {}

        /** {@inheritDoc} */
        public boolean op(float value) {
            return true;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return TRUE;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return Boolean.TRUE.toString();
        }
    }
}
