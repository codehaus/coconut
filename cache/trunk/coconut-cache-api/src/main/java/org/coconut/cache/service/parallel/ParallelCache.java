/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.parallel;

import org.coconut.cache.CacheEntry;
import org.coconut.operations.Ops.Mapper;
import org.coconut.operations.Ops.Predicate;
import org.coconut.operations.Ops.Procedure;

public abstract class ParallelCache<K, V> /* implements Iterable<CacheEntry<K, V>> */{
    /**
     * Applies the given procedure to all elements in the cache
     *
     * @param procedure
     *            the procedure
     */
    public abstract void apply(Procedure<? super CacheEntry<K, V>> procedure);

    public abstract int size();// return long instead???

    public abstract WithKeyValues<K> withKeys();
    public abstract WithKeyValues<V> withValues();
    public abstract WithFilter<K, V> withFilter(Predicate<? super CacheEntry<K, V>> selector);
    public abstract <U> WithMapping<U> withMapping(
            Mapper<? super CacheEntry<K, V>, ? extends U> mapper);

    public static abstract class WithFilter<K, V> {
        public abstract WithFilter<K, V> withFilter(Predicate<? super WithFilter<K, V>> selector);
        public abstract void apply(Procedure<? super CacheEntry<K, V>> procedure);
        public abstract int size();// return long instead???
    }

    public static abstract class WithMapping<T> {
        public abstract void apply(Procedure<? super T> procedure);
        public abstract int size();// return long instead???
    }

    public static abstract class WithKeyValues<T> {
        public abstract void apply(Procedure<? super T> procedure);
        public abstract int size();// return long instead???
    }
}
