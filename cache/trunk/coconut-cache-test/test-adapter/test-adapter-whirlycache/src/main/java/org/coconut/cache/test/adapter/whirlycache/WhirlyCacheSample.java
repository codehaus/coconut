/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.adapter.whirlycache;

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheManager;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
class WhirlyCacheSample {
    public static void main(String[] args) throws Exception {
        Logger.getLogger("com.opensymphony.oscache.base.algorithm.AbstractConcurrentReadCache").setLevel(Level.OFF);
        Logger.getLogger("com.opensymphony.oscache.base.Cache").setLevel(Level.OFF);

        // Use the cache manager to create the default cache
        Cache c = CacheManager.getInstance().getCache();

        // Put an object into the cache
        c.store("yourKeyName", 3);

        // Get the object back out of the cache
        Integer o = (Integer) c.retrieve("yourKeyName");
        System.out.println(o);
        // Shut down the cache manager
        CacheManager.getInstance().shutdown();
    }
}
