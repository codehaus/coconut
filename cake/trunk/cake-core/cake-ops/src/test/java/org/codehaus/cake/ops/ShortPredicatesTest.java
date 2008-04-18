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

import static org.codehaus.cake.ops.ShortPredicates.*;
import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.*;

import org.codehaus.cake.ops.ShortPredicates.*;
import org.codehaus.cake.ops.Ops.*;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;
/**
 * Various tests for {@link ShortPredicates}.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ShortPredicatesTest.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class ShortPredicatesTest {

    /**
     * Tests {@link LongPredicates#and(LongPredicate, LongPredicate)}.
     */
    @Test
    public void and() {
        assertTrue(ShortPredicates.and(TRUE, TRUE).op((short) 1));
        assertFalse(ShortPredicates.and(TRUE, FALSE).op((short) 1));
        assertFalse(ShortPredicates.and(FALSE, TRUE).op((short) 1));
        assertFalse(ShortPredicates.and(FALSE, FALSE).op((short) 1));

        ShortPredicates.AndShortPredicate p = new ShortPredicates.AndShortPredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        ShortPredicates.and(FALSE, TestUtil.dummy(ShortPredicate.class)).op((short) 1);
    }

    /**
     * Tests that {@link ShortPredicates#and(ShortPredicate, ShortPredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE() {
        ShortPredicates.and(null, TRUE);
    }

    /**
     * Tests that {@link ShortPredicates#and(ShortPredicate, ShortPredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE1() {
        ShortPredicates.and(TRUE, null);
    }
    
    /* Test greater then */
    @Test
    public void equalsTo() {
        ShortPredicate f = ShortPredicates.equalsTo((short) 5);
        assertEquals((short) 5, new EqualsToShortPredicate((short) 5).getEqualsTo());
        assertFalse(f.op((short) 4));
        assertTrue(f.op((short) 5));
        assertFalse(f.op((short) 6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }
    
    /**
     * Tests {@link ShortPredicates#FALSE}.
     */
    @Test
    public void falsePredicate() {
        assertFalse(FALSE.op((short) 2));
        assertFalse(FALSE.op(Short.MIN_VALUE));
        FALSE.toString(); // does not fail
        assertIsSerializable(FALSE);
        assertSame(FALSE, TestUtil.serializeAndUnserialize(FALSE));
    }
   
    /* Test greater then */
    @Test
    public void greaterThen() {
        ShortPredicate f = ShortPredicates.greaterThen((short) 5);
        assertEquals((short) 5, new GreaterThenShortPredicate((short) 5).getGreaterThen());
        assertFalse(f.op((short) 4));
        assertFalse(f.op((short) 5));
        assertTrue(f.op((short) 6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    @Test
    public void greaterThenOrEquals() {
        ShortPredicate f = ShortPredicates.greaterThenOrEquals((short) 5);
        assertEquals((short) 5, new GreaterThenOrEqualsShortPredicate((short) 5).getGreaterThenOrEquals());
        assertFalse(f.op((short) 4));
        assertTrue(f.op((short) 5));
        assertTrue(f.op((short) 6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThen() {
        ShortPredicate f = ShortPredicates.lessThen((short) 5);
        assertEquals((short) 5, new LessThenShortPredicate((short) 5).getLessThen());
        assertTrue(f.op((short) 4));
        assertFalse(f.op((short) 5));
        assertFalse(f.op((short) 6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThenOrEquals() {
        ShortPredicate f = ShortPredicates.lessThenOrEquals((short) 5);
        assertEquals((short) 5, new LessThenOrEqualsShortPredicate((short) 5).getLessThenOrEquals());
        assertTrue(f.op((short) 4));
        assertTrue(f.op((short) 5));
        assertFalse(f.op((short) 6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }
   
   
     /**
     * Tests that {@link ShortPredicates#not(ShortPredicate)} throws a
     * {@link NullPointerException} when invoked with a <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void notNPE() {
        ShortPredicates.not(null);
    }

    /**
     * Tests {@link ShortPredicates#TRUE}.
     */
    @Test
    public void notPredicate() {
        assertFalse(ShortPredicates.not(TRUE).op((short) 2));
        assertTrue(ShortPredicates.not(FALSE).op((short) 2));
        ShortPredicates.not(TRUE).toString(); // does not fail
        assertIsSerializable(ShortPredicates.not(TRUE));
        assertSame(TRUE, ((NotShortPredicate) ShortPredicates.not(TRUE)).getPredicate());
    }

    /**
     * Tests {@link ShortPredicates#or(ShortPredicate, ShortPredicate)}.
     */
    @Test
    public void or() {
        assertTrue(ShortPredicates.or(TRUE, TRUE).op((short) 1));
        assertTrue(ShortPredicates.or(TRUE, FALSE).op((short) 1));
        assertTrue(ShortPredicates.or(FALSE, TRUE).op((short) 1));
        assertFalse(ShortPredicates.or(FALSE, FALSE).op((short) 1));

        ShortPredicates.OrShortPredicate p = new ShortPredicates.OrShortPredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        ShortPredicates.or(TRUE, TestUtil.dummy(ShortPredicate.class)).op((short) 1);
    }

    /**
     * Tests that {@link ShortPredicates#or(ShortPredicate, ShortPredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE() {
        ShortPredicates.or(null, TRUE);
    }

    /**
     * Tests that {@link ShortPredicates#or(ShortPredicate, ShortPredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE1() {
        ShortPredicates.or(TRUE, null);
    }
   
    
   /**
     * Tests {@link ShortPredicates#TRUE}.
     */
    @Test
    public void truePredicate() {
        assertTrue(TRUE.op((short) 2));
        assertTrue(TRUE.op(Short.MIN_VALUE));
        TRUE.toString(); // does not fail
        assertIsSerializable(TRUE);
        assertSame(TRUE, TestUtil.serializeAndUnserialize(TRUE));
    }
}