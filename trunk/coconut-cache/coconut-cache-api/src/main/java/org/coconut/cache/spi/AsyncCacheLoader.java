/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import org.coconut.cache.CacheLoader;
import org.coconut.core.Callback;

/**
 * Any CacheLoader implementation  
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */

public interface AsyncCacheLoader<K, V> extends CacheLoader<K, V> {
    
    /**
     * @param key
     * @param c
     * @return
     */
    Future<?> asyncLoad(final K key, Callback<V> c);

    Future<?> asyncLoadAll(final Collection<? extends K> keys, Callback<Map<K, V>> c);

}
