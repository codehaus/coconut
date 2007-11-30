/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.coconut.core.Mapper;

/**
 * Factory and utility methods for {@link Predicate}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public final class CollectionPredicates {

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private CollectionPredicates() {}

    // /CLOVER:ON

    /**
     * Returns whether or not <b>all</b> of elements in the specified can be accepted by
     * the specified predicate.
     * 
     * @param <E>
     *            the types accepted
     * @param iterable
     *            the collection to check
     * @param predicate
     *            the predicate to test against
     * @return whether or not all of elements in the specified can be accepted by the
     *         specified predicate
     */
    public static <E> boolean allTrue(Iterable<E> iterable, Predicate<? super E> predicate) {
        if (iterable == null) {
            throw new NullPointerException("iterable is null");
        } else if (predicate == null) {
            throw new NullPointerException("predicate is null");
        }
        for (E s : iterable) {
            if (!predicate.evaluate(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether or not <b>any</b> of elements in the specified can be accepted by
     * the specified predicate.
     * 
     * @param <E>
     *            the types accepted
     * @param iterable
     *            the iterable to check
     * @param predicate
     *            the predicate to test against
     * @return whether or not any of elements in the specified can be accepted by the
     *         specified predicate
     */
    public static <E> boolean anyTrue(Iterable<E> iterable, Predicate<? super E> predicate) {
        if (iterable == null) {
            throw new NullPointerException("iterable is null");
        } else if (predicate == null) {
            throw new NullPointerException("predicate is null");
        }
        for (E s : iterable) {
            if (predicate.evaluate(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Filters the specified iterable, returning a list of those items that evaluated to
     * true given the specified predicate.
     * 
     * @param <E>
     *            the types of items that are filtered
     * @param iterable
     *            the iterable to filter
     * @param predicate
     *            the predicate to evaluate items accordingly to
     * @return a collection of filteres items
     */
    public static <E> List<E> filter(Iterable<E> iterable, Predicate<? super E> predicate) {
        if (iterable == null) {
            throw new NullPointerException("iterable is null");
        } else if (predicate == null) {
            throw new NullPointerException("predicate is null");
        }
        List<E> list = new ArrayList<E>();
        for (E e : iterable) {
            if (predicate.evaluate(e)) {
                list.add(e);
            }
        }
        return list;
    }

    public static <E> void removeFrom(Iterable<E> iterable, Predicate<? super E> predicate) {
        if (iterable == null) {
            throw new NullPointerException("iterable is null");
        } else if (predicate == null) {
            throw new NullPointerException("predicate is null");
        }
        for (Iterator<E> i = iterable.iterator(); i.hasNext();) {
            if (predicate.evaluate(i.next())) {
                i.remove();
            }
        }
    }

    public static <K, V> Map<K, V> filterMap(Map<K, V> map, Predicate<Map.Entry<K, V>> predicate) {
        if (map == null) {
            throw new NullPointerException("map is null");
        } else if (predicate == null) {
            throw new NullPointerException("predicate is null");
        }
        Map<K, V> m = new HashMap<K, V>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (predicate.evaluate(entry)) {
                m.put(entry.getKey(), entry.getValue());
            }
        }
        return m;
    }

    public static <K, V> Map<K, V> filterMapKeys(Map<K, V> map, Predicate<? super K> predicate) {
        if (map == null) {
            throw new NullPointerException("map is null");
        } else if (predicate == null) {
            throw new NullPointerException("predicate is null");
        }
        Map<K, V> m = new HashMap<K, V>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (predicate.evaluate(entry.getKey())) {
                m.put(entry.getKey(), entry.getValue());
            }
        }
        return m;
    }

    public static <K, V> Map<K, V> filterMapValues(Map<K, V> map, Predicate<? super V> predicate) {
        if (map == null) {
            throw new NullPointerException("map is null");
        } else if (predicate == null) {
            throw new NullPointerException("predicate is null");
        }
        Map<K, V> m = new HashMap<K, V>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (predicate.evaluate(entry.getValue())) {
                m.put(entry.getKey(), entry.getValue());
            }
        }
        return m;
    }

    public static <F, T> Predicate<F> mapperPredicate(final Mapper<F, T> mapper,
            Predicate<? super T> predicate) {
        return new MapperPredicate<F, T>(mapper, predicate);
    }

    /**
     * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
     * @version $Id$
     */
    final static class MapperPredicate<F, T> implements Predicate<F>, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -6292758840373110577L;

        private final Predicate<? super T> predicate;

        /** The object to compare with. */
        private final Mapper<F, T> mapper;

        public MapperPredicate(final Mapper<F, T> mapper, Predicate<? super T> predicate) {
            if (mapper == null) {
                throw new NullPointerException("mapper is null");
            } else if (predicate == null) {
                throw new NullPointerException("predicate is null");
            }
            this.predicate = predicate;
            this.mapper = mapper;
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
        public boolean evaluate(F element) {
            return predicate.evaluate(mapper.map(element));
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
         * Returns the mappter that will map the object before applying the predicate on
         * it.
         */
        public Mapper<F, T> getMapper() {
            return mapper;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "convert " + mapper;
        }
    }
}
