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
 * Various implementations of {@link ShortPredicate}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ShortPredicates.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class ShortPredicates {

    /** A ShortPredicate that always evaluates to <code>false</code>. */
    public static final ShortPredicate FALSE = new FalseShortPredicate();

    /** A ShortPredicate that always evaluates to <code>true</code>. */
    public static final ShortPredicate TRUE = new TrueShortPredicate();

    ///CLOVER:OFF
    /** Cannot instantiate. */
    private ShortPredicates() {}
    ///CLOVER:ON
    
    /**
     * Creates a ShortPredicate that performs a logical AND on two supplied predicates. The
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
     *            the left side ShortPredicate
     * @param right
     *            the right side ShortPredicate
     * @return the newly created ShortPredicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static ShortPredicate and(ShortPredicate left, ShortPredicate right) {
        return new AndShortPredicate(left, right);
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
    public static ShortPredicate equalsTo(short element) {
        return new EqualsToShortPredicate(element);
    }
    
    /**
     * Creates a ShortPredicate that evaluates to <code>true</code> if the element being
     * tested is greater then the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created ShortPredicate
     */
    public static ShortPredicate greaterThen(short element) {
        return new GreaterThenShortPredicate(element);
    }

    /**
     * Creates a ShortPredicate that evaluates to <code>true</code> if the element being
     * tested is greater then or equals to the element being used to construct the
     * predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created ShortPredicate
     */
    public static ShortPredicate greaterThenOrEquals(short element) {
        return new GreaterThenOrEqualsShortPredicate(element);
    }

    /**
     * Creates a ShortPredicate that evaluates to <code>true</code> if the element being
     * tested is less then the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created ShortPredicate
     */
    public static ShortPredicate lessThen(short element) {
        return new LessThenShortPredicate(element);
    }

    /**
     * Creates a ShortPredicate that evaluates to <code>true</code> if the element being
     * tested is less then or equals to the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created ShortPredicate
     */
    public static ShortPredicate lessThenOrEquals(short element) {
        return new LessThenOrEqualsShortPredicate(element);
    }
    
    /**
     * Creates a ShortPredicate that performs a logical logical NOT on the supplied
     * ShortPredicate. More formally
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
     * @return the newly created ShortPredicate
     * @throws NullPointerException
     *             if the specified predicate is <code>null</code>
     */
    public static ShortPredicate not(ShortPredicate predicate) {
        return new NotShortPredicate(predicate);
    }

    /**
     * Creates a ShortPredicate that performs a logical OR on two supplied predicates. The
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
     *            the left side ShortPredicate
     * @param right
     *            the right side ShortPredicate
     * @return the newly created ShortPredicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static ShortPredicate or(ShortPredicate left, ShortPredicate right) {
        return new OrShortPredicate(left, right);
    }
    
    /**
     * A ShortPredicate that performs a logical exclusive AND on two supplied predicates.
     */
    static final class AndShortPredicate implements ShortPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 6981902451700512606L;

        /** The left side operand. */
        private final ShortPredicate left;

        /** The right side operand. */
        private final ShortPredicate right;

        /**
         * Creates a new <code>AndShortPredicate</code>.
         *
         * @param left
         *            the left side ShortPredicate
         * @param right
         *            the right side ShortPredicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        AndShortPredicate(ShortPredicate left, ShortPredicate right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean op(short element) {
            return left.op(element) && right.op(element);
        }

        /**
         * Returns the left side ShortPredicate.
         *
         * @return the left side ShortPredicate.
         */
        public ShortPredicate getLeft() {
            return left;
        }

        /**
         * Returns the right side ShortPredicate.
         *
         * @return the right side ShortPredicate.
         */
        public ShortPredicate getRight() {
            return right;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }
    
    static class EqualsToShortPredicate implements ShortPredicate, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 8220487572042162409L;

        /** The value to compare with. */
        private final short equalsTo;

        EqualsToShortPredicate(short equalsTo) {
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
        public boolean op(short t) {
            return equalsTo == t;
        }

        /**
         * @return the value we are comparing with
         */
        public short getEqualsTo() {
            return equalsTo;
        }
    }
    
     /**
     * a ShortPredicate that always evaluates to <tt>false</tt>. Use {@link #FALSE} to
     * get an instance of this ShortPredicate.
     *
     * @see TrueShortPredicate
     */
    static final class FalseShortPredicate implements ShortPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -3048464662394104180L;

        /** Creates a new FalseShortPredicate. */
        FalseShortPredicate() {}

        /** {@inheritDoc} */
        public boolean op(short value) {
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
    
    static class GreaterThenShortPredicate implements ShortPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 7198592614364500859L;

        /** The value to compare with. */
        private final short greaterThen;

        GreaterThenShortPredicate(short greaterThen) {
            this.greaterThen = greaterThen;
        }

        /** {@inheritDoc} */
        public boolean op(short t) {
            return greaterThen < t;
        }

        public short getGreaterThen() {
            return greaterThen;
        }
    }
    
    static class GreaterThenOrEqualsShortPredicate implements ShortPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -4681995097900012563L;

        /** The value to compare with. */
        private final short greaterThenOrEquals;

        GreaterThenOrEqualsShortPredicate(short greaterThenOrEquals) {
            this.greaterThenOrEquals = greaterThenOrEquals;
        }

        /** {@inheritDoc} */
        public boolean op(short t) {
            return greaterThenOrEquals <= t;
        }

        public short getGreaterThenOrEquals() {
            return greaterThenOrEquals;
        }
    }
    
    static class LessThenShortPredicate implements ShortPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -9180606923416408020L;

        /** The value to compare with. */
        private final short lessThen;

        LessThenShortPredicate(short lessThen) {
            this.lessThen = lessThen;
        }

        /** {@inheritDoc} */
        public boolean op(short t) {
            return lessThen > t;
        }

        public short getLessThen() {
            return lessThen;
        }
    }

    static class LessThenOrEqualsShortPredicate implements ShortPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 8711220473905545122L;

        /** The value to compare with. */
        private final short lessThenOrEquals;

        LessThenOrEqualsShortPredicate(short lessThenOrEquals) {
            this.lessThenOrEquals = lessThenOrEquals;
        }

        /** {@inheritDoc} */
        public boolean op(short t) {
            return lessThenOrEquals >= t;
        }

        public short getLessThenOrEquals() {
            return lessThenOrEquals;
        }
    }
    /**
     * A ShortPredicate that evaluates to true iff the Predicate used for constructing
     * evaluates to <code>false</code>.
     */
    static final class NotShortPredicate implements ShortPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -5117781730584740429L;

        /** The ShortPredicate to negate. */
        private final ShortPredicate predicate;

        /**
         * Creates a new NotShortPredicate.
         *
         * @param predicate
         *            the predicate to negate.
         * @throws NullPointerException
         *             if the specified predicate is <code>null</code>
         */
        NotShortPredicate(ShortPredicate predicate) {
            if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
        }

        /**
         * Returns a boolean representing the logical NOT value of the supplied
         * ShortPredicate.
         *
         * @param element
         *            the element to test
         * @return the logical NOT of the supplied ShortPredicate
         */
        public boolean op(short element) {
            return !predicate.op(element);
        }

        /**
         * Returns the predicate that is being negated.
         *
         * @return the predicate that is being negated.
         */
        public ShortPredicate getPredicate() {
            return predicate;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "!(" + predicate + ")";
        }
    }

    /**
     * A ShortPredicate that performs a logical exclusive OR on two supplied predicates.
     */
    static final class OrShortPredicate implements ShortPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 747277162607915666L;

        /** The left side operand. */
        private final ShortPredicate left;

        /** The right side operand. */
        private final ShortPredicate right;

        /**
         * Creates a new <code>OrShortPredicate</code>.
         *
         * @param left
         *            the left side ShortPredicate
         * @param right
         *            the right side ShortPredicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        OrShortPredicate(ShortPredicate left, ShortPredicate right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean op(short element) {
            return left.op(element) || right.op(element);
        }

        /**
         * Returns the left side ShortPredicate.
         *
         * @return the left side ShortPredicate.
         */
        public ShortPredicate getLeft() {
            return left;
        }

        /**
         * Returns the right side ShortPredicate.
         *
         * @return the right side ShortPredicate.
         */
        public ShortPredicate getRight() {
            return right;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }
    
    /**
     * A ShortPredicate that always evaluates to <tt>true</tt>. Use {@link #TRUE} to get
     * an instance of this ShortPredicate.
     *
     * @see FalseShortPredicate
     */
    static final class TrueShortPredicate implements ShortPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3258129137502925875L;

        /** Creates a new TrueShortPredicate. */
        TrueShortPredicate() {}

        /** {@inheritDoc} */
        public boolean op(short value) {
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