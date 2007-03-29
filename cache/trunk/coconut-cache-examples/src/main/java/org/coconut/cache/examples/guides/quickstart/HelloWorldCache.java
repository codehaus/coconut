/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
// START SNIPPET: helloworld
package org.coconut.cache.examples.guides.quickstart;

import org.coconut.cache.Cache;
import org.coconut.cache.defaults.UnsynchronizedCache;

public class HelloWorldCache {
    public static void main(String[] args) {
        Cache<Integer, String> cache = new UnsynchronizedCache<Integer, String>();
        cache.put(5, "helloworld");
        System.out.println(cache.get(5)); // prints helloworld
    }
}
//END SNIPPET: helloworld