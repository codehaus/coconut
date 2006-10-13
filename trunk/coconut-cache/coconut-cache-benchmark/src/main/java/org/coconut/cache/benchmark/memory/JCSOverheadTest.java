/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark.memory;

import java.util.Properties;

import org.apache.jcs.JCS;
import org.apache.jcs.engine.CompositeCacheAttributes;
import org.apache.jcs.engine.behavior.ICompositeCacheAttributes;
import org.apache.jcs.engine.control.CompositeCacheManager;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class JCSOverheadTest extends AbstractMemoryOverheadTest {

    static {
        shutupLogger("org.apache.jcs.*");
        shutupLogger("org.apache.jcs.engine.control.CompositeCacheManager");
        shutupLogger("org.apache.jcs.engine.memory.lru.LRUMemoryCache");
        shutupLogger("org.apache.jcs.utils.threadpool.ThreadPoolManager");
        shutupLogger("org.apache.jcs.engine.control.CompositeCacheConfigurator");
        shutupLogger("org.apache.jcs.config.OptionConverter");
        shutupLogger("org.apache.jcs.engine.control.event.ElementEventQueue");
        shutupLogger("org.apache.jcs.engine.control.CompositeCache");
        shutupLogger("org.apache.jcs.utils.struct.DoubleLinkedList");
        shutupLogger("org.apache.jcs.engine.memory.AbstractMemoryCache");
        
        Logger.getLogger("org.apache.jcs.engine.control.CompositeCacheManager").setLevel(Level.OFF);
        Logger.getLogger("org.apache.jcs.engine.memory.lru.LRUMemoryCache").setLevel(Level.OFF);

        CompositeCacheManager cc = CompositeCacheManager.getUnconfiguredInstance();
        Properties props = new Properties();
        props.put("jcs.default", "");
        props.put("jcs.default.cacheattributes",
                "org.apache.jcs.engine.CompositeCacheAttributes");
        props.put("jcs.default.cacheattributes.MemoryCacheName",
                "org.apache.jcs.engine.memory.lru.LRUMemoryCache");
        cc.configure(props);
    }

    /**
     * @see org.coconut.cache.benchmark.memory.MemoryOverheadTest#test(org.coconut.cache.benchmark.memory.MemoryTestResult)
     */
    public void test(MemoryTestResult runs) throws Exception {

        runs.start();
        for (int i = 0; i < runs.getTotal(); i++) {
            ICompositeCacheAttributes cattr = new CompositeCacheAttributes();
            cattr.setMaxObjects(100000);
            runs.set(i, JCS.getInstance("Cache" + System.nanoTime(), cattr));
        }
        final int iterations = runs.getIterations();
        for (int i = 0; i < runs.getTotal(); i++) {
            JCS c = runs.get(i);
            for (int j = 0; j < iterations; j++) {
                c.put(getString(j), getInt(j));
            }
        }
        runs.stop();
        for (int i = 0; i < runs.getTotal(); i++) {
            JCS c = runs.get(i);
            c.dispose();
        }

    }
    

    public String toString() {
        return "JCS 1.2.7.9.2 (" + JCS.class.getCanonicalName().toString() + ")";
    }
}
