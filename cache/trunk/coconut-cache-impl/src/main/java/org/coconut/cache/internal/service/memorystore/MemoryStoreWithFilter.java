/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.memorystore;

import java.util.Collection;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.CacheEntry;
import org.coconut.forkjoin.ParallelArray;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;

public interface MemoryStoreWithFilter<K, V> extends MemoryStoreWithMapping<CacheEntry<K, V>> {

    void clear();

    ParallelArray<CacheEntry<K, V>> removeAll();

    ParallelArray<CacheEntry<K, V>> retainAll(Collection<? super CacheEntry<K, V>> procedure);

    <T> ParallelArray<CacheEntry<K, V>> retainAll(
            Mapper<? super CacheEntry<K, V>, ? extends T> mapper, Collection<? super T> procedure);

    MemoryStoreWithMapping<K> withKeys();

    MemoryStoreWithMapping<V> withValues();

    MemoryStoreWithFilter<K, V> withFilter(Predicate<? super CacheEntry<K, V>> selector);

    MemoryStoreWithFilter<K, V> withFilterOnAttributes(Predicate<? super AttributeMap> selector);

    MemoryStoreWithFilter<K, V> withFilterOnKeys(Predicate<? super K> selector);

    MemoryStoreWithFilter<K, V> withFilterOnValues(Predicate<? super V> selector);
}