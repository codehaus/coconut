/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.loading;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.UnsynchronizedCache;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ReloadEntryExample {
    public static void main(String[] args) {
        // START SNIPPET: class
        CacheConfiguration<String, String> cc = CacheConfiguration.create();
        cc.loading().setDefaultTimeToRefresh(55 * 60, TimeUnit.SECONDS);
        Cache<String, String> cache = cc.newCacheInstance(UnsynchronizedCache.class);
        cache.services().expiration().put("key", "value", 60 * 60, TimeUnit.SECONDS);
        // element will expire after 1 hours, but should be reloaded 5 minutes
        // before it expires.
        // END SNIPPET: class
    }
 
}
