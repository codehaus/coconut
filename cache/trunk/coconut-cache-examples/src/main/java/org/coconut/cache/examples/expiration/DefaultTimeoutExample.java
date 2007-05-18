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
public class DefaultTimeoutExample {
    public static void main(String[] args) {
        // START SNIPPET: class
        CacheConfiguration<String, String> cc = CacheConfiguration.create();
        cc.expiration().setDefaultTimeToLive(24 * 60 * 60, TimeUnit.SECONDS);
        Cache<String, String> cache = cc.newInstance(UnsynchronizedCache.class);
        cache.put("key", "value"); // element will expire after 24 hours
        // END SNIPPET: class
    }
}
