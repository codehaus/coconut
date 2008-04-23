/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.ops;

import java.io.Serializable;
import java.util.Comparator;

import org.codehaus.cake.ops.Ops.Op;
import org.codehaus.cake.ops.Ops.Reducer;

/**
 * Various implementations of {@link Op}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ObjectOps.java 600 2008-04-08 10:01:32Z kasper $
 */
public final class ObjectOps {
    /** An Op that returns the specified argument. */
    public static final Op CONSTANT_OP = new ConstantOp();

    /**
     * A reducer returning the maximum of two Comparable elements, treating null as less than any
     * non-null element. The Reducer is serializable
     */
    public static final Reducer MAX_REDUCER = new NaturalMaxReducer();

    /**
     * A reducer returning the minimum of two Comparable elements, treating null as less than any
     * non-null element. The Reducer is serializable
     */
    public static final Reducer MIN_REDUCER = new NaturalMinReducer();

    /** Cannot instantiate. */
    private ObjectOps() {}

    /**
     * Creates a composite mapper that applies a second mapper to the results of applying the first
     * one.
     */
    public static <T, U, V> Op<T, V> compoundMapper(Op<? super T, ? extends U> first,
            Op<? super U, ? extends V> second) {
        return new CompoundMapper<T, U, V>(first, second);
    }

    public static <T> Op<T, T> constant() {
        return CONSTANT_OP;
    }

    /**
     * Returns a reducer returning the maximum of two Comparable elements, treating null as less
     * than any non-null element.
     * 
     * @return a maximum reducer
     * @param <T>
     *            the types of elements accepted by the Reducer
     */
    public static <T extends Comparable<? super T>> Reducer<T> maxReducer() {
        return MAX_REDUCER;
    }

    /**
     * A reducer returning the maximum of two elements, using the given comparator, and treating
     * null as less than any non-null element.
     * 
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created Reducer
     * @param <T>
     *            the types of elements accepted by the Reducer
     */
    public static <T> Reducer<T> maxReducer(Comparator<? super T> comparator) {
        return new MaxReducer(comparator);
    }

    /**
     * Returns a reducer that returns the minimum of two Comparable elements, treating null as less
     * than any non-null element.
     * 
     * @return a minimum reducer
     * @param <T>
     *            the types of elements accepted by the Reducer
     */
    public static <T extends Comparable<? super T>> Reducer<T> minReducer() {
        return MIN_REDUCER;
    }

    /**
     * A reducer returning the minimum of two elements, using the given comparator, and treating
     * null as greater than any non-null element.
     * 
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created Reducer
     * @param <T>
     *            the types of elements accepted by the Reducer
     */
    public static <T> Reducer<T> minReducer(Comparator<? super T> comparator) {
        return new MinReducer(comparator);
    }

    /**
     * A composite mapper that applies a second mapper to the results of applying the first one.
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
        public V op(T t) {
            return second.op(first.op(t));
        }
    }

    /**
     * An Op that returns the same object being provided to the {@link #op(Object)} method.
     * 
     * @param <T>
     *            the type of objects accepted by the Mapper
     */
    static final class ConstantOp<T> implements Op<T, T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8159540593935721003L;

        /** {@inheritDoc} */
        public T op(T element) {
            return element;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return CONSTANT_OP;
        }
    }

    /**
     * A reducer returning the maximum of two elements, using the given comparator, and treating
     * null as less than any non-null element.
     */
    static final class MaxReducer<T> implements Reducer<T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -5456282180421493663L;

        /** Comparator used when reducing. */
        private final Comparator<? super T> comparator;

        /**
         * Creates a MaxReducer.
         * 
         * @param comparator
         *            the Comparator to use
         */
        MaxReducer(Comparator<? super T> comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public T op(T a, T b) {
            return a != null && (b == null || comparator.compare(a, b) >= 0) ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two elements, using the given comparator, and treating
     * null as greater than any non-null element.
     */
    static final class MinReducer<T> implements Reducer<T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 223676769245813970L;

        /** Comparator used when reducing. */
        private final Comparator<? super T> comparator;

        /**
         * Creates a MinReducer.
         * 
         * @param comparator
         *            the Comparator to use
         */
        MinReducer(Comparator<? super T> comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public T op(T a, T b) {
            return a != null && (b == null || comparator.compare(a, b) <= 0) ? a : b;
        }
    }

    /**
     * A reducer returning the maximum of two Comparable elements, treating null as less than any
     * non-null element.
     */
    static final class NaturalMaxReducer<T extends Comparable<? super T>> implements Reducer<T>,
            Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 5079675958818175983L;

        /** {@inheritDoc} */
        public T op(T a, T b) {
            return a != null && (b == null || a.compareTo(b) >= 0) ? a : b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MAX_REDUCER;
        }
    }

    /**
     * A reducer returning the minimum of two Comparable elements, treating null as less than any
     * non-null element.
     */
    static final class NaturalMinReducer<T extends Comparable<? super T>> implements Reducer<T>,
            Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -6750364835779757657L;

        /** {@inheritDoc} */
        public T op(T a, T b) {
            return a != null && (b == null || a.compareTo(b) <= 0) ? a : b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MIN_REDUCER;
        }
    }
}
