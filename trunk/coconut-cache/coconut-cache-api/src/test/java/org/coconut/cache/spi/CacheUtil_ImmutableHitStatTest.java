/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

import junit.framework.JUnit4TestAdapter;

import org.coconut.cache.Cache;
import org.coconut.cache.Cache.HitStat;
import org.coconut.cache.spi.CacheUtil;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;
import org.junit.Test;

/**
 * Test ImmutableHitStat
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id: Caches_ImmutableHitStatTest.java 186 2007-01-24 12:30:38Z kasper $
 */
public class CacheUtil_ImmutableHitStatTest extends MockTestCase {

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CacheUtil_ImmutableHitStatTest.class);
    }

    @Test
    public void testHitStat00() {
        HitStat hs = CacheUtil.newImmutableHitStat(0, 0);
        assertEquals(0l, hs.getNumberOfHits());
        assertEquals(0l, hs.getNumberOfMisses());
        // per default contract
        assertEquals(Double.NaN, hs.getHitRatio(), 0.000001);
    }

    @Test
    public void testHitStat() {
        HitStat hs = CacheUtil.newImmutableHitStat(80, 20);
        assertEquals(80l, hs.getNumberOfHits());
        assertEquals(20l, hs.getNumberOfMisses());
        assertEquals(0.8, hs.getHitRatio(), 0.000001);
    }

    @Test
    public void testHitStatConstructor() {
        Mock mock = mock(Cache.HitStat.class);
        mock.expects(once()).method("getNumberOfHits").will(
                returnValue((long) 80));
        mock.expects(once()).method("getNumberOfMisses").will(
                returnValue((long) 20));
        Cache.HitStat chs = (Cache.HitStat) mock.proxy();
        HitStat hs = CacheUtil.newImmutableHitStat(chs);
        assertEquals(80l, hs.getNumberOfHits());
        assertEquals(20l, hs.getNumberOfMisses());
        assertEquals(0.8, hs.getHitRatio(), 0.000001);
    }

    @Test
    public void testEqualsHashcode() {
        Cache.HitStat hs = new Cache.HitStat() {
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
        HitStat hs1 = CacheUtil.newImmutableHitStat(0, 0);
        HitStat hs2 = CacheUtil.newImmutableHitStat(80, 20);
        assertFalse(hs1.equals(null));
        assertFalse(hs1.equals(new Object()));
        assertFalse(hs1.equals(hs2));
        assertTrue(hs2.equals(hs2));
        assertTrue(hs2.equals(hs));
        
        assertFalse(hs1.hashCode() == hs2.hashCode()); // well its possible
        assertEquals(CacheUtil.newImmutableHitStat(80, 20).hashCode(), hs2.hashCode());
    }
}
