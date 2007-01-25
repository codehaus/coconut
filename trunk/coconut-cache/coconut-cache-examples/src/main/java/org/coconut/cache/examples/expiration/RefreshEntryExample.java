/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.UnsynchronizedCache;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class RefreshEntryExample {
    public static void main(String[] args) {
        // START SNIPPET: class
        CacheConfiguration<String, String> cc = CacheConfiguration.create();
        cc.expiration().setRefreshInterval(5 * 60, TimeUnit.SECONDS);
        Cache<String, String> cache = cc.newInstance(UnsynchronizedCache.class);
        cache.put("key", "value", 60 * 60, TimeUnit.SECONDS);
        // element will expire after 1 hours, but should be refreshed 5 minutes
        // before it expires.
        // END SNIPPET: class
    }
}
