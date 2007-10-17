/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.junit.Before;

/**
 * The abstract base class for all {@link LoadingService} tests. The standard cache (c)
 * referenced in {@link AbstractCacheTCKTest} is initialized with an
 * {@link IntegerToStringLoader} cache loader.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ExplicitLoading.java 374 2007-08-20 18:29:36Z kasper $
 */
public class AbstractLoadingTestBundle extends AbstractCacheTCKTest {

    IntegerToStringLoader loader;

    @Before
    public void setupLoading() {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        loader = new IntegerToStringLoader();
        c = newCache(cc.loading().setLoader(loader));
    }
}
