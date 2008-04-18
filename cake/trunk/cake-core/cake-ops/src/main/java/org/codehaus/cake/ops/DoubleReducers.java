package org.codehaus.cake.ops;

import java.io.Serializable;

import org.codehaus.cake.ops.Ops.DoubleReducer;

public class DoubleReducers {
    private final static BinaryDoubleSubtractOp BINART_DOUBLE_SUBTRACT_OP = new BinaryDoubleSubtractOp();

    private final static BinaryDoubleAddOp BINARY_DOUBLE_ADD_OP = new BinaryDoubleAddOp();

    public static DoubleReducer doubleAddReducer() {
        return BINARY_DOUBLE_ADD_OP;
    }

    public static DoubleReducer doubleSubtractReducer() {
        return BINART_DOUBLE_SUBTRACT_OP;
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

}
