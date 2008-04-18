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

import org.codehaus.cake.ops.Ops.BinaryDoubleOp;
import org.codehaus.cake.ops.Ops.DoubleOp;
import org.codehaus.cake.ops.Ops.DoubleReducer;

public class DoubleOps {
    private final static BinaryDoubleSubtractOp BINART_DOUBLE_SUBTRACT_OP = new BinaryDoubleSubtractOp();
    private final static BinaryDoubleAddOp BINARY_DOUBLE_ADD_OP = new BinaryDoubleAddOp();
    private final static DoubleAbs DOUBLE_ABS_OP = new DoubleAbs();
    
    public static DoubleOp abs() {
        return DOUBLE_ABS_OP;
    }
    public static DoubleOp add(double add) {
        return new DoubleAddOp(add);
    }

    public static DoubleReducer add() {
        return BINARY_DOUBLE_ADD_OP;
    }

    public static BinaryDoubleOp subtract() {
        return BINART_DOUBLE_SUBTRACT_OP;
    }
    public static DoubleOp subtract(double substract) {
        return null;
    }
    public static DoubleOp divide(double add) {
        return new DoubleDivideOp(add);
    }

    public static DoubleOp multiply(double add) {
        return new DoubleMultiplyOp(add);
    }
    public static BinaryDoubleOp multiply() {
        throw new UnsupportedOperationException();
    }
    static final class BinaryDoubleAddOp implements DoubleReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -830758681673022439L;

        public double op(double a, double b) {
            return a + b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return BINARY_DOUBLE_ADD_OP;
        }
    }
    static final class BinaryDoubleSubtractOp implements DoubleReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8583260658972887816L;

        public double op(double a, double b) {
            return a - b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return BINART_DOUBLE_SUBTRACT_OP;
        }
    }

    static final class DoubleAbs implements DoubleOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8583260658972887816L;

        public double op(double a) {
            return Math.abs(a);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return DOUBLE_ABS_OP;
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
}
