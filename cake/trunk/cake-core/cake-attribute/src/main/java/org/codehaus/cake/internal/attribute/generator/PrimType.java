package org.codehaus.cake.internal.attribute.generator;

import org.codehaus.cake.attribute.Attribute;
import org.codehaus.cake.attribute.BooleanAttribute;
import org.codehaus.cake.attribute.ByteAttribute;
import org.codehaus.cake.attribute.CharAttribute;
import org.codehaus.cake.attribute.DoubleAttribute;
import org.codehaus.cake.attribute.FloatAttribute;
import org.codehaus.cake.attribute.IntAttribute;
import org.codehaus.cake.attribute.LongAttribute;
import org.codehaus.cake.attribute.ObjectAttribute;
import org.codehaus.cake.attribute.ShortAttribute;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public enum PrimType {
    LONG(Long.class, LongAttribute.class, Type.LONG_TYPE, Opcodes.LLOAD, Opcodes.LSTORE,
            Opcodes.LRETURN) {
        public int indexInc() {
            return 2;
        }
    },
    DOUBLE(Double.class, DoubleAttribute.class, Type.DOUBLE_TYPE, Opcodes.DLOAD, Opcodes.DSTORE,
            Opcodes.DRETURN) {
        public int indexInc() {
            return 2;
        }
    },
    INT(Integer.class, IntAttribute.class, Type.INT_TYPE, Opcodes.ILOAD, Opcodes.ISTORE,
            Opcodes.IRETURN),

    SHORT(Short.class, ShortAttribute.class, Type.SHORT_TYPE, Opcodes.ILOAD, Opcodes.ISTORE,
            Opcodes.IRETURN),

    BYTE(Byte.class, ByteAttribute.class, Type.BYTE_TYPE, Opcodes.ILOAD, Opcodes.ISTORE,
            Opcodes.IRETURN),

    CHAR(Character.class, CharAttribute.class, Type.CHAR_TYPE, Opcodes.ILOAD, Opcodes.ISTORE,
            Opcodes.IRETURN),

    BOOLEAN(Boolean.class, BooleanAttribute.class, Type.BOOLEAN_TYPE, Opcodes.ILOAD,
            Opcodes.ISTORE, Opcodes.IRETURN),

    FLOAT(Float.class, FloatAttribute.class, Type.FLOAT_TYPE, Opcodes.FLOAD, Opcodes.FSTORE,
            Opcodes.FRETURN),

    OBJECT(Object.class, ObjectAttribute.class, Type.getType(Object.class), Opcodes.ALOAD,
            Opcodes.ASTORE, Opcodes.ARETURN);

    private final Type type;
    private final Type primType;
    private final int returncode;
    private final int loadCode;
    private final int storeCode;
    private final Type object;// java.lang.Integer

    private PrimType(Class object, Class<? extends Attribute> c, Type primType, int loadCode,
            int storeCode, int returnCode) {
        type = Type.getType(c);
        this.object = Type.getType(object);
        this.primType = primType;
        this.returncode = returnCode;
        this.loadCode = loadCode;
        this.storeCode = storeCode;

    }

    public String getDescriptor() {
        return type.getDescriptor();
    }

    public Type getType() {
        return type;
    }

    public int returnCode() {
        return returncode;
    }

    public int loadCode() {
        return loadCode;
    }

    public int storeCode() {
        return storeCode;
    }

    public Type getPrimType() {
        return primType;
    }

    // java.lang.Integer
    public Type getObjectType() {
        return object;
    }

    public String getPrimDescriptor() {
        return getPrimType().getDescriptor();
    }

    public int indexInc() {
        return 1;
    }

    public static PrimType from(Attribute<?> a) {
        if (a instanceof LongAttribute) {
            return LONG;
        } else if (a instanceof DoubleAttribute) {
            return DOUBLE;
        } else if (a instanceof IntAttribute) {
            return INT;
        } else if (a instanceof ShortAttribute) {
            return SHORT;
        } else if (a instanceof ByteAttribute) {
            return BYTE;
        } else if (a instanceof FloatAttribute) {
            return FLOAT;
        } else if (a instanceof CharAttribute) {
            return CHAR;
        } else if (a instanceof BooleanAttribute) {
            return BOOLEAN;
        } else  {
            //if (a instanceof ObjectAttribute) {
            return OBJECT;
        }
    }
}
