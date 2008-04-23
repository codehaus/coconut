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

import org.codehaus.cake.ops.Ops.DoubleComparator;
import org.codehaus.cake.ops.Ops.DoubleOp;
import org.codehaus.cake.ops.Ops.DoublePredicate;
import org.codehaus.cake.ops.Ops.DoubleReducer;
/**
 * Various implementations of {@link DoublePredicate}.
 * <p>
 * This class is normally best used via <tt>import static</tt>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: DoubleOps.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class DoubleOps {
    final static DoubleAbsOp ABS_OP = new DoubleAbsOp();

    
    final static DoubleAddReducer ADD_REDUCER = new DoubleAddReducer();

    /**
     * A comparator for doubles relying on natural ordering. The comparator is Serializable.
     */
    public static final DoubleComparator COMPARATOR = new NaturalDoubleComparator();

    final static DoubleDivideReducer DIVIDE_REDUCER = new DoubleDivideReducer();
     /**
     * A reducer returning the maximum of two double elements, using natural comparator.
     * The Reducer is serializable.
     */
     static final DoubleReducer MAX_REDUCER = new NaturalDoubleMaxReducer();

     /**
     * A reducer returning the minimum of two double elements, using natural comparator.
     * The Reducer is serializable.
     */
     static final DoubleReducer MIN_REDUCER = new NaturalDoubleMinReducer();
    
     final static DoubleMultiplyReducer MULTIPLY_REDUCER = new DoubleMultiplyReducer();
    
     /**
     * A comparator that imposes the reverse of the <i>natural ordering</i> on doubles. The
     * comparator is Serializable.
     */
    public static final DoubleComparator REVERSE_COMPARATOR = new NaturalDoubleReverseComparator();

        final static DoubleSubtractReducer SUBTRACT_REDUCER = new DoubleSubtractReducer();
    
    ///CLOVER:OFF
    /** Cannot instantiate. */
    private DoubleOps() {}
    ///CLOVER:ON
    
    public static DoubleOp abs() {
        return ABS_OP;
    }
    
    public static DoubleReducer add() {
    return ADD_REDUCER;
   }
    
    public static DoubleOp add(double add) {
        return new DoubleAddOp(add);
    }
    
    public static DoubleReducer divide() {
        return DIVIDE_REDUCER;
    }
    
    public static DoubleOp divide(double divide) {
        return new DoubleDivideOp(divide);
    }
    
    /**
     * A reducer returning the maximum of two double elements, using the specified
     * comparator.
     *
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created reducer
     */
    public static DoubleReducer max(DoubleComparator comparator) {
        return new DoubleMaxReducer(comparator);
    }

    /**
     * A reducer returning the minimum of two double elements, using the specified
     * comparator.
     *
     * @param comparator
     *            the comparator to use when comparing elements
     * @return the newly created reducer
     */
    public static DoubleReducer min(DoubleComparator comparator) {
        return new DoubleMinReducer(comparator);
    }
    
    public static DoubleReducer multiply() {
        return MULTIPLY_REDUCER;
    }
    
    
    public static DoubleOp multiply(double multiply) {
        return new DoubleMultiplyOp(multiply);
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
    public static DoubleComparator reverseOrder(DoubleComparator comparator) {
        return new ReverseDoubleComparator(comparator);
    }
         public static DoubleReducer subtract() {
            return SUBTRACT_REDUCER;
        }
    public static DoubleOp subtract(double substract) {
        return new DoubleSubtractOp(substract);
    }
    static final class DoubleAbsOp implements DoubleOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8583260658972887816L;

        public double op(double a) {
            return Math.abs(a);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return ABS_OP;
        }
    }

    static final class DoubleAddOp implements DoubleOp, Serializable {
    /** serialVersionUID. */
    private static final long serialVersionUID = -6604604690824553900L;

    private final double add;

    public DoubleAddOp(double add) {
        this.add = add;
    }

    public double op(double a) {
        return a + add;
    }
   }    static final class DoubleAddReducer implements DoubleReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -830758681673022439L;

        public double op(double a, double b) {
            return a + b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return ADD_REDUCER;
        }
    }

    static final class DoubleDivideOp implements DoubleOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 661378303438906777L;

        private final double divide;

        public DoubleDivideOp(double divide) {
            this.divide = divide;
        }

        public double op(double a) {
            return a / divide;
        }
    }

    static final class DoubleDivideReducer implements DoubleReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -330758681673022439L;

        public double op(double a, double b) {
            return a / b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return DIVIDE_REDUCER;
        }
    }
    
            /**
             * A reducer returning the maximum of two double elements, using the given comparator.
             */
            static final class DoubleMaxReducer implements DoubleReducer, Serializable {
                /** serialVersionUID. */
                private static final long serialVersionUID = 2065097741025480432L;

                /** Comparator used when reducing. */
                private final DoubleComparator comparator;

                /**
                 * Creates a DoubleMaxReducer.
                 *
                 * @param comparator
                 *            the comparator to use
                 */
                DoubleMaxReducer(DoubleComparator comparator) {
                    if (comparator == null) {
                        throw new NullPointerException("comparator is null");
                    }
                    this.comparator = comparator;
                }

                /** {@inheritDoc} */
                public double op(double a, double b) {
                    return comparator.compare(a, b) >= 0 ? a : b;
                }
            }
    
    /**
     * A reducer returning the minimum of two double elements, using the given comparator.
     */
    static final class DoubleMinReducer implements DoubleReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6109941145562459503L;

        /** Comparator used when reducing. */
        private final DoubleComparator comparator;

        /**
         * Creates a DoubleMinReducer.
         *
         * @param comparator
         *            the comparator to use
         */
        DoubleMinReducer(DoubleComparator comparator) {
            if (comparator == null) {
                throw new NullPointerException("comparator is null");
            }
            this.comparator = comparator;
        }

        /** {@inheritDoc} */
        public double op(double a, double b) {
            return comparator.compare(a, b) <= 0 ? a : b;
        }
    }

    static final class DoubleMultiplyOp implements DoubleOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 6099641660816235381L;

        private final double multiply;

        public DoubleMultiplyOp(double multiply) {
            this.multiply = multiply;
        }

        public double op(double a) {
            return a * multiply;
        }
    }    
    static final class DoubleMultiplyReducer implements DoubleReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -130758681673022439L;

        public double op(double a, double b) {
            return a * b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MULTIPLY_REDUCER;
        }
    }

    static final class DoubleSubtractOp implements DoubleOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -23423423410L;

        private final double subtract;

        public DoubleSubtractOp(double subtract) {
            this.subtract = subtract;
        }

        public double op(double a) {
            return a - subtract;
        }
    }
     
    static final class DoubleSubtractReducer implements DoubleReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8583260658972887816L;

        public double op(double a, double b) {
            return a - b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return SUBTRACT_REDUCER;
        }
    }

    /** A comparator for doubles relying on natural ordering. */
    static final class NaturalDoubleComparator implements DoubleComparator, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = 8763765406476535022L;

        /** {@inheritDoc} */
        public int compare(double a, double b) {
            return Double.compare(a, b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return COMPARATOR;
        }
    }
    
    /** A reducer returning the maximum of two double elements, using natural comparator. */
   static final class NaturalDoubleMaxReducer implements DoubleReducer, Serializable {

    /** serialVersionUID. */
    private static final long serialVersionUID = -5902864811727900806L;

    /** {@inheritDoc} */
    public double op(double a, double b) {
        return Math.max(a, b);
    }

    /** @return Preserves singleton property */
    private Object readResolve() {
        return MAX_REDUCER;
    }
   }

    /** A reducer returning the minimum of two double elements, using natural comparator. */
    static final class NaturalDoubleMinReducer implements DoubleReducer, Serializable {

        /** serialVersionUID. */
        private static final long serialVersionUID = 9005140841348156699L;

        /** {@inheritDoc} */
        public double op(double a, double b) {
            return Math.min(a, b);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return MIN_REDUCER;
        }
    }
    
        /** A comparator for doubles relying on natural ordering. */
        static final class NaturalDoubleReverseComparator implements DoubleComparator, Serializable {
            /** serialVersionUID. */
            private static final long serialVersionUID = -7289505884757339069L;

            /** {@inheritDoc} */
            public int compare(double a, double b) {
                return -Double.compare(a, b);

            }

            /** @return Preserves singleton property */
            private Object readResolve() {
                return REVERSE_COMPARATOR;
            }
        }

    /** A comparator that reserves the result of another DoubleComparator. */
   static final class ReverseDoubleComparator implements DoubleComparator, Serializable {
      /** serialVersionUID. */
      private static final long serialVersionUID = 1585665469031127321L;

      /** The comparator to reverse. */
      private final DoubleComparator comparator;

      /**
       * Creates a new ReverseDoubleComparator.
       * 
       * @param comparator
       *            the comparator to reverse
       */
      ReverseDoubleComparator(DoubleComparator comparator) {
    if (comparator == null) {
        throw new NullPointerException("comparator is null");
    }
    this.comparator = comparator;
      }

      /** {@inheritDoc} */
      public int compare(double a, double b) {
    return -comparator.compare(a, b);
      }
   }
}