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

import org.codehaus.cake.ops.Ops.BinaryLongOp;
import org.codehaus.cake.ops.Ops.LongOp;
import org.codehaus.cake.ops.Ops.LongReducer;

public class LongOps {
    private final static BinaryLongSubtractOp BINART_DOUBLE_SUBTRACT_OP = new BinaryLongSubtractOp();
    private final static BinaryLongAddOp BINARY_DOUBLE_ADD_OP = new BinaryLongAddOp();
    private final static LongAbs DOUBLE_ABS_OP = new LongAbs();
    
    public static LongOp abs() {
        return DOUBLE_ABS_OP;
    }
    public static LongOp add(long add) {
        return new LongAddOp(add);
    }

    public static BinaryLongOp add() {
        return BINARY_DOUBLE_ADD_OP;
    }

    public static BinaryLongOp subtract() {
        return BINART_DOUBLE_SUBTRACT_OP;
    }

    public static LongOp divide(long add) {
        return new LongDivideOp(add);
    }

    public static LongOp multiply(long add) {
        return new LongMultiplyOp(add);
    }
    public static BinaryLongOp multiply() {
        throw new UnsupportedOperationException();
    }
    static final class BinaryLongAddOp implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -830758681673022439L;

        public long op(long a, long b) {
            return a + b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return BINARY_DOUBLE_ADD_OP;
        }
    }
    static final class BinaryLongSubtractOp implements LongReducer, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8583260658972887816L;

        public long op(long a, long b) {
            return a - b;
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return BINART_DOUBLE_SUBTRACT_OP;
        }
    }

    static final class LongAbs implements LongOp, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -8583260658972887816L;

        public long op(long a) {
            return Math.abs(a);
        }

        /** @return Preserves singleton property */
        private Object readResolve() {
            return DOUBLE_ABS_OP;
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
}
