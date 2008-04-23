package org.codehaus.cake.internal.attribute.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.cake.attribute.Attribute;
import org.codehaus.cake.attribute.AttributeMap;
import org.codehaus.cake.attribute.BooleanAttribute;
import org.codehaus.cake.attribute.ByteAttribute;
import org.codehaus.cake.attribute.CharAttribute;
import org.codehaus.cake.attribute.DoubleAttribute;
import org.codehaus.cake.attribute.FloatAttribute;
import org.codehaus.cake.attribute.IntAttribute;
import org.codehaus.cake.attribute.LongAttribute;
import org.codehaus.cake.attribute.ObjectAttribute;
import org.codehaus.cake.attribute.ShortAttribute;

public class Checker {
    private AttributeMap map;
    private final LinkedHashMap<DefaultAttributeConfiguration, Object> params;
    private final Map<DefaultAttributeConfiguration, Object> visible;
    private final Set<Attribute<?>> visibleAttributes = new HashSet<Attribute<?>>();

    Checker(AttributeMap map, LinkedHashMap<DefaultAttributeConfiguration, Object> m) {
        this.map = map;
        visible = new HashMap<DefaultAttributeConfiguration, Object>();
        for (Map.Entry<DefaultAttributeConfiguration, Object> i : m.entrySet()) {
            if (!i.getKey().isHidden()) {
                visible.put(i.getKey(), i.getValue());
                visibleAttributes.add(i.getKey().getAttribute());
            }
        }
        this.params = m;
    }

    void check(DefaultAttributeConfiguration i, BooleanAttribute la, boolean l) {
        assertEquals(l, map.get(la, false));
        assertEquals(l, map.get(la, true));
        assertEquals(l, map.get(la));
        assertEquals(false, map.get(new BooleanAttribute("ff", false) {}, false));
        assertEquals(true, map.get(new BooleanAttribute("ff", false) {}, true));
        assertEquals(false, map.get(new BooleanAttribute("ff", true) {}, false));
        assertEquals(true, map.get(new BooleanAttribute("ff", true) {}, true));
        assertEquals(false, map.get(new BooleanAttribute("ff", false) {}));
        assertEquals(true, map.get(new BooleanAttribute("ff", true) {}));
    }

    void check(DefaultAttributeConfiguration i, ByteAttribute la, byte l) {
        assertEquals(l, map.get(la, (byte) -56));
        assertEquals(l, map.get(la, l));
        assertEquals((byte) 34, map.get(new ByteAttribute("ff", (byte) 44) {}, (byte) 34));
        assertEquals(Byte.MIN_VALUE, map.get(new ByteAttribute("ff", (byte) 44) {}, Byte.MIN_VALUE));
        assertEquals(l, map.get(la));
        assertEquals((byte) 44, map.get(new ByteAttribute("ff", (byte) 44) {}));

        // put
        if (i.isMutable()) {
            assertEquals(l, map.put(la, (byte) 111));
            assertEquals((byte) 111, map.get(la));
            assertEquals((byte) 111, map.get(la, (byte) 34));
        } else {
            try {
                map.put(la, (byte) 34);
                throw new AssertionError("should fail");
            } catch (UnsupportedOperationException ok) {
            }
        }
        try {
            map.remove(la);
            throw new AssertionError("should fail");
        } catch (UnsupportedOperationException ok) {
        }

        initMap();
    }

    void check(DefaultAttributeConfiguration i, CharAttribute la, char l) {
        assertEquals(l, map.get(la, (char) -56));
        assertEquals(l, map.get(la, l));
        assertEquals((char) 34, map.get(new CharAttribute("ff", (char) 44) {}, (char) 34));
        assertEquals(Character.MIN_VALUE, map.get(new CharAttribute("ff", (char) 44) {},
                Character.MIN_VALUE));
        assertEquals(l, map.get(la));
        assertEquals((char) 44, map.get(new CharAttribute("ff", (char) 44) {}));

        // put
        if (i.isMutable()) {
            assertEquals(l, map.put(la, (char) 111));
            assertEquals((char) 111, map.get(la));
            assertEquals((char) 111, map.get(la, (char) 34));
        } else {
            try {
                map.put(la, (char) 34);
                throw new AssertionError("should fail");
            } catch (UnsupportedOperationException ok) {
            }
        }
        try {
            map.remove(la);
            throw new AssertionError("should fail");
        } catch (UnsupportedOperationException ok) {
        }

        initMap();
    }

    void check(DefaultAttributeConfiguration i, DoubleAttribute la, double l) {
        assertEquals(l, map.get(la, -565656), 0);
        assertEquals(l, map.get(la, l), 0);
        assertEquals(34d, map.get(new DoubleAttribute("ff", 44) {}, 34), 0);
        assertEquals(Double.MIN_VALUE, map.get(new DoubleAttribute("ff", 44) {}, Double.MIN_VALUE),
                0);
        assertEquals(l, map.get(la), 0);
        assertEquals(44d, map.get(new DoubleAttribute("ff", 44) {}), 0);

        // put
        if (i.isMutable()) {
            assertEquals(l, map.put(la, 242374634), 0);
            assertEquals(242374634d, map.get(la), 0);
            assertEquals(242374634d, map.get(la, 234), 0);
        } else {
            try {
                map.put(la, 345345);
                throw new AssertionError("should fail");
            } catch (UnsupportedOperationException ok) {
            }
        }
        try {
            map.remove(la);
            throw new AssertionError("should fail");
        } catch (UnsupportedOperationException ok) {
        }

        initMap();
    }

    void check(DefaultAttributeConfiguration i, FloatAttribute la, float l) {
        assertEquals(l, map.get(la, -565656), 0);
        assertEquals(l, map.get(la, l), 0);
        assertEquals(34f, map.get(new FloatAttribute("ff", 44) {}, 34), 0);
        assertEquals(Float.MIN_VALUE, map.get(new FloatAttribute("ff", 44) {}, Float.MIN_VALUE), 0);
        assertEquals(l, map.get(la), 0);
        assertEquals(44f, map.get(new FloatAttribute("ff", 44) {}), 0);

        // put
        if (i.isMutable()) {
            assertEquals(l, map.put(la, 242374634), 0);
            assertEquals(242374634f, map.get(la), 0);
            assertEquals(242374634f, map.get(la, 234), 0);
        } else {
            try {
                map.put(la, 345345);
                throw new AssertionError("should fail");
            } catch (UnsupportedOperationException ok) {
            }
        }
        try {
            map.remove(la);
            throw new AssertionError("should fail");
        } catch (UnsupportedOperationException ok) {
        }

        initMap();
    }

    void check(DefaultAttributeConfiguration i, IntAttribute la, int l) {
        assertEquals(l, map.get(la, -565656));
        assertEquals(l, map.get(la, l));
        assertEquals(34, map.get(new IntAttribute("ff", 44) {}, 34));
        assertEquals(Integer.MIN_VALUE, map.get(new IntAttribute("ff", 44) {}, Integer.MIN_VALUE));
        assertEquals(l, map.get(la));
        assertEquals(44, map.get(new IntAttribute("ff", 44) {}));

        // put
        if (i.isMutable()) {
            assertEquals(l, map.put(la, 242374634));
            assertEquals(242374634, map.get(la));
            assertEquals(242374634, map.get(la, 234));
        } else {
            try {
                map.put(la, 345345);
                throw new AssertionError("should fail");
            } catch (UnsupportedOperationException ok) {
            }
        }
        try {
            map.remove(la);
            throw new AssertionError("should fail");
        } catch (UnsupportedOperationException ok) {
        }

        initMap();
    }

    void check(DefaultAttributeConfiguration i, LongAttribute la, long l) {
        assertEquals(l, map.get(la, -565656));
        assertEquals(l, map.get(la, l));
        assertEquals(34L, map.get(new LongAttribute("ff", 44) {}, 34));
        assertEquals(Long.MIN_VALUE, map.get(new LongAttribute("ff", 44) {}, Long.MIN_VALUE));
        assertEquals(l, map.get(la));
        assertEquals(44L, map.get(new LongAttribute("ff", 44) {}));

        // put
        if (i.isMutable()) {
            assertEquals(l, map.put(la, 242374634));
            assertEquals(242374634L, map.get(la));
            assertEquals(242374634L, map.get(la, 234));
        } else {
            try {
                map.put(la, 345345);
                throw new AssertionError("should fail");
            } catch (UnsupportedOperationException ok) {
            }
        }
        try {
            map.remove(la);
            throw new AssertionError("should fail");
        } catch (UnsupportedOperationException ok) {
        }

        initMap();
    }

    void check(DefaultAttributeConfiguration i, Object value) {
        // System.out.println(map);
        Attribute<?> a = i.getAttribute();
        assertEquals(!i.isHidden(), map.contains(a));
        assertEquals(!i.isHidden(), map.attributeSet().contains(a));

        if (a instanceof BooleanAttribute) {
            check(i, (BooleanAttribute) a, ((Boolean) value).booleanValue());
        }
        if (a instanceof ByteAttribute) {
            check(i, (ByteAttribute) a, ((Byte) value).byteValue());
        }
        if (a instanceof CharAttribute) {
            check(i, (CharAttribute) a, ((Character) value).charValue());
        }
        if (a instanceof DoubleAttribute) {
            check(i, (DoubleAttribute) a, ((Double) value).doubleValue());
        }
        if (a instanceof FloatAttribute) {
            check(i, (FloatAttribute) a, ((Float) value).floatValue());
        }
        if (a instanceof IntAttribute) {
            check(i, (IntAttribute) a, ((Integer) value).intValue());
        }
        if (a instanceof LongAttribute) {
            check(i, (LongAttribute) a, ((Long) value).longValue());
        }
        if (a instanceof ShortAttribute) {
            check(i, (ShortAttribute) a, ((Short) value).shortValue());
        }
        if (a instanceof ObjectAttribute) {
            check(i, (ObjectAttribute) a, value);
        }
    }

    void check(DefaultAttributeConfiguration i, ObjectAttribute la, Object l) {
        assertEquals(l, map.get(la, new Object()));
        assertEquals(l, map.get(la, l));
        assertEquals("34", map.get(new ObjectAttribute(String.class, "44") {}, "34"));
        assertEquals(l, map.get(la));
        assertEquals("44", map.get(new ObjectAttribute(String.class, "44") {}));
        final Object o1;
        final Object o2;
        if (la.getType().equals(String.class)) {
            o1 = "jlkhk3jh4";
            o2 = "jlkhk3jhd4";
        } else if (la.getType().equals(Map.class)) {
            o1 = new HashMap();
            ((Map) o1).put("k2g34", "value");
            o2 = new HashMap();
            ((Map) o2).put("k2dg34", "value");
        } else if (la.getType().equals(Long.class)) {
            o1 = new Long(2390847298374L);
            o2 = new Long(23908472983724L);
        } else {
            throw new AssertionError(la.getType().toString());
        }
        if (i.isMutable()) {
            assertEquals(l, map.put(la, o1));
            assertEquals(o1, map.get(la));
            assertEquals(o1, map.get(la, l));
            assertEquals(o1, map.get(la, 02));
        } else {
            try {
                map.put(la, o1);
                throw new AssertionError("should fail");
            } catch (UnsupportedOperationException ok) {
            }
        }
        try {
            map.remove(la);
            throw new AssertionError("should fail");
        } catch (UnsupportedOperationException ok) {
        }

        initMap();
    }

    void check(DefaultAttributeConfiguration i, ShortAttribute la, short l) {
        assertEquals(l, map.get(la, (short) -56));
        assertEquals(l, map.get(la, l));
        assertEquals((short) 34, map.get(new ShortAttribute("ff", (short) 44) {}, (short) 34));
        assertEquals(Short.MIN_VALUE, map.get(new ShortAttribute("ff", (short) 44) {},
                Short.MIN_VALUE));
        assertEquals(l, map.get(la));
        assertEquals((short) 44, map.get(new ShortAttribute("ff", (short) 44) {}));

        // put
        if (i.isMutable()) {
            assertEquals(l, map.put(la, (short) 111));
            assertEquals((short) 111, map.get(la));
            assertEquals((short) 111, map.get(la, (short) 34));
        } else {
            try {
                map.put(la, (short) 34);
                throw new AssertionError("should fail");
            } catch (UnsupportedOperationException ok) {
            }
        }
        try {
            map.remove(la);
            throw new AssertionError("should fail");
        } catch (UnsupportedOperationException ok) {
        }

        initMap();
    }

    void initMap() {
        initMap("foo" + System.nanoTime());
    }

    void initMap(String className) {
        List<DefaultAttributeConfiguration> list = new ArrayList<DefaultAttributeConfiguration>();
        for (Map.Entry<DefaultAttributeConfiguration, Object> i : params.entrySet()) {
            list.add(i.getKey());
        }
        try {
            Class<AttributeMap> c = DefaultMapGenerator.generate(className, list);
            map = newInstance(params, c, false);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }

    void run() {
        try {
            map.clear();
            throw new AssertionError();
        } catch (UnsupportedOperationException ok) {
        }

        assertEquals(visible.size(), map.size());
        assertEquals(visible.size(), map.values().size());
        assertEquals(visible.isEmpty(), map.isEmpty());
        assertEquals(visible.keySet().size(), map.attributeSet().size());

        Map tmp = new HashMap();
        for (Map.Entry<DefaultAttributeConfiguration, Object> i : visible.entrySet()) {
            tmp.put(i.getKey().getAttribute(), i.getValue());
        }
        assertEquals(tmp.entrySet(), map.entrySet());
        assertEquals(map.entrySet(), tmp.entrySet());

        assertTrue(visible.values().containsAll(map.values()));
        assertTrue(map.values().containsAll(visible.values()));
        assertEquals(visibleAttributes, map.attributeSet());
        Map<Attribute, Object> hashCode = new HashMap<Attribute, Object>();
        for (Map.Entry<DefaultAttributeConfiguration, Object> i : params.entrySet()) {
            check(i.getKey(), i.getValue());
            if (!i.getKey().isHidden()) {
                hashCode.put(i.getKey().getAttribute(), i.getValue());
            }
        }
        if (map.size() > 0) {
            Iterator<?> iter = map.attributeSet().iterator();
            iter.next();
            try {
                iter.remove();
                throw new AssertionError();
            } catch (UnsupportedOperationException ok) {
            }
        }
        assertEquals(hashCode.hashCode(), map.hashCode());
        assertEquals(map, map);
        // no clone arguments
        AttributeMap m = newInstance(params, map.getClass(), false);
        assertEquals(m, map);
        assertEquals(map, m);
        initMap();
        assertEquals(m.hashCode(), map.hashCode());
        assertEquals(m, map);
        assertEquals(map, m);
        m = newInstance(params, map.getClass(), true);
        assertEquals(m, map);
        assertEquals(map, m);
        initMap();
        assertEquals(m.hashCode(), map.hashCode());
        assertEquals(m, map);
        assertEquals(map, m);
        // /default
        assertFalse(map.contains(new ObjectAttribute("dd", Object.class) {}));
    }

    static AttributeMap newInstance(LinkedHashMap<DefaultAttributeConfiguration, Object> params,
            Class c, boolean tryClone) {
        Class<?>[] types = new Class[params.size()];
        Object[] args = new Object[params.size()];
        int count = 0;
        for (Map.Entry<DefaultAttributeConfiguration, Object> i : params.entrySet()) {
            if (tryClone) {
                Object v = i.getValue();
                if (v instanceof String) {
                    args[count] = new String(((String) v).toCharArray());
                } else if (v instanceof Boolean) {
                    args[count] = new Boolean(((Boolean) v).booleanValue());
                } else if (v instanceof Byte) {
                    args[count] = new Byte(((Byte) v).byteValue());
                } else if (v instanceof Character) {
                    args[count] = new Character(((Character) v).charValue());
                } else if (v instanceof Double) {
                    args[count] = new Double(((Double) v).doubleValue());
                } else if (v instanceof Float) {
                    args[count] = new Float(((Float) v).floatValue());
                } else if (v instanceof Integer) {
                    args[count] = new Integer(((Integer) v).intValue());
                } else if (v instanceof Long) {
                    args[count] = new Long(((Long) v).longValue());
                } else if (v instanceof Short) {
                    args[count] = new Short(((Short) v).shortValue());
                } else if (v instanceof HashMap) {
                    args[count] = new HashMap((Map) v);
                } else if (v instanceof LinkedHashMap) {
                    args[count] = new LinkedHashMap((Map) v);
                } else {
                    if (i.getValue() != null) {
                        throw new AssertionError(i.getValue().getClass() + "");
                    }
                }
                if (i.getValue() != null) {
                    assertFalse(args[count] == i.getValue());
                }
            } else {
                args[count] = i.getValue();
            }
            Class<?> type = i.getKey().getAttribute().getType();
            if (type == null) {
                System.out.println("type is null " + type);
            }
            if (type.equals(HashMap.class)) {
                types[count++] = Map.class;
            } else {
                types[count++] = type;
            }
            // System.err.println(params.size() + "," + Arrays.asList(types));
        }
        try {
            Constructor<AttributeMap> con = c.getConstructor(types);
            // System.err.println(Arrays.asList(args));
            // System.err.println("---------");
            // System.out.println(types.length);
            // System.out.println(args.length);
            // System.err.println(con);
            // System.err.println(Arrays.toString(args) + ", " + args[0].getClass() + " " +
            // tryClone);
            return con.newInstance(args);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }

    public static void run(DefaultAttributeConfiguration ai, Object value) {
        LinkedHashMap<DefaultAttributeConfiguration, Object> m = new LinkedHashMap<DefaultAttributeConfiguration, Object>();
        m.put(ai, value);
        run(m);
    }

    public static void run(DefaultAttributeConfiguration a1, Object v1,
            DefaultAttributeConfiguration a2, Object v2) {
        LinkedHashMap<DefaultAttributeConfiguration, Object> m = new LinkedHashMap<DefaultAttributeConfiguration, Object>();
        m.put(a1, v1);
        m.put(a2, v2);
        run(m);
    }

    public static void run(DefaultAttributeConfiguration a1, Object v1,
            DefaultAttributeConfiguration a2, Object v2, DefaultAttributeConfiguration a3, Object v3) {
        LinkedHashMap<DefaultAttributeConfiguration, Object> m = new LinkedHashMap<DefaultAttributeConfiguration, Object>();
        m.put(a1, v1);
        m.put(a2, v2);
        m.put(a3, v3);
        run(m);
    }

    public static void run(DefaultAttributeConfiguration a1, Object v1,
            DefaultAttributeConfiguration a2, Object v2, DefaultAttributeConfiguration a3,
            Object v3, DefaultAttributeConfiguration a4, Object v4) {
        LinkedHashMap<DefaultAttributeConfiguration, Object> m = new LinkedHashMap<DefaultAttributeConfiguration, Object>();
        m.put(a1, v1);
        m.put(a2, v2);
        m.put(a3, v3);
        m.put(a4, v4);
        run(m);
    }

    public static void run(DefaultAttributeConfiguration a1, Object v1,
            DefaultAttributeConfiguration a2, Object v2, DefaultAttributeConfiguration a3,
            Object v3, DefaultAttributeConfiguration a4, Object v4,
            DefaultAttributeConfiguration a5, Object v5) {
        LinkedHashMap<DefaultAttributeConfiguration, Object> m = new LinkedHashMap<DefaultAttributeConfiguration, Object>();
        m.put(a1, v1);
        m.put(a2, v2);
        m.put(a3, v3);
        m.put(a4, v4);
        m.put(a5, v5);
        run(m);
    }

    static void run(LinkedHashMap<DefaultAttributeConfiguration, Object> m) {
        List<DefaultAttributeConfiguration> list = new ArrayList<DefaultAttributeConfiguration>();
        for (Map.Entry<DefaultAttributeConfiguration, Object> i : m.entrySet()) {
            list.add(i.getKey());
        }
        AttributeMap map = null;
        try {
            Class<AttributeMap> c = DefaultMapGenerator.generate("foo" + System.nanoTime(), list);
            map = newInstance(m, c, false);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AssertionError();
        }
        Checker checker = new Checker(map, m);
        checker.run();
    }
}
