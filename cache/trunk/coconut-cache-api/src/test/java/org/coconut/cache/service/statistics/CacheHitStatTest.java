/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.statistics;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheHitStatTest {

    @Test(expected = IllegalArgumentException.class)
    public void testIAE1() {
        new CacheHitStat(-1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIAE2() {
        new CacheHitStat(0, -1);

    }

    @Test
    public void testHitStat00() {
        CacheHitStat hs = new CacheHitStat(0, 0);
        assertEquals(0l, hs.getNumberOfHits());
        assertEquals(0l, hs.getNumberOfMisses());
        // per default contract
        assertEquals(Double.NaN, hs.getHitRatio(), 0.000001);
    }

    @Test
    public void testHitStat() {
        CacheHitStat hs = new CacheHitStat(80, 20);
        assertEquals(80l, hs.getNumberOfHits());
        assertEquals(20l, hs.getNumberOfMisses());
        assertEquals(0.8, hs.getHitRatio(), 0.000001);
        assertTrue(hs.toString().contains("80"));
        assertTrue(hs.toString().contains("20"));
        assertTrue(hs.toString().contains("0.8"));
    }

    @Test
    public void testEqualsHashcode() {

        CacheHitStat hs1 = new CacheHitStat(0, 0);
        CacheHitStat hs2 = new CacheHitStat(80, 20);
        assertFalse(hs1.equals(null));
        assertFalse(hs1.equals(new Object()));
        assertFalse(hs1.equals(hs2));
        assertTrue(hs2.equals(hs2));
        assertTrue(hs2.equals(new CacheHitStat(80, 20)));

        assertFalse(hs1.hashCode() == hs2.hashCode()); // well its possible
        assertEquals(new CacheHitStat(80, 20).hashCode(), hs2.hashCode());
    }
}
