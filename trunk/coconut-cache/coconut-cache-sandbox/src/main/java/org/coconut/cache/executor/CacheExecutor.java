/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.executor;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.coconut.cache.CacheEntry;
import org.coconut.core.EventProcessor;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
// , CompletionService<T> e
public interface CacheExecutor<K, V> {
    //
    // Future<?> execute(K k, EventHandler<CacheEntry<K, V>> handler);

    Future<?> execute(Filter<? super CacheEntry<K, V>> filter,
            EventProcessor<Collection<CacheEntry<K, V>>> handler);

    Future<?> execute(Collection<? extends K> k,
            EventProcessor<Collection<CacheEntry<K, V>>> handler, Executor e);

    Future<?> submitAll(Collection<? extends K> k,
            EventProcessor<Collection<CacheEntry<K, V>>> handler, Executor e);

    <T> Future<T> submit(K k, Calculator<T, CacheEntry<K, V>> cal);

    void executse(Collection<? extends K> k,
            EventProcessor<Collection<CacheEntry<K, V>>> handler, Executor e);
}
