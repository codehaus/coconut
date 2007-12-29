/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.forkjoin;

import java.io.Serializable;

import jsr166y.forkjoin.Ops.Mapper;

/**
 * Various implementations of {@link Mapper}.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class Mappers {
    static final Mapper NOOP_MAPPER = new NoOpMapper();

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

    public static <T> Mapper<T, T> noOpMapper() {
        return NOOP_MAPPER;
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
            return NOOP_MAPPER;
        }
    }
}
