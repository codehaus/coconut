/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.general;

// START SNIPPET: class
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.UnsynchronizedCache;

public class SetNameExample {
    public static void main(String[] args) {
        CacheConfiguration<String, String> cc = CacheConfiguration.create();
        cc.setName("MyCache");
        UnsynchronizedCache<String, String> c = cc.newInstance(UnsynchronizedCache.class);
        System.out.println(c);
    }
}
// END SNIPPET: class

