/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.benchmark.sample;

import org.jboss.cache.TreeCache;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class JbossTester {
    public static void main(String[] args) throws Exception {
        TreeCache tree = new TreeCache();
        TreeCache tree2 = new TreeCache();
        tree.startService(); // kick start tree cache
        tree2.startService();
        tree.put("/a", "ben", "me"); // create a cache entry.
        System.out.println(tree.get("/a","ben"));
        System.out.println(tree2.get("/a","ben"));
        // cache.getClusterName();
    }
}
