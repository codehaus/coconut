/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark.memory;

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheConfiguration;
import com.whirlycott.cache.CacheManager;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class WhirlyCacheMemoryOverheadTest extends AbstractMemoryOverheadTest {
    /**
     * @see org.coconut.cache.policy.concurrent.OverHeadTester#create(int)
     */
    public void test(MemoryTestResult runs) throws Exception {
        runs.start();
        CacheConfiguration conf = new CacheConfiguration();
        String name = "WhirlyCache " + System.nanoTime();
        conf.setName(name);
        conf.setBackend("com.whirlycott.cache.impl.ConcurrentHashMapImpl");
        conf.setMaxSize(runs.getIterations());
        conf.setTunerSleepTime(Integer.MAX_VALUE);
        conf.setPolicy("com.whirlycott.cache.policy.FIFOMaintenancePolicy");
        for (int i = 0; i < runs.getTotal(); i++) {
            runs.set(i, CacheManager.getInstance().createCache(conf));
        }
        final int iterations = runs.getIterations();
        for (int i = 0; i < runs.getTotal(); i++) {
            Cache c = runs.get(i);
            for (int j = 0; j < iterations; j++) {
                c.store(getString(j), getInt(j));
            }
        }
        runs.stop();
        for (String s : CacheManager.getInstance().getCacheNames()) {
            CacheManager.getInstance().destroy(s);
        }
    }

    public String toString() {
        return "WhirlyCache 1.0.1 (" + Cache.class.getCanonicalName().toString()
                + ")";
    }
}

