/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.management;

//START SNIPPET: class
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.SynchronizedCache;

public class EnableManagement {
    public static void main(String[] args) throws InterruptedException {
        CacheConfiguration<String, String> conf = CacheConfiguration.create("ManagementTest");
        conf.management().setEnabled(true);
        Cache<String, String> cache = conf.newCacheInstance(SynchronizedCache.class);
        cache.put("hello", "world");
        Thread.sleep(10 * 60 * 1000); // sleep 10 minutes
    }
}
//END SNIPPET: class
