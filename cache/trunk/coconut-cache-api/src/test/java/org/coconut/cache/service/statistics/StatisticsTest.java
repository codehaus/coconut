/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.statistics;

import org.coconut.test.MockTestCase;
import org.jmock.Mock;
import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class StatisticsTest extends MockTestCase{
    
    public void testIAE1() {
        try {
            Statistics.newImmutableHitStat(-1, 0);
            fail("Did not fail with IllegalArgumentException");
        } catch (IllegalArgumentException npe) {
        }
    }

    public void testIAE2() {
        try {
            Statistics.newImmutableHitStat(0, -1);
            fail("Did not fail with IllegalArgumentException");
        } catch (IllegalArgumentException npe) {
        }
    }

    @Test
    public void testHitStat00() {
        CacheHitStat hs = Statistics.newImmutableHitStat(0, 0);
        assertEquals(0l, hs.getNumberOfHits());
        assertEquals(0l, hs.getNumberOfMisses());
        // per default contract
        assertEquals(Double.NaN, hs.getHitRatio(), 0.000001);
    }

    @Test
    public void testHitStat() {
        CacheHitStat hs = Statistics.newImmutableHitStat(80, 20);
        assertEquals(80l, hs.getNumberOfHits());
        assertEquals(20l, hs.getNumberOfMisses());
        assertEquals(0.8, hs.getHitRatio(), 0.000001);
        assertTrue(hs.toString().contains("80"));
        assertTrue(hs.toString().contains("20"));
        assertTrue(hs.toString().contains("0.8"));
    }

    @Test
    public void testHitStatConstructor() {
        Mock mock = mock(CacheHitStat.class);
        mock.expects(once()).method("getNumberOfHits").will(returnValue((long) 80));
        mock.expects(once()).method("getNumberOfMisses").will(returnValue((long) 20));
        CacheHitStat chs = (CacheHitStat) mock.proxy();
        CacheHitStat hs = Statistics.newImmutableHitStat(chs);
        assertEquals(80l, hs.getNumberOfHits());
        assertEquals(20l, hs.getNumberOfMisses());
        assertEquals(0.8, hs.getHitRatio(), 0.000001);
    }

    @Test
    public void testEqualsHashcode() {
        CacheHitStat hs = new CacheHitStat() {
            public long getNumberOfHits() {
                return 80;
            }

            public long getNumberOfMisses() {
                return 20;
            }

            public float getHitRatio() {
                throw new UnsupportedOperationException(); // not used
            }

        };
        CacheHitStat hs1 = Statistics.newImmutableHitStat(0, 0);
        CacheHitStat hs2 = Statistics.newImmutableHitStat(80, 20);
        assertFalse(hs1.equals(null));
        assertFalse(hs1.equals(new Object()));
        assertFalse(hs1.equals(hs2));
        assertTrue(hs2.equals(hs2));
        assertTrue(hs2.equals(hs));

        assertFalse(hs1.hashCode() == hs2.hashCode()); // well its possible
        assertEquals(Statistics.newImmutableHitStat(80, 20).hashCode(), hs2.hashCode());
    }
}
