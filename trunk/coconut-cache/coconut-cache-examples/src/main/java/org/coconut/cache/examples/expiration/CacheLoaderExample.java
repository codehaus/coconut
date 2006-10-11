/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.expiration;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.defaults.memory.UnlimitedCache;
import org.coconut.cache.util.AbstractCacheLoader;
import org.coconut.cache.util.DefaultCacheEntry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheLoaderExample {
    public static void main(String[] args) {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.newConf();
        cc.backend().setExtendedLoader(new MyLoader());
        Cache<Integer, String> cache = cc.newInstance(UnlimitedCache.class);
        cache.get(4); //item will expire after 1 hour (60 * 60 * 1000)
    }

    static class MyLoader extends
            AbstractCacheLoader<Integer, CacheEntry<Integer, String>> {
        public CacheEntry<Integer, String> load(Integer key) throws Exception {
            return DefaultCacheEntry.entryWithExpiration(key, "val=" + key, System
                    .currentTimeMillis() + 60 * 60 * 1000);
        }
    }
}
