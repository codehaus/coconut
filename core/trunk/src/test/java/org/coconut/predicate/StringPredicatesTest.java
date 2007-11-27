/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringPredicatesTest {
    
    @Test
    public void contains() {
        Predicate<String> p = StringPredicates.contains("foo");
        assertNotNull(p);
        assertTrue(p.evaluate("foo"));
        assertTrue(p.evaluate("bfoo"));
        assertTrue(p.evaluate("foofff"));
        assertFalse(p.evaluate("fofofofo"));
        assertEquals(StringPredicates.contains("foo"), p);
        assertEquals(StringPredicates.contains("foo").hashCode(), p.hashCode());
        assertNotNull(p.toString());
    }
    @Test
    public void startsWith() {
        Predicate<String> p = StringPredicates.startsWith("foo");
        assertNotNull(p);
        assertTrue(p.evaluate("foo"));
        assertFalse(p.evaluate("bfoo"));
        assertTrue(p.evaluate("foofff"));
        assertEquals(StringPredicates.startsWith("foo"), p);
        assertEquals(StringPredicates.startsWith("foo").hashCode(), p.hashCode());
        assertNotNull(p.toString());
    }

    @Test(expected = NullPointerException.class)
    public void startsWithNPE() {
        StringPredicates.startsWith(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void containsNPE() {
        StringPredicates.contains(null);
    }
}

