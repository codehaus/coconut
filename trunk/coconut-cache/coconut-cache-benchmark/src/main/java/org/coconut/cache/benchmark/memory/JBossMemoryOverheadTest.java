/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark.memory;

import org.jboss.cache.TreeCache;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class JBossMemoryOverheadTest extends AbstractMemoryOverheadTest {

    /**
     * @see org.coconut.cache.benchmark.memory.MemoryOverheadTest#test(org.coconut.cache.benchmark.memory.MemoryTestResult)
     */
    public void test(MemoryTestResult runs) throws Exception {
        runs.start();

        for (int i = 0; i < runs.getTotal(); i++) {
            runs.set(i, new TreeCache());
        }
        final int iterations = runs.getIterations();
        for (int i = 0; i < runs.getTotal(); i++) {
            TreeCache c = runs.get(i);
            c.startService();
            for (int j = 0; j < iterations; j++) {
                c.put("/foo", getString(j), getInt(j));
            }
        }
        runs.stop();
        for (int i = 0; i < runs.getTotal(); i++) {
            TreeCache c = runs.get(i);
            c.stopService();
            c.destroyService();
        }
    }

    public String toString() {
        return "JBoss Cache 1.4.0 (" + TreeCache.class.getCanonicalName().toString() + ")";
    }
}
