/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import java.io.Serializable;
import java.util.Comparator;

import org.coconut.filter.LogicFilters.AnyFilter;

/**
 * Contains common filters that can be used for testing whether two objects are
 * equal (according to the general Java equals contract), the same, or if they
 * implement Comparablce how they relate.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ComparisonFilters.java 36 2006-08-22 09:59:45Z kasper $
 */
public final class ComparisonFilters {

    public static <E> SameFilter<E> same(E element) {
        return new SameFilter<E>(element);
    }

    public static <E> GreaterThenFilter<E> greatherThen(E element) {
        return new GreaterThenFilter<E>(element);
    }

    public static <E> GreaterThenFilter<E> greatherThen(E object,
            final Comparator<? extends E> comparator) {
        return new GreaterThenFilter<E>(object, comparator);
    }

    //
    // public static <E, T extends E & Comparable<? super T>>
    // GreaterThenOrEqualFilter<E> greatherThenOrEqual(
    // T element) {
    // return new GreaterThenOrEqualFilter<E>(element);
    // }

    public static <E> GreaterThenOrEqualFilter<E> greatherThenOrEqual(E object,
            final Comparator<? extends E> comparator) {
        return new GreaterThenOrEqualFilter<E>(object, comparator);
    }

    public static <E> LessThenFilter<E> lessThen(E element) {
        return new LessThenFilter<E>(element);
    }

    public static <E> LessThenOrEqualFilter<E> lessThenOrEqual(E object) {
        return new LessThenOrEqualFilter<E>(object);
    }

    public static <E> LessThenOrEqualFilter<E> lessThenOrEqual(E object,
            final Comparator<? extends E> comparator) {
        return new LessThenOrEqualFilter<E>(object, comparator);
    }

    public static <E> LessThenFilter<E> lessThen(E object,
            final Comparator<? extends E> comparator) {
        return new LessThenFilter<E>(object, comparator);
    }

    public static <E> Filter<E> between(E first, E second) {
        return LogicFilters.and(greatherThen(first), lessThen(second));
    }

    // public static <E, T extends E & Comparable<? super T>>
    // LessThenOrEqualFilter<E> lessThenOrEqual(
    // T element) {
    // return new LessThenOrEqualFilter<E>(element);
    // }
    //

    //    
    // public static <E, T extends E & Comparable<? super T>> Filter<E> between(
    // T left, T right) {
    // GreaterThenFilter<E> f1 = new GreaterThenFilter<E>(left);
    // LessThenFilter<E> f2 = new LessThenFilter<E>(right);
    // return LogicFilters.and(f1, f2);
    // }

    /**
     * Returns a Filter that accepts all elements that are
     * {@link Object#equals equal} to the specified object.
     * 
     * @param object
     *            the object we test against.
     * @return a new EqualFilter
     * @throws NullPointerException
     *             if the specified object is <code>null</code>
     */
    public static <E> EqualsFilter<E> equal(E object) {
        return new EqualsFilter<E>(object);
    }

    @SuppressWarnings( { "unchecked" })
    public static <E> AnyFilter<E> anyEquals(E... elements) {
        EqualsFilter[] filter = new EqualsFilter[elements.length];
        for (int i = 0; i < filter.length; i++) {
            filter[i] = equal(elements[i]);
        }
        return LogicFilters.any((Filter[]) filter);
    }

    /**
     * A Filter that accepts all elements that are {@link Object#equals equal}
     * to the specified object.
     */
    public final static class EqualsFilter<E> implements Filter<E>, Serializable {

        /** Default <code>serialVersionUID</code> */
        private static final long serialVersionUID = 3761971557773620791L;

        /** The object to compare with. */
        private final E object;

        /**
         * Creates a new EqualsFilter
         * 
         * @param object
         *            the object to compare against.
         * @throws NullPointerException
         *             if the specified object is null
         */
        public EqualsFilter(final E object) throws NullPointerException {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            this.object = object;
        }

        /**
         * Returns the object we are comparing with.
         * 
         * @return the object we are comparing with.
         */
        public E getObject() {
            return object;
        }

        /**
         * Accepts all elements that are {@link Object#equals equal} to the
         * specified object.
         * 
         * @param element
         *            the element to test against.
         * @return <code>true</code> if the filter accepts the element;
         *         <code>false</code> otherwise.
         */
        public boolean accept(E element) {
            return object.equals(element);
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "equals " + object;
        }
    }

    /**
     * A greather-then filter as per Comparable/Comparator contract.
     */
    public final static class GreaterThenFilter<E> implements Filter<E>, Serializable {

        /** <code>serialVersionUID</code> */
        private static final long serialVersionUID = -6815218477296552273L;

        /** The object to compare against */
        private final E object;

        private final Comparator comparator;

        /**
         * Creates a new Filter that accepts all elements that have the same
         * object identity as the element supplied.
         * 
         * @param object
         *            the objetc to compare with.
         */
        public GreaterThenFilter(E object, final Comparator<? extends E> comparator) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.object = object;
            this.comparator = comparator;
        }

        public GreaterThenFilter(E object) {
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
         * Returns the object we are comparing.
         * 
         * @return the object we are comparing
         */
        public E getObject() {
            return object;
        }

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        @SuppressWarnings("unchecked")
        public boolean accept(E element) {
            if (comparator == null) {
                return ((Comparable) object).compareTo(element) < 0;
            } else {
                return comparator.compare(object, element) < 0;
            }
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "$x > " + object;
        }

        @SuppressWarnings("unchecked")
        public Comparator<? extends E> getComparator() {
            return comparator;
        }
    }

    /**
     * A Greather Then Or Equal filter as per Comparable/Comparator contract.
     */
    public final static class GreaterThenOrEqualFilter<E> implements Filter<E>,
            Serializable {

        /** <code>serialVersionUID</code> */
        private static final long serialVersionUID = -6815218477296552273L;

        /** The object to compare against */
        private final E object;

        private final Comparator comparator;

        /**
         * Creates a new Filter that accepts all elements that have the same
         * object identity as the element supplied.
         * 
         * @param object
         *            the objetc to compare with.
         */
        public GreaterThenOrEqualFilter(E object, final Comparator<? extends E> comparator) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.object = object;
            this.comparator = comparator;
        }

        // public <T extends E & Comparable<? super T> >
        // GreaterThenOrEqualFilter(T object) {
        // if (object == null) {
        // throw new NullPointerException("element is null");
        // }
        // if (!(object instanceof Comparable)) {
        // throw new IllegalArgumentException(
        // "object not instanceof Comparable");
        // }
        // this.object = null; //object;
        // this.comparator = null;
        // }

        /**
         * Returns the object we are comparing.
         * 
         * @return the object we are comparing
         */
        public E getObject() {
            return object;
        }

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        @SuppressWarnings("unchecked")
        public boolean accept(E element) {
            if (comparator == null) {
                return ((Comparable) object).compareTo(element) <= 0;
            } else {
                return comparator.compare(object, element) <= 0;
            }
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return " >= " + object;
        }

        @SuppressWarnings("unchecked")
        public Comparator<? extends E> getComparator() {
            return comparator;
        }
    }

    /**
     * A Less Then filter as per Comparable/Comparator contract.
     */
    public final static class LessThenFilter<E> implements Filter<E>, Serializable {

        /** <code>serialVersionUID</code> */
        private static final long serialVersionUID = 1330339174193813467L;

        /** The object to compare against */
        private final E object;

        private final Comparator comparator;

        /**
         * Creates a new Filter that accepts all elements that have the same
         * object identity as the element supplied.
         * 
         * @param object
         *            the objetc to compare with.
         */
        public LessThenFilter(E object, final Comparator<? extends E> comparator) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.object = object;
            this.comparator = comparator;
        }

        public LessThenFilter(E object) {
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
         * Returns the object we are comparing.
         * 
         * @return the object we are comparing
         */
        public E getObject() {
            return object;
        }

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        @SuppressWarnings("unchecked")
        public boolean accept(E element) {
            if (comparator == null) {
                return ((Comparable) object).compareTo(element) > 0;
            } else {
                return comparator.compare(object, element) > 0;
            }
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "$x < " + object;
        }

        @SuppressWarnings("unchecked")
        public Comparator<? extends E> getComparator() {
            return comparator;
        }
    }

    /**
     * A Less Then filter as per Comparable/Comparator contract.
     */
    public final static class LessThenOrEqualFilter<E> implements Filter<E>, Serializable {

        /** <code>serialVersionUID</code> */
        private static final long serialVersionUID = 1330339174193813467L;

        /** The object to compare against */
        private final E object;

        private final Comparator comparator;

        /**
         * Creates a new Filter that accepts all elements that have the same
         * object identity as the element supplied.
         * 
         * @param object
         *            the objetc to compare with.
         */
        public LessThenOrEqualFilter(E object, final Comparator<? extends E> comparator) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.object = object;
            this.comparator = comparator;
        }

        public <T extends E> LessThenOrEqualFilter(T object) {
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
         * Returns the object we are comparing.
         * 
         * @return the object we are comparing
         */
        public E getObject() {
            return object;
        }

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        @SuppressWarnings("unchecked")
        public boolean accept(E element) {
            if (comparator == null) {
                return ((Comparable) object).compareTo(element) >= 0;
            } else {
                return comparator.compare(object, element) >= 0;
            }
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return " <= " + object;
        }

        @SuppressWarnings("unchecked")
        public Comparator<? extends E> getComparator() {
            return comparator;
        }
    }

    /**
     * A filter that accepts all elements that have the same object identity as
     * the one specified.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
     * @version $Id: ComparisonFilters.java 36 2006-08-22 09:59:45Z kasper $
     */
    public final static class SameFilter<E> implements Filter<E>, Serializable {

        /** Default <code>serialVersionUID</code> */
        private static final long serialVersionUID = 3761971557773620791L;

        /** The object to compare against */
        private final E object;

        /**
         * Creates a new Filter that accepts all elements that have the same
         * object identity as the element supplied.
         * 
         * @param object
         *            the objetc to compare with.
         */
        public SameFilter(final E object) {
            if (object == null) {
                throw new NullPointerException("element is null");
            }
            this.object = object;
        }

        /**
         * Returns the object we are comparing.
         * 
         * @return the object we are comparing
         */
        public E getObject() {
            return object;
        }

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        public boolean accept(E element) {
            return object == element;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "is (==) " + object;
        }
    }
}
