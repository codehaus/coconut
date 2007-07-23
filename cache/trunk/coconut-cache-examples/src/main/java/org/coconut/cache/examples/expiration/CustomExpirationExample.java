/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.expiration;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.filter.Filter;

public class CustomExpirationExample {
    // START SNIPPET: class
    static class CustomExpirationFilter<K, V> implements Filter<CacheEntry<K, V>> {
        public boolean accept(CacheEntry<K, V> entry) {
            long delta = System.currentTimeMillis() - entry.getLastAccessTime();
            return delta > 60 * 60 * 1000;
            // return true to indicate that the entry has expired
        }
    }

    public static void main(String[] args) {
        CacheConfiguration<String, String> cc = CacheConfiguration.create();
        cc.expiration().setExpirationFilter(new CustomExpirationFilter<String, String>());
        Cache<String, String> cache = cc.newInstance(UnsynchronizedCache.class);
        cache.put("key", "value");
        // element will expire if has not been accessed with the last 1 hour
    }
    // END SNIPPET: class
}
