/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.jsr166yops;

import java.io.Serializable;

import jsr166y.forkjoin.Ops.Op;

/**
 * Various implementations of {@link Op}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class Mappers {
    public static final Op CONSTANT_MAPPER = new NoOpMapper();

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private Mappers() {}

    // /CLOVER:ON

    /**
     * Creates a composite mapper that applies a second mapper to the results of applying
     * the first one.
     */
    public static <T, U, V> Op<T, V> compoundMapper(Op<? super T, ? extends U> first,
            Op<? super U, ? extends V> second) {
        return new CompoundMapper<T, U, V>(first, second);
    }

    public static <T> Op<T, T> constant() {
        return CONSTANT_MAPPER;
    }

    /**
     * A composite mapper that applies a second mapper to the results of applying the
     * first one.
     */
    static final class CompoundMapper<T, U, V> implements Op<T, V>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 8856225241069810045L;

        private final Op<? super T, ? extends U> first;

        private final Op<? super U, ? extends V> second;

        CompoundMapper(Op<? super T, ? extends U> first, Op<? super U, ? extends V> second) {
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
        public V op(T t) {
            return second.op(first.op(t));
        }
    }

    /**
     * A Mappper that returns the same object being provided to the {@link #op(Object)}
     * method.
     *
     * @param <T>
     *            the type of objects accepted by the Mapper
     */
    static final class NoOpMapper<T> implements Op<T, T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8159540593935721003L;

        /** {@inheritDoc} */
        public T op(T element) {
            return element;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return CONSTANT_MAPPER;
        }
    }
}
