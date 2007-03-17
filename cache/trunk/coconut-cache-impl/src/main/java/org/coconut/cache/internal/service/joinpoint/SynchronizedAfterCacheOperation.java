/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.joinpoint;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.joinpoint.InternalCacheOperation;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface SynchronizedAfterCacheOperation<K, V> extends InternalCacheOperation<K, V> {
    void afterCacheClearSync(Cache<K, V> cache, long started, int previousSize,
            long previousCapacity, Iterable<? extends CacheEntry<K, V>> entries);
    
}
