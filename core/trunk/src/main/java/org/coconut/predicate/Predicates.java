/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.coconut.predicate.spi.CompositePredicate;

/**
 * Various implementations of {@link Predicate}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class Predicates {

    /** A predicate that always return False. */
    public static final Predicate FALSE = Predicates.FalsePredicate.INSTANCE;

    /** A predicate that returns whether or not the element being tested is null. */
    public final static Predicate IS_NULL = new IsNullFilter();

    /** A predicate that returns whether or not the element being tested is a number. */
    public final static Predicate IS_NUMBER = isType(Number.class);

    /** A predicate that always return True. */
    public static final Predicate TRUE = Predicates.TruePredicate.INSTANCE;

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Predicates() {}

    /**
     * As {@link #all(Predicate...)} except taking an {@link Iterable} as parameter.
     * 
     * @param predicates
     *            the predicates to evaluate against
     * @return a Predicate that tests all elements
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified iterable is <code>null</code> or contains a null
     *             element
     */
    public static <E> Predicate<E> all(Iterable<? extends Predicate<? super E>> predicates) {
        return new Predicates.AllPredicate<E>(predicates);
    }

    // /CLOVER:ON
    /**
     * Returns a Predicate that evaluates to true iff each of the specified predicates
     * evaluates to true. The returned predicate uses short-circuit evaluation (or minimal
     * evaluation). That is, subsequent arguments are only evaluated if the previous
     * arguments does not suffice to determine the truth value.
     * <p>
     * The Predicate will use a copy of the array of supplied predicates.
     * <p>
     * If all the supplied predicates are serializable the returned predicate will also be
     * serializable.
     * 
     * @param predicates
     *            the predicates to test against
     * @return a Predicate that tests all elements
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static <E> Predicate<E> all(Predicate<? super E>... predicates) {
        return new Predicates.AllPredicate<E>(predicates);
    }

    /**
     * Returns a Predicate that evaluates to true iff both of its components evaluates to
     * true. The returned predicate uses short-circuit evaluation (or minimal evaluation).
     * That is, if the specified left-hand predicate evaluates to <code>false</code> the
     * right-hand predicate will not be evaluated. This is equivalent to:
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
     *            the left-hand predicate
     * @param right
     *            the right hand predicate
     * @return an and predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static <E> Predicate<E> and(Predicate<? super E> left, Predicate<? super E> right) {
        return new Predicates.AndPredicate<E>(left, right);
    }

    /**
     * As {@link #any(Predicate...)} except taking an {@link Iterable} as parameter.
     * 
     * @param predicates
     *            the predicates to evaluate against
     * @return a Predicate that tests all elements
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified iterable is <code>null</code> or contains a null
     *             element
     */
    public static <E> Predicate<E> any(Iterable<? extends Predicate<? super E>> predicates) {
        return new Predicates.AnyPredicate<E>(predicates);
    }

    /**
     * Returns a Predicate that evaluates to true if any of the specified predicates
     * evaluates to true. The returned predicate uses short-circuit evaluation (or minimal
     * evaluation). That is, subsequent arguments are only evaluated if the previous
     * arguments does not suffice to determine the truth value.
     * <p>
     * The Predicate will use a copy of the array of supplied predicates.
     * <p>
     * If all the supplied predicates are serializable the returned predicate will also be
     * serializable.
     * 
     * @param predicates
     *            the predicates to test against
     * @return a Predicate that tests all elements
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static <E> Predicate<E> any(Predicate<? super E>... predicates) {
        return new Predicates.AnyPredicate<E>(predicates);
    }

    /**
     * Returns a Predicate that evaluates to true if any of the specified elements are
     * equal to the element that is being tested. The returned predicate uses
     * short-circuit evaluation (or minimal evaluation). That is, subsequent arguments are
     * only evaluated if the previous arguments does not suffice to determine the truth
     * value.
     * <p>
     * The Predicate will use a copy of the array of supplied predicates.
     * <p>
     * If all the supplied predicates are serializable the returned predicate will also be
     * serializable.
     * 
     * @param elements
     *            the elements to test against
     * @return a Predicate that tests all elements
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if any of the specified elements are <code>null</code>
     */
    public static <E> Predicate<E> anyEquals(E... elements) {
        List<Predicate<E>> list = new ArrayList<Predicate<E>>();
        for (E e : elements) {
            list.add(Predicates.equalsTo(e));
        }
        return any(list);
    }

    /**
     * As {@link #anyEquals(Object...)} except taking an {@link Iterable} as parameter.
     * 
     * @param elements
     *            the element to evaluate
     * @return a Predicate that tests all elements
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified iterable is <code>null</code> or contains a null
     *             element
     */
    public static <E> Predicate<E> anyEquals(Iterable<? extends E> elements) {
        List<Predicate<E>> list = new ArrayList<Predicate<E>>();
        for (E e : elements) {
            list.add(Predicates.equalsTo(e));
        }
        return any(list);
    }

    /**
     * Returns a Predicate that evaluates to true if any of the specified elements are a
     * supertype of the element that is being tested. The returned predicate uses
     * short-circuit evaluation (or minimal evaluation). That is, subsequent arguments are
     * only evaluated if the previous arguments does not suffice to determine the truth
     * value.
     * <p>
     * The Predicate will use a copy of the array of supplied predicates.
     * <p>
     * If all the supplied predicates are serializable the returned predicate will also be
     * serializable.
     * 
     * @param classes
     *            the types to test against
     * @return a Predicate that tests all elements
     * @throws NullPointerException
     *             if any of the specified classes are <code>null</code>
     */
    public static Predicate anyType(Class<?>... classes) {
        List<Predicate<?>> list = new ArrayList<Predicate<?>>();
        for (Class<?> c : classes) {
            list.add(isType(c));
        }
        return any(list);
    }

    /**
     * As {@link #anyType(Class...)} except taking an {@link Iterable} as parameter.
     * 
     * @param classes
     *            the types to test against
     * @return a Predicate that tests all elements
     * @throws NullPointerException
     *             if the specified iterable is <code>null</code> or contains a null
     *             element
     */
    public static Predicate anyType(Iterable<? extends Class<?>> classes) {
        List<Predicate<?>> list = new ArrayList<Predicate<?>>();
        for (Class<?> c : classes) {
            list.add(isType(c));
        }
        return any(list);
    }

    /**
     * Returns a Predicate that evaluates to true if the element being tested is between
     * the two specified elements (both inclusive). This is equivalent to:
     * 
     * <pre>
     * left &lt;= element_being_tester &lt;= right
     * </pre>
     * 
     * <p>
     * If both of the supplied elements are serializable the returned predicate will also
     * be serializable.
     * 
     * @param left
     *            the left-hand element to compare with
     * @param right
     *            the right hand element to compare with
     * @return a between predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if any of the specified elements are <code>null</code>
     */
    public static <E> Predicate<E> between(E left, E right) {
        return and((Predicate) Predicates.greaterThenOrEqual(left), (Predicate) Predicates
                .lessThenOrEqual(right));
    }

    /**
     * As {@link #between(Object, Object)} except using the specified {@link Comparator}
     * when evaluating elements.
     * 
     * @param left
     *            the left-hand element to compare with
     * @param right
     *            the right hand element to compare with
     * @param comparator
     *            the comparator to compare elements with
     * @return a between predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified comparator or any of the specified elements are
     *             <code>null</code>
     */
    public static <E> Predicate<E> between(E left, E right, Comparator<? extends E> comparator) {
        return and((Predicate) Predicates.greaterThenOrEqual(left, comparator),
                (Predicate) Predicates.lessThenOrEqual(right, comparator));
    }

    /**
     * Returns a Predicate that accepts all elements that are {@link Object#equals equal}
     * to the specified object.
     * <p>
     * If the specified object is serializable the returned predicate will also be
     * serializable.
     * 
     * @param object
     *            the object we test against.
     * @return an equals predicate
     * @throws NullPointerException
     *             if the specified object is <code>null</code>
     * @param <E>
     *            the type of elements accepted by the predicate
     */
    public static <E> Predicate<E> equalsTo(E object) {
        return new Predicates.EqualsToPredicate<E>(object);
    }

    /**
     * Returns a predicate that always evaluates to <code>false</code>. This predicate
     * is serializable.
     * <p>
     * This example illustrates the type-safe way to obtain a true predicate:
     * 
     * <pre>
     * Predicate&lt;String&gt; s = Predicates.truePredicate();
     * </pre>
     * 
     * Implementation note: Implementations of this method need not create a separate
     * <tt>predicate</tt> object for each call. Using this method is likely to have
     * comparable cost to using the like-named field. (Unlike this method, the field does
     * not provide type safety.)
     * 
     * @see #TRUE
     * @return a predicate that returns <tt>true</tt> for any element passed to the
     *         {@link Predicate#evaluate(Object)} method.
     * @param <E>
     *            the type of elements accepted by the predicate
     */
    @SuppressWarnings("unchecked")
    public static <E> Predicate<E> falsePredicate() {
        return FALSE;
    }

    /**
     * Returns a Predicate that evaluates to true if the element being tested is greater
     * then the element being used to construct the predicate. The predicate will use the
     * objects natural comparator.
     * <p>
     * If the supplied element is serializable the returned predicate will also be
     * serializable.
     * 
     * @param element
     *            the element to compare with
     * @return a greater then predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified element is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified element does not implement {@link Comparable}
     */
    public static <E> Predicate<E> greaterThen(E element) {
        return new Predicates.GreaterThenPredicate<E>(element);
    }

    /**
     * Returns a Predicate that evaluates to true if the element being tested is greater
     * then the element being used to construct the predicate. The predicate will use the
     * specified Comparator to compare the objects.
     * <p>
     * If the supplied element and Comparator is serializable the returned predicate will
     * also be serializable.
     * 
     * @param element
     *            the element to compare with
     * @param comparator
     *            the Comparator used for comparing elements
     * @return a greater then predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified element is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified element does not implement {@link Comparable}
     */
    public static <E> Predicate<E> greaterThen(E element, final Comparator<? extends E> comparator) {
        return new Predicates.GreaterThenPredicate<E>(element, comparator);
    }

    /**
     * Returns a Predicate that evaluates to true if the element being tested is greater
     * then or equal to the element being used to construct the predicate. The predicate
     * will use the objects natural comparator.
     * <p>
     * If the supplied element is serializable the returned predicate will also be
     * serializable.
     * 
     * @param element
     *            the element to compare with
     * @return a greater then or equal predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified element is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified element does not implement {@link Comparable}
     */
    public static <E> Predicate<E> greaterThenOrEqual(E element) {
        return new Predicates.GreaterThenOrEqualPredicate<E>(element);
    }

    /**
     * Returns a Predicate that evaluates to true if the element being tested is greater
     * then or equal to the element being used to construct the predicate. The predicate
     * will use the specified Comparator to compare the objects.
     * <p>
     * If the supplied element and Comparator is serializable the returned predicate will
     * also be serializable.
     * 
     * @param element
     *            the element to compare with
     * @param comparator
     *            the Comparator used for comparing elements
     * @return a greater then or equals predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified element is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified element does not implement {@link Comparable}
     */
    public static <E> Predicate<E> greaterThenOrEqual(E element,
            final Comparator<? extends E> comparator) {
        return new Predicates.GreaterThenOrEqualPredicate<E>(element, comparator);
    }

    /**
     * Returns a Predicate that tests whether the element being tested is
     * <code>null</code>. This predicate is serializable.
     * <p>
     * Implementation note: Implementations of this method need not create a separate
     * <tt>predicate</tt> object for each call. Using this method is likely to have
     * comparable cost to using the like-named field. (Unlike this method, the field does
     * not provide type safety.)
     * 
     * @param <E>
     *            the types that are accepted by the predicate.
     * @return a Predicate that tests whether the element being tested is
     *         <code>null</code>.
     */
    public static <E> Predicate<E> isNull() {
        return IS_NULL;
    }

    @SuppressWarnings("unchecked")
    public static <E> Predicate<E> isNumber() {
        return IS_NUMBER;
    }

    public static Predicate isType(Class clazz) {
        return new IsTypePredicate(clazz);
    }

    public static <E> Predicate<E> lessThen(E element) {
        return new Predicates.LessThenPredicate<E>(element);
    }

    public static <E> Predicate<E> lessThen(E object, final Comparator<? extends E> comparator) {
        return new Predicates.LessThenPredicate<E>(object, comparator);
    }

    public static <E> Predicate<E> lessThenOrEqual(E object) {
        return new Predicates.LessThenOrEqualPredicate<E>(object);
    }

    public static <E> Predicate<E> lessThenOrEqual(E object,
            final Comparator<? extends E> comparator) {
        return new Predicates.LessThenOrEqualPredicate<E>(object, comparator);
    }

    public static <E> Predicate<E> not(Predicate<E> predicate) {
        return new Predicates.NotPredicate<E>(predicate);
    }

    public static <E> Predicate<E> notNullAnd(Predicate<E> f) {
        return new NotNullAndFilter<E>(f);
    }

    public static <E> Predicate<E> or(Predicate<E> left, Predicate<E> right) {
        return new Predicates.OrPredicate<E>(left, right);
    }

    public static <E> Predicate<E> same(E element) {
        return new Predicates.SamePredicate<E>(element);
    }

    /**
     * Returns the true predicate. This predicate is serializable.
     * <p>
     * This example illustrates the type-safe way to obtain a true predicate:
     * 
     * <pre>
     * Predicate&lt;String&gt; s = Predicates.truePredicate();
     * </pre>
     * 
     * Implementation note: Implementations of this method need not create a separate
     * <tt>predicate</tt> object for each call. Using this method is likely to have
     * comparable cost to using the like-named field. (Unlike this method, the field does
     * not provide type safety.)
     * 
     * @see #TRUE
     * @return a predicate that returns <tt>true</tt> for any element passed to the
     *         {@link Predicate#evaluate(Object)} method.
     * @param <E>
     *            the type of elements accepted by the predicate
     */
    @SuppressWarnings("unchecked")
    public static <E> Predicate<E> truePredicate() {
        return TRUE;
    }

    /**
     * This method returns a Predicate that performs xor on two other predicates.
     * 
     * @param left
     *            the left hand side of the expression
     * @param right
     *            the right hand side of the expression
     * @return a Predicate that performs xor on two other predicates.
     */
    public static <E> Predicate<E> xor(Predicate<E> left, Predicate<E> right) {
        return new Predicates.XorPredicate<E>(left, right);
    }

    static <E> Predicate<E>[] iterableToArray(Iterable<? extends Predicate<? super E>> iterable) {
        if (iterable == null) {
            throw new NullPointerException("iterable is null");
        }
        ArrayList list = new ArrayList();
        for (Predicate p : iterable) {
            if (p == null) {
                throw new NullPointerException("iterable contained a null");
            }
            list.add(p);
        }
        return (Predicate[]) list.toArray(new Predicate[list.size()]);
    }

    /**
     * A Predicate that tests that <tt>all</tt> of the supplied Predicates accepts a
     * given element.
     */
    final static class AllPredicate<E> implements Predicate<E>, CompositePredicate<E>,
            Iterable<Predicate<? super E>>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -2054989348063839373L;

        /** All the predicates that are being checked. */
        private final Predicate<? super E>[] predicates;

        /**
         * Constructs a new AllPredicate.
         * 
         * @param iterable
         *            the iterable to test
         */
        public AllPredicate(Iterable<? extends Predicate<? super E>> iterable) {
            this.predicates = iterableToArray(iterable);
        }

        /**
         * Constructs a new AllPredicate. The Predicate will use a copy of the array of
         * supplied predicates.
         * 
         * @param predicates
         *            the predicates to test
         */
        @SuppressWarnings("unchecked")
        public AllPredicate(final Predicate<? super E>[] predicates) {
            this.predicates = new Predicate[predicates.length];
            System.arraycopy(predicates, 0, this.predicates, 0, predicates.length);
            for (int i = 0; i < this.predicates.length; i++) {
                if (this.predicates[i] == null) {
                    throw new NullPointerException("predicates contained a null on index = " + i);
                }
            }
        }

        /**
         * Returns <tt>true</tt> if all supplied Predicates accepts the element.
         * 
         * @param element
         *            the element to test
         * @return <tt>true</tt> if all supplied Predicates accepts the element.
         */
        public boolean evaluate(E element) {
            for (Predicate<? super E> predicate : predicates) {
                if (!predicate.evaluate(element)) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Returns the predicates we are testing against.
         * 
         * @return the predicates we are testing against
         */
        public List<Predicate<? super E>> getPredicates() {
            return Collections.unmodifiableList(Arrays.asList(predicates));
        }

        /** {@inheritDoc} */
        public Iterator<Predicate<? super E>> iterator() {
            return Arrays.asList(predicates).iterator();
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            if (predicates.length == 0) {
                return "";
            } else if (predicates.length == 1) {
                return predicates[0].toString();
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("((");
                builder.append(predicates[0]);
                builder.append(")");
                for (int i = 1; i < predicates.length; i++) {
                    builder.append(" and (");
                    builder.append(predicates[i]);
                    builder.append(")");
                }
                builder.append(")");
                return builder.toString();
            }
        }
    }

    /**
     * A Predicate that performs a logical exclusive AND on two supplied predicates.
     */
    final static class AndPredicate<E> implements Predicate<E>, CompositePredicate<E>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 6981902451700512606L;

        /** The left side operand. */
        private final Predicate<? super E> left;

        /** The right side operand. */
        private final Predicate<? super E> right;

        /**
         * Constructs a new <code>AndPredicate</code>.
         * 
         * @param left
         *            the left side operand
         * @param right
         *            the right side operand
         */
        public AndPredicate(final Predicate<? super E> left, final Predicate<? super E> right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean evaluate(E element) {
            return left.evaluate(element) && right.evaluate(element);
        }

        /**
         * Returns the left side operand.
         * 
         * @return the left side operand.
         */
        public Predicate<? super E> getLeftPredicate() {
            return left;
        }

        /** {@inheritDoc} */
        @SuppressWarnings("unchecked")
        public List<? extends Predicate<? super E>> getPredicates() {
            return (List) Arrays.asList(left, right);
        }

        /**
         * Returns the right side operand.
         * 
         * @return the right side operand.
         */
        public Predicate<? super E> getRightPredicate() {
            return right;
        }

        /** {@inheritDoc} */
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }

    /**
     * A Predicate that tests that at least one of the supplied predicates accepts a given
     * element.
     */
    final static class AnyPredicate<E> implements Predicate<E>, CompositePredicate<E>,
            Iterable<Predicate<? super E>>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -848457724637828171L;

        /** All the predicates that are being checked. */
        private final Predicate<? super E>[] predicates;

        /**
         * Constructs a new AllPredicate.
         * 
         * @param iterable
         *            the iterable to test
         */
        public AnyPredicate(Iterable<? extends Predicate<? super E>> iterable) {
            this.predicates = iterableToArray(iterable);
        }

        /**
         * Constructs a new AllPredicate. The Predicate will use a copy of the array of
         * supplied predicates.
         * 
         * @param predicates
         *            the predicates to test
         */
        @SuppressWarnings("unchecked")
        public AnyPredicate(final Predicate<? super E>[] predicates) {
            this.predicates = new Predicate[predicates.length];
            System.arraycopy(predicates, 0, this.predicates, 0, predicates.length);
            for (int i = 0; i < this.predicates.length; i++) {
                if (this.predicates[i] == null) {
                    throw new NullPointerException("predicates contained a null on index = " + i);
                }
            }
        }

        /**
         * Returns <tt>true</tt> if all supplied Predicates accepts the element.
         * 
         * @param element
         *            the element to test
         * @return <tt>true</tt> if all supplied Predicates accepts the element.
         */
        public boolean evaluate(E element) {
            for (Predicate<? super E> predicate : predicates) {
                if (predicate.evaluate(element)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns the predicates we are testing against.
         * 
         * @return the predicates we are testing against
         */
        public List<Predicate<? super E>> getPredicates() {
            return Collections.unmodifiableList(Arrays.asList(predicates));
        }

        /** {@inheritDoc} */
        public Iterator<Predicate<? super E>> iterator() {
            return Arrays.asList(predicates).iterator();
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            if (predicates.length == 0) {
                return "";
            } else if (predicates.length == 1) {
                return predicates[0].toString();
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("((");
                builder.append(predicates[0]);
                builder.append(")");
                for (int i = 1; i < predicates.length; i++) {
                    builder.append(" or (");
                    builder.append(predicates[i]);
                    builder.append(")");
                }
                builder.append(")");
                return builder.toString();
            }
        }
    }

    /**
     * A Predicate that accepts all elements that are {@link Object#equals equal} to the
     * specified object.
     */
    final static class EqualsToPredicate<E> implements Predicate<E>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -802615306772905787L;

        /** The object to compare with. */
        private final E object;

        /**
         * Creates a new EqualsPredicate.
         * 
         * @param object
         *            the object to compare against.
         * @throws NullPointerException
         *             if the specified object is null
         */
        public EqualsToPredicate(final E object) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            this.object = object;
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
        public boolean evaluate(E element) {
            return object.equals(element);
        }

        /**
         * Returns the object we are comparing with.
         * 
         * @return the object we are comparing with.
         */
        public E getObject() {
            return object;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "equals " + object;
        }
    }

    /**
     * A Predicate that always returns <tt>false</tt>. Use {@link #INSTANCE} or
     * {@link org.coconut.predicate.Predicates#FALSE} to get an instance of this
     * Predicate.
     */
    final static class FalsePredicate implements Predicate, Serializable {

        /** The one and only instance. */
        static final FalsePredicate INSTANCE = new FalsePredicate();

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -3048464662394104180L;

        /** Construct a new FalsePredicate. */
        private FalsePredicate() {}

        /**
         * Returns <tt>false</tt> for any element passed to this method.
         * 
         * @param element
         *            the element to test
         * @return <tt>false</tt> for any element passed to this method
         */
        public boolean evaluate(Object element) {
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return Boolean.FALSE.toString();
        }
    }

    /**
     * A Greather Then Or Equal predicate as per Comparable/Comparator contract.
     */
    final static class GreaterThenOrEqualPredicate<E> implements Predicate<E>, Serializable {

        /** <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -6815218477296552273L;

        /**
         * The comparator to compare elements with or null if the objects natural
         * comparator should be used.
         */
        private final Comparator comparator;

        /** The object to compare against. */
        private final E object;

        /**
         * Creates a new greater then predicate.
         * 
         * @param object
         *            the object to compare with.
         * @param comparator
         *            the comparator that should be used to compare elements
         */
        public GreaterThenOrEqualPredicate(E object, final Comparator<? extends E> comparator) {
            if (object == null) {
                throw new NullPointerException("element is null");
            } else if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.object = object;
            this.comparator = comparator;
        }

        /**
         * Creates a new greater then predicate.
         * 
         * @param object
         *            the object to compare with.
         */
        public GreaterThenOrEqualPredicate(E object) {
            if (object == null) {
                throw new NullPointerException("element is null");
            } else if (!(object instanceof Comparable)) {
                throw new IllegalArgumentException("object not instanceof Comparable");
            }
            this.object = object;
            this.comparator = null;
        }

        /** {@inheritDoc} */
        @SuppressWarnings("unchecked")
        public boolean evaluate(E element) {
            if (comparator == null) {
                return ((Comparable) object).compareTo(element) <= 0;
            } else {
                return comparator.compare(object, element) <= 0;
            }
        }

        /**
         * @return the comparator to compare elements with or null if the objects natural
         *         comparator should be used.
         */
        public Comparator<? extends E> getComparator() {
            return comparator;
        }

        /**
         * Returns the object we are comparing.
         * 
         * @return the object we are comparing
         */
        public E getObject() {
            return object;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return " >= " + object;
        }
    }

    /**
     * A greather-then predicate as per Comparable/Comparator contract.
     */
    final static class GreaterThenPredicate<E> implements Predicate<E>, Serializable {

        /** <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -6815218477296552273L;

        /**
         * The comparator to compare elements with or null if the objects natural
         * comparator should be used.
         */
        private final Comparator comparator;

        /** The object to compare against. */
        private final E object;

        /**
         * Creates a new greater then predicate.
         * 
         * @param object
         *            the object to compare with.
         */
        public GreaterThenPredicate(E object) {
            if (object == null) {
                throw new NullPointerException("element is null");
            } else if (!(object instanceof Comparable)) {
                throw new IllegalArgumentException("object not instanceof Comparable");
            }
            this.object = object;
            this.comparator = null;
        }

        /**
         * Creates a new greater then predicate.
         * 
         * @param object
         *            the objetc to compare with.
         * @param comparator
         *            the comparator that should be used to compare elements
         */
        public GreaterThenPredicate(E object, final Comparator<? extends E> comparator) {
            if (object == null) {
                throw new NullPointerException("element is null");
            } else if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.object = object;
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        @SuppressWarnings("unchecked")
        public boolean evaluate(E element) {
            if (comparator == null) {
                return ((Comparable) object).compareTo(element) < 0;
            } else {
                return comparator.compare(object, element) < 0;
            }
        }

        /**
         * @return the comparator to compare elements with or null if the objects natural
         *         comparator should be used.
         */
        public Comparator<? extends E> getComparator() {
            return comparator;
        }

        /**
         * Returns the object we are comparing.
         * 
         * @return the object we are comparing
         */
        public E getObject() {
            return object;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "$x > " + object;
        }
    }

    /**
     * A predicate that tests whether or not an element is <code>null</code>.
     */
    final static class IsNullFilter implements Predicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6280765768913457567L;

        /** {@inheritDoc} */
        public boolean evaluate(Object element) {
            return element == null;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "is null";
        }
    }

    /**
     * If this filter is specified with a class this Filter will match any objects of the
     * specific type or that is super class of the specified class. If this Filter is
     * specified with an interface it will match any class that implements the interface.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
     * @version $Id$
     */
    final static class IsTypePredicate implements Predicate, Serializable {

        /** A Filter that accepts all classes. */
        public static final Predicate<?> ALL = new IsTypePredicate(Object.class);

        /** A default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3256440304922996793L;

        /** The class we are testing against. */
        private final Class<?> theClass;

        /**
         * Constructs a new ClassBasedFilter.
         * 
         * @param theClass
         *            the class we are testing against.
         * @throws NullPointerException
         *             if the class that was supplied is <code>null</code>.
         */
        public IsTypePredicate(final Class<?> theClass) {
            if (theClass == null) {
                throw new NullPointerException("theClass is null");
            }
            this.theClass = theClass;
        }

        /**
         * Tests the given element for acceptance.
         * 
         * @param element
         *            the element to test against.
         * @return <code>true</code> if the filter accepts the element;
         *         <code>false</code> otherwise.
         */
        public boolean evaluate(Object element) {
            return theClass.isAssignableFrom(element.getClass());
        }

        /**
         * Returns the class we are testing against.
         * 
         * @return Returns the theClass.
         */
        public Class<?> getFilteredClass() {
            return theClass;
        }
    }

    /**
     * A Less Then predicate as per Comparable/Comparator contract.
     */
    final static class LessThenOrEqualPredicate<E> implements Predicate<E>, Serializable {

        /** <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 1330339174193813467L;

        /**
         * The comparator to compare elements with or null if the objects natural
         * comparator should be used.
         */

        private final Comparator comparator;

        /** The object to compare against. */
        private final E object;

        /**
         * Creates a new less then or equal predicate.
         * 
         * @param object
         *            the objetc to compare with.
         * @param comparator
         *            the comparator that should be used to compare elements
         */
        public LessThenOrEqualPredicate(E object, final Comparator<? extends E> comparator) {
            if (object == null) {
                throw new NullPointerException("element is null");
            } else if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.object = object;
            this.comparator = comparator;
        }

        /**
         * Creates a new less then or equals predicate.
         * 
         * @param object
         *            the object to compare with.
         */
        public LessThenOrEqualPredicate(E object) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            if (!(object instanceof Comparable)) {
                throw new IllegalArgumentException("object not instanceof Comparable");
            }
            this.object = object;
            this.comparator = null;
        }

        /** {@inheritDoc} */
        @SuppressWarnings("unchecked")
        public boolean evaluate(E element) {
            if (comparator == null) {
                return ((Comparable) object).compareTo(element) >= 0;
            } else {
                return comparator.compare(object, element) >= 0;
            }
        }

        /**
         * @return the comparator to compare elements with or null if the objects natural
         *         comparator should be used.
         */
        public Comparator<? extends E> getComparator() {
            return comparator;
        }

        /**
         * Returns the object we are comparing.
         * 
         * @return the object we are comparing
         */
        public E getObject() {
            return object;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return " <= " + object;
        }
    }

    /**
     * A Less Then predicate as per Comparable/Comparator contract.
     */
    final static class LessThenPredicate<E> implements Predicate<E>, Serializable {

        /** <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 1330339174193813467L;

        /**
         * The comparator to compare elements with or null if the objects natural
         * comparator should be used.
         */

        private final Comparator comparator;

        /** The object to compare against. */
        private final E object;

        /**
         * Creates a new less then predicate.
         * 
         * @param object
         *            the object to compare with.
         */
        public LessThenPredicate(E object) {
            if (object == null) {
                throw new NullPointerException("element is null");
            } else if (!(object instanceof Comparable)) {
                throw new IllegalArgumentException("object not instanceof Comparable");
            }
            this.object = object;
            this.comparator = null;
        }

        /**
         * Creates a new less then Predicate.
         * 
         * @param object
         *            the object to compare with.
         * @param comparator
         *            the comparator that should be used to compare elements
         */
        public LessThenPredicate(E object, final Comparator<? extends E> comparator) {
            if (object == null) {
                throw new NullPointerException("element is null");
            } else if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.object = object;
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        @SuppressWarnings("unchecked")
        public boolean evaluate(E element) {
            if (comparator == null) {
                return ((Comparable) object).compareTo(element) > 0;
            } else {
                return comparator.compare(object, element) > 0;
            }
        }

        /**
         * @return the comparator to compare elements with or null if the objects natural
         *         comparator should be used.
         */
        public Comparator<? extends E> getComparator() {
            return comparator;
        }

        /**
         * Returns the object we are comparing.
         * 
         * @return the object we are comparing
         */
        public E getObject() {
            return object;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "$x < " + object;
        }
    }

    final static class NotNullAndFilter<T> implements Predicate<T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -324206595097699714L;

        /** the other filter. */
        private final Predicate<T> filter;

        public NotNullAndFilter(Predicate<T> filter) {
            if (filter == null) {
                throw new NullPointerException("filter is null");
            }
            this.filter = filter;
        }

        /** {@inheritDoc} */
        public boolean evaluate(T element) {
            return element != null && filter.evaluate(element);
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "is not null and " + filter.toString();
        }

    }

    /**
     * A Predicate that test that a supplied Predicate does <tt>not</tt> accept a given
     * Element.
     */
    final static class NotPredicate<E> implements Predicate<E>, CompositePredicate<E>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -5117781730584740429L;

        /** The Predicate to negate. */
        private final Predicate<E> predicate;

        /**
         * Creates a new Predicate that negates the result of the supplied Predicate.
         * 
         * @param predicate
         *            the predicate to negate.
         */
        public NotPredicate(final Predicate<E> predicate) {
            if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
        }

        /**
         * Returns a boolean representing the logical NOT value of the supplied Predicate.
         * If the specified Predicates accept() method returns <tt>true</tt>, this
         * method returns <tt>false</tt>; if it is <tt>false</tt>, this method
         * returns <tt>true</tt>.
         * 
         * @param element
         *            the element to test
         * @return the logical NOT of the supplied Predicate
         */
        public boolean evaluate(E element) {
            return !predicate.evaluate(element);
        }

        /**
         * Returns the predicate that is being negated.
         * 
         * @return the predicate that is being negated.
         */
        public Predicate<E> getPredicate() {
            return predicate;
        }

        /**
         * @see org.coconut.predicate.spi.CompositePredicate#getPredicates()
         */
        public List<Predicate<E>> getPredicates() {
            return Arrays.asList(predicate);
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "!(" + predicate + ")";
        }
    }

    /**
     * A Predicate that performs a logical inclusive OR on two supplied predicates.
     */
    final static class OrPredicate<E> implements Predicate<E>, CompositePredicate<E>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 7602293335100183390L;

        /** The left side operand. */
        private final Predicate<E> left;

        /** The right side operand. */
        private final Predicate<E> right;

        /**
         * Constructs a new <code>AndPredicate</code>.
         * 
         * @param left
         *            the left side operand
         * @param right
         *            the right side operand
         */
        public OrPredicate(final Predicate<E> left, final Predicate<E> right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            }
            if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean evaluate(final E element) {
            return left.evaluate(element) || right.evaluate(element);
        }

        /**
         * Returns the left side operand.
         * 
         * @return the left side operand.
         */
        public Predicate<E> getLeftPredicate() {
            return left;
        }

        /**
         * @see org.coconut.predicate.spi.CompositePredicate#getPredicates()
         */
        @SuppressWarnings("unchecked")
        public List<Predicate<E>> getPredicates() {
            return Arrays.asList(left, right);
        }

        /**
         * Returns the right side operand.
         * 
         * @return the right side operand.
         */
        public Predicate<E> getRightPredicate() {
            return right;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") or (" + right + ")";
        }
    }

    /**
     * A predicate that accepts all elements that have the same object identity as the one
     * specified.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
     * @version $Id$
     */
    final static class SamePredicate<E> implements Predicate<E>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3761971557773620791L;

        /** The object to compare against. */
        private final E object;

        /**
         * Creates a new Predicate that accepts all elements that have the same object
         * identity as the element supplied.
         * 
         * @param object
         *            the objetc to compare with.
         */
        public SamePredicate(final E object) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            this.object = object;
        }

        /** {@inheritDoc} */
        public boolean evaluate(E element) {
            return object == element;
        }

        /**
         * Returns the object we are comparing.
         * 
         * @return the object we are comparing
         */
        public E getObject() {
            return object;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "is (==) " + object;
        }
    }

    /**
     * A Predicate that always returns <tt>true</tt>. Use {@link #INSTANCE} or
     * {@link org.coconut.predicate.Predicates#TRUE} to get an instance of this Predicate.
     * 
     * @see FalsePredicate
     */
    final static class TruePredicate implements Predicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3258129137502925875L;

        /** The TruePredicate instance. */
        static final TruePredicate INSTANCE = new TruePredicate();

        /** Construct a new TruePredicate. */
        private TruePredicate() {}

        /**
         * Returns <tt>true</tt> for any element passed to this method.
         * 
         * @param element
         *            the element to test
         * @return <tt>true</tt> for any element passed to this method
         */
        public boolean evaluate(Object element) {
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "true";
        }
    }

    /**
     * A Predicate that performs a logical exclusive OR (XOR) on two supplied predicates.
     */
    final static class XorPredicate<E> implements CompositePredicate<E>, Predicate<E>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 1155267141991954303L;

        /** The left side operand. */
        private final Predicate<E> left;

        /** The right side operand. */
        private final Predicate<E> right;

        /**
         * Constructs a new <code>AndPredicate</code>.
         * 
         * @param left
         *            the left side operand
         * @param right
         *            the right side operand
         */
        public XorPredicate(final Predicate<E> left, final Predicate<E> right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /** {@inheritDoc} */
        public boolean evaluate(E element) {
            return left.evaluate(element) ^ right.evaluate(element);
        }

        /**
         * Returns the left side operand.
         * 
         * @return the left side operand.
         */
        public Predicate<E> getLeftPredicate() {
            return left;
        }

        /** {@inheritDoc} */
        public List<Predicate<E>> getPredicates() {
            return Arrays.asList(left, right);
        }

        /**
         * Returns the right side operand.
         * 
         * @return the right side operand.
         */
        public Predicate<E> getRightPredicate() {
            return right;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") xor (" + right + ")";
        }
    }
}
