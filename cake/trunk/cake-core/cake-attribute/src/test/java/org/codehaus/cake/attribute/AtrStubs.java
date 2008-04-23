package org.codehaus.cake.attribute;

public class AtrStubs {

    public final static ByteAttribute B_1 = new ByteAttribute("B_1", (byte) 1) {};
    public final static ByteAttribute B_2 = new ByteAttribute("B_2", (byte) 2) {};
    public final static ByteAttribute B_3 = new ByteAttribute("B_3", (byte) 3) {};
    public final static BooleanAttribute B_FALSE = new BooleanAttribute("B_FALSE", false) {};

    public final static BooleanAttribute B_FALSE1 = new BooleanAttribute("B_FALSE1", false) {};
    public final static ByteAttribute B_MAX_VALUE = new ByteAttribute("B_MAX_VALUE", Byte.MAX_VALUE) {};
    public final static ByteAttribute B_MIN_VALUE = new ByteAttribute("B_MIN_VALUE", Byte.MIN_VALUE) {};
    public final static BooleanAttribute B_TRUE = new BooleanAttribute("B_TRUE", true) {};
    public final static BooleanAttribute B_TRUE1 = new BooleanAttribute("B_TRUE1", true) {};

    public final static CharAttribute C_1 = new CharAttribute("C_1", (char) 1) {};
    public final static CharAttribute C_2 = new CharAttribute("C_2", (char) 2) {};
    public final static CharAttribute C_3 = new CharAttribute("C_3", (char) 3) {};
    public final static CharAttribute C_MAX_VALUE = new CharAttribute("C_MAX_VALUE",
            Character.MAX_VALUE) {};
    public final static CharAttribute C_MIN_VALUE = new CharAttribute("C_MIN_VALUE",
            Character.MIN_VALUE) {};

    public final static DoubleAttribute D_1 = new DoubleAttribute("D_1", 1.5) {};
    public final static DoubleAttribute D_2 = new DoubleAttribute("D_2", 2.5) {};
    public final static DoubleAttribute D_3 = new DoubleAttribute("D_3", 3.5) {};
    public final static DoubleAttribute D_MAX_VALUE = new DoubleAttribute("D_MAX_VALUE",
            Double.MAX_VALUE) {};
    public final static DoubleAttribute D_MIN_VALUE = new DoubleAttribute("D_MIN_VALUE",
            Double.MIN_VALUE) {};

    public final static FloatAttribute F_1 = new FloatAttribute("F_1", (float) 1.5) {};
    public final static FloatAttribute F_2 = new FloatAttribute("F_2", (float) 2.5) {};
    public final static FloatAttribute F_3 = new FloatAttribute("F_3", (float) 3.5) {};
    public final static FloatAttribute F_MAX_VALUE = new FloatAttribute("F_MAX_VALUE",
            Float.MAX_VALUE) {};
    public final static FloatAttribute F_MIN_VALUE = new FloatAttribute("F_MIN_VALUE",
            Float.MIN_VALUE) {};

    public final static IntAttribute I_1 = new IntAttribute("I_1", 1) {};
    public final static IntAttribute I_2 = new IntAttribute("I_2", 2) {};
    public final static IntAttribute I_3 = new IntAttribute("I_3", 3) {};
    public final static IntAttribute I_MAX_VALUE = new IntAttribute("I_MAX_VALUE",
            Integer.MAX_VALUE) {};
    public final static IntAttribute I_MIN_VALUE = new IntAttribute("I_MIN_VALUE",
            Integer.MIN_VALUE) {};

    public final static LongAttribute L_1 = new LongAttribute("L_1", 1) {};
    public final static LongAttribute L_2 = new LongAttribute("L_2", 2) {};
    public final static LongAttribute L_3 = new LongAttribute("L_3", 3) {};
    public final static LongAttribute L_MAX_VALUE = new LongAttribute("L_MAX_VALUE", Long.MAX_VALUE) {};
    public final static LongAttribute L_MIN_VALUE = new LongAttribute("L_MIN_VALUE", Long.MIN_VALUE) {};

    public final static ObjectAttribute<String> O_1 = new ObjectAttribute<String>("O_1",
            String.class, "1.5") {};
    public final static ObjectAttribute<String> O_2 = new ObjectAttribute<String>("O_2",
            String.class, "15") {};
    public final static ObjectAttribute<String> O_3 = new ObjectAttribute<String>("O_3",
            String.class, null) {};
    public final static ShortAttribute S_1 = new ShortAttribute("S_1", (short) 1) {};
    public final static ShortAttribute S_2 = new ShortAttribute("S_2", (short) 2) {};

    public final static ShortAttribute S_3 = new ShortAttribute("S_3", (short) 3) {};
    public final static ShortAttribute S_MAX_VALUE = new ShortAttribute("S_MAX_VALUE",
            Short.MAX_VALUE) {};
    public final static ShortAttribute S_MIN_VALUE = new ShortAttribute("S_MIN_VALUE",
            Short.MIN_VALUE) {};

    static WithAttributes withAtr(final AttributeMap map) {
        return new WithAttributes() {
            @Override
            public AttributeMap getAttributes() {
                return map;
            }
        };
    }
}
