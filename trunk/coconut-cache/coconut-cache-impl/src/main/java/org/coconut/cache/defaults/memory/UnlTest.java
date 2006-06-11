/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.defaults.memory;

import static org.coconut.filter.ComparisonFilters.between;
import static org.coconut.filter.ComparisonFilters.greatherThen;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheFilters;
import org.coconut.cache.Caches;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class UnlTest {
    public static void main(String[] args) {
        Cache<Integer, String> c = new UnlimitedCache<Integer, String>();
        for (int i = 0; i < 256;i++) {
            c.put(i, Integer.toHexString(i));
        }
        for (CacheEntry entry : CacheFilters.queryByKey(c, between(40, 45))) {
            System.out.println(entry);
        }
        System.out.println("------");
        
        for (CacheEntry entry : CacheFilters.queryByValue(c, greatherThen("f5"))) {
            System.out.println(entry);
        }

    }
}
