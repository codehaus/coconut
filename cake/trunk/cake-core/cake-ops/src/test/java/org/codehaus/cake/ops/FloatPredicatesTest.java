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

import static org.codehaus.cake.ops.FloatPredicates.FALSE;
import static org.codehaus.cake.ops.FloatPredicates.TRUE;
import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.codehaus.cake.ops.FloatPredicates.EqualsToFloatPredicate;
import org.codehaus.cake.ops.FloatPredicates.GreaterThenFloatPredicate;
import org.codehaus.cake.ops.FloatPredicates.GreaterThenOrEqualsFloatPredicate;
import org.codehaus.cake.ops.FloatPredicates.LessThenFloatPredicate;
import org.codehaus.cake.ops.FloatPredicates.LessThenOrEqualsFloatPredicate;
import org.codehaus.cake.ops.FloatPredicates.NotFloatPredicate;
import org.codehaus.cake.ops.Ops.FloatPredicate;
import org.codehaus.cake.ops.Ops.LongPredicate;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;
/**
 * Various tests for {@link FloatPredicates}.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: FloatPredicatesTest.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class FloatPredicatesTest {

    /**
     * Tests {@link LongPredicates#and(LongPredicate, LongPredicate)}.
     */
    @Test
    public void and() {
        assertTrue(FloatPredicates.and(TRUE, TRUE).op(1F));
        assertFalse(FloatPredicates.and(TRUE, FALSE).op(1F));
        assertFalse(FloatPredicates.and(FALSE, TRUE).op(1F));
        assertFalse(FloatPredicates.and(FALSE, FALSE).op(1F));

        FloatPredicates.AndFloatPredicate p = new FloatPredicates.AndFloatPredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        FloatPredicates.and(FALSE, TestUtil.dummy(FloatPredicate.class)).op(1F);
    }

    /**
     * Tests that {@link FloatPredicates#and(FloatPredicate, FloatPredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE() {
        FloatPredicates.and(null, TRUE);
    }

    /**
     * Tests that {@link FloatPredicates#and(FloatPredicate, FloatPredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE1() {
        FloatPredicates.and(TRUE, null);
    }
    
    /* Test greater then */
    @Test
    public void equalsTo() {
        FloatPredicate f = FloatPredicates.equalsTo(5F);
        assertEquals(5F, new EqualsToFloatPredicate(5F).getEqualsTo(),0);
        assertFalse(f.op(4F));
        assertTrue(f.op(5F));
        assertFalse(f.op(6F));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }
    
    /**
     * Tests {@link FloatPredicates#FALSE}.
     */
    @Test
    public void falsePredicate() {
        assertFalse(FALSE.op(2F));
        assertFalse(FALSE.op(Float.MIN_VALUE));
        FALSE.toString(); // does not fail
        assertIsSerializable(FALSE);
        assertSame(FALSE, TestUtil.serializeAndUnserialize(FALSE));
    }
   
    /* Test greater then */
    @Test
    public void greaterThen() {
        FloatPredicate f = FloatPredicates.greaterThen(5F);
        assertEquals(5F, new GreaterThenFloatPredicate(5F).getGreaterThen(),0);
        assertFalse(f.op(4F));
        assertFalse(f.op(5F));
        assertTrue(f.op(6F));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    @Test
    public void greaterThenOrEquals() {
        FloatPredicate f = FloatPredicates.greaterThenOrEquals(5F);
        assertEquals(5F, new GreaterThenOrEqualsFloatPredicate(5F).getGreaterThenOrEquals(),0);
        assertFalse(f.op(4F));
        assertTrue(f.op(5F));
        assertTrue(f.op(6F));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThen() {
        FloatPredicate f = FloatPredicates.lessThen(5F);
        assertEquals(5F, new LessThenFloatPredicate(5F).getLessThen(),0);
        assertTrue(f.op(4F));
        assertFalse(f.op(5F));
        assertFalse(f.op(6F));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThenOrEquals() {
        FloatPredicate f = FloatPredicates.lessThenOrEquals(5F);
        assertEquals(5F, new LessThenOrEqualsFloatPredicate(5F).getLessThenOrEquals(),0);
        assertTrue(f.op(4F));
        assertTrue(f.op(5F));
        assertFalse(f.op(6F));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }
   
   
     /**
     * Tests that {@link FloatPredicates#not(FloatPredicate)} throws a
     * {@link NullPointerException} when invoked with a <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void notNPE() {
        FloatPredicates.not(null);
    }

    /**
     * Tests {@link FloatPredicates#TRUE}.
     */
    @Test
    public void notPredicate() {
        assertFalse(FloatPredicates.not(TRUE).op(2F));
        assertTrue(FloatPredicates.not(FALSE).op(2F));
        FloatPredicates.not(TRUE).toString(); // does not fail
        assertIsSerializable(FloatPredicates.not(TRUE));
        assertSame(TRUE, ((NotFloatPredicate) FloatPredicates.not(TRUE)).getPredicate());
    }

    /**
     * Tests {@link FloatPredicates#or(FloatPredicate, FloatPredicate)}.
     */
    @Test
    public void or() {
        assertTrue(FloatPredicates.or(TRUE, TRUE).op(1F));
        assertTrue(FloatPredicates.or(TRUE, FALSE).op(1F));
        assertTrue(FloatPredicates.or(FALSE, TRUE).op(1F));
        assertFalse(FloatPredicates.or(FALSE, FALSE).op(1F));

        FloatPredicates.OrFloatPredicate p = new FloatPredicates.OrFloatPredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        FloatPredicates.or(TRUE, TestUtil.dummy(FloatPredicate.class)).op(1F);
    }

    /**
     * Tests that {@link FloatPredicates#or(FloatPredicate, FloatPredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE() {
        FloatPredicates.or(null, TRUE);
    }

    /**
     * Tests that {@link FloatPredicates#or(FloatPredicate, FloatPredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE1() {
        FloatPredicates.or(TRUE, null);
    }
   
    
   /**
     * Tests {@link FloatPredicates#TRUE}.
     */
    @Test
    public void truePredicate() {
        assertTrue(TRUE.op(2F));
        assertTrue(TRUE.op(Float.MIN_VALUE));
        TRUE.toString(); // does not fail
        assertIsSerializable(TRUE);
        assertSame(TRUE, TestUtil.serializeAndUnserialize(TRUE));
    }
}