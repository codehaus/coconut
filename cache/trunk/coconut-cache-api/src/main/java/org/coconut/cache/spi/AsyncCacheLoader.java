/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.core.AttributeMap;
import org.coconut.core.Callback;

/**
 * An asynchronous CacheLoader. Normally users do not define asynchronously
 * cache loaders themself. Instead they implement {@link CacheLoader} and a
 * specified {@link java.util.concurrent.Executor} takes care of loading the
 * values. However, some services such as an NIO based cache loader, where
 * entries are asynchronously loaded over the network. This interface might be
 * useful.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface AsyncCacheLoader<K, V> extends CacheLoader<K, V> {

    /**
     * Loads a single value asynchronously.
     * 
     * @param key
     *            the key whose associated value is to be returned.
     * @param c
     *            the callback that is called once the request is completed
     * @return a Future representing pending completion of the load, and
     *         whose <tt>get()</tt> method will return <tt>null</tt> upon
     *         completion.
     */
    Future<?> asyncLoad(final K key, AttributeMap attributes, Callback<V> c);

    Future<?> asyncLoadAll(final Map<? extends K, AttributeMap> keysWithAttributes, Callback<Map<K, V>> c);

}
