/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache;

import java.util.List;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheQuery<K, V> extends Iterable<CacheEntry<K, V>> {

    /**
     * If count>total_number thats okay only total_number is returned
     * 
     * @param count
     * @return
     */
    List<CacheEntry<K, V>> getNext(int maxCount);

  //  List<CacheEntry<K, V>> get(int from, int to);

    //invoke
    
    /**
     * Returns all cache entries from the current index. The current index will
     * be updated to the end of the list.
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
     * filter.
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
