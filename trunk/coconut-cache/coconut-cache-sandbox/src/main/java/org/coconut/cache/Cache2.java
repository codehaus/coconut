/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.concurrent.locks.ReadWriteLock;

import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface Cache2<K, V> extends Cache<K, V> {

    /**
     * Tries to acquire a lock on either the cache instance, a single item in
     * the cache, or a set of items in the cache depending on the specified
     * arguments. This operation is optional.
     * <p>
     * getLock() : A call to getLock() with no arguments will return a lock that
     * can be used to acquire a lock on the whole cache. Read and write lock
     * should have semantics as the getLock(all items). Furthermore there are
     * the additional constraints. Locks cannot be acquired on non existing
     * items
     * <p>
     * getLock(k) : a call to getLock() with a single key argument will return a
     * lock that can be used to acquire a lock on the cache entry with the
     * specified key. trying to acquire a lock on an item that does not exist
     * will return a d
     * <p>
     * getLock(k1, k2, k3) = a call to getLock() with multiple key arguments
     * will return a lock that can be used to acquire a lock on all the cache
     * entries with the specified keys. //use lock comperator or natural
     * ordering What about locks on non existing keys??? we might for example
     * want to insert 2,3,4,5 and then unlock when all have been inserted. We
     * could use finalization/weak queue. Using this method for locking multiple
     * elements is prefereble to locking one element at a time because it avoids
     * potential deadlock. The
     * {@link java.util.concurrent.locks.Lock#newCondition()} method is not
     * supported by the returned lock. And any invocation of the method will
     * throw an {@link java.lang.UnsupportedOperationException}
     * <p>
     * TODO Sematics of read lock versus write lock
     * <p>
     * Unless otherwise specified the lock is held by the thread that locks the
     * lock.
     * <p>
     * Might want to set useOnlyWriteLock=true to save the overhead of a full
     * read-writelock implementation, this should be adaptive... so for example.
     * 
     * @param keys
     * @return a read write lock that can be used to lock a specific element,
     *         elements or the whole cache depending on the usage
     * @throws IllegalArgumentException
     *             if multiple key arguments are no natural order exists among
     *             the keys and no lock key comparator has been specified for
     *             the cache
     * @throws NullPointerException
     *             if any of the specified keys are <tt>null</tt>.
     * @throws UnsupportedOperationException
     *             if the <tt>getLock</tt> method is not supported by this
     *             cache
     */
    ReadWriteLock getLock(K... keys);

    /**
     * This method is used to make queries into the cache locating cache entries
     * that match a particular criteria. The returned {@link CacheQuery} can be
     * used for returning all the matching cache entries at once or just a small
     * subset (paging functionality) at a time.
     * <p>
     * NOTICE: querying a cache can be a very time consuming affair especially
     * if no usefull indexes are available at query time.
     * <p>
     * If a backend stored is configured <tt>and</tt> it supports querying it
     * will be used for querying otherwise only the local cache will be queried.
     * 
     * @param filter
     *            the filter used to identify which entries should be retrieved
     * @return a cache query that can be used to retrieve the matching entries
     */
    CacheQuery<K, V> query(Filter<? super CacheEntry<K, V>> filter);
}
