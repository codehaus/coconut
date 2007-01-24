/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.examples.general;

// START SNIPPET: class
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.defaults.UnsynchronizedCache;

public class SetNameExample {
    public static void main(String[] args) {
        CacheConfiguration<String, String> cc = CacheConfiguration.newConf();
        cc.setName("MyCache");
        UnsynchronizedCache<String, String> c = cc.create(UnsynchronizedCache.class);
        System.out.println(c);
    }
}
// END SNIPPET: class

