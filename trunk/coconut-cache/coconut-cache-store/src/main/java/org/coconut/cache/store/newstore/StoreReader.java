/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.store.newstore;

import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.coconut.cache.CacheEntry;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface StoreReader<K,V> {
    
    void storeEntry(CacheEntry<K, V> entry, ObjectOutput oo);
    CacheEntry<K, V> loadEntry(ObjectInput oi);
    K loadKey(ObjectInput oi);
    V loadValue(ObjectInput oi);
}
