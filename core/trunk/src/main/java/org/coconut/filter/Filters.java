/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.coconut.filter.CollectionFilters.IsTypeFilter;
import org.coconut.filter.spi.CompositeFilter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Filters {
    /** A filter that always return False. */
    public static final Filters.FalseFilter FALSE = Filters.FalseFilter.INSTANCE;

    public final static IsTypeFilter IS_NUMBER = isType(Number.class);

    /** A filter that always return True. */
    public static final Filters.TrueFilter TRUE = Filters.TrueFilter.INSTANCE;

    /**
     * A Filter that tests that <tt>all</tt> of the supplied Filters accepts a
     * given element.
     */
    final static class AllFilter<E> implements Filter<E>, CompositeFilter<E>,
            Iterable<Filter<E>>, Serializable {

        /** Default <code>serialVersionUID</code> */
        private static final long serialVersionUID = -8945752276662769791L;

        private final Filter<E>[] filters;

        /**
         * Constructs a new AllFilter. The Filter will use a copy of the array
         * of supplied filters.
         * 
         * @param filters
         *            the filters to test
         */
        @SuppressWarnings("unchecked")
        public AllFilter(final Filter<? super E>[] filters) {
            this.filters = new Filter[filters.length];
            System.arraycopy(filters, 0, this.filters, 0, filters.length);
            for (int i = 0; i < this.filters.length; i++) {
                if (this.filters[i] == null) {
                    throw new NullPointerException("filters contained a null on index = "
                            + i);
                }
            }
        }

        /**
         * Returns <tt>true</tt> if all supplied Filters accepts the element.
         * 
         * @param element
         *            the element to test
         * @return <tt>true</tt> if all supplied Filters accepts the element.
         */
        public boolean accept(E element) {
            for (Filter<E> filter : filters) {
                if (!filter.accept(element)) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Returns the filters we are testing against.
         * 
         * @return the filters we are testing against
         */
        public List<Filter<E>> getFilters() {
            return new ArrayList<Filter<E>>(Arrays.asList(filters));
        }

        /**
         * @see java.lang.Iterable#iterator()
         */
        public Iterator<Filter<E>> iterator() {
            return Arrays.asList(filters).iterator();
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            if (filters.length == 0) {
                return "";
            } else if (filters.length == 1) {
                return filters[0].toString();
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("((");
                builder.append(filters[0]);
                builder.append(")");
                for (int i = 1; i < filters.length; i++) {
                    builder.append(" and (");
                    builder.append(filters[i]);
                    builder.append(")");
                }
                builder.append(")");
                return builder.toString();
            }
        }
    }

    /**
     * A Filter that performs a logical exclusive AND on two supplied filters.
     * The filter TODO check focs for javas and.
     */
    final static class AndFilter<E> implements Filter<E>, CompositeFilter<E>,
            Serializable {

        /** Default <code>serialVersionUID</code> */
        private static final long serialVersionUID = 6981902451700512606L;

        private final boolean isStrict;

        /** The left side operand. */
        private final Filter<E> left;

        /** The right side operand. */
        private final Filter<E> right;

        /**
         * Constructs a new <code>AndFilter</code>.
         * 
         * @param left
         *            the left side operand
         * @param right
         *            the right side operand
         */
        public AndFilter(final Filter<E> left, final Filter<E> right) {
            this(left, right, true);
        }

        public AndFilter(final Filter<E> left, final Filter<E> right, boolean isStrict) {
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

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        public boolean accept(E element) {
            return left.accept(element) && right.accept(element);
        }

        /**
         * @see org.coconut.filter.spi.CompositeFilter#getFilters()
         */
        @SuppressWarnings("unchecked")
        public List<Filter<E>> getFilters() {
            return Arrays.asList(left, right);
        }

        /**
         * Returns the left side operand.
         * 
         * @return the left side operand.
         */
        public Filter<E> getLeftFilter() {
            return left;
        }

        /**
         * Returns the right side operand.
         * 
         * @return the right side operand.
         */
        public Filter<E> getRightFilter() {
            return right;
        }

        /**
         * Returns whether the operands must be evaluated left and then right
         * (strict) or if each of them can be evaluated indenpendently.
         * 
         */
        public boolean isStrict() {
            return isStrict;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "(" + left + ") && (" + right + ")";
        }
    }

    /**
     * A Filter that tests that at least one of the supplied filters accepts a
     * given element.
     */
    final static class AnyFilter<E> implements Filter<E>, CompositeFilter<E>,
            Iterable<Filter<E>>, Serializable {

        /** Default <code>serialVersionUID</code> */
        private static final long serialVersionUID = 3257282517878192437L;

        private final Filter<E>[] filters;

        /**
         * @param filters
         */
        @SuppressWarnings("unchecked")
        public AnyFilter(final Filter<? super E>[] filters) {
            this.filters = new Filter[filters.length];
            System.arraycopy(filters, 0, this.filters, 0, filters.length);
            for (int i = 0; i < this.filters.length; i++) {
                if (this.filters[i] == null) {
                    throw new NullPointerException("filters contained a null on index = "
                            + i);
                }
            }
        }

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        public boolean accept(E element) {
            for (Filter<E> filter : filters) {
                if (filter.accept(element)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns the filters we are testing against.
         * 
         * @return the filters we are testing against
         */
        public List<Filter<E>> getFilters() {
            return new ArrayList<Filter<E>>(Arrays.asList(filters));
        }

        /**
         * @see java.lang.Iterable#iterator()
         */
        public Iterator<Filter<E>> iterator() {
            return Arrays.asList(filters).iterator();
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            if (filters.length == 0) {
                return "";
            } else if (filters.length == 1) {
                return filters[0].toString();
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("((");
                builder.append(filters[0]);
                builder.append(")");
                for (int i = 1; i < filters.length; i++) {
                    builder.append(" or (");
                    builder.append(filters[i]);
                    builder.append(")");
                }
                builder.append(")");
                return builder.toString();
            }
        }
    }

    /**
     * A Filter that accepts all elements that are {@link Object#equals equal}
     * to the specified object.
     */
    final static class EqualsFilter<E> implements Filter<E>, Serializable {

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
         * Returns the object we are comparing with.
         * 
         * @return the object we are comparing with.
         */
        public E getObject() {
            return object;
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
     * A Filter that always returns <tt>false</tt>. Use {@link #INSTANCE} or
     * {@link org.coconut.filter.Filters#FALSE} to get an instance of this
     * Filter.
     * 
     * @see TrueFilter
     */
    final static class FalseFilter implements Filter, Serializable {

        /** The one and only instance. */
        static final FalseFilter INSTANCE = new FalseFilter();

        /** Default <code>serialVersionUID</code> */
        private static final long serialVersionUID = -3048464662394104180L;

        private FalseFilter() {/* Private Constructor */
        }

        /**
         * Returns <tt>false</tt> for all elements passed to this method.
         * 
         * @param element
         *            the element to test
         * @return <tt>false</tt> for all elements passed to this method
         */
        public boolean accept(Object element) {
            return false;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "false";
        }
    }

    /**
     * A greather-then filter as per Comparable/Comparator contract.
     */
    final static class GreaterThenFilter<E> implements Filter<E>, Serializable {

        /** <code>serialVersionUID</code> */
        private static final long serialVersionUID = -6815218477296552273L;

        private final Comparator comparator;

        /** The object to compare against */
        private final E object;

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

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "$x > " + object;
        }
    }

    /**
     * A Greather Then Or Equal filter as per Comparable/Comparator contract.
     */
    final static class GreaterThenOrEqualFilter<E> implements Filter<E>, Serializable {

        /** <code>serialVersionUID</code> */
        private static final long serialVersionUID = -6815218477296552273L;

        private final Comparator comparator;

        /** The object to compare against */
        private final E object;

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

        public <T extends E> GreaterThenOrEqualFilter(T object) {
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

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return " >= " + object;
        }
    }

    /**
     * A Less Then filter as per Comparable/Comparator contract.
     */
    final static class LessThenFilter<E> implements Filter<E>, Serializable {

        /** <code>serialVersionUID</code> */
        private static final long serialVersionUID = 1330339174193813467L;

        private final Comparator comparator;

        /** The object to compare against */
        private final E object;

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

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "$x < " + object;
        }
    }

    /**
     * A Less Then filter as per Comparable/Comparator contract.
     */
    final static class LessThenOrEqualFilter<E> implements Filter<E>, Serializable {

        /** <code>serialVersionUID</code> */
        private static final long serialVersionUID = 1330339174193813467L;

        private final Comparator comparator;

        /** The object to compare against */
        private final E object;

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
            } else if (comparator == null) {
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

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return " <= " + object;
        }
    }

    /**
     * A Filter that test that a supplied Filter does <tt>not</tt> accept a
     * given Element.
     */
    final static class NotFilter<E> implements Filter<E>, CompositeFilter<E>,
            Serializable {

        /** Default <code>serialVersionUID</code> */
        private static final long serialVersionUID = -5117781730584740429L;

        /** The Filter to negate */
        private final Filter<E> filter;

        /**
         * Creates a new Filter that negates the result of the supplied Filter.
         * 
         * @param filter
         *            the filter to negate.
         */
        public NotFilter(final Filter<E> filter) {
            if (filter == null) {
                throw new NullPointerException("filter is null");
            }
            this.filter = filter;
        }

        /**
         * Returns a boolean representing the logical NOT value of the supplied
         * Filter. If the specified Filters accept() method returns
         * <tt>true</tt>, this method returns <tt>false</tt>; if it is
         * <tt>false</tt>, this method returns <tt>true</tt>.
         * 
         * @param element
         *            the element to test
         * @return the logical NOT of the supplied Filter
         */
        public boolean accept(E element) {
            return !filter.accept(element);
        }

        /**
         * Returns the filter that is being negated.
         * 
         * @return the filter that is being negated.
         */
        public Filter<E> getFilter() {
            return filter;
        }

        /**
         * @see org.coconut.filter.spi.CompositeFilter#getFilters()
         */
        public List<Filter<E>> getFilters() {
            return Arrays.asList(filter);
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "!(" + filter + ")";
        }
    }

    /**
     * A Filter that performs a logical inclusive OR on two supplied filters.
     */
    final static class OrFilter<E> implements Filter<E>, CompositeFilter<E>, Serializable {

        /** Default <code>serialVersionUID</code> */
        private static final long serialVersionUID = 7602293335100183390L;

        /** The left side operand. */
        private final Filter<E> left;

        /** The right side operand. */
        private final Filter<E> right;

        /**
         * Constructs a new <code>AndFilter</code>.
         * 
         * @param left
         *            the left side operand
         * @param right
         *            the right side operand
         */
        public OrFilter(final Filter<E> left, final Filter<E> right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            }
            if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        public boolean accept(final E element) {
            return left.accept(element) || right.accept(element);
        }

        /**
         * @see org.coconut.filter.spi.CompositeFilter#getFilters()
         */
        @SuppressWarnings("unchecked")
        public List<Filter<E>> getFilters() {
            return Arrays.asList(left, right);
        }

        /**
         * Returns the left side operand.
         * 
         * @return the left side operand.
         */
        public Filter<E> getLeftFilter() {
            return left;
        }

        /**
         * Returns the right side operand.
         * 
         * @return the right side operand.
         */
        public Filter<E> getRightFilter() {
            return right;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "(" + left + ") or (" + right + ")";
        }
    }

    /**
     * A filter that accepts all elements that have the same object identity as
     * the one specified.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
     * @version $Id: Filters.java 36 2006-08-22 09:59:45Z kasper $
     */
    final static class SameFilter<E> implements Filter<E>, Serializable {

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
         * @see org.coconut.filter.Filter#accept(Object)
         */
        public boolean accept(E element) {
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

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "is (==) " + object;
        }
    }

    /**
     * A Filter that always returns <tt>true</tt>. Use {@link #INSTANCE} or
     * {@link org.coconut.filter.Filters#TRUE} to get an instance of this
     * Filter.
     * 
     * @see FalseFilter
     */
    final static class TrueFilter implements Filter, Serializable {

        /** The TrueFilter instance. */
        static final TrueFilter INSTANCE = new TrueFilter();

        /** Default <code>serialVersionUID</code> */
        private static final long serialVersionUID = 3258129137502925875L;

        private TrueFilter() {/* Private Constructor */
        }

        /**
         * Returns <tt>true</tt> for all elements passed to this method.
         * 
         * @param element
         *            the element to test
         * @return <tt>true</tt> for all elements passed to this method
         */
        public boolean accept(Object element) {
            return true;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "true";
        }
    }

    /**
     * A Filter that performs a logical exclusive OR (XOR) on two supplied
     * filters.
     */
    final static class XorFilter<E> implements CompositeFilter<E>, Filter<E>,
            Serializable {

        /** Default <code>serialVersionUID</code> */
        private static final long serialVersionUID = 1155267141991954303L;

        /** The left side operand. */
        private final Filter<E> left;

        /** The right side operand. */
        private final Filter<E> right;

        /**
         * Constructs a new <code>AndFilter</code>.
         * 
         * @param left
         *            the left side operand
         * @param right
         *            the right side operand
         */
        public XorFilter(final Filter<E> left, final Filter<E> right) {
            if (left == null) {
                throw new NullPointerException("left is null");
            } else if (right == null) {
                throw new NullPointerException("right is null");
            }
            this.left = left;
            this.right = right;
        }

        /**
         * @see org.coconut.filter.Filter#accept(Object)
         */
        public boolean accept(E element) {
            return left.accept(element) ^ right.accept(element);
        }

        /**
         * @see org.coconut.filter.spi.CompositeFilter#getFilters()
         */
        public List<Filter<E>> getFilters() {
            return Arrays.asList(left, right);
        }

        /**
         * Returns the left side operand.
         * 
         * @return the left side operand.
         */
        public Filter<E> getLeftFilter() {
            return left;
        }

        /**
         * Returns the right side operand.
         * 
         * @return the right side operand.
         */
        public Filter<E> getRightFilter() {
            return right;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "(" + left + ") xor (" + right + ")";
        }
    }

    /**
     * Returns a Filter that only accepts an element if <tt>all</tt> the
     * filters accept the element. The Filter will use a copy of the array of
     * supplied filters.
     * 
     * @param filters
     *            the filters to test
     * @return a Filter that tests all elements
     */
    public static <E> Filters.AllFilter<E> all(Filter<E>... filters) {
        return new Filters.AllFilter<E>(filters);
    }

    public static <E> Filters.AndFilter<E> and(Filter<E> left, Filter<E> right) {
        return new Filters.AndFilter<E>(left, right);
    }

    public static <E> Filters.AndFilter<E> and(Filter<E> left, Filter<E> right,
            boolean isStrict) {
        return new Filters.AndFilter<E>(left, right, isStrict);
    }

    public static <E> Filters.AnyFilter<E> any(Filter<E>... filters) {
        return new Filters.AnyFilter<E>(filters);
    }

    @SuppressWarnings( { "unchecked" })
    public static <E> AnyFilter<E> anyEquals(E... elements) {
        Filters.EqualsFilter[] filter = new Filters.EqualsFilter[elements.length];
        for (int i = 0; i < filter.length; i++) {
            filter[i] = Filters.equal(elements[i]);
        }
        return any((Filter[]) filter);
    }

    @SuppressWarnings("unchecked")
    public static Filters.AnyFilter<IsTypeFilter> anyType(Class... clazz) {
        IsTypeFilter[] cbf = new IsTypeFilter[clazz.length];
        for (int i = 0; i < cbf.length; i++) {
            cbf[i] = isType(clazz[i]);
        }
        return Filters.any((Filter[]) cbf);
    }

    public static <E> Filter<E> between(E first, E second) {
        return and(Filters.greatherThen(first), Filters.lessThen(second));
    }

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
    public static <E> Filters.EqualsFilter<E> equal(E object) {
        return new Filters.EqualsFilter<E>(object);
    }

    @SuppressWarnings("unchecked")
    public static <E> Filter<E> falseFilter() {
        return FALSE;
    }

    public static <E> Filters.GreaterThenFilter<E> greatherThen(E element) {
        return new Filters.GreaterThenFilter<E>(element);
    }

    public static <E> Filters.GreaterThenFilter<E> greatherThen(E object,
            final Comparator<? extends E> comparator) {
        return new Filters.GreaterThenFilter<E>(object, comparator);
    }

    public static <E> Filters.GreaterThenOrEqualFilter<E> greatherThenOrEqual(E object) {
        return new Filters.GreaterThenOrEqualFilter<E>(object);
    }

    public static <E> Filters.GreaterThenOrEqualFilter<E> greatherThenOrEqual(E object,
            final Comparator<? extends E> comparator) {
        return new Filters.GreaterThenOrEqualFilter<E>(object, comparator);
    }

    public static IsTypeFilter isType(Class clazz) {
        return new IsTypeFilter(clazz);
    }

    public static <E> Filters.LessThenFilter<E> lessThen(E element) {
        return new Filters.LessThenFilter<E>(element);
    }

    public static <E> Filters.LessThenFilter<E> lessThen(E object,
            final Comparator<? extends E> comparator) {
        return new Filters.LessThenFilter<E>(object, comparator);
    }

    public static <E> Filters.LessThenOrEqualFilter<E> lessThenOrEqual(E object) {
        return new Filters.LessThenOrEqualFilter<E>(object);
    }

    public static <E> Filters.LessThenOrEqualFilter<E> lessThenOrEqual(E object,
            final Comparator<? extends E> comparator) {
        return new Filters.LessThenOrEqualFilter<E>(object, comparator);
    }

    public static <E> Filters.NotFilter<E> not(Filter<E> filter) {
        return new Filters.NotFilter<E>(filter);
    }

    public static <E> Filters.OrFilter<E> or(Filter<E> left, Filter<E> right) {
        return new Filters.OrFilter<E>(left, right);
    }

    public static <E> Filters.SameFilter<E> same(E element) {
        return new Filters.SameFilter<E>(element);
    }

    @SuppressWarnings("unchecked")
    public static <E> Filter<E> trueFilter() {
        return TRUE;
    }

    /**
     * This method returns a Filter that performs xor on two other filters.
     * 
     * @param left
     *            the left hand side of the expression
     * @param right
     *            the right hand side of the expression
     * @return a Filter that performs xor on two other filters.
     */
    public static <E> Filters.XorFilter<E> xor(Filter<E> left, Filter<E> right) {
        return new Filters.XorFilter<E>(left, right);
    }

}
