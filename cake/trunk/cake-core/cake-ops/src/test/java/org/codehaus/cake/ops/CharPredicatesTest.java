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

import static org.codehaus.cake.ops.CharPredicates.*;
import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.*;

import org.codehaus.cake.ops.CharPredicates.*;
import org.codehaus.cake.ops.Ops.*;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;
/**
 * Various tests for {@link CharPredicates}.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CharPredicatesTest.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class CharPredicatesTest {

    /**
     * Tests {@link LongPredicates#and(LongPredicate, LongPredicate)}.
     */
    @Test
    public void and() {
        assertTrue(CharPredicates.and(TRUE, TRUE).op((char) 1));
        assertFalse(CharPredicates.and(TRUE, FALSE).op((char) 1));
        assertFalse(CharPredicates.and(FALSE, TRUE).op((char) 1));
        assertFalse(CharPredicates.and(FALSE, FALSE).op((char) 1));

        CharPredicates.AndCharPredicate p = new CharPredicates.AndCharPredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        CharPredicates.and(FALSE, TestUtil.dummy(CharPredicate.class)).op((char) 1);
    }

    /**
     * Tests that {@link CharPredicates#and(CharPredicate, CharPredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE() {
        CharPredicates.and(null, TRUE);
    }

    /**
     * Tests that {@link CharPredicates#and(CharPredicate, CharPredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE1() {
        CharPredicates.and(TRUE, null);
    }
    
    /* Test greater then */
    @Test
    public void equalsTo() {
        CharPredicate f = CharPredicates.equalsTo((char) 5);
        assertEquals((char) 5, new EqualsToCharPredicate((char) 5).getEqualsTo());
        assertFalse(f.op((char) 4));
        assertTrue(f.op((char) 5));
        assertFalse(f.op((char) 6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }
    
    /**
     * Tests {@link CharPredicates#FALSE}.
     */
    @Test
    public void falsePredicate() {
        assertFalse(FALSE.op((char) 2));
        assertFalse(FALSE.op(Character.MIN_VALUE));
        FALSE.toString(); // does not fail
        assertIsSerializable(FALSE);
        assertSame(FALSE, TestUtil.serializeAndUnserialize(FALSE));
    }
   
    /* Test greater then */
    @Test
    public void greaterThen() {
        CharPredicate f = CharPredicates.greaterThen((char) 5);
        assertEquals((char) 5, new GreaterThenCharPredicate((char) 5).getGreaterThen());
        assertFalse(f.op((char) 4));
        assertFalse(f.op((char) 5));
        assertTrue(f.op((char) 6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    @Test
    public void greaterThenOrEquals() {
        CharPredicate f = CharPredicates.greaterThenOrEquals((char) 5);
        assertEquals((char) 5, new GreaterThenOrEqualsCharPredicate((char) 5).getGreaterThenOrEquals());
        assertFalse(f.op((char) 4));
        assertTrue(f.op((char) 5));
        assertTrue(f.op((char) 6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThen() {
        CharPredicate f = CharPredicates.lessThen((char) 5);
        assertEquals((char) 5, new LessThenCharPredicate((char) 5).getLessThen());
        assertTrue(f.op((char) 4));
        assertFalse(f.op((char) 5));
        assertFalse(f.op((char) 6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThenOrEquals() {
        CharPredicate f = CharPredicates.lessThenOrEquals((char) 5);
        assertEquals((char) 5, new LessThenOrEqualsCharPredicate((char) 5).getLessThenOrEquals());
        assertTrue(f.op((char) 4));
        assertTrue(f.op((char) 5));
        assertFalse(f.op((char) 6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }
   
   
     /**
     * Tests that {@link CharPredicates#not(CharPredicate)} throws a
     * {@link NullPointerException} when invoked with a <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void notNPE() {
        CharPredicates.not(null);
    }

    /**
     * Tests {@link CharPredicates#TRUE}.
     */
    @Test
    public void notPredicate() {
        assertFalse(CharPredicates.not(TRUE).op((char) 2));
        assertTrue(CharPredicates.not(FALSE).op((char) 2));
        CharPredicates.not(TRUE).toString(); // does not fail
        assertIsSerializable(CharPredicates.not(TRUE));
        assertSame(TRUE, ((NotCharPredicate) CharPredicates.not(TRUE)).getPredicate());
    }

    /**
     * Tests {@link CharPredicates#or(CharPredicate, CharPredicate)}.
     */
    @Test
    public void or() {
        assertTrue(CharPredicates.or(TRUE, TRUE).op((char) 1));
        assertTrue(CharPredicates.or(TRUE, FALSE).op((char) 1));
        assertTrue(CharPredicates.or(FALSE, TRUE).op((char) 1));
        assertFalse(CharPredicates.or(FALSE, FALSE).op((char) 1));

        CharPredicates.OrCharPredicate p = new CharPredicates.OrCharPredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        CharPredicates.or(TRUE, TestUtil.dummy(CharPredicate.class)).op((char) 1);
    }

    /**
     * Tests that {@link CharPredicates#or(CharPredicate, CharPredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE() {
        CharPredicates.or(null, TRUE);
    }

    /**
     * Tests that {@link CharPredicates#or(CharPredicate, CharPredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE1() {
        CharPredicates.or(TRUE, null);
    }
   
    
   /**
     * Tests {@link CharPredicates#TRUE}.
     */
    @Test
    public void truePredicate() {
        assertTrue(TRUE.op((char) 2));
        assertTrue(TRUE.op(Character.MIN_VALUE));
        TRUE.toString(); // does not fail
        assertIsSerializable(TRUE);
        assertSame(TRUE, TestUtil.serializeAndUnserialize(TRUE));
    }
}