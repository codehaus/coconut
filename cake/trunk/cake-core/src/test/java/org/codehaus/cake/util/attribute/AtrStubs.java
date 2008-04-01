package org.codehaus.cake.util.attribute;

public class AtrStubs {

    final static Attribute<Boolean> B_TRUE = new Attribute<Boolean>("B_TRUE", Boolean.class, true) {};
    final static Attribute<Boolean> B_FALSE = new Attribute<Boolean>("B_FALSE", Boolean.class,
            false) {};
    final static Attribute<Boolean> B_TRUE1 = new Attribute<Boolean>("B_TRUE1", Boolean.class, true) {};
    final static Attribute<Boolean> B_FALSE1 = new Attribute<Boolean>("B_FALSE1", Boolean.class,
            false) {};

    final static Attribute<Byte> B_1 = new Attribute<Byte>("B_1", Byte.class, (byte) 1) {};
    final static Attribute<Byte> B_2 = new Attribute<Byte>("B_2", Byte.class, (byte) 2) {};
    final static Attribute<Byte> B_3 = new Attribute<Byte>("B_3", Byte.class, (byte) 3) {};
    final static Attribute<Byte> B_MIN_VALUE = new Attribute<Byte>("B_MIN_VALUE", Byte.class,
            Byte.MIN_VALUE) {};
    final static Attribute<Byte> B_MAX_VALUE = new Attribute<Byte>("B_MAX_VALUE", Byte.class,
            Byte.MAX_VALUE) {};

    final static Attribute<Character> C_1 = new Attribute<Character>("C_1", Character.class,
            (char) 1) {};
    final static Attribute<Character> C_2 = new Attribute<Character>("C_2", Character.class,
            (char) 2) {};
    final static Attribute<Character> C_3 = new Attribute<Character>("C_3", Character.class,
            (char) 3) {};
    final static Attribute<Character> C_MIN_VALUE = new Attribute<Character>("C_MIN_VALUE",
            Character.class, Character.MIN_VALUE) {};
    final static Attribute<Character> C_MAX_VALUE = new Attribute<Character>("C_MAX_VALUE",
            Character.class, Character.MAX_VALUE) {};

    final static Attribute<Float> F_1 = new Attribute<Float>("F_1", Float.class, (float) 1.5) {};
    final static Attribute<Float> F_2 = new Attribute<Float>("F_2", Float.class, (float) 2.5) {};
    final static Attribute<Float> F_3 = new Attribute<Float>("F_3", Float.class, (float) 3.5) {};
    final static Attribute<Float> F_MIN_VALUE = new Attribute<Float>("F_MIN_VALUE", Float.class,
            Float.MIN_VALUE) {};
    final static Attribute<Float> F_MAX_VALUE = new Attribute<Float>("F_MAX_VALUE", Float.class,
            Float.MAX_VALUE) {};

    final static Attribute<Short> S_1 = new Attribute<Short>("S_1", Short.class, (short) 1) {};
    final static Attribute<Short> S_2 = new Attribute<Short>("S_2", Short.class, (short) 2) {};
    final static Attribute<Short> S_3 = new Attribute<Short>("S_3", Short.class, (short) 3) {};
    final static Attribute<Short> S_MIN_VALUE = new Attribute<Short>("S_MIN_VALUE", Short.class,
            Short.MIN_VALUE) {};
    final static Attribute<Short> S_MAX_VALUE = new Attribute<Short>("S_MAX_VALUE", Short.class,
            Short.MAX_VALUE) {};

    final static IntAttribute I_1 = new IntAttribute("I_1", 1) {};
    final static IntAttribute I_2 = new IntAttribute("I_2", 2) {};
    final static IntAttribute I_3 = new IntAttribute("I_3", 3) {};
    final static IntAttribute I_MIN_VALUE = new IntAttribute("I_MIN_VALUE", Integer.MIN_VALUE) {};
    final static IntAttribute I_MAX_VALUE = new IntAttribute("I_MAX_VALUE", Integer.MAX_VALUE) {};

    final static LongAttribute L_1 = new LongAttribute("L_1", 1) {};
    final static LongAttribute L_2 = new LongAttribute("L_2", 2) {};
    final static LongAttribute L_3 = new LongAttribute("L_3", 3) {};
    final static LongAttribute L_MIN_VALUE = new LongAttribute("L_MIN_VALUE", Long.MIN_VALUE) {};
    final static LongAttribute L_MAX_VALUE = new LongAttribute("L_MAX_VALUE", Long.MAX_VALUE) {};

    final static DoubleAttribute D_1 = new DoubleAttribute("D_1", 1.5) {};
    final static DoubleAttribute D_2 = new DoubleAttribute("D_2", 2.5) {};
    final static DoubleAttribute D_3 = new DoubleAttribute("D_3", 3.5) {};
    final static DoubleAttribute D_MIN_VALUE = new DoubleAttribute("D_MIN_VALUE", Double.MIN_VALUE) {};
    final static DoubleAttribute D_MAX_VALUE = new DoubleAttribute("D_MAX_VALUE", Double.MAX_VALUE) {};

    static WithAttributes withAtr(final AttributeMap map) {
        return new WithAttributes() {
            @Override
            public AttributeMap getAttributes() {
                return map;
            }
        };
    }
}
