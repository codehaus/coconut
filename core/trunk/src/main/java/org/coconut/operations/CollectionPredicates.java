/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.coconut.operations.Ops.Predicate;

/**
 * Various implementations of {@link Predicate} that operates on {@link Collection},
 * {@link Iterable} and {@link Map}.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: CollectionPredicates.java 498 2007-12-02 17:17:11Z kasper $
 */
public final class CollectionPredicates {

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private CollectionPredicates() {}

    // /CLOVER:ON

    /**
     * Filters the specified iterable, returning a new list of those items that evaluated
     * to true given the specified predicate.
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

    /**
     * Filters the specified map, returning a new map of those items that evaluated to
     * true given the specified predicate.
     *
     * @param <K>
     *            the type of keys in the map
     * @param <V>
     *            the type of values in the map
     * @param map
     *            the map to filter
     * @param predicate
     *            the predicate to evaluate items accordingly to
     * @return a collection of filteres items
     */
    public static <K, V> Map<K, V> filterMap(Map<K, V> map,
            Predicate<? super Map.Entry<K, V>> predicate) {
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

    /**
     * Returns whether or not <b>all</b> of elements in the specified can be accepted by
     * the specified predicate.
     *
     * @param <E>
     *            the types accepted
     * @param iterable
     *            the iterable to check
     * @param predicate
     *            the predicate to test against
     * @return whether or not all of elements in the specified can be accepted by the
     *         specified predicate
     */
    public static <E> boolean isAllTrue(Iterable<E> iterable, Predicate<? super E> predicate) {
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
    public static <E> boolean isAnyTrue(Iterable<E> iterable, Predicate<? super E> predicate) {
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

    public static <E> void retain(Iterable<E> iterable, Predicate<? super E> predicate) {
        if (iterable == null) {
            throw new NullPointerException("iterable is null");
        } else if (predicate == null) {
            throw new NullPointerException("predicate is null");
        }
        for (Iterator<E> i = iterable.iterator(); i.hasNext();) {
            if (!predicate.evaluate(i.next())) {
                i.remove();
            }
        }
    }
    
    public static Predicate containedWithin(Collection col) {
        return new ContainsPredicate(col);
    }
    /**
     * A Predicate that evaluates to <code>true</code> iff the element being evaluated
     * is {@link Object#equals equal} to the element being specified.
     */
    final static class ContainsPredicate<E> implements Predicate<E>, Serializable {

        /** Default <code>serialVersionUID</code>. */
        private static final long serialVersionUID = -802615306772905787L;

        /** The element to compare with. */
        private final Collection<E> collection;

        /**
         * Creates an IsEqualsPredicate.
         *
         * @param element
         *            the element to use for comparison
         * @throws NullPointerException
         *             if the specified element is <code>null</code>
         */
        public ContainsPredicate(Collection<E> collection) {
            if (collection == null) {
                throw new NullPointerException("collection is null");
            }
            this.collection = collection;
        }

        /** {@inheritDoc} */
        public boolean evaluate(E element) {
            return collection.contains(element);
        }

        /**
         * Returns the element we are comparing with.
         *
         * @return the element we are comparing with
         */
        public Collection getElement() {
            return collection;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "isContained In " + collection;
        }
    }

}
