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
import static org.codehaus.cake.ops.Ops.*;
import java.math.*;
/**
 * Various implementations of {@link IntPredicate}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: IntOps.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class IntOps {

     final static IntAddReducer ADD_REDUCER = new IntAddReducer();

     final static IntSubtractReducer SUBTRACT_REDUCER = new IntSubtractReducer();
    
     final static IntDivideReducer DIVIDE_REDUCER = new IntDivideReducer();
    
     final static IntMultiplyReducer MULTIPLY_REDUCER = new IntMultiplyReducer();

     final static IntAbsOp ABS_OP = new IntAbsOp();
    /**
     * A comparator for ints relying on natural ordering. The comparator is Serializable.
     */
    public static final IntComparator COMPARATOR = new NaturalIntComparator();

    /**
     * A comparator that imposes the reverse of the <i>natural ordering</i> on ints. The
     * comparator is Serializable.
     */
    public static final IntComparator REVERSE_COMPARATOR = new NaturalIntReverseComparator();

    /**
     * A reducer returning the maximum of two int elements, using natural comparator.
     * The Reducer is serializable.
     */
     static final IntReducer MAX_REDUCER = new NaturalIntMaxReducer();

    /**
     * A reducer returning the minimum of two int elements, using natural comparator.
     * The Reducer is serializable.
     */
     static final IntReducer MIN_REDUCER = new NaturalIntMinReducer();
    ///CLOVER:OFF
    /** Cannot instantiate. */
    private IntOps() {}
    ///CLOVER:ON
    public static IntOp abs() {
        return ABS_OP;
    }
    public static IntReducer add() {
        return ADD_REDUCER;
    }
    
    public static IntOp add(int add) {
        return new IntAddOp(add);
    }
    
    public static IntReducer divide() {
        return DIVIDE_REDUCER;
    }
    
    public static IntOp divide(int divide) {
        return new IntDivideOp(divide);
    }
    
    public static IntReducer multiply() {
        return MULTIPLY_REDUCER;
    }
    
    public static IntOp multiply(int multiply) {
        return new IntMultiplyOp(multiply);
    }
    
    public static IntReducer subtract() {
        return SUBTRACT_REDUCER;
    }
    
    public static IntOp subtract(int substract) {
        return new IntSubtractOp(substract);
    }
    /**
     * A reducer returning the maximum of two int elements, using the specified
     * comparator.
     *
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created reducer
     */
    public static IntReducer max(IntComparator comparator) {
        return new IntMaxReducer(comparator);
    }

    /**
     * A reducer returning the minimum of two int elements, using the specified
     * comparator.
     *
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created reducer
     */
    public static IntReducer min(IntComparator comparator) {
        return new IntMinReducer(comparator);
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
    public static IntComparator reverseOrder(IntComparator comparator) {
        return new ReverseIntComparator(comparator);
    }
    static final class IntSubtractReducer implements IntReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8583260658972887816L;

        public int op(int a, int b) {
            return a - b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return SUBTRACT_REDUCER;
        }
    }
    
    static final class IntAddReducer implements IntReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -830758681673022439L;

        public int op(int a, int b) {
            return a + b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return ADD_REDUCER;
        }
    }
    
    
    static final class IntMultiplyReducer implements IntReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -130758681673022439L;

        public int op(int a, int b) {
            return a * b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MULTIPLY_REDUCER;
        }
    }
    
    static final class IntDivideReducer implements IntReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -330758681673022439L;

        public int op(int a, int b) {
            return a / b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return DIVIDE_REDUCER;
        }
    }
    static final class IntAbsOp implements IntOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8583260658972887816L;

        public int op(int a) {
            return Math.abs(a);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return ABS_OP;
        }
    }
    /** A comparator for ints relying on natural ordering. */
    static final class NaturalIntComparator implements IntComparator, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 8763765406476535022L;

        /** {@inheritDoc} */
        public int compare(int a, int b) {
            return a < b ? -1 : a > b ? 1 : 0;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return COMPARATOR;
        }
    }

    /** A comparator for ints relying on natural ordering. */
    static final class NaturalIntReverseComparator implements IntComparator, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -7289505884757339069L;

        /** {@inheritDoc} */
        public int compare(int a, int b) {
            return a < b ? 1 : a > b ? -1 : 0;

        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return REVERSE_COMPARATOR;
        }
    }
    /**
     * A reducer returning the maximum of two int elements, using the given comparator.
     */
    static final class IntMaxReducer implements IntReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 2065097741025480432L;

        /** Comparator used when reducing. */
        private final IntComparator comparator;

        /**
         * Creates a IntMaxReducer.
         *
         * @param comparator
         *            the comparator to use
         */
        IntMaxReducer(IntComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public int op(int a, int b) {
            return comparator.compare(a, b) >= 0 ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two int elements, using the given comparator.
     */
    static final class IntMinReducer implements IntReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6109941145562459503L;

        /** Comparator used when reducing. */
        private final IntComparator comparator;

        /**
         * Creates a IntMinReducer.
         *
         * @param comparator
         *            the comparator to use
         */
        IntMinReducer(IntComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public int op(int a, int b) {
            return comparator.compare(a, b) <= 0 ? a : b;
        }
    }
    
        /** A reducer returning the maximum of two int elements, using natural comparator. */
    static final class NaturalIntMaxReducer implements IntReducer, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -5902864811727900806L;

        /** {@inheritDoc} */
        public int op(int a, int b) {
            return Math.max(a, b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MAX_REDUCER;
        }
    }

    /** A reducer returning the minimum of two int elements, using natural comparator. */
    static final class NaturalIntMinReducer implements IntReducer, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 9005140841348156699L;

        /** {@inheritDoc} */
        public int op(int a, int b) {
            return Math.min(a, b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MIN_REDUCER;
        }
    }
    
        /** A comparator that reserves the result of another DoubleComparator. */
    static final class ReverseIntComparator implements IntComparator, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 1585665469031127321L;

        /** The comparator to reverse. */
        private final IntComparator comparator;

        /**
         * Creates a new ReverseIntComparator.
         * 
         * @param comparator
         *            the comparator to reverse
         */
        ReverseIntComparator(IntComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public int compare(int a, int b) {
            return -comparator.compare(a, b);
        }
    }
    static final class IntAddOp implements IntOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -6604604690824553900L;

        private final int add;

        public IntAddOp(int add) {
            this.add = add;
        }

        public int op(int a) {
            return a + add;
        }
    }
    static final class IntSubtractOp implements IntOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -23423423410L;

        private final int subtract;

        public IntSubtractOp(int subtract) {
            this.subtract = subtract;
        }

        public int op(int a) {
            return a - subtract;
        }
    }
    static final class IntDivideOp implements IntOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 661378303438906777L;

        private final int divide;

        public IntDivideOp(int divide) {
            this.divide = divide;
        }

        public int op(int a) {
            return a / divide;
        }
    }

    static final class IntMultiplyOp implements IntOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6099641660816235381L;

        private final int multiply;

        public IntMultiplyOp(int multiply) {
            this.multiply = multiply;
        }

        public int op(int a) {
            return a * multiply;
        }
    }
}