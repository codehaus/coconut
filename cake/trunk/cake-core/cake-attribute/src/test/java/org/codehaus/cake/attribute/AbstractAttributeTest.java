/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;

public abstract class AbstractAttributeTest {

    protected static final Collection<Long> NEGATIV_LONGS = Arrays.asList(Long.MIN_VALUE, -10l,
            -5l, -1l);

    protected static final Collection<Long> NON_NEGATIV_LONGS = Arrays.asList(0l, 1L, 10l,
            Long.MAX_VALUE);

    protected static final Collection<Long> NON_POSITIVE_LONGS = Arrays.asList(Long.MIN_VALUE,
            -10l, -5l, -1l, 0L);

    protected static final Collection<Long> POSITIV_LONGS = Arrays.asList(1L, 10l, Long.MAX_VALUE);

    final Attribute a;

    final Class c;

    final Object defaultValue;

    final Collection invalid;

    final Collection valid;

    public AbstractAttributeTest(Attribute a, Collection valid, Collection invalid) {
        this.a = a;
        c = a.getType();
        this.valid = valid;
        this.invalid = invalid;
        defaultValue=a.getDefault();
//        try {
//            defaultValue = a.getClass().getField("DEFAULT_VALUE").get(null);
//        } catch (Exception e) {
//            throw new AssertionError(e);
//        }
    }

    @Test
    public void defaultValue() throws Exception {
        assertTrue(defaultValue.getClass().equals(a.getType())
                || defaultValue.getClass().equals(fromPrimitive(a.getType())));
    }

    @Test
    public void get() throws Exception {
        Method getMethod = null;
        try {
            getMethod = a.getClass().getMethod("get", AttributeMap.class);
        } catch (NoSuchMethodException e) {
            getMethod = a.getClass().getMethod("get" + capitalize(a.getName()),
                    AttributeMap.class);
        }

        assertTrue(getMethod.getReturnType().equals(c));

        assertEquals(defaultValue, getMethod.invoke(null, Attributes.EMPTY_ATTRIBUTE_MAP));
        for (Object l : valid) {
            AttributeMap am = newMap();
            am.put(a, l);
            assertEquals(l, getMethod.invoke(null, am));
        }
        // no validation for invalid attributes
        for (Object l : invalid) {
            AttributeMap am = newMap();
            am.put(a, l);
            assertEquals(l, getMethod.invoke(null, am));
        }
    }

    @Test
    public void instance() throws Exception {
        assertSame(a, a.getClass().getField("INSTANCE").get(null));
    }

    @Test
    public void isValid() {
        isValid(valid.toArray());
        isNotValid(invalid.toArray());
    }

    @Test
    public void name() throws Exception {
        String name = (String) a.getClass().getField("NAME").get(null);
        assertEquals(name, a.getName());
    }

    @Test
    public void serializableAndPreservesSingletonProperty()  {
        TestUtil.assertIsSerializable(a);
        assertSame(a, TestUtil.serializeAndUnserialize(a));
    }

    protected Attribute a() {
        return a;
    }

    protected void isNotValid(Object... o) {
        for (Object oo : o) {
            assertFalse(a.isValid(oo));
            try {
                a.checkValid(oo);
                throw new AssertionError("Should fail");
            } catch (IllegalArgumentException ok) {/* ignore */}
        }
    }

    protected void isValid(Object... o) {
        for (Object oo : o) {
            assertTrue(a.isValid(oo));
            a.checkValid(oo);
        }
    }

    protected AttributeMap newMap() {
        return new DefaultAttributeMap();
    }
    
    static String capitalize(String capitalizeMe) {
        return capitalizeMe.substring(0, 1).toUpperCase()
                + capitalizeMe.substring(1);
    }
     static Class fromPrimitive(Class c) {
        if (c.equals(Integer.TYPE)) {
            return Integer.class;
        } else if (c.equals(Double.TYPE)) {
            return Double.class;
        } else if (c.equals(Byte.TYPE)) {
            return Byte.class;
        } else if (c.equals(Float.TYPE)) {
            return Float.class;
        } else if (c.equals(Long.TYPE)) {
            return Long.class;
        } else if (c.equals(Short.TYPE)) {
            return Short.class;
        } else if (c.equals(Boolean.TYPE)) {
            return Boolean.class;
        } else if (c.equals(Character.TYPE)) {
            return Character.class;
        } else if (c.equals(Void.TYPE)) {
            return Void.class;
        } else {
            return c;
        }
    }
}
