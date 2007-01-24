/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.general;

//START SNIPPET: class
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.util.AbstractCacheLoader;

public class PlusTwoExample {
    static class Plus2Loader extends AbstractCacheLoader<Integer, Integer> {
        public Integer load(Integer key) throws Exception {
            return key + 2;
        }
    }

    public static void main(String[] args) {
        CacheConfiguration<Integer, Integer> cc = CacheConfiguration.newConf();
        cc.backend().setBackend(new Plus2Loader());
        UnsynchronizedCache<Integer, Integer> c = cc.create(UnsynchronizedCache.class);
        System.out.println(c.get(5));
        System.out.println(c.get(8));
    }
}
//END SNIPPET: class