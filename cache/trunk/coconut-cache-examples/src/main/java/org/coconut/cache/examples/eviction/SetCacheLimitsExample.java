/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.eviction;

import org.coconut.cache.CacheConfiguration;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SetCacheLimitsExample {
    // START SNIPPET: class
    public static void main(String[] args) {
        CacheConfiguration<String, String> cc = CacheConfiguration.create();
        cc.eviction().setMaximumSize(1000);
        cc.eviction().setMaximumCapacity(50000);
    }
    // END SNIPPET: class
}
