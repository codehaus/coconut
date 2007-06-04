/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.tree;

import org.coconut.cache.CacheConfiguration;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheTreeConfiguration {
    // do we allow to insert items into leaves?
    private boolean allowEntriesInParents = false;

    // do we allow to add tree while running.
    // if false cacheTreeService throws UnsupportedOperationException...
    private boolean allowMutable = false;

    // cache a
    // cache a->b
    // cache a->c
    // if we a.clear() do we only post ClearedEvent(A) or also ClearedEvent(B),
    // and ClearedEvent(C).
    private boolean postRecursiveCacheEvents = false;
    

    //hvordan skal configurations nedarvning foregå???? 

    <K, V> CacheTree<K, V> addChild(CacheTree<K, V> parent, CacheConfiguration<K, V> conf) {
        return null;
    }

    //f.eks. parents eviction policy benyttes med mindre, at et child selv definere en ny.
    
}
