package org.codehaus.cake.internal.attribute.generator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

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
import org.junit.Ignore;
import org.junit.Test;

public class DefaultMapGeneratorTest {

    private final static LongAttribute L_1 = new LongAttribute("L_1", 1) {};
    private final static LongAttribute L_2 = new LongAttribute("L_2", 2) {};
    private final static LongAttribute L_3 = new LongAttribute("L_3", 3) {};
    private final static LongAttribute L_4 = new LongAttribute("L_4", 1) {};
    private final static LongAttribute L_5 = new LongAttribute("L_5", 2) {};
    private final static IntAttribute I_1 = new IntAttribute("L_1", 1) {};
    private final static IntAttribute I_5 = new IntAttribute("L_5", 2) {};
    private final static DoubleAttribute D_1 = new DoubleAttribute("D_1", 1.5) {};
    private final static ShortAttribute S_4 = new ShortAttribute("S_4", (short) 1) {};
    private final static ByteAttribute B_4 = new ByteAttribute("B_4", (byte) 1) {};
    private final static FloatAttribute F_1 = new FloatAttribute("F_1", 1.5f) {};
    private final static ObjectAttribute<String> O_1 = new ObjectAttribute<String>("O_1",
            String.class, "1.5f") {};
    private final static ObjectAttribute<Map> O_2 = new ObjectAttribute<Map>("O_1", Map.class,
            new HashMap()) {};
    private final static DefaultAttributeConfiguration LI1 = new DefaultAttributeConfiguration(L_1, false, false);
    private final static DefaultAttributeConfiguration LI2 = new DefaultAttributeConfiguration(L_2, false, false);
    private final static DefaultAttributeConfiguration LI3 = new DefaultAttributeConfiguration(L_3, false, true);
    private final static DefaultAttributeConfiguration LI4 = new DefaultAttributeConfiguration(L_4, true, false);
    private final static DefaultAttributeConfiguration LI5 = new DefaultAttributeConfiguration(L_5, true, true);
    private final static DefaultAttributeConfiguration DI1 = new DefaultAttributeConfiguration(D_1, false, false);
    private final static DefaultAttributeConfiguration II1 = new DefaultAttributeConfiguration(I_1, false, false);
    private final static DefaultAttributeConfiguration II5 = new DefaultAttributeConfiguration(I_5, true, true);
    private final static DefaultAttributeConfiguration SI4 = new DefaultAttributeConfiguration(S_4, true, false);
    private final static DefaultAttributeConfiguration BI4 = new DefaultAttributeConfiguration(B_4, true, false);
    private final static DefaultAttributeConfiguration FI1 = new DefaultAttributeConfiguration(F_1, false, false);
    private final static DefaultAttributeConfiguration OI1 = new DefaultAttributeConfiguration(O_1, false, false);
    private final static DefaultAttributeConfiguration OI4 = new DefaultAttributeConfiguration(O_1, true, false);
    private final static DefaultAttributeConfiguration OI5 = new DefaultAttributeConfiguration(O_1, false, true);
    private final static Random r = new Random(3);

    @Test
    public void checkO() {
        Checker.run(OI1, "ddf");
        Checker.run(OI4, "ddf");
        Checker.run(OI5, "ddf");
        Checker.run(new DefaultAttributeConfiguration(O_2, false, false), new HashMap());
        Checker.run(new DefaultAttributeConfiguration(O_2, false, true), new HashMap());
        Checker.run(new DefaultAttributeConfiguration(O_2, true, false), new HashMap());
    }

    @Test
    public void check() {
        Checker.run(FI1, 5f);
        Checker.run(OI1, "ddf");
        Checker.run(II1, 5);
        Checker.run(LI1, 5L);
        Checker.run(LI2, 5L);
        Checker.run(LI3, 5L);
        Checker.run(LI4, 5L);
        Checker.run(LI5, 5L);
        Checker.run(LI1, 1L, LI2, 3L, LI3, 6L, LI4, 3L, LI5, 6L);
        Checker.run(DI1, 5.0d);
        Checker.run(DI1, 5.0d, LI1, 5L, BI4, (byte) 5);
        Checker.run(DI1, 5.0d, II1, 5, LI1, 5L, SI4, (short) 5, II5, 7);
    }

    @Ignore
    @Test
    public void check2() {
        for (int i = 0; i < 100; i++) {
            run(r.nextInt(50));
        }
    }

    @Test
    public void check4() {
        run(10);
        run(10);
        run(10);
        run(10);
    }

    @Test
    @Ignore
    public void check5() {
        LinkedHashMap<DefaultAttributeConfiguration, Object> map = new LinkedHashMap<DefaultAttributeConfiguration, Object>();
        // AttributeInfo info = newInfo(new ObjectAttribute(Map.class, new HashMap()) {});
        // map.put(info, null);
        DefaultAttributeConfiguration info = newInfo(new ObjectAttribute(String.class, "B") {});
        map.put(info, "");
        // info = newInfo(new ObjectAttribute(Long.class, null) {});
        // map.put(info, Long.MIN_VALUE);
        Checker.run(map);
    }

    void run(int count) {
        int same = r.nextInt(30);
        LinkedHashMap<DefaultAttributeConfiguration, Object> map = new LinkedHashMap<DefaultAttributeConfiguration, Object>();
        for (int i = 0; i < count; i++) {
            int next = same <= 8 ? same : r.nextInt(8);
            switch (next) {
            case 0:
                addBoolean(map);
                break;
            case 1:
                addByte(map);
                break;
            case 2:
                addChar(map);
                break;
            case 3:
                addDouble(map);
                break;
            case 4:
                addFloat(map);
                break;
            case 5:
                addInt(map);
                break;
            case 6:
                addLong(map);
                break;
            case 7:
                addShort(map);
                break;
            case 8:
                addObject(map);
                break;
            default:
                System.out.println("Invalid.");
                break;
            }
        }
        Checker.run(map);
    }

    public void addBoolean(Map<DefaultAttributeConfiguration, Object> map) {
        map.put(newInfo(new BooleanAttribute(r.nextBoolean()) {}), r.nextBoolean());
    }

    public void addByte(Map<DefaultAttributeConfiguration, Object> map) {
        byte b = (byte) r.nextInt();
        map.put(newInfo(new ByteAttribute(b) {}), r.nextBoolean() ? b : (byte) r.nextInt());
    }

    public void addChar(Map<DefaultAttributeConfiguration, Object> map) {
        char b = (char) r.nextInt();
        map.put(newInfo(new CharAttribute(b) {}), r.nextBoolean() ? b : (char) r.nextInt());
    }

    public void addDouble(Map<DefaultAttributeConfiguration, Object> map) {
        double b = r.nextDouble();
        map.put(newInfo(new DoubleAttribute(b) {}), r.nextBoolean() ? b : r.nextDouble());
    }

    public void addFloat(Map<DefaultAttributeConfiguration, Object> map) {
        float b = r.nextFloat();
        map.put(newInfo(new FloatAttribute(b) {}), r.nextBoolean() ? b : r.nextFloat());
    }

    public void addInt(Map<DefaultAttributeConfiguration, Object> map) {
        int b = r.nextInt();
        map.put(newInfo(new IntAttribute(b) {}), r.nextBoolean() ? b : r.nextInt());
    }

    public void addLong(Map<DefaultAttributeConfiguration, Object> map) {
        long b = r.nextLong();
        map.put(newInfo(new LongAttribute(b) {}), r.nextBoolean() ? b : r.nextLong());
    }

    public void addShort(Map<DefaultAttributeConfiguration, Object> map) {
        short b = (short) r.nextInt();
        map.put(newInfo(new ShortAttribute(b) {}), r.nextBoolean() ? b : (short) r.nextInt());
    }

    public void addObject(Map<DefaultAttributeConfiguration, Object> map) {
        Class type = null;
        Object defaultValue = null;
        Object value = null;
        int next = r.nextInt(3);
        switch (next) {
        case 0:
            type = String.class;
            defaultValue = chooseRandom(null, "", "A", "B");
            value = chooseRandom(null, "", "A", "B");
            break;
        case 1:
            type = Long.class;
            defaultValue = chooseRandom(null, 1L, Long.MAX_VALUE, Long.MIN_VALUE);
            value = chooseRandom(null, 1L, Long.MAX_VALUE, Long.MIN_VALUE);
            break;
        case 2:
            type = Map.class;
            defaultValue = chooseRandom(null, new LinkedHashMap(), new HashMap());
            value = chooseRandom(null, new LinkedHashMap(), new HashMap());
            break;
        default:
            throw new AssertionError();
        }
        DefaultAttributeConfiguration info = newInfo(new ObjectAttribute(type, defaultValue) {});
        // System.err.println(info + ", " + value);
        map.put(info, r.nextBoolean() ? defaultValue : value);
        // System.out.println(info + "|||'" + map.get(info) + "'");
    }

    static Object chooseRandom(Object... objects) {
        int n = r.nextInt(objects.length);
        return objects[n];
    }

    DefaultAttributeConfiguration newInfo(Attribute<?> a) {
        return new DefaultAttributeConfiguration(a, r.nextBoolean(), r.nextBoolean());
    }

}
