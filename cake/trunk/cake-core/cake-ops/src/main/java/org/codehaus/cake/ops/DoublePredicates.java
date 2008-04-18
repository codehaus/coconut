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
 * Various implementations of {@link DoublePredicate}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: DoublePredicates.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class DoublePredicates {

    /** A DoublePredicate that always evaluates to <code>false</code>. */
    public static final DoublePredicate FALSE = new FalseDoublePredicate();

    /** A DoublePredicate that always evaluates to <code>true</code>. */
    public static final DoublePredicate TRUE = new TrueDoublePredicate();

    ///CLOVER:OFF
    /** Cannot instantiate. */
    private DoublePredicates() {}
    ///CLOVER:ON
    
    /**
     * Creates a DoublePredicate that performs a logical AND on two supplied predicates. The
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
     *            the left side DoublePredicate
     * @param right
     *            the right side DoublePredicate
     * @return the newly created DoublePredicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static DoublePredicate and(DoublePredicate left, DoublePredicate right) {
        return new AndDoublePredicate(left, right);
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
    public static DoublePredicate equalsTo(double element) {
        return new EqualsToDoublePredicate(element);
    }
    
    /**
     * Creates a DoublePredicate that evaluates to <code>true</code> if the element being
     * tested is greater then the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created DoublePredicate
     */
    public static DoublePredicate greaterThen(double element) {
        return new GreaterThenDoublePredicate(element);
    }

    /**
     * Creates a DoublePredicate that evaluates to <code>true</code> if the element being
     * tested is greater then or equals to the element being used to construct the
     * predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created DoublePredicate
     */
    public static DoublePredicate greaterThenOrEquals(double element) {
        return new GreaterThenOrEqualsDoublePredicate(element);
    }

    /**
     * Creates a DoublePredicate that evaluates to <code>true</code> if the element being
     * tested is less then the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created DoublePredicate
     */
    public static DoublePredicate lessThen(double element) {
        return new LessThenDoublePredicate(element);
    }

    /**
     * Creates a DoublePredicate that evaluates to <code>true</code> if the element being
     * tested is less then or equals to the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created DoublePredicate
     */
    public static DoublePredicate lessThenOrEquals(double element) {
        return new LessThenOrEqualsDoublePredicate(element);
    }
    
    /**
     * Creates a DoublePredicate that performs a logical logical NOT on the supplied
     * DoublePredicate. More formally
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
     * @return the newly created DoublePredicate
     * @throws NullPointerException
     *             if the specified predicate is <code>null</code>
     */
    public static DoublePredicate not(DoublePredicate predicate) {
        return new NotDoublePredicate(predicate);
    }

    /**
     * Creates a DoublePredicate that performs a logical OR on two supplied predicates. The
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
     *            the left side DoublePredicate
     * @param right
     *            the right side DoublePredicate
     * @return the newly created DoublePredicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static DoublePredicate or(DoublePredicate left, DoublePredicate right) {
        return new OrDoublePredicate(left, right);
    }
    
    /**
     * A DoublePredicate that performs a logical exclusive AND on two supplied predicates.
     */
    static final class AndDoublePredicate implements DoublePredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 6981902451700512606L;

        /** The left side operand. */
        private final DoublePredicate left;

        /** The right side operand. */
        private final DoublePredicate right;

        /**
         * Creates a new <code>AndDoublePredicate</code>.
         *
         * @param left
         *            the left side DoublePredicate
         * @param right
         *            the right side DoublePredicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        AndDoublePredicate(DoublePredicate left, DoublePredicate right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean op(double element) {
            return left.op(element) && right.op(element);
        }

        /**
         * Returns the left side DoublePredicate.
         *
         * @return the left side DoublePredicate.
         */
        public DoublePredicate getLeft() {
            return left;
        }

        /**
         * Returns the right side DoublePredicate.
         *
         * @return the right side DoublePredicate.
         */
        public DoublePredicate getRight() {
            return right;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }
    
    static class EqualsToDoublePredicate implements DoublePredicate, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 8220487572042162409L;

        /** The value to compare with. */
        private final double equalsTo;

        EqualsToDoublePredicate(double equalsTo) {
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
        public boolean op(double t) {
            return equalsTo == t;
        }

        /**
         * @return the value we are comparing with
         */
        public double getEqualsTo() {
            return equalsTo;
        }
    }
    
     /**
     * a DoublePredicate that always evaluates to <tt>false</tt>. Use {@link #FALSE} to
     * get an instance of this DoublePredicate.
     *
     * @see TrueDoublePredicate
     */
    static final class FalseDoublePredicate implements DoublePredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -3048464662394104180L;

        /** Creates a new FalseDoublePredicate. */
        FalseDoublePredicate() {}

        /** {@inheritDoc} */
        public boolean op(double value) {
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
    
    static class GreaterThenDoublePredicate implements DoublePredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 7198592614364500859L;

        /** The value to compare with. */
        private final double greaterThen;

        GreaterThenDoublePredicate(double greaterThen) {
            this.greaterThen = greaterThen;
        }

        /** {@inheritDoc} */
        public boolean op(double t) {
            return greaterThen < t;
        }

        public double getGreaterThen() {
            return greaterThen;
        }
    }
    
    static class GreaterThenOrEqualsDoublePredicate implements DoublePredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -4681995097900012563L;

        /** The value to compare with. */
        private final double greaterThenOrEquals;

        GreaterThenOrEqualsDoublePredicate(double greaterThenOrEquals) {
            this.greaterThenOrEquals = greaterThenOrEquals;
        }

        /** {@inheritDoc} */
        public boolean op(double t) {
            return greaterThenOrEquals <= t;
        }

        public double getGreaterThenOrEquals() {
            return greaterThenOrEquals;
        }
    }
    
    static class LessThenDoublePredicate implements DoublePredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -9180606923416408020L;

        /** The value to compare with. */
        private final double lessThen;

        LessThenDoublePredicate(double lessThen) {
            this.lessThen = lessThen;
        }

        /** {@inheritDoc} */
        public boolean op(double t) {
            return lessThen > t;
        }

        public double getLessThen() {
            return lessThen;
        }
    }

    static class LessThenOrEqualsDoublePredicate implements DoublePredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 8711220473905545122L;

        /** The value to compare with. */
        private final double lessThenOrEquals;

        LessThenOrEqualsDoublePredicate(double lessThenOrEquals) {
            this.lessThenOrEquals = lessThenOrEquals;
        }

        /** {@inheritDoc} */
        public boolean op(double t) {
            return lessThenOrEquals >= t;
        }

        public double getLessThenOrEquals() {
            return lessThenOrEquals;
        }
    }
    /**
     * A DoublePredicate that evaluates to true iff the Predicate used for constructing
     * evaluates to <code>false</code>.
     */
    static final class NotDoublePredicate implements DoublePredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -5117781730584740429L;

        /** The DoublePredicate to negate. */
        private final DoublePredicate predicate;

        /**
         * Creates a new NotDoublePredicate.
         *
         * @param predicate
         *            the predicate to negate.
         * @throws NullPointerException
         *             if the specified predicate is <code>null</code>
         */
        NotDoublePredicate(DoublePredicate predicate) {
            if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
        }

        /**
         * Returns a boolean representing the logical NOT value of the supplied
         * DoublePredicate.
         *
         * @param element
         *            the element to test
         * @return the logical NOT of the supplied DoublePredicate
         */
        public boolean op(double element) {
            return !predicate.op(element);
        }

        /**
         * Returns the predicate that is being negated.
         *
         * @return the predicate that is being negated.
         */
        public DoublePredicate getPredicate() {
            return predicate;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "!(" + predicate + ")";
        }
    }

    /**
     * A DoublePredicate that performs a logical exclusive OR on two supplied predicates.
     */
    static final class OrDoublePredicate implements DoublePredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 747277162607915666L;

        /** The left side operand. */
        private final DoublePredicate left;

        /** The right side operand. */
        private final DoublePredicate right;

        /**
         * Creates a new <code>OrDoublePredicate</code>.
         *
         * @param left
         *            the left side DoublePredicate
         * @param right
         *            the right side DoublePredicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        OrDoublePredicate(DoublePredicate left, DoublePredicate right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean op(double element) {
            return left.op(element) || right.op(element);
        }

        /**
         * Returns the left side DoublePredicate.
         *
         * @return the left side DoublePredicate.
         */
        public DoublePredicate getLeft() {
            return left;
        }

        /**
         * Returns the right side DoublePredicate.
         *
         * @return the right side DoublePredicate.
         */
        public DoublePredicate getRight() {
            return right;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }
    
    /**
     * A DoublePredicate that always evaluates to <tt>true</tt>. Use {@link #TRUE} to get
     * an instance of this DoublePredicate.
     *
     * @see FalseDoublePredicate
     */
    static final class TrueDoublePredicate implements DoublePredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3258129137502925875L;

        /** Creates a new TrueDoublePredicate. */
        TrueDoublePredicate() {}

        /** {@inheritDoc} */
        public boolean op(double value) {
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