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
 * Various implementations of {@link BytePredicate}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: BytePredicates.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class BytePredicates {

    /** A BytePredicate that always evaluates to <code>false</code>. */
    public static final BytePredicate FALSE = new FalseBytePredicate();

    /** A BytePredicate that always evaluates to <code>true</code>. */
    public static final BytePredicate TRUE = new TrueBytePredicate();

    ///CLOVER:OFF
    /** Cannot instantiate. */
    private BytePredicates() {}
    ///CLOVER:ON
    
    /**
     * Creates a BytePredicate that performs a logical AND on two supplied predicates. The
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
     *            the left side BytePredicate
     * @param right
     *            the right side BytePredicate
     * @return the newly created BytePredicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static BytePredicate and(BytePredicate left, BytePredicate right) {
        return new AndBytePredicate(left, right);
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
    public static BytePredicate equalsTo(byte element) {
        return new EqualsToBytePredicate(element);
    }
    
    /**
     * Creates a BytePredicate that evaluates to <code>true</code> if the element being
     * tested is greater then the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created BytePredicate
     */
    public static BytePredicate greaterThen(byte element) {
        return new GreaterThenBytePredicate(element);
    }

    /**
     * Creates a BytePredicate that evaluates to <code>true</code> if the element being
     * tested is greater then or equals to the element being used to construct the
     * predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created BytePredicate
     */
    public static BytePredicate greaterThenOrEquals(byte element) {
        return new GreaterThenOrEqualsBytePredicate(element);
    }

    /**
     * Creates a BytePredicate that evaluates to <code>true</code> if the element being
     * tested is less then the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created BytePredicate
     */
    public static BytePredicate lessThen(byte element) {
        return new LessThenBytePredicate(element);
    }

    /**
     * Creates a BytePredicate that evaluates to <code>true</code> if the element being
     * tested is less then or equals to the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created BytePredicate
     */
    public static BytePredicate lessThenOrEquals(byte element) {
        return new LessThenOrEqualsBytePredicate(element);
    }
    
    /**
     * Creates a BytePredicate that performs a logical logical NOT on the supplied
     * BytePredicate. More formally
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
     * @return the newly created BytePredicate
     * @throws NullPointerException
     *             if the specified predicate is <code>null</code>
     */
    public static BytePredicate not(BytePredicate predicate) {
        return new NotBytePredicate(predicate);
    }

    /**
     * Creates a BytePredicate that performs a logical OR on two supplied predicates. The
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
     *            the left side BytePredicate
     * @param right
     *            the right side BytePredicate
     * @return the newly created BytePredicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static BytePredicate or(BytePredicate left, BytePredicate right) {
        return new OrBytePredicate(left, right);
    }
    
    /**
     * A BytePredicate that performs a logical exclusive AND on two supplied predicates.
     */
    static final class AndBytePredicate implements BytePredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 6981902451700512606L;

        /** The left side operand. */
        private final BytePredicate left;

        /** The right side operand. */
        private final BytePredicate right;

        /**
         * Creates a new <code>AndBytePredicate</code>.
         *
         * @param left
         *            the left side BytePredicate
         * @param right
         *            the right side BytePredicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        AndBytePredicate(BytePredicate left, BytePredicate right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean op(byte element) {
            return left.op(element) && right.op(element);
        }

        /**
         * Returns the left side BytePredicate.
         *
         * @return the left side BytePredicate.
         */
        public BytePredicate getLeft() {
            return left;
        }

        /**
         * Returns the right side BytePredicate.
         *
         * @return the right side BytePredicate.
         */
        public BytePredicate getRight() {
            return right;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }
    
    static class EqualsToBytePredicate implements BytePredicate, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 8220487572042162409L;

        /** The value to compare with. */
        private final byte equalsTo;

        EqualsToBytePredicate(byte equalsTo) {
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
        public boolean op(byte t) {
            return equalsTo == t;
        }

        /**
         * @return the value we are comparing with
         */
        public byte getEqualsTo() {
            return equalsTo;
        }
    }
    
     /**
     * a BytePredicate that always evaluates to <tt>false</tt>. Use {@link #FALSE} to
     * get an instance of this BytePredicate.
     *
     * @see TrueBytePredicate
     */
    static final class FalseBytePredicate implements BytePredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -3048464662394104180L;

        /** Creates a new FalseBytePredicate. */
        FalseBytePredicate() {}

        /** {@inheritDoc} */
        public boolean op(byte value) {
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
    
    static class GreaterThenBytePredicate implements BytePredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 7198592614364500859L;

        /** The value to compare with. */
        private final byte greaterThen;

        GreaterThenBytePredicate(byte greaterThen) {
            this.greaterThen = greaterThen;
        }

        /** {@inheritDoc} */
        public boolean op(byte t) {
            return greaterThen < t;
        }

        public byte getGreaterThen() {
            return greaterThen;
        }
    }
    
    static class GreaterThenOrEqualsBytePredicate implements BytePredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -4681995097900012563L;

        /** The value to compare with. */
        private final byte greaterThenOrEquals;

        GreaterThenOrEqualsBytePredicate(byte greaterThenOrEquals) {
            this.greaterThenOrEquals = greaterThenOrEquals;
        }

        /** {@inheritDoc} */
        public boolean op(byte t) {
            return greaterThenOrEquals <= t;
        }

        public byte getGreaterThenOrEquals() {
            return greaterThenOrEquals;
        }
    }
    
    static class LessThenBytePredicate implements BytePredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -9180606923416408020L;

        /** The value to compare with. */
        private final byte lessThen;

        LessThenBytePredicate(byte lessThen) {
            this.lessThen = lessThen;
        }

        /** {@inheritDoc} */
        public boolean op(byte t) {
            return lessThen > t;
        }

        public byte getLessThen() {
            return lessThen;
        }
    }

    static class LessThenOrEqualsBytePredicate implements BytePredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 8711220473905545122L;

        /** The value to compare with. */
        private final byte lessThenOrEquals;

        LessThenOrEqualsBytePredicate(byte lessThenOrEquals) {
            this.lessThenOrEquals = lessThenOrEquals;
        }

        /** {@inheritDoc} */
        public boolean op(byte t) {
            return lessThenOrEquals >= t;
        }

        public byte getLessThenOrEquals() {
            return lessThenOrEquals;
        }
    }
    /**
     * A BytePredicate that evaluates to true iff the Predicate used for constructing
     * evaluates to <code>false</code>.
     */
    static final class NotBytePredicate implements BytePredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -5117781730584740429L;

        /** The BytePredicate to negate. */
        private final BytePredicate predicate;

        /**
         * Creates a new NotBytePredicate.
         *
         * @param predicate
         *            the predicate to negate.
         * @throws NullPointerException
         *             if the specified predicate is <code>null</code>
         */
        NotBytePredicate(BytePredicate predicate) {
            if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
        }

        /**
         * Returns a boolean representing the logical NOT value of the supplied
         * BytePredicate.
         *
         * @param element
         *            the element to test
         * @return the logical NOT of the supplied BytePredicate
         */
        public boolean op(byte element) {
            return !predicate.op(element);
        }

        /**
         * Returns the predicate that is being negated.
         *
         * @return the predicate that is being negated.
         */
        public BytePredicate getPredicate() {
            return predicate;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "!(" + predicate + ")";
        }
    }

    /**
     * A BytePredicate that performs a logical exclusive OR on two supplied predicates.
     */
    static final class OrBytePredicate implements BytePredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 747277162607915666L;

        /** The left side operand. */
        private final BytePredicate left;

        /** The right side operand. */
        private final BytePredicate right;

        /**
         * Creates a new <code>OrBytePredicate</code>.
         *
         * @param left
         *            the left side BytePredicate
         * @param right
         *            the right side BytePredicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        OrBytePredicate(BytePredicate left, BytePredicate right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean op(byte element) {
            return left.op(element) || right.op(element);
        }

        /**
         * Returns the left side BytePredicate.
         *
         * @return the left side BytePredicate.
         */
        public BytePredicate getLeft() {
            return left;
        }

        /**
         * Returns the right side BytePredicate.
         *
         * @return the right side BytePredicate.
         */
        public BytePredicate getRight() {
            return right;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }
    
    /**
     * A BytePredicate that always evaluates to <tt>true</tt>. Use {@link #TRUE} to get
     * an instance of this BytePredicate.
     *
     * @see FalseBytePredicate
     */
    static final class TrueBytePredicate implements BytePredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3258129137502925875L;

        /** Creates a new TrueBytePredicate. */
        TrueBytePredicate() {}

        /** {@inheritDoc} */
        public boolean op(byte value) {
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