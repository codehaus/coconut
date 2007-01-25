/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
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

