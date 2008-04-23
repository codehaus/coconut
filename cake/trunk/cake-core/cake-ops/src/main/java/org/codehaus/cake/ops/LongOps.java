/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://cake.codehaus.org/LICENSE
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.ops;

import java.io.Serializable;

import org.codehaus.cake.ops.Ops.LongComparator;
import org.codehaus.cake.ops.Ops.LongOp;
import org.codehaus.cake.ops.Ops.LongPredicate;
import org.codehaus.cake.ops.Ops.LongReducer;

/**
 * Various implementations of {@link LongPredicate}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: LongOps.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class LongOps {
    final static LongAbsOp ABS_OP = new LongAbsOp();

    final static LongAddReducer ADD_REDUCER = new LongAddReducer();

    /**
     * A comparator for longs relying on natural ordering. The comparator is Serializable.
     */
    public static final LongComparator COMPARATOR = new NaturalLongComparator();

    final static LongDivideReducer DIVIDE_REDUCER = new LongDivideReducer();
    /**
     * A reducer returning the maximum of two long elements, using natural comparator. The Reducer
     * is serializable.
     */
    static final LongReducer MAX_REDUCER = new NaturalLongMaxReducer();

    /**
     * A reducer returning the minimum of two long elements, using natural comparator. The Reducer
     * is serializable.
     */
    static final LongReducer MIN_REDUCER = new NaturalLongMinReducer();

    final static LongMultiplyReducer MULTIPLY_REDUCER = new LongMultiplyReducer();

    /**
     * A comparator that imposes the reverse of the <i>natural ordering</i> on longs. The
     * comparator is Serializable.
     */
    public static final LongComparator REVERSE_COMPARATOR = new NaturalLongReverseComparator();

    final static LongSubtractReducer SUBTRACT_REDUCER = new LongSubtractReducer();

    // /CLOVER:OFF
    /** Cannot instantiate. */
    private LongOps() {}

    // /CLOVER:ON

    public static LongOp abs() {
        return ABS_OP;
    }

    public static LongReducer add() {
        return ADD_REDUCER;
    }

    public static LongOp add(long add) {
        return new LongAddOp(add);
    }

    public static LongReducer divide() {
        return DIVIDE_REDUCER;
    }

    public static LongOp divide(long divide) {
        return new LongDivideOp(divide);
    }

    /**
     * A reducer returning the maximum of two long elements, using the specified comparator.
     * 
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created reducer
     */
    public static LongReducer max(LongComparator comparator) {
        return new LongMaxReducer(comparator);
    }

    /**
     * A reducer returning the minimum of two long elements, using the specified comparator.
     * 
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created reducer
     */
    public static LongReducer min(LongComparator comparator) {
        return new LongMinReducer(comparator);
    }

    public static LongReducer multiply() {
        return MULTIPLY_REDUCER;
    }

    public static LongOp multiply(long multiply) {
        return new LongMultiplyOp(multiply);
    }

    /**
     * Creates a comparator that imposes the reverse ordering of the specified comparator.
     * <p>
     * The returned comparator is serializable (assuming the specified comparator is also
     * serializable).
     * 
     * @param comparator
     *            the comparator to reverse
     * @return a comparator that imposes the reverse ordering of the specified comparator.
     */
    public static LongComparator reverseOrder(LongComparator comparator) {
        return new ReverseLongComparator(comparator);
    }

    public static LongReducer subtract() {
        return SUBTRACT_REDUCER;
    }

    public static LongOp subtract(long substract) {
        return new LongSubtractOp(substract);
    }

    static final class LongAbsOp implements LongOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8583260658972887816L;

        public long op(long a) {
            return Math.abs(a);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return ABS_OP;
        }
    }

    static final class LongAddOp implements LongOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -6604604690824553900L;

        private final long add;

        public LongAddOp(long add) {
            this.add = add;
        }

        public long op(long a) {
            return a + add;
        }
    }

    static final class LongAddReducer implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -830758681673022439L;

        public long op(long a, long b) {
            return a + b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return ADD_REDUCER;
        }
    }

    static final class LongDivideOp implements LongOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 661378303438906777L;

        private final long divide;

        public LongDivideOp(long divide) {
            this.divide = divide;
        }

        public long op(long a) {
            return a / divide;
        }
    }

    static final class LongDivideReducer implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -330758681673022439L;

        public long op(long a, long b) {
            return a / b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return DIVIDE_REDUCER;
        }
    }

    /**
     * A reducer returning the maximum of two long elements, using the given comparator.
     */
    static final class LongMaxReducer implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 2065097741025480432L;

        /** Comparator used when reducing. */
        private final LongComparator comparator;

        /**
         * Creates a LongMaxReducer.
         * 
         * @param comparator
         *            the comparator to use
         */
        LongMaxReducer(LongComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public long op(long a, long b) {
            return comparator.compare(a, b) >= 0 ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two long elements, using the given comparator.
     */
    static final class LongMinReducer implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6109941145562459503L;

        /** Comparator used when reducing. */
        private final LongComparator comparator;

        /**
         * Creates a LongMinReducer.
         * 
         * @param comparator
         *            the comparator to use
         */
        LongMinReducer(LongComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public long op(long a, long b) {
            return comparator.compare(a, b) <= 0 ? a : b;
        }
    }

    static final class LongMultiplyOp implements LongOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6099641660816235381L;

        private final long multiply;

        public LongMultiplyOp(long multiply) {
            this.multiply = multiply;
        }

        public long op(long a) {
            return a * multiply;
        }
    }

    static final class LongMultiplyReducer implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -130758681673022439L;

        public long op(long a, long b) {
            return a * b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MULTIPLY_REDUCER;
        }
    }

    static final class LongSubtractOp implements LongOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -23423423410L;

        private final long subtract;

        public LongSubtractOp(long subtract) {
            this.subtract = subtract;
        }

        public long op(long a) {
            return a - subtract;
        }
    }

    static final class LongSubtractReducer implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8583260658972887816L;

        public long op(long a, long b) {
            return a - b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return SUBTRACT_REDUCER;
        }
    }

    /** A comparator for longs relying on natural ordering. */
    static final class NaturalLongComparator implements LongComparator, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 8763765406476535022L;

        /** {@inheritDoc} */
        public int compare(long a, long b) {
            return a < b ? -1 : a > b ? 1 : 0;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return COMPARATOR;
        }
    }

    /** A reducer returning the maximum of two long elements, using natural comparator. */
    static final class NaturalLongMaxReducer implements LongReducer, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -5902864811727900806L;

        /** {@inheritDoc} */
        public long op(long a, long b) {
            return Math.max(a, b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MAX_REDUCER;
        }
    }

    /** A reducer returning the minimum of two long elements, using natural comparator. */
    static final class NaturalLongMinReducer implements LongReducer, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 9005140841348156699L;

        /** {@inheritDoc} */
        public long op(long a, long b) {
            return Math.min(a, b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MIN_REDUCER;
        }
    }

    /** A comparator for longs relying on natural ordering. */
    static final class NaturalLongReverseComparator implements LongComparator, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -7289505884757339069L;

        /** {@inheritDoc} */
        public int compare(long a, long b) {
            return a < b ? 1 : a > b ? -1 : 0;

        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return REVERSE_COMPARATOR;
        }
    }

    /** A comparator that reserves the result of another DoubleComparator. */
    static final class ReverseLongComparator implements LongComparator, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 1585665469031127321L;

        /** The comparator to reverse. */
        private final LongComparator comparator;

        /**
         * Creates a new ReverseLongComparator.
         * 
         * @param comparator
         *            the comparator to reverse
         */
        ReverseLongComparator(LongComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public int compare(long a, long b) {
            return -comparator.compare(a, b);
        }
    }
}
