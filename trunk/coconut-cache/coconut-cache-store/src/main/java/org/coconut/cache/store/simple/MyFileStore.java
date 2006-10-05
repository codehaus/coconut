/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.store.simple;

import java.io.File;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntryEvent;
import org.coconut.core.EventHandlers;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class MyFileStore {

    public static void main(String[] args) {
        File base = new File(SimpleFileStore.ff);
        if (!base.exists())
            base.mkdir();

        SimpleFileStore sfs = new SimpleFileStore();
        Cache<String, String> c = null;
        // new DefaultSynchronousCache<String, String>(Policies.newFIFO(),2,
        // sfs);
        c.getEventBus().subscribe(EventHandlers.toSystemOut(),
                CacheEntryEvent.ITEM_FILTER);
        c.put("Hello", "World");
        c.put("Hello1", "World");
        c.put("Hello2", "World");
        c.put("Hello3", "World");
        c.put("Hello4", "World");
        System.out.println(c.containsKey("Hello3"));
        System.out.println(c.get("Hello3"));
    }
    
    
  
}
