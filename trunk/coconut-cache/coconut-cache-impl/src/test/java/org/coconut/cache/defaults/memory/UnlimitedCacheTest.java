/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults.memory;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.coconut.cache.tck.AbstractCacheImplTest;
import org.coconut.cache.tck.CacheTestBundle;
import org.coconut.cache.tck.core.BasicCache;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;


public class UnlimitedCacheTest extends CacheTestBundle {


	public static TestSuite suite() {
		AbstractCacheImplTest ts = new AbstractCacheImplTest(
				UnlimitedCache.class);
        ts.setName("foo");
		System.out.println("suite");
		// suite.addTestBundle(ExtendedCacheLoader.class);
		// suite.init();
		// suite.addCoreFeatures();
		// suite.addTestBundle(EventBusFeature.class);
		//ts.addTestBundle(UnlimitedCacheTest.class);
		ts.addTestBundle(BasicCache.class);
		// ts.addTest(uct);
		// suite.addTestBundle(ExpirationStrict.class);
		// suite.addTestBundle(Expiration.class);
		// suite.addTestBundle(ExpirationOnEvict.class);
		// suite.addTestBundle(CacheEntryToPolicy.class);
		// suite.addTestBundle(ExpirationStrict.class);
		// suite.addTestBundle(ExpirationLazySingleThreaded.class);
		// suite.addTestBundle(ExpirationCommon.class);
		// suite.addTestBundle(ExpirationOnEvict.class);
		// suite.addTestBundle(CacheEntryBundle.class);
		// suite.addTestBundle(HitStat.class);
		// suite.addTest(new JUnit4TestAdapter(UnlimitedCacheTest.class));
		// ts.addTestSuite(UnlimitedCacheTest.class);
		return ts;
	}

}
