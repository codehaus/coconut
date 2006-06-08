package org.coconut.cache.defaults.memory;

import junit.framework.TestSuite;

import org.coconut.cache.tck.AbstractCacheImplTest;

public class UnlimitedCacheTest extends AbstractCacheImplTest {

    public UnlimitedCacheTest() {
        super(UnlimitedCache.class);
    }

    public static TestSuite suite() {
        AbstractCacheImplTest suite = new UnlimitedCacheTest();
        //suite.addTestBundle(ExtendedCacheLoader.class);
        suite.init();
//        suite.addCoreFeatures();
//        suite.addTestBundle(EventBusFeature.class);
//        suite.addTestBundle(Loading.class);
//        suite.addTestBundle(ExpirationStrict.class);
//        suite.addTestBundle(Expiration.class);
//        suite.addTestBundle(ExpirationOnEvict.class);
//        suite.addTestBundle(CacheEntryToPolicy.class);
//        suite.addTestBundle(ExpirationStrict.class);
//        suite.addTestBundle(ExpirationLazySingleThreaded.class);
//        suite.addTestBundle(ExpirationCommon.class);
//        suite.addTestBundle(ExpirationOnEvict.class);
//        suite.addTestBundle(CacheEntryBundle.class);
//        suite.addTestBundle(HitStat.class);
//        suite.addTest(new JUnit4TestAdapter(UnlimitedCacheTest.class));
        return suite;
    }

}
