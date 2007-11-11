/*
 * Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package $package;

import org.coconut.cache.Cache;
import org.coconut.cache.defaults.UnsynchronizedCache;

public class HelloCache {
    public static void main(String[] args) {
        Cache<String, String> cache = new UnsynchronizedCache<String, String>();
        cache.put("key", "HelloWorld");
        System.out.println(cache.get("key"));
    }
}
