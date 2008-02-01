/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.jsr166yops;

import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import jsr166y.forkjoin.Ops.Predicate;

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
        Predicate<String> p = StringOps.contains("foo");
        assertNotNull(p);
        assertTrue(p.op("foo"));
        assertTrue(p.op("bfoo"));
        assertTrue(p.op("foofff"));
        assertFalse(p.op("fofofofo"));
        assertEquals(StringOps.contains("foo"), p);
        assertEquals(StringOps.contains("foo").hashCode(), p.hashCode());
        assertNotNull(p.toString());
        assertIsSerializable(p);
    }

    @Test(expected = NullPointerException.class)
    public void containsNPE() {
        StringOps.contains(null);
    }

    @Test
    public void startsWith() {
        Predicate<String> p = StringOps.startsWith("foo");
        assertNotNull(p);
        assertTrue(p.op("foo"));
        assertFalse(p.op("bfoo"));
        assertTrue(p.op("foofff"));
        assertEquals(StringOps.startsWith("foo"), p);
        assertEquals(StringOps.startsWith("foo").hashCode(), p.hashCode());
        assertNotNull(p.toString());
        assertIsSerializable(p);
    }

    @Test
    public void endsWith() {
        Predicate<String> p = StringOps.endsWith("foo");
        assertNotNull(p);
        assertTrue(p.op("foo"));
        assertFalse(p.op("foob"));
        assertTrue(p.op("ffffoo"));
        assertEquals(StringOps.endsWith("foo"), p);
        assertEquals(StringOps.endsWith("foo").hashCode(), p.hashCode());
        assertNotNull(p.toString());
        assertIsSerializable(p);
    }

    @Test(expected = NullPointerException.class)
    public void startsWithNPE() {
        StringOps.startsWith(null);
    }

    @Test(expected = NullPointerException.class)
    public void endsWithNPE() {
        StringOps.endsWith(null);
    }

    @Test
    public void equalsIgnoreCase() {
        Predicate<String> p = StringOps.equalsToIgnoreCase("foo");
        assertNotNull(p);
        assertTrue(p.op("foo"));
        assertFalse(p.op("boo"));
        assertTrue(p.op("Foo"));
        assertTrue(p.op("FOO"));
        assertEquals(StringOps.equalsToIgnoreCase("foo"), p);
        assertEquals(StringOps.equalsToIgnoreCase("foo").hashCode(), p.hashCode());
        assertNotNull(p.toString());
        assertIsSerializable(p);
    }

    @Test(expected = NullPointerException.class)
    public void equalsIgnoreCaseNPE() {
        StringOps.equalsToIgnoreCase(null);
    }
}
