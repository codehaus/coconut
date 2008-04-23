package org.codehaus.cake.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PairTest {

    @Test
    public void simpleImmutableEntry() {

        Pair<Integer, Integer> me = new Pair(0, 1);
        assertEquals(0, me.getFirst().intValue());
        assertEquals(me.getFirst(), new Pair(me).getFirst());
        assertEquals(1, me.getSecond().intValue());
        assertEquals(me.getSecond(), new Pair(me).getSecond());
        assertEquals("{0,1}", me.toString());
    }

    @Test
    public void simpleImmutableEntryEquals() {
        Pair me = new Pair(0, 1);
        assertFalse(me.equals(null));
        assertFalse(me.equals(new Object()));
        assertTrue(me.equals(me));
        assertFalse(me.equals(new Pair(0, 0)));
        assertFalse(me.equals(new Pair(0, null)));
        assertFalse(me.equals(new Pair(1, 1)));
        assertFalse(me.equals(new Pair(null, 1)));
        assertTrue(me.equals(new Pair(0, 1)));
    }

    @Test
    public void simpleImmutableEntryHashcode() {
        assertEquals(0, new Pair(null, null).hashCode());
        assertEquals(100 ^ 200, new Pair(100, 200).hashCode());
    }

}
