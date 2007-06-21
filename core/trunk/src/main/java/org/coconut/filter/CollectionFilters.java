/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter;

import static org.coconut.core.Transformers.mapEntryToKey;
import static org.coconut.core.Transformers.mapEntryToValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.coconut.core.EventProcessor;
import org.coconut.core.Transformer;
import org.coconut.core.Transformers;

/**
 * Factory and utility methods for Filter.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: Filters.java 67 2006-09-28 08:07:48Z kasper $
 */
public final class CollectionFilters {

    ///CLOVER:OFF
    /** Cannot instantiate. */
    private CollectionFilters() {
    }
    ///CLOVER:ON
    
    public static <T> Filter<T> isNull() {
        return new IsNullFilter();
    }

    public static <T> Filter<T> notNullAnd(Filter<T> f) {
        return new NotNullAndFilter<T>(f);
    }

    public static <E> Collection<E> filter(Collection<E> collection, Filter<E> filter) {
        if (collection == null) {
            throw new NullPointerException("collection is null");
        } else if (filter == null) {
            throw new NullPointerException("filter is null");
        }
        List<E> list = new ArrayList<E>();
        for (E e : collection) {
            if (filter.accept(e)) {
                list.add(e);
            }
        }
        return list;
    }

    public static <E> void apply(Iterable<E> iterable, Filter<E> filter,
            EventProcessor<E> handler) {
        // usefull??
        for (E e : iterable) {
            if (filter.accept(e)) {
                handler.process(e);
            }
        }
    }

    public static <K, V> Map<K, V> filterMap(Map<K, V> map, Filter<Map.Entry<K, V>> filter) {
        Map<K, V> m = new HashMap<K, V>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (filter.accept(entry)) {
                m.put(entry.getKey(), entry.getValue());
            }
        }
        return m;
    }

    public static <K, V> Map<K, V> filterMapKeys(Map<K, V> map, Filter<K> filter) {
        Map<K, V> m = new HashMap<K, V>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (filter.accept(entry.getKey())) {
                m.put(entry.getKey(), entry.getValue());
            }
        }
        return m;
    }

    public static <K, V> Map<K, V> filterMapValues(Map<K, V> map, Filter<V> filter) {
        Map<K, V> m = new HashMap<K, V>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (filter.accept(entry.getValue())) {
                m.put(entry.getKey(), entry.getValue());
            }
        }
        return m;
    }

    public static <E> void removeFrom(Iterable<E> iterable, Filter<E> filter) {
        for (Iterator<E> i = iterable.iterator(); i.hasNext();) {
            if (filter.accept(i.next())) {
                i.remove();
            }
        }
    }

    public static <E> List<E> filterList(List<E> list, Class<E> filter) {
        return filterList(list, Filters.isType(filter));
    }

    public static <E> List<E> filterList(List<E> list, Filter<E> filter) {
        return (List<E>) filter(list, filter);
    }

    public static <E> boolean acceptAny(Collection<E> collection, Filter<E> filter) {
        for (E s : collection) {
            if (filter.accept(s)) {
                return true;
            }
        }
        return false;
    }

    public static <E> boolean acceptAll(Collection<E> collection, Filter<E> filter) {
        for (E s : collection) {
            if (!filter.accept(s)) {
                return false;
            }
        }
        return true;
    }

    // public TransformerFilter(final Transformer<F, Boolean> transformer) {
    // this

    // }

    /**
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
     * @version $Id: Filters.java 67 2006-09-28 08:07:48Z kasper $
     */
    final static class TransformerFilter<F, T> implements Filter<F>, Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -6292758840373110577L;

        /** The object to compare with. */
        private final Transformer<F, T> transformer;

        private final Filter<T> filter;

        public TransformerFilter(final Transformer<F, T> transformer, Filter<T> filter) {
            if (transformer == null) {
                throw new NullPointerException("transformer is null");
            } else if (filter == null) {
                throw new NullPointerException("filter is null");
            }
            this.filter = filter;
            this.transformer = transformer;
        }

        /**
         * Returns the Filter we are testing against.
         * 
         * @return the Filter we are testing against.
         */
        public Filter<T> getFilter() {
            return filter;
        }

        /**
         * Returns the transformer that will transform the object before
         * applying the filter on it.
         * 
         */
        public Transformer<F, T> getTransformer() {
            return transformer;
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
        public boolean accept(F element) {
            return filter.accept(transformer.transform(element));
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "convert " + transformer;
        }

    }

    /**
     * If this filter is specified with a class this Filter will match any
     * objects of the specific type or that is super class of the specified
     * class. If this Filter is specified with an interface it will match any
     * class that implements the interface.
     * 
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
     * @version $Id: Filters.java 67 2006-09-28 08:07:48Z kasper $
     */
    final static class IsTypeFilter implements Filter, Serializable {

        /** A Filter that accepts all classes. */
        public static final Filter<?> ALL = new IsTypeFilter(Object.class);

        /** A default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = 3256440304922996793L;

        /** The class we are testing against */
        private final Class<?> theClass;

        /**
         * Constructs a new ClassBasedFilter.
         * 
         * @param theClass
         *            the class we are testing against.
         * @throws NullPointerException
         *             if the class that was supplied is <code>null</code>.
         */
        public IsTypeFilter(final Class<?> theClass) {
            if (theClass == null) {
                throw new NullPointerException("theClass is null");
            }
            this.theClass = theClass;
        }

        /**
         * Returns the class we are testing against.
         * 
         * @return Returns the theClass.
         */
        public Class<?> getFilteredClass() {
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
        public boolean accept(Object element) {
            return theClass.isAssignableFrom(element.getClass());
        }
    }

    final static class IsNullFilter implements Filter, Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = 6280765768913457567L;

        public boolean accept(Object element) {
            return element == null;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "is null";
        }
    }

    final static class NotNullAndFilter<T> implements Filter<T>, Serializable {
        /** serialVersionUID */
        private static final long serialVersionUID = -324206595097699714L;

        /** the other filter */
        private final Filter<T> filter;

        public NotNullAndFilter(Filter<T> filter) {
            if (filter == null) {
                throw new NullPointerException("filter is null");
            }
            this.filter = filter;
        }

        public boolean accept(T element) {
            return element != null && filter.accept(element);
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "is not null and " + filter.toString();
        }

    }

    public static <F, T> Filter<F> transformFilter(final Transformer<F, T> transformer,
            Filter<T> filter) {
        return new TransformerFilter<F, T>(transformer, filter);
    }

    public static <E> Filter<E> transformFilter(Class<E> c, String method, Filter<?> f) {
        Transformer<E, ?> t = Transformers.transform(c, method);
        return new TransformerFilter(t, f);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<Map.Entry<K, V>> keyFilter(Filter<K> filter) {
        return new TransformerFilter(mapEntryToKey(), filter);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<Map.Entry<K, V>> valueFilter(Filter<V> filter) {
        return new TransformerFilter(mapEntryToValue(), filter);
    }

    /**
     * Returns a Filter that only accepts event regarding a particular key.
     * 
     * @param key
     *            the key that is accepted
     * @return a filter that only accepts event regarding a particular key.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Filter<Map.Entry<K, V>> keyEqualsFilter(K key) {
        return (Filter) keyFilter(Filters.equal(key));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<Map.Entry<K, V>> valueEqualsFilter(V value) {
        return (Filter) valueFilter(Filters.equal(value));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<Map.Entry<K, V>> anyKeyEquals(final K... keys) {
        return (Filter) keyFilter(Filters.anyEquals(keys));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Filter<Map.Entry<K, V>> anyKeyInCollection(
            final Collection<? extends K> keys) {
        return (Filter) keyFilter(Filters.anyEquals(keys.toArray()));
    }

    /**
     * Creates a filter that accepts all cache events which is being mapped to
     * any of the specified values.
     * 
     * @param values
     *            the values that are accepted by the filter
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Filter<Map.Entry<K, V>> anyValueEquals(final V... values) {
        return (Filter) valueFilter(Filters.anyEquals(values));
    }

    /**
     * Creates a filter that accepts all cache events which is being mapped to
     * any of the values contained in the specified Collection.
     * 
     * @param values
     *            the values that are accepted by the filter
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Filter<Map.Entry<K, V>> anyValueInCollection(
            final Collection<? extends V> values) {
        // TODO what about null values in the collection?
        return (Filter) valueFilter(Filters.anyEquals(values.toArray()));
    }
}
