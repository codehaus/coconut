/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.coconut.predicate.CollectionPredicates.IsTypePredicate;
import org.coconut.predicate.spi.CompositePredicate;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class Predicates {
    /** A predicate that always return False. */
    public static final Predicates.FalsePredicate FALSE = Predicates.FalsePredicate.INSTANCE;
    
    public final static IsTypePredicate IS_NUMBER = isType(Number.class);

    /** A predicate that always return True. */
    public static final Predicates.TruePredicate TRUE = Predicates.TruePredicate.INSTANCE;

    ///CLOVER:OFF
    /** Cannot instantiate. */
    private Predicates() {}
    ///CLOVER:ON

    /**
     * A Predicate that tests that <tt>all</tt> of the supplied Predicates accepts a given
     * element.
     */
    final static class AllPredicate<E> implements Predicate<E>, CompositePredicate<E>,
            Iterable<Predicate<E>>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -8945752276662769791L;

        /** All the predicates that are being checked. */
        private final Predicate<E>[] predicates;

        /**
         * Constructs a new AllPredicate. The Predicate will use a copy of the array of supplied
         * predicates.
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
                    throw new NullPointerException("predicates contained a null on index = "
                            + i);
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
            for (Predicate<E> predicate : predicates) {
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
        public List<Predicate<E>> getPredicates() {
            return new ArrayList<Predicate<E>>(Arrays.asList(predicates));
        }

        /** {@inheritDoc} */
        public Iterator<Predicate<E>> iterator() {
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
     * A Predicate that performs a logical exclusive AND on two supplied predicates. The predicate
     * TODO check focs for javas and.
     */
    final static class AndPredicate<E> implements Predicate<E>, CompositePredicate<E>,
            Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 6981902451700512606L;

        private final boolean isStrict;

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
        public AndPredicate(final Predicate<E> left, final Predicate<E> right) {
            this(left, right, true);
        }

        public AndPredicate(final Predicate<E> left, final Predicate<E> right, boolean isStrict) {
            if (left == null) {
                throw new NullPointerException("left is null");
            }
            if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
            this.isStrict = isStrict;
        }

        /** {@inheritDoc} */
        public boolean evaluate(E element) {
            return left.evaluate(element) && right.evaluate(element);
        }

        /** {@inheritDoc} */
        @SuppressWarnings("unchecked")
        public List<Predicate<E>> getPredicates() {
            return Arrays.asList(left, right);
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
         * Returns the right side operand.
         * 
         * @return the right side operand.
         */
        public Predicate<E> getRightPredicate() {
            return right;
        }

        /**
         * Returns whether the operands must be evaluated left and then right (strict) or
         * if each of them can be evaluated indenpendently.
         */
        public boolean isStrict() {
            return isStrict;
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
            Iterable<Predicate<E>>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3257282517878192437L;

        private final Predicate<E>[] predicates;

        /**
         * @param predicates
         */
        @SuppressWarnings("unchecked")
        public AnyPredicate(final Predicate<? super E>[] predicates) {
            this.predicates = new Predicate[predicates.length];
            System.arraycopy(predicates, 0, this.predicates, 0, predicates.length);
            for (int i = 0; i < this.predicates.length; i++) {
                if (this.predicates[i] == null) {
                    throw new NullPointerException("predicates contained a null on index = "
                            + i);
                }
            }
        }

        /** {@inheritDoc} */
        public boolean evaluate(E element) {
            for (Predicate<E> predicate : predicates) {
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
        public List<Predicate<E>> getPredicates() {
            return new ArrayList<Predicate<E>>(Arrays.asList(predicates));
        }

        /** {@inheritDoc} */
        public Iterator<Predicate<E>> iterator() {
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
    final static class EqualsPredicate<E> implements Predicate<E>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3761971557773620791L;

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
        public EqualsPredicate(final E object) throws NullPointerException {
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
     * {@link org.coconut.predicate.Predicates#FALSE} to get an instance of this Predicate.
     * 
     * @see TruePredicate
     */
    final static class FalsePredicate implements Predicate, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -3048464662394104180L;

        /** The one and only instance. */
        static final FalsePredicate INSTANCE = new FalsePredicate();

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
            return "false";
        }
    }

    /**
     * A greather-then predicate as per Comparable/Comparator contract.
     */
    final static class GreaterThenPredicate<E> implements Predicate<E>, Serializable {

        /** <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -6815218477296552273L;

        private final Comparator comparator;

        /** The object to compare against. */
        private final E object;

        public GreaterThenPredicate(E object) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            if (!(object instanceof Comparable)) {
                throw new IllegalArgumentException("object not instanceof Comparable");
            }
            this.object = object;
            this.comparator = null;
        }

        /**
         * Creates a new Predicate that accepts all elements that have the same object
         * identity as the element supplied.
         * 
         * @param object
         *            the objetc to compare with.
         */
        public GreaterThenPredicate(E object, final Comparator<? extends E> comparator) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            if (comparator == null) {
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

        @SuppressWarnings("unchecked")
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
     * A Greather Then Or Equal predicate as per Comparable/Comparator contract.
     */
    final static class GreaterThenOrEqualPredicate<E> implements Predicate<E>, Serializable {

        /** <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -6815218477296552273L;

        private final Comparator comparator;

        /** The object to compare against. */
        private final E object;

        /**
         * Creates a new Predicate that accepts all elements that have the same object
         * identity as the element supplied.
         * 
         * @param object
         *            the objetc to compare with.
         */
        public GreaterThenOrEqualPredicate(E object, final Comparator<? extends E> comparator) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.object = object;
            this.comparator = comparator;
        }

        public <T extends E> GreaterThenOrEqualPredicate(T object) {
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
                return ((Comparable) object).compareTo(element) <= 0;
            } else {
                return comparator.compare(object, element) <= 0;
            }
        }

        @SuppressWarnings("unchecked")
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
     * A Less Then predicate as per Comparable/Comparator contract.
     */
    final static class LessThenPredicate<E> implements Predicate<E>, Serializable {

        /** <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 1330339174193813467L;

        private final Comparator comparator;

        /** The object to compare against. */
        private final E object;

        public LessThenPredicate(E object) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            if (!(object instanceof Comparable)) {
                throw new IllegalArgumentException("object not instanceof Comparable");
            }
            this.object = object;
            this.comparator = null;
        }

        /**
         * Creates a new Predicate that accepts all elements that have the same object
         * identity as the element supplied.
         * 
         * @param object
         *            the objetc to compare with.
         */
        public LessThenPredicate(E object, final Comparator<? extends E> comparator) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            if (comparator == null) {
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

        @SuppressWarnings("unchecked")
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

    /**
     * A Less Then predicate as per Comparable/Comparator contract.
     */
    final static class LessThenOrEqualPredicate<E> implements Predicate<E>, Serializable {

        /** <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 1330339174193813467L;

        private final Comparator comparator;

        /** The object to compare against. */
        private final E object;

        /**
         * Creates a new Predicate that accepts all elements that have the same object
         * identity as the element supplied.
         * 
         * @param object
         *            the objetc to compare with.
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

        public <T extends E> LessThenOrEqualPredicate(T object) {
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

        @SuppressWarnings("unchecked")
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
     * A Predicate that test that a supplied Predicate does <tt>not</tt> accept a given
     * Element.
     */
    final static class NotPredicate<E> implements Predicate<E>, CompositePredicate<E>,
            Serializable {

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
         * Returns a boolean representing the logical NOT value of the supplied Predicate. If
         * the specified Predicates accept() method returns <tt>true</tt>, this method
         * returns <tt>false</tt>; if it is <tt>false</tt>, this method returns
         * <tt>true</tt>.
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
         * @see org.coconut.predicate.spi.CompositePredicate#getPredicates()
         */
        @SuppressWarnings("unchecked")
        public List<Predicate<E>> getPredicates() {
            return Arrays.asList(left, right);
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
     * @version $Id: Predicates.java 36 2006-08-22 09:59:45Z kasper $
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
    final static class XorPredicate<E> implements CompositePredicate<E>, Predicate<E>,
            Serializable {

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

        /** {@inheritDoc} */
        public List<Predicate<E>> getPredicates() {
            return Arrays.asList(left, right);
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

    /**
     * Returns a Predicate that only accepts an element if <tt>all</tt> the predicates accept
     * the element. The Predicate will use a copy of the array of supplied predicates.
     * 
     * @param predicates
     *            the predicates to test
     * @return a Predicate that tests all elements
     */
    public static <E> Predicates.AllPredicate<E> all(Predicate<E>... predicates) {
        return new Predicates.AllPredicate<E>(predicates);
    }

    public static <E> Predicates.AndPredicate<E> and(Predicate<E> left, Predicate<E> right) {
        return new Predicates.AndPredicate<E>(left, right);
    }

    public static <E> Predicates.AndPredicate<E> and(Predicate<E> left, Predicate<E> right,
            boolean isStrict) {
        return new Predicates.AndPredicate<E>(left, right, isStrict);
    }

    public static <E> Predicates.AnyPredicate<E> any(Predicate<E>... predicates) {
        return new Predicates.AnyPredicate<E>(predicates);
    }

    @SuppressWarnings( { "unchecked" })
    public static <E> AnyPredicate<E> anyEquals(E... elements) {
        Predicates.EqualsPredicate[] predicate = new Predicates.EqualsPredicate[elements.length];
        for (int i = 0; i < predicate.length; i++) {
            predicate[i] = Predicates.equal(elements[i]);
        }
        return any(predicate);
    }

    @SuppressWarnings("unchecked")
    public static Predicates.AnyPredicate<IsTypePredicate> anyType(Class... clazz) {
        IsTypePredicate[] cbf = new IsTypePredicate[clazz.length];
        for (int i = 0; i < cbf.length; i++) {
            cbf[i] = isType(clazz[i]);
        }
        return Predicates.any(cbf);
    }

    public static <E> Predicate<E> between(E first, E second) {
        return and(Predicates.greatherThen(first), Predicates.lessThen(second));
    }

    /**
     * Returns a Predicate that accepts all elements that are {@link Object#equals equal} to
     * the specified object.
     * 
     * @param object
     *            the object we test against.
     * @return a new EqualPredicate
     * @throws NullPointerException
     *             if the specified object is <code>null</code>
     */
    public static <E> Predicates.EqualsPredicate<E> equal(E object) {
        return new Predicates.EqualsPredicate<E>(object);
    }

    @SuppressWarnings("unchecked")
    public static <E> Predicate<E> falsePredicate() {
        return FALSE;
    }

    public static <E> Predicates.GreaterThenPredicate<E> greatherThen(E element) {
        return new Predicates.GreaterThenPredicate<E>(element);
    }

    public static <E> Predicates.GreaterThenPredicate<E> greatherThen(E object,
            final Comparator<? extends E> comparator) {
        return new Predicates.GreaterThenPredicate<E>(object, comparator);
    }

    public static <E> Predicates.GreaterThenOrEqualPredicate<E> greatherThenOrEqual(E object) {
        return new Predicates.GreaterThenOrEqualPredicate<E>(object);
    }

    public static <E> Predicates.GreaterThenOrEqualPredicate<E> greatherThenOrEqual(E object,
            final Comparator<? extends E> comparator) {
        return new Predicates.GreaterThenOrEqualPredicate<E>(object, comparator);
    }

    public static IsTypePredicate isType(Class clazz) {
        return new IsTypePredicate(clazz);
    }

    public static <E> Predicates.LessThenPredicate<E> lessThen(E element) {
        return new Predicates.LessThenPredicate<E>(element);
    }

    public static <E> Predicates.LessThenPredicate<E> lessThen(E object,
            final Comparator<? extends E> comparator) {
        return new Predicates.LessThenPredicate<E>(object, comparator);
    }

    public static <E> Predicates.LessThenOrEqualPredicate<E> lessThenOrEqual(E object) {
        return new Predicates.LessThenOrEqualPredicate<E>(object);
    }

    public static <E> Predicates.LessThenOrEqualPredicate<E> lessThenOrEqual(E object,
            final Comparator<? extends E> comparator) {
        return new Predicates.LessThenOrEqualPredicate<E>(object, comparator);
    }

    public static <E> Predicates.NotPredicate<E> not(Predicate<E> predicate) {
        return new Predicates.NotPredicate<E>(predicate);
    }

    public static <E> Predicates.OrPredicate<E> or(Predicate<E> left, Predicate<E> right) {
        return new Predicates.OrPredicate<E>(left, right);
    }

    public static <E> Predicates.SamePredicate<E> same(E element) {
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
    public static <E> Predicates.XorPredicate<E> xor(Predicate<E> left, Predicate<E> right) {
        return new Predicates.XorPredicate<E>(left, right);
    }

}
