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
 * Various implementations of {@link CharPredicate}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CharPredicates.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class CharPredicates {

    /** A CharPredicate that always evaluates to <code>false</code>. */
    public static final CharPredicate FALSE = new FalseCharPredicate();

    /** A CharPredicate that always evaluates to <code>true</code>. */
    public static final CharPredicate TRUE = new TrueCharPredicate();

    /** Cannot instantiate. */
    private CharPredicates() {}
    
    /**
     * Creates a CharPredicate that performs a logical AND on two supplied predicates. The
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
     *            the left side CharPredicate
     * @param right
     *            the right side CharPredicate
     * @return the newly created CharPredicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static CharPredicate and(CharPredicate left, CharPredicate right) {
        return new AndCharPredicate(left, right);
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
    public static CharPredicate equalsTo(char element) {
        return new EqualsToCharPredicate(element);
    }
    
    /**
     * Creates a CharPredicate that evaluates to <code>true</code> if the element being
     * tested is greater then the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created CharPredicate
     */
    public static CharPredicate greaterThen(char element) {
        return new GreaterThenCharPredicate(element);
    }

    /**
     * Creates a CharPredicate that evaluates to <code>true</code> if the element being
     * tested is greater then or equals to the element being used to construct the
     * predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created CharPredicate
     */
    public static CharPredicate greaterThenOrEquals(char element) {
        return new GreaterThenOrEqualsCharPredicate(element);
    }

    /**
     * Creates a CharPredicate that evaluates to <code>true</code> if the element being
     * tested is less then the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created CharPredicate
     */
    public static CharPredicate lessThen(char element) {
        return new LessThenCharPredicate(element);
    }

    /**
     * Creates a CharPredicate that evaluates to <code>true</code> if the element being
     * tested is less then or equals to the element being used to construct the predicate.
     * <p>
     * The returned predicate is serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created CharPredicate
     */
    public static CharPredicate lessThenOrEquals(char element) {
        return new LessThenOrEqualsCharPredicate(element);
    }
    
    /**
     * Creates a CharPredicate that performs a logical logical NOT on the supplied
     * CharPredicate. More formally
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
     * @return the newly created CharPredicate
     * @throws NullPointerException
     *             if the specified predicate is <code>null</code>
     */
    public static CharPredicate not(CharPredicate predicate) {
        return new NotCharPredicate(predicate);
    }

    /**
     * Creates a CharPredicate that performs a logical OR on two supplied predicates. The
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
     *            the left side CharPredicate
     * @param right
     *            the right side CharPredicate
     * @return the newly created CharPredicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static CharPredicate or(CharPredicate left, CharPredicate right) {
        return new OrCharPredicate(left, right);
    }
    
    /**
     * A CharPredicate that performs a logical exclusive AND on two supplied predicates.
     */
    static final class AndCharPredicate implements CharPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 6981902451700512606L;

        /** The left side operand. */
        private final CharPredicate left;

        /** The right side operand. */
        private final CharPredicate right;

        /**
         * Creates a new <code>AndCharPredicate</code>.
         *
         * @param left
         *            the left side CharPredicate
         * @param right
         *            the right side CharPredicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        AndCharPredicate(CharPredicate left, CharPredicate right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean op(char element) {
            return left.op(element) && right.op(element);
        }

        /**
         * Returns the left side CharPredicate.
         *
         * @return the left side CharPredicate.
         */
        public CharPredicate getLeft() {
            return left;
        }

        /**
         * Returns the right side CharPredicate.
         *
         * @return the right side CharPredicate.
         */
        public CharPredicate getRight() {
            return right;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }
    
    static class EqualsToCharPredicate implements CharPredicate, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 8220487572042162409L;

        /** The value to compare with. */
        private final char equalsTo;

        EqualsToCharPredicate(char equalsTo) {
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
        public boolean op(char t) {
            return equalsTo == t;
        }

        /**
         * @return the value we are comparing with
         */
        public char getEqualsTo() {
            return equalsTo;
        }
    }
    
     /**
     * a CharPredicate that always evaluates to <tt>false</tt>. Use {@link #FALSE} to
     * get an instance of this CharPredicate.
     *
     * @see TrueCharPredicate
     */
    static final class FalseCharPredicate implements CharPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -3048464662394104180L;

        /** Creates a new FalseCharPredicate. */
        FalseCharPredicate() {}

        /** {@inheritDoc} */
        public boolean op(char value) {
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
    
    static class GreaterThenCharPredicate implements CharPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 7198592614364500859L;

        /** The value to compare with. */
        private final char greaterThen;

        GreaterThenCharPredicate(char greaterThen) {
            this.greaterThen = greaterThen;
        }

        /** {@inheritDoc} */
        public boolean op(char t) {
            return greaterThen < t;
        }

        public char getGreaterThen() {
            return greaterThen;
        }
    }
    
    static class GreaterThenOrEqualsCharPredicate implements CharPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -4681995097900012563L;

        /** The value to compare with. */
        private final char greaterThenOrEquals;

        GreaterThenOrEqualsCharPredicate(char greaterThenOrEquals) {
            this.greaterThenOrEquals = greaterThenOrEquals;
        }

        /** {@inheritDoc} */
        public boolean op(char t) {
            return greaterThenOrEquals <= t;
        }

        public char getGreaterThenOrEquals() {
            return greaterThenOrEquals;
        }
    }
    
    static class LessThenCharPredicate implements CharPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -9180606923416408020L;

        /** The value to compare with. */
        private final char lessThen;

        LessThenCharPredicate(char lessThen) {
            this.lessThen = lessThen;
        }

        /** {@inheritDoc} */
        public boolean op(char t) {
            return lessThen > t;
        }

        public char getLessThen() {
            return lessThen;
        }
    }

    static class LessThenOrEqualsCharPredicate implements CharPredicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 8711220473905545122L;

        /** The value to compare with. */
        private final char lessThenOrEquals;

        LessThenOrEqualsCharPredicate(char lessThenOrEquals) {
            this.lessThenOrEquals = lessThenOrEquals;
        }

        /** {@inheritDoc} */
        public boolean op(char t) {
            return lessThenOrEquals >= t;
        }

        public char getLessThenOrEquals() {
            return lessThenOrEquals;
        }
    }
    /**
     * A CharPredicate that evaluates to true iff the Predicate used for constructing
     * evaluates to <code>false</code>.
     */
    static final class NotCharPredicate implements CharPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -5117781730584740429L;

        /** The CharPredicate to negate. */
        private final CharPredicate predicate;

        /**
         * Creates a new NotCharPredicate.
         *
         * @param predicate
         *            the predicate to negate.
         * @throws NullPointerException
         *             if the specified predicate is <code>null</code>
         */
        NotCharPredicate(CharPredicate predicate) {
            if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
        }

        /**
         * Returns a boolean representing the logical NOT value of the supplied
         * CharPredicate.
         *
         * @param element
         *            the element to test
         * @return the logical NOT of the supplied CharPredicate
         */
        public boolean op(char element) {
            return !predicate.op(element);
        }

        /**
         * Returns the predicate that is being negated.
         *
         * @return the predicate that is being negated.
         */
        public CharPredicate getPredicate() {
            return predicate;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "!(" + predicate + ")";
        }
    }

    /**
     * A CharPredicate that performs a logical exclusive OR on two supplied predicates.
     */
    static final class OrCharPredicate implements CharPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 747277162607915666L;

        /** The left side operand. */
        private final CharPredicate left;

        /** The right side operand. */
        private final CharPredicate right;

        /**
         * Creates a new <code>OrCharPredicate</code>.
         *
         * @param left
         *            the left side CharPredicate
         * @param right
         *            the right side CharPredicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        OrCharPredicate(CharPredicate left, CharPredicate right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean op(char element) {
            return left.op(element) || right.op(element);
        }

        /**
         * Returns the left side CharPredicate.
         *
         * @return the left side CharPredicate.
         */
        public CharPredicate getLeft() {
            return left;
        }

        /**
         * Returns the right side CharPredicate.
         *
         * @return the right side CharPredicate.
         */
        public CharPredicate getRight() {
            return right;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }
    
    /**
     * A CharPredicate that always evaluates to <tt>true</tt>. Use {@link #TRUE} to get
     * an instance of this CharPredicate.
     *
     * @see FalseCharPredicate
     */
    static final class TrueCharPredicate implements CharPredicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3258129137502925875L;

        /** Creates a new TrueCharPredicate. */
        TrueCharPredicate() {}

        /** {@inheritDoc} */
        public boolean op(char value) {
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