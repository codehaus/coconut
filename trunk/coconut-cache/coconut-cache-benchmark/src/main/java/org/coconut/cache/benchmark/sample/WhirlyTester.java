/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark.sample;

import com.whirlycott.cache.Cache;
import com.whirlycott.cache.CacheException;
import com.whirlycott.cache.CacheManager;

import java.io.PrintWriter;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class WhirlyTester {
    public static void main(String[] args) throws CacheException {

        Logger.getLogger("com.whirlycott.cache.CacheManager").setLevel(Level.DEBUG);
        ConsoleAppender ap = new ConsoleAppender();
        ap.setWriter(new PrintWriter(System.err));
        ap.setLayout(new SimpleLayout());
        Logger.getLogger("com.whirlycott.cache.CacheManager").addAppender(ap);
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
