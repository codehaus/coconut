/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.UnsynchronizedCache;

/**
 * Example showing how to specify a default time to live for a cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheTimeoutExample {
    public static void main(String[] args) {
        // START SNIPPET: class
        CacheConfiguration<String, String> cc = CacheConfiguration.create();
        cc.expiration().setDefaultTimeToLive(24 * 60 * 60, TimeUnit.SECONDS);
        Cache<String, String> cache = cc.newCacheInstance(UnsynchronizedCache.class);
        cache.put("key", "value"); // element will expire after 24 hours
        // END SNIPPET: class
    }
}
