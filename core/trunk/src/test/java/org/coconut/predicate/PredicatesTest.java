/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    
    @Test(expected = NullPointerException.class)
    public void notNullAndNPE() {
        Predicate<Integer> p = Predicates.notNullAnd(null);
    }

    @Test
    public void notNullAnd() {
        Predicate<Integer> p = Predicates.notNullAnd(Predicates.anyEquals(1, 2));
        assertFalse(p.evaluate(null));
        assertTrue(p.evaluate(1));
        assertFalse(p.evaluate(3));
        p.toString();
    }

    @Test
    public void testAnyEqualsFilter() {
        AnyPredicate<String> filter = (AnyPredicate) Predicates.anyEquals("1", "2");
        assertTrue(filter.evaluate("1"));
        assertTrue(filter.evaluate("2"));
        assertFalse(filter.evaluate("3"));
    }

    @Test
    public void testNullFilter() {
        Predicate f = Predicates.isNull();
        assertTrue(f.evaluate(null));
        assertFalse(f.evaluate(1));
        assertFalse(f.evaluate(f));
        f.toString();// no fail
    }
}
