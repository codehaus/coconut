/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.defaults.memory.UnlimitedCache;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
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
        CacheConfiguration<String, String> cc = CacheConfiguration.newConf();
        cc.expiration().setFilter(new CustomExpirationFilter<String, String>());
        Cache<String, String> cache = cc.create(UnlimitedCache.class);
        cache.put("key", "value");
        // element will expire if has not been accessed with the last 1 hour
    }
    // END SNIPPET: class
}
