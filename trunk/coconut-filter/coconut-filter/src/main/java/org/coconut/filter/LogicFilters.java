/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.coconut.filter.spi.CompositeFilter;

/**
 * This package contains common logic Filters used in Coconut.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: LogicFilters.java 36 2006-08-22 09:59:45Z kasper $
 */
@SuppressWarnings("unchecked")
public class LogicFilters {

    /** A filter that always return False. */
    public static final FalseFilter FALSE = FalseFilter.INSTANCE;

    /** A filter that always return True. */
    public static final TrueFilter TRUE = TrueFilter.INSTANCE;

    @SuppressWarnings("unchecked")
    public static <E> Filter<E> falseFilter() {
        return FALSE;
    }

    @SuppressWarnings("unchecked")
    public static <E> Filter<E> trueFilter() {
        return TRUE;
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
    public static <E> AllFilter<E> all(Filter<E>... filters) {
        return new AllFilter<E>(filters);
    }

    public static <E> AndFilter<E> and(Filter<E> left, Filter<E> right) {
        return new AndFilter<E>(left, right);
    }

    public static <E> AndFilter<E> and(Filter<E> left, Filter<E> right, boolean isStrict) {
        return new AndFilter<E>(left, right, isStrict);
    }

    public static <E> AnyFilter<E> any(Filter<E>... filters) {
        return new AnyFilter<E>(filters);
    }

    public static <E> NotFilter<E> not(Filter<E> filter) {
        return new NotFilter<E>(filter);
    }

    public static <E> OrFilter<E> or(Filter<E> left, Filter<E> right) {
        return new OrFilter<E>(left, right);
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
    public static <E> XorFilter<E> xor(Filter<E> left, Filter<E> right) {
        return new XorFilter<E>(left, right);
    }

    /**
     * A Filter that tests that <tt>all</tt> of the supplied Filters accepts a
     * given element.
     */
    public final static class AllFilter<E> implements Filter<E>, CompositeFilter<E>,
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
         * Returns the filters we are testing against.
         * 
         * @return the filters we are testing against
         */
        public List<Filter<E>> getFilters() {
            return new ArrayList<Filter<E>>(Arrays.asList(filters));
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
    public final static class AndFilter<E> implements Filter<E>, CompositeFilter<E>,
            Serializable {

        /** Default <code>serialVersionUID</code> */
        private static final long serialVersionUID = 6981902451700512606L;

        /** The left side operand. */
        private final Filter<E> left;

        /** The right side operand. */
        private final Filter<E> right;

        private final boolean isStrict;

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
         * Returns the left side operand.
         * 
         * @return the left side operand.
         */
        public Filter<E> getLeftFilter() {
            return left;
        }

        /**
         * Returns whether the operands must be evaluated left and then right
         * (strict) or if each of them can be evaluated indenpendently.
         * 
         * @return
         */
        public boolean isStrict() {
            return isStrict;
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
         * @see org.coconut.filter.CompositeFilter#getFilters()
         */
        @SuppressWarnings("unchecked")
        public List<Filter<E>> getFilters() {
            return Arrays.asList(left, right);
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
    public final static class AnyFilter<E> implements Filter<E>, CompositeFilter<E>,
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
         * Returns the filters we are testing against.
         * 
         * @return the filters we are testing against
         */
        public List<Filter<E>> getFilters() {
            return new ArrayList<Filter<E>>(Arrays.asList(filters));
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
     * A Filter that always returns <tt>false</tt>. Use {@link #INSTANCE} or
     * {@link org.coconut.filter.Filters#FALSE} to get an instance of this
     * Filter.
     * 
     * @see TrueFilter
     */
    public final static class FalseFilter implements Filter, Serializable {

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
     * A Filter that test that a supplied Filter does <tt>not</tt> accept a
     * given Element.
     */
    public final static class NotFilter<E> implements Filter<E>, CompositeFilter<E>,
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
         * @see org.coconut.filter.CompositeFilter#getFilters()
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
    public final static class OrFilter<E> implements Filter<E>, CompositeFilter<E>,
            Serializable {

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
         * @see org.coconut.filter.CompositeFilter#getFilters()
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
     * A Filter that always returns <tt>true</tt>. Use {@link #INSTANCE} or
     * {@link org.coconut.filter.Filters#TRUE} to get an instance of this
     * Filter.
     * 
     * @see FalseFilter
     */
    public final static class TrueFilter implements Filter, Serializable {

        /** The TrueFilter instance. */
        static final TrueFilter INSTANCE = new TrueFilter();

        /** Default <code>serialVersionUID</code> */
        private static final long serialVersionUID = 3258129137502925875L;

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

        private TrueFilter() {/* Private Constructor */
        }
    }

    /**
     * A Filter that performs a logical exclusive OR (XOR) on two supplied
     * filters.
     */
    public final static class XorFilter<E> implements CompositeFilter<E>, Filter<E>,
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
         * @see org.coconut.filter.CompositeFilter#getFilters()
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
}
