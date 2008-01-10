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
public interface CacheStoreHandler {

    
    //Decides if we want to return the element already stored in the store
    // for example map#put(k,v) always returns the previous element.
    // but this can be kind of slow if we don't need it. So this
    //method decised if we want to retrieve it
    
    boolean retrieveRemoved(K key, V value); //AttributeMap attributes???
    
}
