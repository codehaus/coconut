/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.ops;

import static org.codehaus.cake.ops.Comparators.DOUBLE_COMPARATOR;
import static org.codehaus.cake.ops.Comparators.DOUBLE_REVERSE_COMPARATOR;
import static org.codehaus.cake.ops.Comparators.INT_COMPARATOR;
import static org.codehaus.cake.ops.Comparators.INT_REVERSE_COMPARATOR;
import static org.codehaus.cake.ops.Comparators.LONG_COMPARATOR;
import static org.codehaus.cake.ops.Comparators.LONG_REVERSE_COMPARATOR;
import static org.codehaus.cake.ops.Comparators.NATURAL_COMPARATOR;
import static org.codehaus.cake.ops.Comparators.NATURAL_REVERSE_COMPARATOR;
import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.codehaus.cake.test.util.TestUtil.dummy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.codehaus.cake.ops.Ops.DoubleComparator;
import org.codehaus.cake.ops.Ops.IntComparator;
import org.codehaus.cake.ops.Ops.LongComparator;
import org.codehaus.cake.ops.Ops.Op;
import org.codehaus.cake.test.util.TestUtil;
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
     *
     */
    @Test
    public void mappedComparator() {
        Collection<Integer> c1 = Arrays.asList(1);
        Collection<Integer> c2 = Arrays.asList(1, 2);

        Comparator c = Comparators.mappedComparator(new CollectionToSizeMapper());
        assertEquals(0, c.compare(c1, Arrays.asList("A")));
        assertTrue(c.compare(c2, c1) > 0);
        assertTrue(c.compare(c1, c2) < 0);
        c.toString(); // does not fail
        assertIsSerializable(c);
    }

    @Test(expected = NullPointerException.class)
    public void mappedComparator_NPE() {
        Comparators.mappedComparator((Op) null);
    }

    @Test(expected = NullPointerException.class)
    public void mappedComparator_NPE1() {
        Comparators.mappedComparator(null, dummy(Comparator.class));
    }

    @Test(expected = NullPointerException.class)
    public void mappedComparator_NPE2() {
        Comparators.mappedComparator(dummy(Op.class), null);
    }

    @Test
    public void mappedComparatorComparator() {
        Collection<Integer> c1 = Arrays.asList(1);
        Collection<Integer> c2 = Arrays.asList(1, 2);

        Comparator c = Comparators.mappedComparator(new CollectionToSizeMapper(),
                NATURAL_REVERSE_COMPARATOR);
        assertEquals(0, c.compare(c1, Arrays.asList("A")));
        assertTrue(c.compare(c2, c1) < 0);
        assertTrue(c.compare(c1, c2) > 0);
        c.toString(); // does not fail
        assertIsSerializable(c);
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
     * Tests {@link Comparators#nullGreatestOrder()}.
     */
    @Test
    public void nullGreatestOrder() {
        Comparator<Integer> c = Comparators.nullGreatestOrder();
        assertEquals(0, c.compare(null, null));
        assertEquals(0, c.compare(Integer.MIN_VALUE, Integer.MIN_VALUE));
        assertTrue(c.compare(null, Integer.MAX_VALUE) > 0);
        assertTrue(c.compare(Integer.MAX_VALUE, null) < 0);
        assertSame(c, Comparators.NULL_GREATEST_ORDER);
        c.toString(); // does not fail
        assertIsSerializable(c);
        assertSame(c, TestUtil.serializeAndUnserialize(Comparators.nullGreatestOrder()));
    }

    /**
     * Tests {@link Comparators#nullGreatestOrder(Comparator)}.
     */
    @Test
    public void nullGreatestOrderComparator() {
        Comparator<Integer> c = Comparators
                .nullGreatestOrder(Comparators.NATURAL_REVERSE_COMPARATOR);
        assertEquals(0, c.compare(null, null));
        assertEquals(0, c.compare(Integer.MIN_VALUE, Integer.MIN_VALUE));
        assertTrue(c.compare(2, 1) < 0);
        assertTrue(c.compare(1, 2) > 0);
        assertTrue(c.compare(null, Integer.MIN_VALUE) > 0);
        assertTrue(c.compare(Integer.MIN_VALUE, null) < 0);
        c.toString(); // does not fail
        assertIsSerializable(c);
    }

    @Test(expected = NullPointerException.class)
    public void nullGreatestOrderComparator_NPE() {
        Comparators.nullGreatestOrder(null);
    }

    /**
     * Tests {@link Comparators#nullLeastOrder()}.
     */
    @Test
    public void nullLeastOrder() {
        Comparator<Integer> c = Comparators.nullLeastOrder();
        assertEquals(0, c.compare(null, null));
        assertEquals(0, c.compare(Integer.MIN_VALUE, Integer.MIN_VALUE));
        assertTrue(c.compare(null, Integer.MIN_VALUE) < 0);
        assertTrue(c.compare(Integer.MIN_VALUE, null) > 0);
        assertSame(c, Comparators.NULL_LEAST_ORDER);
        c.toString(); // does not fail
        assertIsSerializable(c);
        assertSame(c, TestUtil.serializeAndUnserialize(Comparators.nullLeastOrder()));
    }

    /**
     * Tests {@link Comparators#nullLeastOrder(Comparator)}.
     */
    @Test
    public void nullLeastOrderComparator() {
        Comparator<Integer> c = Comparators.nullLeastOrder(Comparators.NATURAL_REVERSE_COMPARATOR);
        assertEquals(0, c.compare(null, null));
        assertEquals(0, c.compare(Integer.MIN_VALUE, Integer.MIN_VALUE));
        assertTrue(c.compare(2, 1) < 0);
        assertTrue(c.compare(1, 2) > 0);
        assertTrue(c.compare(null, Integer.MAX_VALUE) < 0);
        assertTrue(c.compare(Integer.MAX_VALUE, null) > 0);
        c.toString(); // does not fail
        assertIsSerializable(c);
    }

    @Test(expected = NullPointerException.class)
    public void nullLeastOrderComparator_NPE() {
        Comparators.nullLeastOrder(null);
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
// assertSame(NATURAL_REVERSE_COMPARATOR, TestUtil
// .serializeAndUnserialize(NATURAL_REVERSE_COMPARATOR));
    }

    @Test(expected = NullPointerException.class)
    public void reverseOrder_NPE() {
        Comparators.reverseOrder((Comparator) null);
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
    public void reverseOrderDouble_NPE() {
        Comparators.reverseOrder((DoubleComparator) null);
    }

    @Test(expected = NullPointerException.class)
    public void reverseOrderInt_NPE() {
        Comparators.reverseOrder((IntComparator) null);
    }

    @Test(expected = NullPointerException.class)
    public void reverseOrderLong_NPE() {
        Comparators.reverseOrder((LongComparator) null);
    }

    static class CollectionToSizeMapper implements Op<Collection, Integer>, Serializable {
        public Integer op(Collection t) {
            return t.size();
        }
    };
}
