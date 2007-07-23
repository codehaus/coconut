/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.guides.quickstart;

//START SNIPPET: helloworld
import org.coconut.cache.Cache;
import org.coconut.cache.defaults.UnsynchronizedCache;

public class HelloWorldCache {
    public static void main(String[] args) {
        Cache<Integer, String> cache = new UnsynchronizedCache<Integer, String>();
        cache.put(5, "helloworld");
        System.out.println(cache.get(5)); // prints helloworld
    }
}
// END SNIPPET: helloworld
