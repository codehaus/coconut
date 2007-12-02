/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.coconut.predicate.Predicates.AnyPredicate;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public class PredicatesTest {

    @Test
    public void anyType() {
        Predicate p = Predicates.anyType(Integer.class, Double.class);
        assertTrue(p.evaluate(1));
        assertTrue(p.evaluate(1.0));
        assertFalse(p.evaluate(1l));
        assertFalse(p.evaluate(1.0f));
    }

    @Test
    public void isNull() {
        Predicate f = Predicates.isNull();
        assertTrue(f.evaluate(null));
        assertFalse(f.evaluate(1));
        assertFalse(f.evaluate(f));
        f.toString();// no fail
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
        assertFalse(Predicates.isNumber().evaluate(new Character((char) 1)));
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
