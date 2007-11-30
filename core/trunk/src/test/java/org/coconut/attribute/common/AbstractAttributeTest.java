/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.AttributeMaps;
import org.coconut.internal.util.ClassUtils;
import org.coconut.test.TestUtil;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractAttributeTest {

    private final Attribute a;
    private final Object defaultValue;
    private final Class c;

    private final Collection valid;

    private final Collection invalid;

    protected static final Collection<Long> NON_NEGATIV_LONGS = Arrays.asList(0l, 10l,
            Long.MAX_VALUE);

    protected static final Collection<Long> NEGATIV_LONGS = Arrays.asList(Long.MIN_VALUE, -10l,
            -5l, -1l);

    public AbstractAttributeTest(Attribute a, Collection valid, Collection invalid) {
        this.a = a;
        c = a.getAttributeType();
        this.valid = valid;
        this.invalid = invalid;
        try {
            defaultValue = a.getClass().getField("DEFAULT_VALUE").get(null);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    @Test
    public void instance() throws Exception {
        assertSame(a, a.getClass().getField("INSTANCE").get(null));
    }

    @Test
    public void defaultValue() throws Exception {
        assertTrue(defaultValue.getClass().equals(a.getAttributeType())
                || defaultValue.getClass().equals(ClassUtils.fromPrimitive(a.getAttributeType())));
    }

    @Test
    public void name() throws Exception {
        String name = (String) a.getClass().getField("NAME").get(null);
        assertEquals(name, a.getName());
    }

    @Test
    public void serializableAndSingleton() throws IOException, ClassNotFoundException {
        TestUtil.assertIsSerializable(a);
        assertSame(a, TestUtil.serializeAndUnserialize(a));
    }

    protected AttributeMap newMap() {
        return new AttributeMaps.DefaultAttributeMap();
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

    @Test
    public void set() throws Exception {
        Method setMethod = a.getClass().getMethod("set", AttributeMap.class, c);
        assertTrue(setMethod.getReturnType().equals(AttributeMap.class));

        for (Object l : valid) {
            AttributeMap am = newMap();
            if (l instanceof Long) {
                assertEquals(0l, am.getLong(a, 0));
            }
            assertSame(am, setMethod.invoke(null, am, l));
            if (l instanceof Long) {
                assertEquals(l, am.getLong(a, 0));
            }
        }
        for (Object l : invalid) {
            AttributeMap am = newMap();
            try {
                setMethod.invoke(null, am, l);
                throw new AssertionError("Should Throw");
            } catch (InvocationTargetException e) {
                assertTrue(e.getCause() instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void get() throws Exception {
        Method getMethod = a.getClass().getMethod("get", AttributeMap.class);
        assertTrue(getMethod.getReturnType().equals(c));

        assertEquals(defaultValue, getMethod.invoke(null, AttributeMaps.EMPTY_MAP));
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
    public void isValid() {
        isValid(valid.toArray());
        isNotValid(invalid.toArray());
    }

    @Test
    public void singletoninvalid() throws Exception {
        Method singleton = a.getClass().getMethod("singleton", c);
        assertTrue(singleton.getReturnType().equals(AttributeMap.class));
        for (Object l : valid) {
            AttributeMap map = (AttributeMap) singleton.invoke(null, l);
            assertEquals(1, map.size());
            assertTrue(map.containsKey(a));
            assertEquals(l, map.get(a));
            if (l instanceof Long) {
                assertEquals(l, map.getLong(a));
                assertEquals(l, map.getLong(a, 0l));
            }
        }
        for (Object l : invalid) {
            try {
                singleton.invoke(null, l);
                throw new AssertionError("Should Throw");
            } catch (InvocationTargetException e) {
                assertTrue(e.getCause() instanceof IllegalArgumentException);
            }
        }
    }
}
