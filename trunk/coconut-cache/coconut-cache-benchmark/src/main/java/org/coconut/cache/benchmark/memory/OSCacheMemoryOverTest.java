/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark.memory;

import com.opensymphony.oscache.base.Cache;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class OSCacheMemoryOverTest extends AbstractMemoryOverheadTest {

    static {
        shutupLogger("com.opensymphony.oscache.base.algorithm.AbstractConcurrentReadCache");
        shutupLogger("com.opensymphony.oscache.base.Cache");
    }

    public void test(MemoryTestResult runs) throws Exception {
        runs.start();
        for (int i = 0; i < runs.getTotal(); i++) {
            runs.set(i, new com.opensymphony.oscache.base.Cache(true, false, false));
        }
        final int iterations = runs.getIterations();
        for (int i = 0; i < runs.getTotal(); i++) {
            com.opensymphony.oscache.base.Cache c = runs.get(i);
            for (int j = 0; j < iterations; j++) {
                c.putInCache(getString(j), getInt(j));
            }
        }
        runs.stop();
    }

    public String toString() {
        return "OSCache 2.3.2 (" + Cache.class.getCanonicalName().toString() + ")";
    }

}
