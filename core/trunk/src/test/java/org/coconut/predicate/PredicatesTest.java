/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.coconut.test.TestUtil;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class PredicatesTest {
    
    @Test
    public void isNull() {
        assertTrue(Predicates.isNull().evaluate(null));
        assertFalse(Predicates.isNull().evaluate(1));
        assertFalse(Predicates.isNull().evaluate("f"));
        assertSame(Predicates.IS_NULL, Predicates.isNull());
        Predicates.IS_NULL.toString();// no fail
        TestUtil.assertIsSerializable(Predicates.IS_NULL);
    }

    @Test
    public void isNumber() {
        assertSame(Predicates.IS_NUMBER, Predicates.isNumber());
        assertTrue(Predicates.isNumber().evaluate(new Double(1)));
        assertTrue(Predicates.isNumber().evaluate(new Float(1)));
        assertTrue(Predicates.isNumber().evaluate(new Integer(1)));
        assertTrue(Predicates.isNumber().evaluate(new Long(1)));
        assertTrue(Predicates.isNumber().evaluate(new Byte((byte) 1)));
        assertTrue(Predicates.isNumber().evaluate(new Short((short) 1)));

        assertFalse(Predicates.isNumber().evaluate(Boolean.FALSE));
        assertFalse(Predicates.isNumber().evaluate(Character.valueOf((char) 1)));
    }

    @Test
    public void notNullAnd() {
        Predicate<Integer> p = Predicates.notNullAnd(Predicates.anyEquals(1, 2));
        assertFalse(p.evaluate(null));
        assertTrue(p.evaluate(1));
        assertFalse(p.evaluate(3));
        p.toString();
    }

    @Test(expected = NullPointerException.class)
    public void notNullAndNPE() {
       Predicates.notNullAnd(null);
    }
}
