/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.expiration;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.memory.UnlimitedCache;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SpecialExpirationValueExample {
    public static void main(String[] args) {
        // START SNIPPET: class
        CacheConfiguration<String, String> cc = CacheConfiguration.newConf();
        cc.expiration().setDefaultTimeout(24 * 60 * 60, TimeUnit.SECONDS);
        Cache<String, String> cache = cc.create(UnlimitedCache.class);

        cache.put("key1", "value", Cache.DEFAULT_EXPIRATION, TimeUnit.SECONDS);
        // element will expire after 24 hours

        cache.put("key1", "value", Cache.NEVER_EXPIRE, TimeUnit.SECONDS);
        // element will never expire
        // END SNIPPET: class
    }
}
