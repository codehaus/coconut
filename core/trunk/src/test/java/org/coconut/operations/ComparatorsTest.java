/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import static org.coconut.operations.Comparators.DOUBLE_COMPARATOR;
import static org.coconut.operations.Comparators.INT_COMPARATOR;
import static org.coconut.operations.Comparators.LONG_COMPARATOR;
import static org.coconut.operations.Comparators.NATURAL_COMPARATOR;
import static org.coconut.test.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.coconut.test.TestUtil;
import org.junit.Test;

public class ComparatorsTest {

    /**
     * Tests {@link Comparators#NATURAL_COMPARATOR}.
     */
    @Test
    public void naturalComparator() {
        assertEquals(0, NATURAL_COMPARATOR.compare(1, 1));
        assertTrue(NATURAL_COMPARATOR.compare(2, 1) > 0);
        assertTrue(NATURAL_COMPARATOR.compare(1, 2) < 0);
        assertSame(NATURAL_COMPARATOR, Comparators.<Integer> naturalComparator());
        NATURAL_COMPARATOR.toString(); // does not fail
        assertIsSerializable(Comparators.<Integer> naturalComparator());
        assertSame(NATURAL_COMPARATOR, TestUtil.serializeAndUnserialize(NATURAL_COMPARATOR));
    }

    /**
     * Tests {@link Comparators#INT_COMPARATOR}.
     */
    @Test
    public void intComparator() {
        assertEquals(0, INT_COMPARATOR.compare(1, 1));
        assertTrue(INT_COMPARATOR.compare(2, 1) > 0);
        assertTrue(INT_COMPARATOR.compare(1, 2) < 0);
        INT_COMPARATOR.toString(); // does not fail
        assertIsSerializable(INT_COMPARATOR);
        assertSame(INT_COMPARATOR, TestUtil.serializeAndUnserialize(INT_COMPARATOR));
    }

    /**
     * Tests {@link Comparators#LONG_COMPARATOR}.
     */
    @Test
    public void longComparator() {
        assertEquals(0, LONG_COMPARATOR.compare(1, 1));
        assertTrue(LONG_COMPARATOR.compare(2, 1) > 0);
        assertTrue(LONG_COMPARATOR.compare(1, 2) < 0);
        LONG_COMPARATOR.toString(); // does not fail
        assertIsSerializable(LONG_COMPARATOR);
        assertSame(LONG_COMPARATOR, TestUtil.serializeAndUnserialize(LONG_COMPARATOR));
    }

    /**
     * Tests {@link Comparators#DOUBLE_COMPARATOR}.
     */
    @Test
    public void doubleComparator() {
        assertEquals(0, DOUBLE_COMPARATOR.compare(1, 1));
        assertEquals(0, DOUBLE_COMPARATOR.compare(Double.NaN, Double.NaN));
        assertTrue(DOUBLE_COMPARATOR.compare(2, 1) > 0);
        assertTrue(DOUBLE_COMPARATOR.compare(1, 2) < 0);
        DOUBLE_COMPARATOR.toString(); // does not fail
        assertIsSerializable(DOUBLE_COMPARATOR);
        assertSame(DOUBLE_COMPARATOR, TestUtil.serializeAndUnserialize(DOUBLE_COMPARATOR));
    }
}
