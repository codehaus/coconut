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

import static org.codehaus.cake.ops.BytePredicates.*;
import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.*;

import org.codehaus.cake.ops.BytePredicates.*;
import org.codehaus.cake.ops.Ops.*;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;
/**
 * Various tests for {@link BytePredicates}.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: BytePredicatesTest.java 590 2008-03-14 08:16:12Z kasper $
 */
public final class BytePredicatesTest {

    /**
     * Tests {@link LongPredicates#and(LongPredicate, LongPredicate)}.
     */
    @Test
    public void and() {
        assertTrue(BytePredicates.and(TRUE, TRUE).op((byte) 1));
        assertFalse(BytePredicates.and(TRUE, FALSE).op((byte) 1));
        assertFalse(BytePredicates.and(FALSE, TRUE).op((byte) 1));
        assertFalse(BytePredicates.and(FALSE, FALSE).op((byte) 1));

        BytePredicates.AndBytePredicate p = new BytePredicates.AndBytePredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        BytePredicates.and(FALSE, TestUtil.dummy(BytePredicate.class)).op((byte) 1);
    }

    /**
     * Tests that {@link BytePredicates#and(BytePredicate, BytePredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE() {
        BytePredicates.and(null, TRUE);
    }

    /**
     * Tests that {@link BytePredicates#and(BytePredicate, BytePredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void andNPE1() {
        BytePredicates.and(TRUE, null);
    }
    
    /* Test greater then */
    @Test
    public void equalsTo() {
        BytePredicate f = BytePredicates.equalsTo((byte) 5);
        assertEquals((byte) 5, new EqualsToBytePredicate((byte) 5).getEqualsTo());
        assertFalse(f.op((byte) 4));
        assertTrue(f.op((byte) 5));
        assertFalse(f.op((byte) 6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }
    
    /**
     * Tests {@link BytePredicates#FALSE}.
     */
    @Test
    public void falsePredicate() {
        assertFalse(FALSE.op((byte) 2));
        assertFalse(FALSE.op(Byte.MIN_VALUE));
        FALSE.toString(); // does not fail
        assertIsSerializable(FALSE);
        assertSame(FALSE, TestUtil.serializeAndUnserialize(FALSE));
    }
   
    /* Test greater then */
    @Test
    public void greaterThen() {
        BytePredicate f = BytePredicates.greaterThen((byte) 5);
        assertEquals((byte) 5, new GreaterThenBytePredicate((byte) 5).getGreaterThen());
        assertFalse(f.op((byte) 4));
        assertFalse(f.op((byte) 5));
        assertTrue(f.op((byte) 6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    @Test
    public void greaterThenOrEquals() {
        BytePredicate f = BytePredicates.greaterThenOrEquals((byte) 5);
        assertEquals((byte) 5, new GreaterThenOrEqualsBytePredicate((byte) 5).getGreaterThenOrEquals());
        assertFalse(f.op((byte) 4));
        assertTrue(f.op((byte) 5));
        assertTrue(f.op((byte) 6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThen() {
        BytePredicate f = BytePredicates.lessThen((byte) 5);
        assertEquals((byte) 5, new LessThenBytePredicate((byte) 5).getLessThen());
        assertTrue(f.op((byte) 4));
        assertFalse(f.op((byte) 5));
        assertFalse(f.op((byte) 6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }

    /* Test greater then */
    @Test
    public void lessThenOrEquals() {
        BytePredicate f = BytePredicates.lessThenOrEquals((byte) 5);
        assertEquals((byte) 5, new LessThenOrEqualsBytePredicate((byte) 5).getLessThenOrEquals());
        assertTrue(f.op((byte) 4));
        assertTrue(f.op((byte) 5));
        assertFalse(f.op((byte) 6));

        f.toString(); // no exceptions

        TestUtil.assertIsSerializable(f);
    }
   
   
     /**
     * Tests that {@link BytePredicates#not(BytePredicate)} throws a
     * {@link NullPointerException} when invoked with a <code>null</code> argument.
     */
    @Test(expected = NullPointerException.class)
    public void notNPE() {
        BytePredicates.not(null);
    }

    /**
     * Tests {@link BytePredicates#TRUE}.
     */
    @Test
    public void notPredicate() {
        assertFalse(BytePredicates.not(TRUE).op((byte) 2));
        assertTrue(BytePredicates.not(FALSE).op((byte) 2));
        BytePredicates.not(TRUE).toString(); // does not fail
        assertIsSerializable(BytePredicates.not(TRUE));
        assertSame(TRUE, ((NotBytePredicate) BytePredicates.not(TRUE)).getPredicate());
    }

    /**
     * Tests {@link BytePredicates#or(BytePredicate, BytePredicate)}.
     */
    @Test
    public void or() {
        assertTrue(BytePredicates.or(TRUE, TRUE).op((byte) 1));
        assertTrue(BytePredicates.or(TRUE, FALSE).op((byte) 1));
        assertTrue(BytePredicates.or(FALSE, TRUE).op((byte) 1));
        assertFalse(BytePredicates.or(FALSE, FALSE).op((byte) 1));

        BytePredicates.OrBytePredicate p = new BytePredicates.OrBytePredicate(FALSE, TRUE);
        assertSame(p.getLeft(), FALSE);
        assertSame(p.getRight(), TRUE);
        p.toString(); // no exception
        assertIsSerializable(p);

        // shortcircuted evaluation
        BytePredicates.or(TRUE, TestUtil.dummy(BytePredicate.class)).op((byte) 1);
    }

    /**
     * Tests that {@link BytePredicates#or(BytePredicate, BytePredicate)} throws a
     * {@link NullPointerException} when invoked with a left side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE() {
        BytePredicates.or(null, TRUE);
    }

    /**
     * Tests that {@link BytePredicates#or(BytePredicate, BytePredicate)} throws a
     * {@link NullPointerException} when invoked with a right side <code>null</code>
     * argument.
     */
    @Test(expected = NullPointerException.class)
    public void orNPE1() {
        BytePredicates.or(TRUE, null);
    }
   
    
   /**
     * Tests {@link BytePredicates#TRUE}.
     */
    @Test
    public void truePredicate() {
        assertTrue(TRUE.op((byte) 2));
        assertTrue(TRUE.op(Byte.MIN_VALUE));
        TRUE.toString(); // does not fail
        assertIsSerializable(TRUE);
        assertSame(TRUE, TestUtil.serializeAndUnserialize(TRUE));
    }
}