/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults.memory;

import java.util.ArrayList;
import java.util.Set;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EarlyExpirationRefresh<K, V> implements Runnable {
    private final Cache<K, V> cache;
    public EarlyExpirationRefresh(final Cache<K, V> cache) {
        this.cache = cache;
    }

    public void run() {
        long now = System.currentTimeMillis();
        ArrayList<K> keys = new ArrayList<K>();
        for (Object o :  cache.entrySet()) {
           CacheEntry<K, V> ce=(CacheEntry) o;
            if (ce.getExpirationTime() - now > 5 * 60 * 1000) {
                keys.add(ce.getKey());
            }
        }
        cache.loadAllAsync(keys);
    }
}
