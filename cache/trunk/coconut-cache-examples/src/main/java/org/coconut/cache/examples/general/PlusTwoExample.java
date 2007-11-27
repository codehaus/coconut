/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.general;

// START SNIPPET: class
import org.coconut.attribute.AttributeMap;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.UnsynchronizedCache;
import org.coconut.cache.service.loading.AbstractCacheLoader;

public class PlusTwoExample {
    static class Plus2Loader extends AbstractCacheLoader<Integer, Integer> {
        public Integer load(Integer key, AttributeMap ignore) throws Exception {
            return key + 2;
        }
    }

    public static void main(String[] args) {
        CacheConfiguration<Integer, Integer> cc = CacheConfiguration.create();
        cc.loading().setLoader(new Plus2Loader());
        UnsynchronizedCache<Integer, Integer> c = cc
                .newCacheInstance(UnsynchronizedCache.class);
        System.out.println(c.get(5)); // prints 7
        System.out.println(c.get(8));// prints 10
    }
}
// END SNIPPET: class
