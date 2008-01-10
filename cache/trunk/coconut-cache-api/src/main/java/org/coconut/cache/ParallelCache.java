/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.Iterator;

import org.coconut.cache.service.memorystore.MemoryStoreService;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.MapperToLong;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;

/**
 * @param <K>
 * @param <V>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @see MemoryStoreService#getParallelCache()
 */
public abstract class ParallelCache<K, V> implements Iterable<CacheEntry<K, V>> {

    public abstract void apply(Procedure<? super CacheEntry<K, V>> procedure);
    public abstract int size();
    public abstract long volume();
    public abstract <U> WithMapping<U> withMapping(
            Mapper<? super CacheEntry<K, V>, ? extends U> mapper);

    public abstract WithFilter<K, V> withFilter(Predicate<? super CacheEntry<K, V>> selector);
    public abstract WithMapping<K> withKeys();
    public abstract WithMapping<V> withValues();

    public static abstract class WithFilter<K, V> extends WithMapping<CacheEntry<K, V>> {
        public abstract WithFilter<K, V> withFilter(Predicate<? super CacheEntry<K, V>> selector);
        public abstract WithMapping<K> withKeys();
        public abstract WithMapping<V> withValues();
    }

    public static abstract class WithMapping<T> {
        public abstract void apply(Procedure<? super T> procedure);
        public abstract int size();
        public abstract long volume();
        public abstract <U> WithMapping<U> withMapping(Mapper<? super T, ? extends U> mapper);
        public abstract Iterator<T> sequentially();
      //  public abstract WithLongMapping withMapping(MapperToLong<? super T> mapper);
    }
    public static abstract class WithLongMapping {
        public abstract long sum();
    }
}
