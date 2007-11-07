/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.predicate;

import static org.coconut.core.Mappers.mapEntryToKey;
import static org.coconut.core.Mappers.mapEntryToValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.coconut.core.EventProcessor;
import org.coconut.core.Mapper;
import org.coconut.core.Mappers;

/**
 * Factory and utility methods for Filter.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: Filters.java 67 2006-09-28 08:07:48Z kasper $
 */
public final class CollectionPredicates {

    ///CLOVER:OFF
    /** Cannot instantiate. */
    private CollectionPredicates() {
    }
    ///CLOVER:ON
    
    public static <T> Predicate<T> isNull() {
        return new IsNullFilter();
    }

    public static <T> Predicate<T> notNullAnd(Predicate<T> f) {
        return new NotNullAndFilter<T>(f);
    }

    public static <E> Collection<E> filter(Collection<E> collection, Predicate<E> filter) {
        if (collection == null) {
            throw new NullPointerException("collection is null");
        } else if (filter == null) {
            throw new NullPointerException("filter is null");
        }
        List<E> list = new ArrayList<E>();
        for (E e : collection) {
            if (filter.evaluate(e)) {
                list.add(e);
            }
        }
        return list;
    }

    public static <E> void apply(Iterable<E> iterable, Predicate<E> filter,
            EventProcessor<E> handler) {
        // usefull??
        for (E e : iterable) {
            if (filter.evaluate(e)) {
                handler.process(e);
            }
        }
    }

    public static <K, V> Map<K, V> filterMap(Map<K, V> map, Predicate<Map.Entry<K, V>> filter) {
        Map<K, V> m = new HashMap<K, V>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (filter.evaluate(entry)) {
                m.put(entry.getKey(), entry.getValue());
            }
        }
        return m;
    }

    public static <K, V> Map<K, V> filterMapKeys(Map<K, V> map, Predicate<K> filter) {
        Map<K, V> m = new HashMap<K, V>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (filter.evaluate(entry.getKey())) {
                m.put(entry.getKey(), entry.getValue());
            }
        }
        return m;
    }

    public static <K, V> Map<K, V> filterMapValues(Map<K, V> map, Predicate<V> filter) {
        Map<K, V> m = new HashMap<K, V>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (filter.evaluate(entry.getValue())) {
                m.put(entry.getKey(), entry.getValue());
            }
        }
        return m;
    }

    public static <E> void removeFrom(Iterable<E> iterable, Predicate<E> filter) {
        for (Iterator<E> i = iterable.iterator(); i.hasNext();) {
            if (filter.evaluate(i.next())) {
                i.remove();
            }
        }
    }

    public static <E> List<E> filterList(List<E> list, Class<E> filter) {
        return filterList(list, Predicates.isType(filter));
    }

    public static <E> List<E> filterList(List<E> list, Predicate<E> filter) {
        return (List<E>) filter(list, filter);
    }

    public static <E> boolean acceptAny(Collection<E> collection, Predicate<E> filter) {
        for (E s : collection) {
            if (filter.evaluate(s)) {
                return true;
            }
        }
        return false;
    }

    public static <E> boolean acceptAll(Collection<E> collection, Predicate<E> filter) {
        for (E s : collection) {
            if (!filter.evaluate(s)) {
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
    final static class TransformerPredicate<F, T> implements Predicate<F>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -6292758840373110577L;

        /** The object to compare with. */
        private final Mapper<F, T> transformer;

        private final Predicate<T> filter;

        public TransformerPredicate(final Mapper<F, T> transformer, Predicate<T> filter) {
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
        public Predicate<T> getFilter() {
            return filter;
        }

        /**
         * Returns the transformer that will transform the object before
         * applying the filter on it.
         * 
         */
        public Mapper<F, T> getTransformer() {
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
        public boolean evaluate(F element) {
            return filter.evaluate(transformer.map(element));
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
        public boolean evaluate(Object element) {
            return theClass.isAssignableFrom(element.getClass());
        }
    }

    final static class IsNullFilter implements Predicate, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6280765768913457567L;

        public boolean evaluate(Object element) {
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

        public boolean evaluate(T element) {
            return element != null && filter.evaluate(element);
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "is not null and " + filter.toString();
        }

    }

    public static <F, T> Predicate<F> transformFilter(final Mapper<F, T> transformer,
            Predicate<T> filter) {
        return new TransformerPredicate<F, T>(transformer, filter);
    }

    public static <E> Predicate<E> transformFilter(Class<E> c, String method, Predicate<?> f) {
        Mapper<E, ?> t = Mappers.transform(c, method);
        return new TransformerPredicate(t, f);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Predicate<Map.Entry<K, V>> keyFilter(Predicate<K> filter) {
        return new TransformerPredicate(mapEntryToKey(), filter);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Predicate<Map.Entry<K, V>> valueFilter(Predicate<V> filter) {
        return new TransformerPredicate(mapEntryToValue(), filter);
    }

    /**
     * Returns a Filter that only accepts event regarding a particular key.
     * 
     * @param key
     *            the key that is accepted
     * @return a filter that only accepts event regarding a particular key.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Predicate<Map.Entry<K, V>> keyEqualsFilter(K key) {
        return (Predicate) keyFilter(Predicates.equal(key));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Predicate<Map.Entry<K, V>> valueEqualsFilter(V value) {
        return (Predicate) valueFilter(Predicates.equal(value));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Predicate<Map.Entry<K, V>> anyKeyEquals(final K... keys) {
        return (Predicate) keyFilter(Predicates.anyEquals(keys));
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Predicate<Map.Entry<K, V>> anyKeyInCollection(
            final Collection<? extends K> keys) {
        return (Predicate) keyFilter(Predicates.anyEquals(keys.toArray()));
    }

    /**
     * Creates a filter that accepts all cache events which is being mapped to
     * any of the specified values.
     * 
     * @param values
     *            the values that are accepted by the filter
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Predicate<Map.Entry<K, V>> anyValueEquals(final V... values) {
        return (Predicate) valueFilter(Predicates.anyEquals(values));
    }

    /**
     * Creates a filter that accepts all cache events which is being mapped to
     * any of the values contained in the specified Collection.
     * 
     * @param values
     *            the values that are accepted by the filter
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Predicate<Map.Entry<K, V>> anyValueInCollection(
            final Collection<? extends V> values) {
        // TODO what about null values in the collection?
        return (Predicate) valueFilter(Predicates.anyEquals(values.toArray()));
    }
}
