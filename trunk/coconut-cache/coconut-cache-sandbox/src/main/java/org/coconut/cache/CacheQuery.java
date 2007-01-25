/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache;

import java.util.Iterator;
import java.util.List;

/**
 * hmm can this in some way be used as a key-value generator??? Im not sure this
 * is more effective then just specifying an iterator. the batched versions are
 * only usefull when there is some latency when creating the values
 * <p>
 * The {@link Iterator#remove()} method is generally not supported unless
 * otherwise specified.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheQuery<K, V> extends Iterator<CacheEntry<K, V>>, Iterable<CacheEntry<K, V>> {

    /**
     * Returns a list with the specified number of results. If the specified
     * number of results to retrieve are larger then the total number of
     * remaining results. The returned list will only contain the remaining
     * results.
     * 
     * @param maxResults
     *            the maximum number of results to return
     * @return a list of results
     * @throws IllegalArgumentException
     *             if the specified number of results is 0 or less
     */
    List<CacheEntry<K, V>> getNext(int maxResults);

    // List<CacheEntry<K, V>> get(int from, int to);

    // invoke

    /**
     * Returns all the remaining cache entries from the current index. The
     * current index will be updated to the end of the list.
     * 
     * @return a list containing all the matching entries from the current index
     */
    List<CacheEntry<K, V>> getAll();

    // TODO create some where you can specify a time out.
    // for example get(32,12,1,TimeUnit.Minute)
    // or just return a future getAsFuture()

    // /**
    // * I think this should come at a later time.
    // * We probably also need it on the cache instance
    // *
    // * @param comp
    // */
    // void setOrder(Comparator<CacheEntry<K, V>> comp);

    /**
     * Returns the total number of cache entries that match the specified
     * filter. What if the total number of elements cannot be specified return
     * Int.max??
     * 
     * @return the total number of cache entries that match the specified filter
     */
    int getTotalCount();

    /**
     * @return how many items we have already returned
     */
    int getCurrentIndex();

    /**
     * @return whether or not any indexes can be used on the query
     */
    boolean isIndexable();
}
