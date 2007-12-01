package org.coconut.attribute.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.AttributeMaps;
import org.junit.Ignore;
import org.junit.Test;

public class DurationAttributeTest {
    static final AbstractDurationAttribute DA = new AbstractDurationAttribute("foo") {};

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
        AttributeMap am = AttributeMaps.EMPTY_MAP;
        AttributeMap am1 = AttributeMaps.singleton(DA, 1L);
        AttributeMap am10000 = AttributeMaps.singleton(DA, 10000L);
        AttributeMap ammax = AttributeMaps.singleton(DA, Long.MAX_VALUE);

        // No default
        assertEquals(Long.MAX_VALUE, DA.getPrimitive(am, TimeUnit.NANOSECONDS));
        assertEquals(1l, DA.getPrimitive(am1, TimeUnit.NANOSECONDS));
        assertEquals(10000l, DA.getPrimitive(am10000, TimeUnit.NANOSECONDS));
        assertEquals(Long.MAX_VALUE, DA.getPrimitive(ammax, TimeUnit.NANOSECONDS));

        assertEquals(Long.MAX_VALUE, DA.getPrimitive(am, TimeUnit.MICROSECONDS));
        assertEquals(0l, DA.getPrimitive(am1, TimeUnit.MICROSECONDS));
        assertEquals(10l, DA.getPrimitive(am10000, TimeUnit.MICROSECONDS));
        assertEquals(Long.MAX_VALUE, DA.getPrimitive(ammax, TimeUnit.MICROSECONDS));

        // With default
        assertEquals(1l, DA.getPrimitive(am, TimeUnit.NANOSECONDS, 1l));
        assertEquals(-1l, DA.getPrimitive(am, TimeUnit.NANOSECONDS, -1l));
        assertEquals(1l, DA.getPrimitive(am, TimeUnit.MICROSECONDS, 1l));
        assertEquals(Long.MAX_VALUE, DA.getPrimitive(am, TimeUnit.MICROSECONDS, Long.MAX_VALUE));

        assertEquals(1l, DA.getPrimitive(am1, TimeUnit.NANOSECONDS, 2));
        assertEquals(10000l, DA.getPrimitive(am10000, TimeUnit.NANOSECONDS, 2));
        assertEquals(Long.MAX_VALUE, DA.getPrimitive(ammax, TimeUnit.NANOSECONDS, 2));
        assertEquals(0l, DA.getPrimitive(am1, TimeUnit.MICROSECONDS, 2));
        assertEquals(10l, DA.getPrimitive(am10000, TimeUnit.MICROSECONDS, 2));
        assertEquals(Long.MAX_VALUE, DA.getPrimitive(ammax, TimeUnit.MICROSECONDS, 2));
    }

    @Test
    public void set() {
        AttributeMap am = newMap();
        assertEquals(10l, DA.setAttribute(am, 10l, TimeUnit.NANOSECONDS).get(DA));
        assertEquals(10000l, DA.setAttribute(am, 10l, TimeUnit.MICROSECONDS).get(DA));
        assertEquals(10000l, DA.setAttribute(am, new Long(10), TimeUnit.MICROSECONDS).get(DA));
        assertEquals(Long.MAX_VALUE, DA.setAttribute(am, Long.MAX_VALUE, TimeUnit.MICROSECONDS)
                .get(DA));
    }

    @Test
    public void toSingleton() {
        assertEquals(10l, DA.toSingleton(10, TimeUnit.NANOSECONDS).get(DA));
        assertEquals(10000l, DA.toSingleton(10, TimeUnit.MICROSECONDS).get(DA));
        assertEquals(Long.MAX_VALUE, DA.toSingleton(Long.MAX_VALUE, TimeUnit.MICROSECONDS).get(DA));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toSingletonIAE() {
        DA.toSingleton(-10, TimeUnit.NANOSECONDS);
    }

    protected AttributeMap newMap() {
        return new AttributeMaps.DefaultAttributeMap();
    }
}
