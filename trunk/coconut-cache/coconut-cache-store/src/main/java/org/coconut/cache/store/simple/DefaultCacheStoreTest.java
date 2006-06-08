/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.store.simple;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DefaultCacheStoreTest {
    public static void main(String[] args) throws Exception {
        System.out.println("starting");
        DefaultCacheStore<String, Integer> dcs = new DefaultCacheStore<String, Integer>();

        
        dcs.store("hey", 234, false);
        System.out.println(dcs.load("hey2"));
        System.out.println(dcs.load("hey"));
        //dcs.loadAsync(key);
    }
}
