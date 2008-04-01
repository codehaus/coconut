/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.codehaus.cake.internal.util.ArrayUtils;
import org.junit.Test;

public class ArrayUtilsTest {

    @Test
    public void test() {
        Integer[] i = new Integer[] { 1, 2, 3 };
        Integer[] i2 = ArrayUtils.copyOf(i);
        assertTrue(Arrays.equals(i, i2));
        assertEquals(3, i2.length);
    }

    @Test
    public void test1() {
        Integer[] i = new Integer[] { 1, 2, 3 };
        Integer[] i2 = ArrayUtils.copyOf(i, 4);
        assertFalse(Arrays.equals(i, i2));
        assertEquals(4, i2.length);
        assertTrue(Arrays.equals(i, ArrayUtils.copyOf(i2, 3)));
    }

    @Test
    public void test2() {
        Integer[] i = new Integer[] { 1, 2, 3 };
        Object[] i2 = ArrayUtils.copyOf(i, 3, Object[].class);
        assertTrue(Arrays.equals(i, i2));
    }

    @Test
    public void test3() {
        Integer[] i = new Integer[] { 1, 2, 3 };
        Number[] i2 = ArrayUtils.copyOf(i, 3, Number[].class);
        assertTrue(Arrays.equals(i, i2));
    }

    @Test
    public void test4() {
        Integer[] i = new Integer[] { 1, 2, 3 };
        Integer[] i2 = ArrayUtils.copyOf(i, 3, Integer[].class);
        assertTrue(Arrays.equals(i, i2));
    }

    @Test
    public void reverse() {
        Integer[] i3 = new Integer[] { 1, 2, 3 };
        Integer[] i4 = new Integer[] { 1, 2, 3, 4 };
        ArrayUtils.reverse(i3);
        ArrayUtils.reverse(i4);
        assertTrue(i3[0] == 3 && i3[1] == 2 && i3[2] == 1);
        assertTrue(i4[0] == 4 && i4[1] == 3 && i4[2] == 2 && i4[3] == 1);
    }
}
