/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.defaults.memory;

import junit.framework.TestSuite;

import org.coconut.cache.tck.AbstractCacheImplTest;
import org.coconut.cache.tck.loading.Loading;

public class UnlimitedCacheTest extends AbstractCacheImplTest {

    public UnlimitedCacheTest() {
        super(UnlimitedCache.class);
        System.out.println("ok");
        try {
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void testSD() {

    }

    public static TestSuite suite() {
        if (true)
            return new TestSuite(MyTest.class);
        UnlimitedCacheTest suite = new UnlimitedCacheTest();
        // suite.addTestBundle(ExtendedCacheLoader.class);
        // suite.init();
        // suite.addCoreFeatures();
        // suite.addTestBundle(EventBusFeature.class);
        suite.addTestBundle(Loading.class);
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
        return suite;
    }

}
