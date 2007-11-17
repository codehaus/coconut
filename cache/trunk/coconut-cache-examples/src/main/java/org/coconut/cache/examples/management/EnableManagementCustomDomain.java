/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.management;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.SynchronizedCache;

public class EnableManagementCustomDomain {
    public static void main(String[] args) throws InterruptedException {
        CacheConfiguration<String, String> conf = CacheConfiguration.create("ManagementTest");
        // START SNIPPET: class
        conf.management().setEnabled(true).setDomain("com.acme");
        // END SNIPPET: class
        Cache<String, String> cache = conf.newCacheInstance(SynchronizedCache.class);
        cache.put("hello", "world");
        Thread.sleep(10 * 60 * 1000); // sleep 10 minutes
    }
}
// END SNIPPET: class
