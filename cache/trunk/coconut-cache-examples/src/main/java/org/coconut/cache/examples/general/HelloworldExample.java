/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.general;

// START SNIPPET: class
import org.coconut.cache.Cache;
import org.coconut.cache.defaults.UnsynchronizedCache;

public class HelloworldExample {
    public static void main(String[] args) {
        Cache<String, String> c = new UnsynchronizedCache<String, String>();
        c.put("key", "Hello world!");
        System.out.println(c.get("key"));
    }
}
// END SNIPPET: class
