package org.coconut.cache.tck.service.loading;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.coconut.cache.tck.testutil.IntegerToStringLoader;
import org.junit.Before;

public class AbstractLoadingTestBundle extends AbstractCacheTCKTestBundle {

    public static final IntegerToStringLoader DEFAULT_LOADER = new IntegerToStringLoader();

    protected Cache<Integer, String> noLoadableCache;

    @Before
    public void setupLoading() {
        noLoadableCache = c;
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        c = newCache(cc.loading().setLoader(new IntegerToStringLoader()).c());
    }
    
   
}
