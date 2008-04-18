/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://cake.codehaus.org/LICENSE
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.ops;

import static org.codehaus.cake.ops.DoublePredicates.*;
import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.*;

import org.codehaus.cake.ops.DoublePredicates.*;
import org.codehaus.cake.ops.Ops.*;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;
/**
 * Various tests for {@link DoublePredicates}.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: DoublePredicatesTest.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class DoublePredicatesTest {

    /**
     * Tests {@link LongPredicates#and(LongPredicate, LongPredicate)}.
     */
    @Test
    public void and() {
        assertTrue(DoublePredicates.and(TRUE, TRUE).op(1D));
        assertFalse(DoublePredicates.and(TRUE, FALSE).op(1D));
        assertFalse(DoublePredicates.and(FALSE, TRUE).op(1D));
        assertFalse(DoublePredicates.and(FALSE, FALSE).op(1D));

        DoublePredicates.AndDoublePredicate p = new DoublePredicates.AndDoublePredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        DoublePredicates.and(FALSE, TestUtil.dummy(DoublePredicate.class)).op(1D);
    }

    /**
     * Tests that {@link DoublePredicates#and(DoublePredicate, DoublePredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE() {
        DoublePredicates.and(null, TRUE);
    }

    /**
     * Tests that {@link DoublePredicates#and(DoublePredicate, DoublePredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE1() {
        DoublePredicates.and(TRUE, null);
    }
    
    /* Test greater then */
    @Test
    public void equalsTo() {
        DoublePredicate f = DoublePredicates.equalsTo(5D);
        assertEquals(5D, new EqualsToDoublePredicate(5D).getEqualsTo(),0);
        assertFalse(f.op(4D));
        assertTrue(f.op(5D));
        assertFalse(f.op(6D));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }
    
    /**
     * Tests {@link DoublePredicates#FALSE}.
     */
    @Test
    public void falsePredicate() {
        assertFalse(FALSE.op(2D));
        assertFalse(FALSE.op(Double.MIN_VALUE));
        FALSE.toString(); // does not fail
        assertIsSerializable(FALSE);
        assertSame(FALSE, TestUtil.serializeAndUnserialize(FALSE));
    }
   
    /* Test greater then */
    @Test
    public void greaterThen() {
        DoublePredicate f = DoublePredicates.greaterThen(5D);
        assertEquals(5D, new GreaterThenDoublePredicate(5D).getGreaterThen(),0);
        assertFalse(f.op(4D));
        assertFalse(f.op(5D));
        assertTrue(f.op(6D));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    @Test
    public void greaterThenOrEquals() {
        DoublePredicate f = DoublePredicates.greaterThenOrEquals(5D);
        assertEquals(5D, new GreaterThenOrEqualsDoublePredicate(5D).getGreaterThenOrEquals(),0);
        assertFalse(f.op(4D));
        assertTrue(f.op(5D));
        assertTrue(f.op(6D));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThen() {
        DoublePredicate f = DoublePredicates.lessThen(5D);
        assertEquals(5D, new LessThenDoublePredicate(5D).getLessThen(),0);
        assertTrue(f.op(4D));
        assertFalse(f.op(5D));
        assertFalse(f.op(6D));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThenOrEquals() {
        DoublePredicate f = DoublePredicates.lessThenOrEquals(5D);
        assertEquals(5D, new LessThenOrEqualsDoublePredicate(5D).getLessThenOrEquals(),0);
        assertTrue(f.op(4D));
        assertTrue(f.op(5D));
        assertFalse(f.op(6D));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }
   
   
     /**
     * Tests that {@link DoublePredicates#not(DoublePredicate)} throws a
     * {@link NullPointerException} when invoked with a <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void notNPE() {
        DoublePredicates.not(null);
    }

    /**
     * Tests {@link DoublePredicates#TRUE}.
     */
    @Test
    public void notPredicate() {
        assertFalse(DoublePredicates.not(TRUE).op(2D));
        assertTrue(DoublePredicates.not(FALSE).op(2D));
        DoublePredicates.not(TRUE).toString(); // does not fail
        assertIsSerializable(DoublePredicates.not(TRUE));
        assertSame(TRUE, ((NotDoublePredicate) DoublePredicates.not(TRUE)).getPredicate());
    }

    /**
     * Tests {@link DoublePredicates#or(DoublePredicate, DoublePredicate)}.
     */
    @Test
    public void or() {
        assertTrue(DoublePredicates.or(TRUE, TRUE).op(1D));
        assertTrue(DoublePredicates.or(TRUE, FALSE).op(1D));
        assertTrue(DoublePredicates.or(FALSE, TRUE).op(1D));
        assertFalse(DoublePredicates.or(FALSE, FALSE).op(1D));

        DoublePredicates.OrDoublePredicate p = new DoublePredicates.OrDoublePredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        DoublePredicates.or(TRUE, TestUtil.dummy(DoublePredicate.class)).op(1D);
    }

    /**
     * Tests that {@link DoublePredicates#or(DoublePredicate, DoublePredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE() {
        DoublePredicates.or(null, TRUE);
    }

    /**
     * Tests that {@link DoublePredicates#or(DoublePredicate, DoublePredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE1() {
        DoublePredicates.or(TRUE, null);
    }
   
    
   /**
     * Tests {@link DoublePredicates#TRUE}.
     */
    @Test
    public void truePredicate() {
        assertTrue(TRUE.op(2D));
        assertTrue(TRUE.op(Double.MIN_VALUE));
        TRUE.toString(); // does not fail
        assertIsSerializable(TRUE);
        assertSame(TRUE, TestUtil.serializeAndUnserialize(TRUE));
    }
}