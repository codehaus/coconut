/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.codehaus.cake.attribute.AttributeMap;
import org.codehaus.cake.attribute.Attributes;
import org.codehaus.cake.attribute.DefaultAttributeMap;
import org.codehaus.cake.attribute.DurationAttribute;
import org.junit.Test;

public class DurationAttributeTest {
    static final DurationAttribute DA = new DurationAttribute("foo") {};

    @Test
    public void isValid() {
        assertFalse(DA.isValid(Long.MIN_VALUE));
        assertFalse(DA.isValid(0));
        assertTrue(DA.isValid(1));
        assertTrue(DA.isValid(Long.MAX_VALUE));
    }

    @Test
    public void checkValid() {
        assertTrue(DA.isValid(1));
        assertTrue(DA.isValid(Long.MAX_VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkValidIAE() {
        DA.checkValid(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkValidIAE1() {
        DA.checkValid(Long.MIN_VALUE);
    }

    @Test
    public void get() {
        AttributeMap am = Attributes.EMPTY_ATTRIBUTE_MAP;
        AttributeMap am1 = Attributes.singleton(DA, 1L);
        AttributeMap am10000 = Attributes.singleton(DA, 10000L);
        AttributeMap ammax = Attributes.singleton(DA, Long.MAX_VALUE);

        // No default
        assertEquals(Long.MAX_VALUE, DA.getValue(am, TimeUnit.NANOSECONDS));
        assertEquals(1l, DA.getValue(am1, TimeUnit.NANOSECONDS));
        assertEquals(10000l, DA.getValue(am10000, TimeUnit.NANOSECONDS));
        assertEquals(Long.MAX_VALUE, DA.getValue(ammax, TimeUnit.NANOSECONDS));

        assertEquals(Long.MAX_VALUE, DA.getValue(am, TimeUnit.MICROSECONDS));
        assertEquals(0l, DA.getValue(am1, TimeUnit.MICROSECONDS));
        assertEquals(10l, DA.getValue(am10000, TimeUnit.MICROSECONDS));
        assertEquals(Long.MAX_VALUE, DA.getValue(ammax, TimeUnit.MICROSECONDS));

        // With default
        assertEquals(1l, DA.getValue(am, TimeUnit.NANOSECONDS, 1l));
        assertEquals(-1l, DA.getValue(am, TimeUnit.NANOSECONDS, -1l));
        assertEquals(1l, DA.getValue(am, TimeUnit.MICROSECONDS, 1l));
        assertEquals(Long.MAX_VALUE, DA.getValue(am, TimeUnit.MICROSECONDS, Long.MAX_VALUE));

        assertEquals(1l, DA.getValue(am1, TimeUnit.NANOSECONDS, 2));
        assertEquals(10000l, DA.getValue(am10000, TimeUnit.NANOSECONDS, 2));
        assertEquals(Long.MAX_VALUE, DA.getValue(ammax, TimeUnit.NANOSECONDS, 2));
        assertEquals(0l, DA.getValue(am1, TimeUnit.MICROSECONDS, 2));
        assertEquals(10l, DA.getValue(am10000, TimeUnit.MICROSECONDS, 2));
        assertEquals(Long.MAX_VALUE, DA.getValue(ammax, TimeUnit.MICROSECONDS, 2));
    }

    @Test
    public void set() {
        AttributeMap am = newMap();
        assertEquals(10l, DA.set(am, 10l, TimeUnit.NANOSECONDS).get(DA));
        assertEquals(10000l, DA.set(am, 10l, TimeUnit.MICROSECONDS).get(DA));
        assertEquals(10000l, DA.set(am, Long.valueOf(10), TimeUnit.MICROSECONDS).get(DA));
        assertEquals(Long.MAX_VALUE, DA.set(am, Long.MAX_VALUE, TimeUnit.MICROSECONDS)
                .get(DA));
    }

    @Test
    public void toSingleton() {
        assertEquals(10l, DA.singleton(10, TimeUnit.NANOSECONDS).get(DA));
        assertEquals(10000l, DA.singleton(10, TimeUnit.MICROSECONDS).get(DA));
        assertEquals(Long.MAX_VALUE, DA.singleton(Long.MAX_VALUE, TimeUnit.MICROSECONDS).get(DA));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toSingletonIAE() {
        DA.singleton(-10, TimeUnit.NANOSECONDS);
    }

    protected AttributeMap newMap() {
        return new DefaultAttributeMap();
    }
}
