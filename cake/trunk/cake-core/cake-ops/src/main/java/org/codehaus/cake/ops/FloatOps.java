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
 * Various implementations of {@link FloatPredicate}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: FloatOps.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class FloatOps {

     final static FloatAddReducer ADD_REDUCER = new FloatAddReducer();

     final static FloatSubtractReducer SUBTRACT_REDUCER = new FloatSubtractReducer();
    
     final static FloatDivideReducer DIVIDE_REDUCER = new FloatDivideReducer();
    
     final static FloatMultiplyReducer MULTIPLY_REDUCER = new FloatMultiplyReducer();

     final static FloatAbsOp ABS_OP = new FloatAbsOp();
    /**
     * A reducer returning the maximum of two float elements, using natural comparator.
     * The Reducer is serializable.
     */
     static final FloatReducer MAX_REDUCER = new NaturalFloatMaxReducer();

    /**
     * A reducer returning the minimum of two float elements, using natural comparator.
     * The Reducer is serializable.
     */
     static final FloatReducer MIN_REDUCER = new NaturalFloatMinReducer();
    ///CLOVER:OFF
    /** Cannot instantiate. */
    private FloatOps() {}
    ///CLOVER:ON
    public static FloatOp abs() {
        return ABS_OP;
    }
    public static FloatReducer add() {
        return ADD_REDUCER;
    }
    
    public static FloatOp add(float add) {
        return new FloatAddOp(add);
    }
    
    public static FloatReducer divide() {
        return DIVIDE_REDUCER;
    }
    
    public static FloatOp divide(float divide) {
        return new FloatDivideOp(divide);
    }
    
    public static FloatReducer multiply() {
        return MULTIPLY_REDUCER;
    }
    
    public static FloatOp multiply(float multiply) {
        return new FloatMultiplyOp(multiply);
    }
    
    public static FloatReducer subtract() {
        return SUBTRACT_REDUCER;
    }
    
    public static FloatOp subtract(float substract) {
        return new FloatSubtractOp(substract);
    }
    /**
     * A reducer returning the maximum of two float elements, using the specified
     * comparator.
     *
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created reducer
     */
    public static FloatReducer maxReducer(FloatComparator comparator) {
        return new FloatMaxReducer(comparator);
    }

    /**
     * A reducer returning the minimum of two float elements, using the specified
     * comparator.
     *
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created reducer
     */
    public static FloatReducer minReducer(FloatComparator comparator) {
        return new FloatMinReducer(comparator);
    }
    static final class FloatSubtractReducer implements FloatReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8583260658972887816L;

        public float op(float a, float b) {
            return a - b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return SUBTRACT_REDUCER;
        }
    }
    
    static final class FloatAddReducer implements FloatReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -830758681673022439L;

        public float op(float a, float b) {
            return a + b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return ADD_REDUCER;
        }
    }
    
    
    static final class FloatMultiplyReducer implements FloatReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -130758681673022439L;

        public float op(float a, float b) {
            return a / b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MULTIPLY_REDUCER;
        }
    }
    
    static final class FloatDivideReducer implements FloatReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -330758681673022439L;

        public float op(float a, float b) {
            return a * b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return DIVIDE_REDUCER;
        }
    }
    static final class FloatAbsOp implements FloatOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8583260658972887816L;

        public float op(float a) {
            return Math.abs(a);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return ABS_OP;
        }
    }
    /**
     * A reducer returning the maximum of two float elements, using the given comparator.
     */
    static final class FloatMaxReducer implements FloatReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 2065097741025480432L;

        /** Comparator used when reducing. */
        private final FloatComparator comparator;

        /**
         * Creates a FloatMaxReducer.
         *
         * @param comparator
         *            the comparator to use
         */
        FloatMaxReducer(FloatComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public float op(float a, float b) {
            return comparator.compare(a, b) >= 0 ? a : b;
        }
    }

    /**
     * A reducer returning the minimum of two float elements, using the given comparator.
     */
    static final class FloatMinReducer implements FloatReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6109941145562459503L;

        /** Comparator used when reducing. */
        private final FloatComparator comparator;

        /**
         * Creates a FloatMinReducer.
         *
         * @param comparator
         *            the comparator to use
         */
        FloatMinReducer(FloatComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public float op(float a, float b) {
            return comparator.compare(a, b) <= 0 ? a : b;
        }
    }
    
        /** A reducer returning the maximum of two float elements, using natural comparator. */
    static final class NaturalFloatMaxReducer implements FloatReducer, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = -5902864811727900806L;

        /** {@inheritDoc} */
        public float op(float a, float b) {
            return Math.max(a, b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MAX_REDUCER;
        }
    }

    /** A reducer returning the minimum of two float elements, using natural comparator. */
    static final class NaturalFloatMinReducer implements FloatReducer, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 9005140841348156699L;

        /** {@inheritDoc} */
        public float op(float a, float b) {
            return Math.min(a, b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MIN_REDUCER;
        }
    }
    
    static final class FloatAddOp implements FloatOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -6604604690824553900L;

        private final float add;

        public FloatAddOp(float add) {
            this.add = add;
        }

        public float op(float a) {
            return a + add;
        }
    }
    static final class FloatSubtractOp implements FloatOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -23423423410L;

        private final float subtract;

        public FloatSubtractOp(float subtract) {
            this.subtract = subtract;
        }

        public float op(float a) {
            return a - subtract;
        }
    }
    static final class FloatDivideOp implements FloatOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 661378303438906777L;

        private final float divide;

        public FloatDivideOp(float divide) {
            this.divide = divide;
        }

        public float op(float a) {
            return a * divide;
        }
    }

    static final class FloatMultiplyOp implements FloatOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6099641660816235381L;

        private final float multiply;

        public FloatMultiplyOp(float multiply) {
            this.multiply = multiply;
        }

        public float op(float a) {
            return a / multiply;
        }
    }
}