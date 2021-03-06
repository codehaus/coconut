/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.jsr166y.ops;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import jsr166y.forkjoin.Ops.Op;
import jsr166y.forkjoin.Ops.Predicate;


/**
 * Various implementations of {@link Predicate}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class Predicates {

    /** A Predicate that always evaluates to <code>false</code>. */
    public static final Predicate FALSE = new FalsePredicate();

    /** A Predicate that returns <code>false</code> if the element being tested is null. */
    public static final Predicate IS_NOT_NULL = new IsNotNullFilter();

    /** A Predicate that returns <code>true</code> if the element being tested is null. */
    public static final Predicate IS_NULL = not(IS_NOT_NULL);

    /** A Predicate that always evaluates to <code>true</code>. */
    public static final Predicate TRUE = new TruePredicate();

    /** Cannot instantiate. */
    private Predicates() {}

    /**
     * As {@link #allTrue(Predicate[])} except taking an {@link Iterable} as parameter.
     *
     * @param predicates
     *            the predicates to evaluate against
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified iterable is <code>null</code> or contains a null
     *             element
     */
    public static <E> Predicate<E> allTrue(Iterable<? extends Predicate<? super E>> predicates) {
        return new AllPredicate<E>(predicates);
    }

    /**
     * Creates a Predicate that evaluates to true iff each of the specified predicates
     * evaluates to true for the element being tested. The returned predicate uses
     * short-circuit evaluation (or minimal evaluation). That is, subsequent arguments are
     * only evaluated if the previous arguments does not suffice to determine the truth
     * value.
     * <p>
     * The Predicate will use a copy of the array of supplied predicates.
     * <p>
     * If all the supplied predicates are serializable the returned predicate will also be
     * serializable.
     *
     * @param predicates
     *            the predicates to test against
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static <E> Predicate<E> allTrue(Predicate<? super E>... predicates) {
        return new AllPredicate<E>(predicates);
    }

    /**
     * Creates a Predicate that performs a logical AND on two supplied predicates. The
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
     *            the left side Predicate
     * @param right
     *            the right side Predicate
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static <E> Predicate<E> and(Predicate<? super E> left, Predicate<? super E> right) {
        return new AndPredicate<E>(left, right);
    }

    /**
     * Creates a Predicate that evaluates to true if any of the specified elements are
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
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if any of the specified elements are <code>null</code>
     * @see #equalsTo(Object)
     */
    public static <E> Predicate<E> anyEquals(E... elements) {
        List<Predicate<E>> list = new ArrayList<Predicate<E>>();
        for (E e : elements) {
            list.add(equalsTo(e));
        }
        return anyTrue(list);
    }

    /**
     * As {@link #anyEquals(Object...)} except taking an {@link Iterable} as parameter.
     *
     * @param elements
     *            the elements to test against
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified iterable is <code>null</code> or contains a null
     *             element
     * @see #equalsTo(Object)
     */
    public static <E> Predicate<E> anyEquals(Iterable<? extends E> elements) {
        List<Predicate<E>> list = new ArrayList<Predicate<E>>();
        for (E e : elements) {
            list.add(equalsTo(e));
        }
        return anyTrue(list);
    }

    /**
     * As {@link #anyTrue(Predicate[])} except taking an {@link Iterable} as parameter.
     *
     * @param predicates
     *            the predicates to evaluate against
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified iterable is <code>null</code> or contains a null
     *             element
     */
    public static <E> Predicate<E> anyTrue(Iterable<? extends Predicate<? super E>> predicates) {
        return new AnyPredicate<E>(predicates);
    }

    /**
     * Creates a Predicate that evaluates to true if any of the specified predicates
     * evaluates to true for the element being tested. The returned predicate uses
     * short-circuit evaluation (or minimal evaluation). That is, subsequent arguments are
     * only evaluated if the previous arguments does not suffice to determine the truth
     * value.
     * <p>
     * The Predicate will use a copy of the array of supplied predicates.
     * <p>
     * If all the supplied predicates are serializable the returned predicate will also be
     * serializable.
     *
     * @param predicates
     *            the predicates to test against
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static <E> Predicate<E> anyTrue(Predicate<? super E>... predicates) {
        return new AnyPredicate<E>(predicates);
    }

    /**
     * Returns a predicate that tests whether the class of the element being tested is
     * either the same as, or is a superclass or superinterface of, any of the classes or
     * interfaces specified. It returns <code>true</code> if so; otherwise it returns
     * <code>false</code>. This predicate is serializable. The returned predicate uses
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
     * @return the newly created Predicate
     * @throws NullPointerException
     *             if any of the specified classes are <code>null</code>
     * @see #isTypeOf(Class)
     */
    public static Predicate anyType(Class<?>... classes) {
        List<Predicate<?>> list = new ArrayList<Predicate<?>>();
        for (Class<?> c : classes) {
            list.add(isTypeOf(c));
        }
        return anyTrue(list);
    }

    /**
     * As {@link #anyType(Class...)} except taking an {@link Iterable} as parameter.
     *
     * @param classes
     *            the types to test against
     * @return the newly created Predicate
     * @throws NullPointerException
     *             if the specified iterable is <code>null</code> or contains a null
     *             element
     * @see #isTypeOf(Class)
     */
    public static Predicate anyType(Iterable<? extends Class<?>> classes) {
        List<Predicate<?>> list = new ArrayList<Predicate<?>>();
        for (Class<?> c : classes) {
            list.add(isTypeOf(c));
        }
        return anyTrue(list);
    }

    /**
     * Creates a Predicate that evaluates to true if the element being tested is between
     * the two specified elements (both inclusive) according to the <i>natural ordering</i>
     * of the elements. More formally,
     *
     * <pre>
     * left &lt;= element &lt;= right
     * </pre>
     *
     * All elements evaluated must implement the <tt>Comparable</tt> interface.
     * Furthermore, all elements evaluated must be <i>mutually comparable</i> (that is,
     * <tt>left.compareTo(element)</tt> or <tt>right.compareTo(element)</tt> must not
     * throw a <tt>ClassCastException</tt>.
     * <p>
     * If both of the supplied elements are serializable the returned predicate will also
     * be serializable.
     *
     * @param left
     *            the left-hand element to compare with
     * @param right
     *            the right hand element to compare with
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if any of the specified elements are <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified elements does not implement {@link Comparable}
     * @see #between(Object, Object, Comparator)
     * @see Comparable
     */
    public static <E extends Comparable<? super E>> Predicate<E> between(E left, E right) {
        return and((Predicate) greaterThenOrEqual(left), (Predicate) lessThenOrEqual(right));
    }

    /**
     * As {@link #between(Comparable, Comparable)} except using the specified
     * {@link Comparator} when evaluating elements.
     *
     * @param left
     *            the left-hand element to compare with
     * @param right
     *            the right hand element to compare with
     * @param comparator
     *            the comparator to compare elements with
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified comparator or any of the specified elements are
     *             <code>null</code>
     */
    public static <E> Predicate<E> between(E left, E right, Comparator<? extends E> comparator) {
        return and((Predicate) greaterThenOrEqual(left, comparator), (Predicate) lessThenOrEqual(
                right, comparator));
    }

    /**
     * Creates a Predicate that evaluates to <code>true</code> iff the element being
     * evaluated is the same or {@link Object#equals equal} to the element being specified
     * in this method.
     * <p>
     * If the specified object is serializable the returned predicate will also be
     * serializable.
     *
     * @param element
     *            the element to use for comparison
     * @return the newly created Predicate
     * @throws NullPointerException
     *             if the specified element is <code>null</code>
     * @param <E>
     *            the type of elements accepted by the predicate
     */
    public static <E> Predicate<E> equalsTo(E element) {
        return new IsEqualsPredicate<E>(element);
    }

    /**
     * Creates a Predicate that evaluates to <code>true</code> if the element being
     * tested is greater then the element being used to construct the predicate. The
     * predicate will use the objects natural comparator.
     * <p>
     * If the supplied element is serializable the returned predicate will also be
     * serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified element is <code>null</code>
     */
    public static <E extends Comparable<? super E>> Predicate<E> greaterThen(E element) {
        return new GreaterThenPredicate<E>(element);
    }

    /**
     * Creates a Predicate that evaluates to <code>true</code> if the element being
     * tested is greater then the element being used to construct the predicate. The
     * predicate will use the specified Comparator to compare the objects.
     * <p>
     * If the supplied element and Comparator is serializable the returned predicate will
     * also be serializable.
     *
     * @param element
     *            the element to compare with
     * @param comparator
     *            the Comparator used for comparing elements
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified element is <code>null</code>
     */
    public static <E> Predicate<E> greaterThen(E element, final Comparator<? extends E> comparator) {
        return new GreaterThenPredicate<E>(element, comparator);
    }

    /**
     * Creates a Predicate that evaluates to <code>true</code> if the element being
     * tested is greater then or equal to the element being used to construct the
     * predicate. The predicate will use the objects natural comparator.
     * <p>
     * If the supplied element is serializable the returned predicate will also be
     * serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified element is <code>null</code>
     */
    public static <E extends Comparable<? super E>> Predicate<E> greaterThenOrEqual(E element) {
        return new GreaterThenOrEqualPredicate<E>(element);
    }

    /**
     * Creates a Predicate that evaluates to <code>true</code> if the element being
     * tested is greater then or equal to the element being used to construct the
     * predicate. The predicate will use the specified Comparator to compare the objects.
     * <p>
     * If the supplied element and Comparator is serializable the returned predicate will
     * also be serializable.
     *
     * @param element
     *            the element to compare with
     * @param comparator
     *            the Comparator used for comparing elements
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified element is <code>null</code>
     */
    public static <E> Predicate<E> greaterThenOrEqual(E element,
            final Comparator<? extends E> comparator) {
        return new GreaterThenOrEqualPredicate<E>(element, comparator);
    }

    /**
     * Returns a Predicate that always evaluates to <code>false</code>. The returned
     * predicate is serializable.
     * <p>
     * This example illustrates the type-safe way to obtain a false predicate:
     *
     * <pre>
     * Predicate&lt;String&gt; s = Predicates.falsePredicate();
     * </pre>
     *
     * Implementation note: Implementations of this method need not create a separate
     * <tt>predicate</tt> object for each call. Using this method is likely to have
     * comparable cost to using the like-named field. (Unlike this method, the field does
     * not provide type safety.)
     *
     * @see #FALSE
     * @return a Predicate that returns <tt>false</tt> for any element
     * @param <E>
     *            the type of elements accepted by the predicate
     */
    @SuppressWarnings("unchecked")
    public static <E> Predicate<E> isFalse() {
        return FALSE;
    }
    /**
     * Returns a Predicate that returns <code>false</code> if the element being tested
     * is <code>null</code>. This predicate is serializable.
     * <p>
     * Implementation note: Implementations of this method need not create a separate
     * <tt>predicate</tt> object for each call. Using this method is likely to have
     * comparable cost to using the like-named field. (Unlike this method, the field does
     * not provide type safety.)
     *
     * @param <E>
     *            the types that are accepted by the predicate.
     * @return a Predicate that returns <code>false</code> if the element being tested
     *         is <code>null</code>
     */
    public static <E> Predicate<E> isNotNull() {
        return IS_NOT_NULL;
    }

    /**
     * Returns a Predicate that returns <code>true</code> if the element being tested is
     * <code>null</code>. This predicate is serializable.
     * <p>
     * Implementation note: Implementations of this method need not create a separate
     * <tt>predicate</tt> object for each call. Using this method is likely to have
     * comparable cost to using the like-named field. (Unlike this method, the field does
     * not provide type safety.)
     *
     * @param <E>
     *            the types that are accepted by the predicate.
     * @return a Predicate that returns <code>true</code> if the element being tested is
     *         <code>null</code>
     */
    public static <E> Predicate<E> isNull() {
        return IS_NULL;
    }

    /**
     * Creates a Predicate that always evaluates to <code>true</code>. The returned
     * predicate is serializable.
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
     * @return a Predicate that returns <tt>true</tt> for any element
     * @param <E>
     *            the type of elements accepted by the predicate
     */
    @SuppressWarnings("unchecked")
    public static <E> Predicate<E> isTrue() {
        return TRUE;
    }

    /**
     * Creates a predicate that tests whether the class of the element being tested is
     * either the same as, or is a superclass or superinterface of, the class or interface
     * represented by the specified Class parameter. It returns <code>true</code> if so;
     * otherwise it returns <code>false</code>. This predicate is serializable.
     *
     * @param clazz
     *            the class to test
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified clazz is <code>null</code>
     * @throws IllegalArgumentException
     *             if the class represents a primitive type
     * @see Class#isAssignableFrom(Class)
     */
    public static <E> Predicate<E> isTypeOf(Class<?> clazz) {
        return new IsTypePredicate(clazz);
    }

    /**
     * Creates a Predicate that evaluates to <code>true</code> if the element being
     * tested is less then the element being used to construct the predicate. The
     * predicate will use the objects natural comparator.
     * <p>
     * If the supplied element is serializable the returned predicate will also be
     * serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified element is <code>null</code>
     */
    public static <E extends Comparable<? super E>> Predicate<E> lessThen(E element) {
        return new LessThenPredicate<E>(element);
    }

    /**
     * Creates a Predicate that evaluates to <code>true</code> if the element being
     * tested is less then the element being used to construct the predicate. The
     * predicate will use the specified Comparator to compare the objects.
     * <p>
     * If the supplied element and Comparator is serializable the returned predicate will
     * also be serializable.
     *
     * @param element
     *            the element to compare with
     * @param comparator
     *            the Comparator used for comparing elements
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified element is <code>null</code>
     */
    public static <E> Predicate<E> lessThen(E element, final Comparator<? extends E> comparator) {
        return new LessThenPredicate<E>(element, comparator);
    }

    /**
     * Creates a Predicate that evaluates to <code>true</code> if the element being
     * tested is less then or equal to the element being used to construct the predicate.
     * The predicate will use the objects natural comparator.
     * <p>
     * If the supplied element is serializable the returned predicate will also be
     * serializable.
     *
     * @param element
     *            the element to compare with
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified element is <code>null</code>
     */
    public static <E extends Comparable<? super E>> Predicate<E> lessThenOrEqual(E element) {
        return new LessThenOrEqualPredicate<E>(element);
    }

    /**
     * Creates a Predicate that evaluates to <code>true</code> if the element being
     * tested is less then or equal to the element being used to construct the predicate.
     * The predicate will use the specified Comparator to compare the objects.
     * <p>
     * If the supplied element and Comparator is serializable the returned predicate will
     * also be serializable.
     *
     * @param element
     *            the element to compare with
     * @param comparator
     *            the Comparator used for comparing elements
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified element is <code>null</code>
     */
    public static <E> Predicate<E> lessThenOrEqual(E element,
            final Comparator<? extends E> comparator) {
        return new LessThenOrEqualPredicate<E>(element, comparator);
    }

    /**
     * Creates a Predicate that first applies the specified mapper to the argument before
     * evaluating the specified predicate. More formally
     *
     * <pre>
     * predicate.evaluate(mapper.map(element));
     * </pre>
     *
     * <p>
     * If both the supplied mapper and predicate are serializable the returned predicate
     * will also be serializable.
     *
     * @param mapper
     *            the Mapper that will map the element
     * @param predicate
     *            the Predicate that will evaluate the mapped element
     * @return the newly created Predicate
     * @throws NullPointerException
     *             if either the specified mapper or predicate are <code>null</code>
     * @param <F>
     *            the type of elements accepted by the Predicate being created
     * @param <T>
     *            the type of elements accepted by the specified Predicate
     */
    public static <F, T> Predicate<F> mapAndEvaluate(final Op<F, T> mapper,
            Predicate<? super T> predicate) {
        return new MapAndEvaluatePredicate<F, T>(mapper, predicate);
    }

    /**
     * Creates a Predicate that performs a logical logical NOT on the supplied Predicate.
     * More formally
     *
     * <pre>
     * !predicate.evaluate(element);
     * </pre>
     *
     * <p>
     * If the specified predicate is serializable the returned predicate will also be
     * serializable.
     *
     * @param predicate
     *            the predicate to negate
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if the specified predicate is <code>null</code>
     */
    public static <E> Predicate<E> not(Predicate<? super E> predicate) {
        return new NotPredicate<E>(predicate);
    }

    public static <E> Predicate<E> notEqualsTo(E element) {
        return not(equalsTo(element));
    }

    /**
     * Creates a new Predicate that will evaluate to <code>false</code> if the specified
     * element is <code>null</code>. Otherwise, it will return the evalutation result
     * of the specified predicate evaluate the element. More formally
     *
     * <pre>
     * element!=null &amp;&amp; predicate.evaluate(element);
     * </pre>
     *
     * <p>
     * If the specified predicate is serializable the returned predicate will also be
     * serializable.
     *
     * @param predicate
     *            the predicate
     * @return the newly created Predicate
     * @throws NullPointerException
     *             if the specified predicate is <code>null</code>
     * @param <E>
     *            the type of elements accepted by the predicate
     */
    public static <E> Predicate<E> notNullAnd(Predicate<? super E> predicate) {
        return and(IS_NOT_NULL, predicate);
    }

    /**
     * Creates a Predicate that performs a logical OR on two supplied predicates. The
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
     *            the left side Predicate
     * @param right
     *            the right side Predicate
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static <E> Predicate<E> or(Predicate<? super E> left, Predicate<? super E> right) {
        return new OrPredicate<E>(left, right);
    }

    /**
     * Creates a Predicate that evaluates to <code>true</code> iff the element being
     * evaluated has the same object identity as the element being specified in this
     * method.
     * <p>
     * If the specified object is serializable the returned predicate will also be
     * serializable.
     *
     * @param element
     *            the element to use for comparison
     * @return the newly created Predicate
     * @throws NullPointerException
     *             if the specified element is <code>null</code>
     * @param <E>
     *            the type of elements accepted by the predicate
     */
    public static <E> Predicate<E> sameAs(E element) {
        return new IsSamePredicate<E>(element);
    }

    /**
     * Creates a Predicate that performs a logical logical exclusive OR (XOR) on two
     * supplied predicates. More formally
     *
     * <pre>
     * left.evaluate(element) &circ; right.evaluate(element);
     * </pre>
     *
     * <p>
     * If both of the supplied predicates are serializable the returned predicate will
     * also be serializable.
     *
     * @param left
     *            the left side Predicate
     * @param right
     *            the right side Predicate
     * @return the newly created Predicate
     * @param <E>
     *            the type of elements accepted by the predicate
     * @throws NullPointerException
     *             if any of the specified predicates are <code>null</code>
     */
    public static <E> Predicate<E> xor(Predicate<? super E> left, Predicate<? super E> right) {
        return new XorPredicate<E>(left, right);
    }

    /**
     * Creates an array of predicates from an {@link Iterable}.
     *
     * @param iterable
     *            the iterable to convert
     * @return and array of predicate
     * @param <E>
     *            the type of the predicates
     */
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
    static final class AllPredicate<E> implements Predicate<E>, Iterable<Predicate<? super E>>,
            Serializable {

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

        /**
         * Returns <tt>true</tt> if all supplied Predicates accepts the element.
         *
         * @param element
         *            the element to test
         * @return <tt>true</tt> if all supplied Predicates accepts the element.
         */
        public boolean op(E element) {
            for (Predicate<? super E> predicate : predicates) {
                if (!predicate.op(element)) {
                    return false;
                }
            }
            return true;
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
    static final class AndPredicate<E> implements Predicate<E>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 6981902451700512606L;

        /** The left side operand. */
        private final Predicate<? super E> left;

        /** The right side operand. */
        private final Predicate<? super E> right;

        /**
         * Creates a new <code>AndPredicate</code>.
         *
         * @param left
         *            the left side Predicate
         * @param right
         *            the right side Predicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        public AndPredicate(Predicate<? super E> left, Predicate<? super E> right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /**
         * Returns the left side Predicate.
         *
         * @return the left side Predicate.
         */
        public Predicate<? super E> getLeftPredicate() {
            return left;
        }

        /**
         * Returns the right side Predicate.
         *
         * @return the right side Predicate.
         */
        public Predicate<? super E> getRightPredicate() {
            return right;
        }

        /** {@inheritDoc} */
        public boolean op(E element) {
            return left.op(element) && right.op(element);
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }

    /**
     * A Predicate that tests that at least one of the supplied predicates accepts a given
     * element.
     */
    static final class AnyPredicate<E> implements Predicate<E>, Iterable<Predicate<? super E>>,
            Serializable {

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

        /**
         * Returns <tt>true</tt> if all supplied Predicates accepts the element.
         *
         * @param element
         *            the element to test
         * @return <tt>true</tt> if all supplied Predicates accepts the element.
         */
        public boolean op(E element) {
            for (Predicate<? super E> predicate : predicates) {
                if (predicate.op(element)) {
                    return true;
                }
            }
            return false;
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
     * A Predicate that always evaluates to <tt>false</tt>. Use {@link #FALSE} to get
     * an instance of this Predicate.
     *
     * @see TruePredicate
     */
    static final class FalsePredicate implements Predicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -3048464662394104180L;

        /** Creates a new FalsePredicate. */
        private FalsePredicate() {}

        /**
         * Returns <tt>false</tt> for any element.
         *
         * @param element
         *            the element to test
         * @return <tt>false</tt> for any element
         */
        public boolean op(Object element) {
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
     * A Greather Then Or Equal predicate as per Comparable/Comparator contract.
     */
    static final class GreaterThenOrEqualPredicate<E> implements Predicate<E>, Serializable {

        /** <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -6815218477296552273L;

        /**
         * The comparator to compare elements with or null if the objects natural
         * comparator should be used.
         */
        private final Comparator comparator;

        /** The object to compare against. */
        private final Object object;

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
         * @param <T>
         *            type of objects accepted by the predicate
         */
        public <T extends Comparable<? super E>> GreaterThenOrEqualPredicate(T object) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            this.object = object;
            this.comparator = Comparators.NATURAL_COMPARATOR;
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
            return (E) object;
        }

        /** {@inheritDoc} */
        @SuppressWarnings("unchecked")
        public boolean op(E element) {
            return comparator.compare(object, element) <= 0;
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
    static final class GreaterThenPredicate<E> implements Predicate<E>, Serializable {

        /** <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -6815218477296552273L;

        /**
         * The comparator to compare elements with or null if the objects natural
         * comparator should be used.
         */
        private final Comparator comparator;

        /** The object to compare against. */
        private final Object object;

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

        /**
         * Creates a new greater then predicate.
         *
         * @param object
         *            the object to compare with.
         * @param <T>
         *            type of objects accepted by the predicate
         */
        public <T extends Comparable<? super E>> GreaterThenPredicate(T object) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            this.object = object;
            this.comparator = Comparators.NATURAL_COMPARATOR;
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
            return (E) object;
        }

        /** {@inheritDoc} */
        @SuppressWarnings("unchecked")
        public boolean op(E element) {
            return comparator.compare(object, element) < 0;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "$x > " + object;
        }
    }

    /**
     * A Predicate that evaluates to <code>true</code> iff the element being evaluated
     * is {@link Object#equals equal} to the element being specified.
     */
    static final class IsEqualsPredicate<E> implements Predicate<E>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -802615306772905787L;

        /** The element to compare with. */
        private final E element;

        /**
         * Creates an IsEqualsPredicate.
         *
         * @param element
         *            the element to use for comparison
         * @throws NullPointerException
         *             if the specified element is <code>null</code>
         */
        public IsEqualsPredicate(E element) {
            if (element == null) {
                throw new NullPointerException("element is null");
            }
            this.element = element;
        }

        /**
         * Returns the element we are comparing with.
         *
         * @return the element we are comparing with
         */
        public E getElement() {
            return element;
        }

        /** {@inheritDoc} */
        public boolean op(E element) {
            return this.element == element || this.element.equals(element);
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "equals " + element;
        }
    }

    /**
     * A Predicate that returns <code>true</code> if the element being tested is not
     * null.
     */
    static final class IsNotNullFilter implements Predicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6280765768913457567L;

        /** {@inheritDoc} */
        public boolean op(Object element) {
            return element != null;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "is not null";
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return IS_NOT_NULL;
        }
    }

    /**
     * A Predicate that evaluates to <code>true</code> iff the element being evaluated
     * has the same object identity as the element being specified.
     */
    static final class IsSamePredicate<E> implements Predicate<E>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3761971557773620791L;

        /** The element to compare with. */
        private final E element;

        /**
         * Creates an IsSamePredicate.
         *
         * @param element
         *            the element to use for comparison
         * @throws NullPointerException
         *             if the specified element is <code>null</code>
         */
        public IsSamePredicate(E element) {
            if (element == null) {
                throw new NullPointerException("element is null");
            }
            this.element = element;
        }

        /**
         * Returns the element we are comparing with.
         *
         * @return the element we are comparing with
         */
        public E getElement() {
            return element;
        }

        /** {@inheritDoc} */
        public boolean op(E element) {
            return this.element == element;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "is (==) " + element;
        }
    }

    /**
     * A predicate that tests whether the class of the element being tested is either the
     * same as, or is a superclass or superinterface of, the class or interface
     * represented by the specified Class parameter. It returns <code>true</code> if so;
     * otherwise it returns <code>false</code>.
     */
    static final class IsTypePredicate<E> implements Predicate<E>, Serializable {

        /** A default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3256440304922996793L;

        /** The class we are testing against. */
        private final Class<?> theClass;

        /**
         * Creates a new IsTypePredicate.
         *
         * @param theClass
         *            the class we are testing against.
         * @throws NullPointerException
         *             if the specified clazz is <code>null</code>
         * @throws IllegalArgumentException
         *             if the class represents a primitive type
         */
        public IsTypePredicate(Class<?> theClass) {
            if (theClass == null) {
                throw new NullPointerException("theClass is null");
            } else if (theClass.isPrimitive()) {
                throw new IllegalArgumentException(
                        "cannot create IsTypePredicate from primitive class '"
                                + theClass.getName()
                                + "', since all primitive arguments to evaluate() are automatically boxed");
            }

            this.theClass = theClass;
        }

        /**
         * Returns the class we are testing against.
         *
         * @return Returns the theClass.
         */
        public Class<?> getType() {
            return theClass;
        }

        /**
         * Tests the given element for acceptance.
         *
         * @param element
         *            the element to test against.
         * @return <code>true</code> if the filter accepts the element;
         *         <code>false</code> otherwise.
         */
        public boolean op(E element) {
            return theClass.isAssignableFrom(element.getClass());
        }
    }

    /**
     * A Less Then predicate as per Comparable/Comparator contract.
     */
    static final class LessThenOrEqualPredicate<E> implements Predicate<E>, Serializable {

        /** <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 1330339174193813467L;

        /**
         * The comparator to compare elements with or null if the objects natural
         * comparator should be used.
         */
        private final Comparator comparator;

        /** The object to compare against. */
        private final Object object;

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
         * @param <T>
         *            type of objects accepted by the predicate
         */
        public <T extends Comparable<? super E>> LessThenOrEqualPredicate(T object) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            this.object = object;
            this.comparator = Comparators.NATURAL_COMPARATOR;
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
            return (E) object;
        }

        /** {@inheritDoc} */
        @SuppressWarnings("unchecked")
        public boolean op(E element) {
            return comparator.compare(object, element) >= 0;
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
    static final class LessThenPredicate<E> implements Predicate<E>, Serializable {

        /** <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 1330339174193813467L;

        /**
         * The comparator to compare elements with or null if the objects natural
         * comparator should be used.
         */

        private final Comparator comparator;

        /** The object to compare against. */
        private final Object object;

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

        /**
         * Creates a new less then predicate.
         *
         * @param object
         *            the object to compare with.
         * @param <T>
         *            type of objects accepted by the predicate
         */
        public <T extends Comparable<? super E>> LessThenPredicate(T object) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            this.object = object;
            this.comparator = Comparators.NATURAL_COMPARATOR;
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
            return (E) object;
        }

        /** {@inheritDoc} */
        @SuppressWarnings("unchecked")
        public boolean op(E element) {
            return comparator.compare(object, element) > 0;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "$x < " + object;
        }
    }

    /**
     * A Predicate that first applies the specified mapper to the argument before
     * evaluating the specified predicate.
     */
    static final class MapAndEvaluatePredicate<F, T> implements Predicate<F>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -6292758840373110577L;

        /** The mapper used to map the element. */
        private final Op<F, T> mapper;

        /** The predicate to test the mapped value against. */
        private final Predicate<? super T> predicate;

        /**
         * Creates a new MapAndEvaluatePredicate.
         *
         * @param mapper
         *            the mapper used to first map the argument
         * @param predicate
         *            the predicate used to evaluate the mapped argument
         */
        public MapAndEvaluatePredicate(Op<F, T> mapper, Predicate<? super T> predicate) {
            if (mapper == null) {
                throw new NullPointerException("mapper is null");
            } else if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
            this.mapper = mapper;
        }

        /**
         * Returns the mapper that will map the object before applying the predicate on
         * it.
         *
         * @return the mapper that will map the object before applying the predicate on it
         */
        public Op<F, T> getMapper() {
            return mapper;
        }

        /**
         * Returns the Predicate we are testing against.
         *
         * @return the Predicate we are testing against.
         */
        public Predicate<? super T> getPredicate() {
            return predicate;
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
        public boolean op(F element) {
            return predicate.op(mapper.op(element));
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "convert " + mapper;
        }
    }

    /**
     * A Predicate that evaluates to true iff the Predicate used for constructing
     * evaluates to <code>false</code>.
     */
    static final class NotPredicate<E> implements Predicate<E>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -5117781730584740429L;

        /** The Predicate to negate. */
        private final Predicate<? super E> predicate;

        /**
         * Creates a new NotPredicate.
         *
         * @param predicate
         *            the predicate to negate.
         * @throws NullPointerException
         *             if the specified predicate is <code>null</code>
         */
        public NotPredicate(Predicate<? super E> predicate) {
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
        public Predicate<? super E> getPredicate() {
            return predicate;
        }

        /**
         * Returns a boolean representing the logical NOT value of the supplied Predicate.
         *
         * @param element
         *            the element to test
         * @return the logical NOT of the supplied Predicate
         */
        public boolean op(E element) {
            return !predicate.op(element);
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
    static final class OrPredicate<E> implements Predicate<E>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 7602293335100183390L;

        /** The left side operand. */
        private final Predicate<? super E> left;

        /** The right side operand. */
        private final Predicate<? super E> right;

        /**
         * Creates a new <code>OrPredicate</code>.
         *
         * @param left
         *            the left side Predicate
         * @param right
         *            the right side Predicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        OrPredicate(Predicate<? super E> left, Predicate<? super E> right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /**
         * Returns the left side Predicate.
         *
         * @return the left side Predicate.
         */
        public Predicate<? super E> getLeftPredicate() {
            return left;
        }

        /**
         * Returns the right side Predicate.
         *
         * @return the right side Predicate.
         */
        public Predicate<? super E> getRightPredicate() {
            return right;
        }

        /** {@inheritDoc} */
        public boolean op(E element) {
            return left.op(element) || right.op(element);
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") or (" + right + ")";
        }
    }

    /**
     * A Predicate that always evaluates to <tt>true</tt>. Use {@link #TRUE} to get an
     * instance of this Predicate.
     *
     * @see FalsePredicate
     */
    static final class TruePredicate implements Predicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3258129137502925875L;

        /** Creates a new TruePredicate. */
        private TruePredicate() {}

        /**
         * Returns <tt>true</tt> for any element.
         *
         * @param element
         *            the element to test
         * @return <tt>true</tt> for any element
         */
        public boolean op(Object element) {
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

    /**
     * A Predicate that performs a logical exclusive OR (XOR) on two supplied predicates.
     */
    static final class XorPredicate<E> implements Predicate<E>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 1155267141991954303L;

        /** The left side operand. */
        private final Predicate<? super E> left;

        /** The right side operand. */
        private final Predicate<? super E> right;

        /**
         * Creates a new <code>XorPredicate</code>.
         *
         * @param left
         *            the left side Predicate
         * @param right
         *            the right side Predicate
         * @throws NullPointerException
         *             if any of the supplied predicates are <code>null</code>
         */
        XorPredicate(Predicate<? super E> left, Predicate<? super E> right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /**
         * Returns the left side Predicate.
         *
         * @return the left side Predicate.
         */
        public Predicate<? super E> getLeftPredicate() {
            return left;
        }

        /**
         * Returns the right side Predicate.
         *
         * @return the right side Predicate.
         */
        public Predicate<? super E> getRightPredicate() {
            return right;
        }

        /** {@inheritDoc} */
        public boolean op(E element) {
            return left.op(element) ^ right.op(element);
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "(" + left + ") xor (" + right + ")";
        }
    }
}
