/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.tck.AbstractCacheTCKTestBundle;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.junit.Before;

public class AbstractLoadingTestBundle extends AbstractCacheTCKTestBundle {

    public static final IntegerToStringLoader DEFAULT_LOADER = new IntegerToStringLoader();

    protected Cache<Integer, String> noLoadableCache;

    protected IntegerToStringLoader loader;

    @Before
    public void setupLoading() {
        noLoadableCache = c;
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        loader = new IntegerToStringLoader();
        c = newCache(cc.loading().setLoader(loader).c());
    }
    public void awaitLoad() {
        //only unsync now
        //when sync hook up with AbstractCacheTCKTestBundle.threadHelper
    }
    
    public void loadAllAndAwait(Map.Entry<Integer, String>... e) {
        throw new UnsupportedOperationException();
    }
    
    
}
