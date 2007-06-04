/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.executor;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.executor.Calculator;
import org.coconut.core.EventProcessor;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheExecutorService<K, V> {
    //
    // Future<?> execute(K k, EventHandler<CacheEntry<K, V>> handler);

    Future<?> submit(K key, EventProcessor<CacheEntry<K, V>> handler);

    Future<?> invokeAll(Collection<? extends K> keys,
            EventProcessor<Collection<CacheEntry<K, V>>> handler);

    Future<?> invokeAll(Filter<? super CacheEntry<K, V>> keyFilter,
            EventProcessor<Collection<CacheEntry<K, V>>> handler);

    //need some version with timeout?
    <T> Future<T> submit(K k, Calculator<T, CacheEntry<K, V>> cal);

    void execute(Collection<? extends K> k,
            EventProcessor<Collection<CacheEntry<K, V>>> handler, Executor e);
}
