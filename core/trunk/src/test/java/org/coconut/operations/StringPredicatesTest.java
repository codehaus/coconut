/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.coconut.test.TestUtil.assertIsSerializable;
import org.coconut.operations.Ops.Predicate;
import org.junit.Test;

/**
 * Tests {@link StringPredicates}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
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
        assertIsSerializable(p);
    }

    @Test(expected = NullPointerException.class)
    public void containsNPE() {
        StringPredicates.contains(null);
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
        assertIsSerializable(p);
    }

    @Test
    public void endsWith() {
        Predicate<String> p = StringPredicates.endsWith("foo");
        assertNotNull(p);
        assertTrue(p.evaluate("foo"));
        assertFalse(p.evaluate("foob"));
        assertTrue(p.evaluate("ffffoo"));
        assertEquals(StringPredicates.endsWith("foo"), p);
        assertEquals(StringPredicates.endsWith("foo").hashCode(), p.hashCode());
        assertNotNull(p.toString());
        assertIsSerializable(p);
    }

    @Test(expected = NullPointerException.class)
    public void startsWithNPE() {
        StringPredicates.startsWith(null);
    }

    @Test(expected = NullPointerException.class)
    public void endsWithNPE() {
        StringPredicates.endsWith(null);
    }

    @Test
    public void equalsIgnoreCase() {
        Predicate<String> p = StringPredicates.equalsToIgnoreCase("foo");
        assertNotNull(p);
        assertTrue(p.evaluate("foo"));
        assertFalse(p.evaluate("boo"));
        assertTrue(p.evaluate("Foo"));
        assertTrue(p.evaluate("FOO"));
        assertEquals(StringPredicates.equalsToIgnoreCase("foo"), p);
        assertEquals(StringPredicates.equalsToIgnoreCase("foo").hashCode(), p.hashCode());
        assertNotNull(p.toString());
        assertIsSerializable(p);
    }

    @Test(expected = NullPointerException.class)
    public void equalsIgnoreCaseNPE() {
        StringPredicates.equalsToIgnoreCase(null);
    }
}
