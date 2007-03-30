/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.expiration;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.DefaultAttributes;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.service.loading.AbstractCacheLoader;
import org.coconut.core.AttributeMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheEntryLoaderExample {
    // START SNIPPET: class
    static class ExpirationLoader extends AbstractCacheLoader<Integer, String> {
        public String load(Integer key, AttributeMap attributes) throws Exception {
            attributes.putLong(DefaultAttributes.TIME_TO_LIVE_NANO,
                    System.currentTimeMillis() + 60 * 60 * 1000);
            return "val=" + key;
        }
    }

    public static void main(String[] args) {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        cc.serviceLoading().setLoader(new ExpirationLoader());
        Cache<Integer, String> cache = cc.newInstance(UnsynchronizedCache.class);
        cache.get(4); // item will expire after 1 hour (60 * 60 * 1000)
    }
    // END SNIPPET: class
}
