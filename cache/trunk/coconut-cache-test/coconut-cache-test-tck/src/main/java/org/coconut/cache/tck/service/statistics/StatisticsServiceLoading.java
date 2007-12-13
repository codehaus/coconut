/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.statistics;

import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.M4;
import static org.coconut.test.CollectionTestUtil.M5;

import java.util.Arrays;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class StatisticsServiceLoading extends AbstractCacheTCKTest {

    /**
     * Tests that loading of elements does not does not affect the cache statistics
     * gathered.
     * 
     * @throws Exception
     *             test could not complete properly
     */
    @Test
    public void load() throws Exception {
        CacheConfiguration<Integer, String> conf = super.newConf();
        conf.loading().setLoader(new IntegerToStringLoader());
        c = newCache(conf);
        loading().load(M5.getKey());
        loading().loadAll(Arrays.asList(M2.getKey(), M4.getKey()));
        awaitAllLoads();
        assertHitstat(Float.NaN, 0, 0);
    }
}
