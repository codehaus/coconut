/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.forkjoin;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import jsr166y.forkjoin.Ops.Mapper;


/**
 * Various implementations of {@link Mapper}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class Mappers {
    public static final Mapper MAP_ENTRY_TO_KEY_MAPPER = new KeyFromMapEntry();

    public static final Mapper CONSTANT_MAPPER = new NoOpMapper();

    public static final Mapper MAP_ENTRY_TO_VALUE_MAPPER = new ValueFromMapEntry();

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Mappers() {}

    // /CLOVER:ON

    /**
     * Creates a composite mapper that applies a second mapper to the results of applying
     * the first one.
     */
    public static <T, U, V> Mapper<T, V> compoundMapper(Mapper<? super T, ? extends U> first,
            Mapper<? super U, ? extends V> second) {
        return new CompoundMapper<T, U, V>(first, second);
    }

    public static <K, V> Mapper<Map.Entry<K, V>, K> mapEntryToKey() {
        return MAP_ENTRY_TO_KEY_MAPPER;
    }

    public static <T> Mapper<T, T> constant() {
        return CONSTANT_MAPPER;
    }

    public static <K, V> Mapper<Map.Entry<K, V>, V> mapEntryToValue() {
        return MAP_ENTRY_TO_VALUE_MAPPER;
    }

    /**
     * A composite mapper that applies a second mapper to the results of applying the
     * first one.
     */
    static final class CompoundMapper<T, U, V> implements Mapper<T, V>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 8856225241069810045L;

        private final Mapper<? super T, ? extends U> first;

        private final Mapper<? super U, ? extends V> second;

        CompoundMapper(Mapper<? super T, ? extends U> first, Mapper<? super U, ? extends V> second) {
            if (first == null) {
                throw new NullPointerException("first is null");
            } else if (second == null) {
                throw new NullPointerException("second is null");
            }
            this.first = first;
            this.second = second;
        }

        /** {@inheritDoc} */
        // Returns <tt>second.map(first.map(t))</tt>
        public V map(T t) {
            return second.map(first.map(t));
        }
    }

    static class KeyFromMapEntry<K, V> implements Mapper<Map.Entry<K, V>, K>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6825556225078171244L;

        public K map(Entry<K, V> t) {
            return t.getKey();
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MAP_ENTRY_TO_KEY_MAPPER;
        }
    }

    /**
     * A Mappper that returns the same object being provided to the {@link #map(Object)}
     * method.
     *
     * @param <T>
     *            the type of objects accepted by the Mapper
     */
    final static class NoOpMapper<T> implements Mapper<T, T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8159540593935721003L;

        /** {@inheritDoc} */
        public T map(T element) {
            return element;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return CONSTANT_MAPPER;
        }
    }

    static class ValueFromMapEntry<K, V> implements Mapper<Map.Entry<K, V>, V>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -1080832065709931446L;

        public V map(Entry<K, V> t) {
            return t.getValue();
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MAP_ENTRY_TO_VALUE_MAPPER;
        }

    }
}
