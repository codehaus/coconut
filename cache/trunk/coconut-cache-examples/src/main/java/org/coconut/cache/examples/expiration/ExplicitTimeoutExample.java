/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheServices;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.service.expiration.CacheExpirationService;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ExplicitTimeoutExample {
    public static void main(String[] args) {
        // START SNIPPET: class
        Cache<String, String> cache = new UnsynchronizedCache<String, String>();
        CacheExpirationService<String, String> e = CacheServices.expiration(cache);
        e.put("key", "value", 60 * 60, TimeUnit.SECONDS);
        // END SNIPPET: class
    }
}
