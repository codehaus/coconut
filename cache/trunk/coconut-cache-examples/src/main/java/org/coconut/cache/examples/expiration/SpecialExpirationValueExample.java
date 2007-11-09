/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheServices;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.service.expiration.CacheExpirationService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class SpecialExpirationValueExample {
    public static void main(String[] args) {
        // START SNIPPET: class
        CacheConfiguration<String, String> cc = CacheConfiguration.create();
        cc.expiration().setDefaultTimeToLive(24 * 60 * 60, TimeUnit.SECONDS);
        Cache<String, String> cache = cc.newCacheInstance(UnsynchronizedCache.class);

        CacheExpirationService<String, String> e = CacheServices.expiration(cache);
        e.put("key1", "value", CacheExpirationService.DEFAULT_EXPIRATION,
                TimeUnit.SECONDS);
        // element will expire after 24 hours

        e.put("key2", "value", CacheExpirationService.NEVER_EXPIRE, TimeUnit.SECONDS);
        // element will never expire
        // END SNIPPET: class
    }
}
