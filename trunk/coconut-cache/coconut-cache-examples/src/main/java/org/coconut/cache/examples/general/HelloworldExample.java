/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.general;

// START SNIPPET: class
import org.coconut.cache.Cache;
import org.coconut.cache.defaults.memory.UnsynchronizedCache;

public class HelloworldExample {
    public static void main(String[] args) {
        Cache<String, String> c = new UnsynchronizedCache<String, String>();
        c.put("key", "Hello world!");
        System.out.println(c.get("key"));
    }
}
// END SNIPPET: class