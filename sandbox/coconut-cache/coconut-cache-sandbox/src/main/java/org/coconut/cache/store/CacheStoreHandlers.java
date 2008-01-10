/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.store;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheStoreHandlers {

    public class FastStoreHandler {
        
        boolean retrieveRemoved(K key, V value) {
            return false;
        }
    }
    public class SlowStoreHandler { //<--- correct store handler
        
        boolean retrieveRemoved(K key, V value) {
            return true;
        }
    }
}