/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.attribute.common.TimeToLiveAttribute;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
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

        CacheExpirationService<String, String> e = cache.services().expiration();
        cache.put("key1", "value");
        // element will expire after 24 hours

        e.put("key2", "value", TimeToLiveAttribute.FOREVER, TimeUnit.SECONDS);
        // element will never expire
        // END SNIPPET: class
    }
}
