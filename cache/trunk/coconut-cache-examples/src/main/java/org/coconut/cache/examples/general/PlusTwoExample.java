/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.general;

// START SNIPPET: class
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.service.loading.AbstractCacheLoader;
import org.coconut.core.AttributeMap;

public class PlusTwoExample {
    static class Plus2Loader extends AbstractCacheLoader<Integer, Integer> {
        public Integer load(Integer key, AttributeMap ignore) throws Exception {
            return key + 2;
        }
    }

    public static void main(String[] args) {
        CacheConfiguration<Integer, Integer> cc = CacheConfiguration.create();
        cc.serviceLoading().setLoader(new Plus2Loader());
        UnsynchronizedCache<Integer, Integer> c = cc
                .newInstance(UnsynchronizedCache.class);
        System.out.println(c.get(5)); // prints 7
        System.out.println(c.get(8));// prints 10
    }
}
// END SNIPPET: class
