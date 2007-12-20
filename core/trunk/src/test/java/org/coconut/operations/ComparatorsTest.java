/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import static org.coconut.operations.Comparators.DOUBLE_COMPARATOR;
import static org.coconut.operations.Comparators.DOUBLE_REVERSE_COMPARATOR;
import static org.coconut.operations.Comparators.INT_COMPARATOR;
import static org.coconut.operations.Comparators.INT_REVERSE_COMPARATOR;
import static org.coconut.operations.Comparators.LONG_COMPARATOR;
import static org.coconut.operations.Comparators.LONG_REVERSE_COMPARATOR;
import static org.coconut.operations.Comparators.NATURAL_COMPARATOR;
import static org.coconut.operations.Comparators.NATURAL_REVERSE_COMPARATOR;
import static org.coconut.test.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.coconut.operations.Ops.DoubleComparator;
import org.coconut.operations.Ops.IntComparator;
import org.coconut.operations.Ops.LongComparator;
import org.coconut.test.TestUtil;
import org.junit.Test;

public class ComparatorsTest {

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

    /**
     * Tests {@link Comparators#DOUBLE_REVERSE_COMPARATOR}.
     */
    @Test
    public void doubleReverseComparator() {
        assertEquals(0, DOUBLE_REVERSE_COMPARATOR.compare(1, 1));
        assertEquals(0, DOUBLE_REVERSE_COMPARATOR.compare(Double.NaN, Double.NaN));
        assertTrue(DOUBLE_REVERSE_COMPARATOR.compare(2, 1) < 0);
        assertTrue(DOUBLE_REVERSE_COMPARATOR.compare(1, 2) > 0);
        DOUBLE_REVERSE_COMPARATOR.toString(); // does not fail
        assertIsSerializable(DOUBLE_REVERSE_COMPARATOR);
        assertSame(DOUBLE_REVERSE_COMPARATOR, TestUtil
                .serializeAndUnserialize(DOUBLE_REVERSE_COMPARATOR));
    }

    /**
     * Tests {@link Comparators#DOUBLE_REVERSE_COMPARATOR}.
     */
    @Test
    public void doubleReverseComparatorComp() {
        assertEquals(0, Comparators.reverseOrder(DOUBLE_COMPARATOR).compare(1, 1));
        assertEquals(0, Comparators.reverseOrder(DOUBLE_COMPARATOR).compare(Double.NaN, Double.NaN));
        assertTrue(Comparators.reverseOrder(DOUBLE_COMPARATOR).compare(2, 1) < 0);
        assertTrue(Comparators.reverseOrder(DOUBLE_COMPARATOR).compare(1, 2) > 0);
        Comparators.reverseOrder(DOUBLE_COMPARATOR).toString(); // does not fail
        assertIsSerializable(Comparators.reverseOrder(DOUBLE_COMPARATOR));

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
     * Tests {@link Comparators#INT_REVERSE_COMPARATOR}.
     */
    @Test
    public void intReverseComparator() {
        assertEquals(0, INT_REVERSE_COMPARATOR.compare(1, 1));
        assertTrue(INT_REVERSE_COMPARATOR.compare(2, 1) < 0);
        assertTrue(INT_REVERSE_COMPARATOR.compare(1, 2) > 0);
        INT_REVERSE_COMPARATOR.toString(); // does not fail
        assertIsSerializable(INT_REVERSE_COMPARATOR);
        assertSame(INT_REVERSE_COMPARATOR, TestUtil.serializeAndUnserialize(INT_REVERSE_COMPARATOR));
    }

    /**
     * Tests {@link Comparators#INT_REVERSE_COMPARATOR}.
     */
    @Test
    public void intReverseComparatorComp() {
        assertEquals(0, Comparators.reverseOrder(INT_COMPARATOR).compare(1, 1));
        assertTrue(Comparators.reverseOrder(INT_COMPARATOR).compare(2, 1) < 0);
        assertTrue(Comparators.reverseOrder(INT_COMPARATOR).compare(1, 2) > 0);
        Comparators.reverseOrder(INT_COMPARATOR).toString(); // does not fail
        assertIsSerializable(Comparators.reverseOrder(INT_COMPARATOR));
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
     * Tests {@link Comparators#LONG_REVERSE_COMPARATOR}.
     */
    @Test
    public void longReverseComparator() {
        assertEquals(0, LONG_REVERSE_COMPARATOR.compare(1, 1));
        assertTrue(LONG_REVERSE_COMPARATOR.compare(2, 1) < 0);
        assertTrue(LONG_REVERSE_COMPARATOR.compare(1, 2) > 0);
        LONG_REVERSE_COMPARATOR.toString(); // does not fail
        assertIsSerializable(LONG_REVERSE_COMPARATOR);
        assertSame(LONG_REVERSE_COMPARATOR, TestUtil
                .serializeAndUnserialize(LONG_REVERSE_COMPARATOR));
    }

    /**
     * Tests {@link Comparators#reverseOrder(LongComparator)}.
     */
    @Test
    public void longReverseComparatorComp() {
        assertEquals(0, Comparators.reverseOrder(LONG_COMPARATOR).compare(1, 1));
        assertTrue(Comparators.reverseOrder(LONG_COMPARATOR).compare(2, 1) < 0);
        assertTrue(Comparators.reverseOrder(LONG_COMPARATOR).compare(1, 2) > 0);
        Comparators.reverseOrder(LONG_COMPARATOR).toString(); // does not fail
        assertIsSerializable(Comparators.reverseOrder(LONG_COMPARATOR));
    }

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
     * Tests {@link Comparators#reverseOrder()}.
     */
    @Test
    public void reverseOrder() {
        assertEquals(0, NATURAL_REVERSE_COMPARATOR.compare(1, 1));
        assertTrue(NATURAL_REVERSE_COMPARATOR.compare(2, 1) < 0);
        assertTrue(NATURAL_REVERSE_COMPARATOR.compare(1, 2) > 0);
        assertSame(NATURAL_REVERSE_COMPARATOR, Comparators.<Integer> reverseOrder());
        NATURAL_REVERSE_COMPARATOR.toString(); // does not fail
        assertIsSerializable(Comparators.<Integer> reverseOrder());
        assertSame(NATURAL_REVERSE_COMPARATOR, TestUtil
                .serializeAndUnserialize(NATURAL_REVERSE_COMPARATOR));
    }

    /**
     * Tests {@link Comparators#reverseOrder(Comparator)}.
     */
    @Test
    public void reverseOrderComparator() {
        assertEquals(0, Comparators.reverseOrder(NATURAL_COMPARATOR).compare(1, 1));
        assertTrue(Comparators.reverseOrder(NATURAL_COMPARATOR).compare(2, 1) < 0);
        assertTrue(Comparators.reverseOrder(NATURAL_COMPARATOR).compare(1, 2) > 0);
        Comparators.reverseOrder(NATURAL_COMPARATOR).toString(); // does not fail
        assertIsSerializable(Comparators.reverseOrder(NATURAL_COMPARATOR));
    }

    @Test(expected = NullPointerException.class)
    public void reverseOrderDoubleNPE() {
        Comparators.reverseOrder((DoubleComparator) null);
    }

    @Test(expected = NullPointerException.class)
    public void reverseOrderIntNPE() {
        Comparators.reverseOrder((IntComparator) null);
    }

    @Test(expected = NullPointerException.class)
    public void reverseOrderLongNPE() {
        Comparators.reverseOrder((LongComparator) null);
    }

    @Test(expected = NullPointerException.class)
    public void reverseOrderNPE() {
        Comparators.reverseOrder((Comparator) null);
    }
}
