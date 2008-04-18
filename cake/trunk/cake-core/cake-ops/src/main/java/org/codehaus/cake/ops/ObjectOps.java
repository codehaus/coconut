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

import org.codehaus.cake.ops.Ops.Op;

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

    /** Cannot instantiate. */
    private ObjectOps() {}

    /**
     * Creates a composite mapper that applies a second mapper to the results of applying
     * the first one.
     */
    public static <T, U, V> Op<T, V> compoundMapper(Op<? super T, ? extends U> first,
            Op<? super U, ? extends V> second) {
        return new CompoundMapper<T, U, V>(first, second);
    }

    public static <T> Op<T, T> constant() {
        return CONSTANT_OP;
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
        public V op(T t) {
            return second.op(first.op(t));
        }
    }

    /**
     * An Op that returns the same object being provided to the {@link #op(Object)}
     * method.
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
}
