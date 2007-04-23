/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.statistics;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@RunWith(JMock.class)
public class StatisticsTest {

	@Test(expected = IllegalArgumentException.class)
	public void testIAE1() {
		CacheStatistics.newImmutableHitStat(-1, 0);
	}

	public void testIAE2() {
		CacheStatistics.newImmutableHitStat(0, -1);

	}

	@Test
	public void testHitStat00() {
		CacheHitStat hs = CacheStatistics.newImmutableHitStat(0, 0);
		assertEquals(0l, hs.getNumberOfHits());
		assertEquals(0l, hs.getNumberOfMisses());
		// per default contract
		assertEquals(Double.NaN, hs.getHitRatio(), 0.000001);
	}

	@Test
	public void testHitStat() {
		CacheHitStat hs = CacheStatistics.newImmutableHitStat(80, 20);
		assertEquals(80l, hs.getNumberOfHits());
		assertEquals(20l, hs.getNumberOfMisses());
		assertEquals(0.8, hs.getHitRatio(), 0.000001);
		assertTrue(hs.toString().contains("80"));
		assertTrue(hs.toString().contains("20"));
		assertTrue(hs.toString().contains("0.8"));
	}

	Mockery context = new JUnit4Mockery();

	@Test
	public void testHitStatConstructor() {
		final CacheHitStat chs = context.mock(CacheHitStat.class);
		context.checking(new Expectations() {
			{
				one(chs).getNumberOfHits();
				will(returnValue(80l));
				one(chs).getNumberOfMisses();
				will(returnValue(20l));
			}
		});
		CacheHitStat hs = CacheStatistics.newImmutableHitStat(chs);
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
		CacheHitStat hs1 = CacheStatistics.newImmutableHitStat(0, 0);
		CacheHitStat hs2 = CacheStatistics.newImmutableHitStat(80, 20);
		assertFalse(hs1.equals(null));
		assertFalse(hs1.equals(new Object()));
		assertFalse(hs1.equals(hs2));
		assertTrue(hs2.equals(hs2));
		assertTrue(hs2.equals(hs));

		assertFalse(hs1.hashCode() == hs2.hashCode()); // well its possible
		assertEquals(CacheStatistics.newImmutableHitStat(80, 20).hashCode(), hs2
				.hashCode());
	}
}
