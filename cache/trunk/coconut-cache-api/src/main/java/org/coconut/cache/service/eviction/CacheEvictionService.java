/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.eviction;

import java.util.Collection;

import org.coconut.cache.CacheEntry;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheEvictionService<K, V> extends CacheEvictionMXBean {

	Filter<CacheEntry<K, V>> getEvictionFilter();

	/**
     * Sets a filter that the cache can use to determine if a given cache entry
     * should be evicted. Usage of this method only makes sense if the cache
     * stores entries in a background store. For example, a file on the disk.
     * <p>
     * This method is similar to the #setExpirationFilter(Filter) except that
     * this is only used as a tempory
     * <p>
     * If this cache does
     * 
     * @param filter
     *            the filter to check entries against
     * @see #getEvictionFilter()
     * @throws UnsupportedOperationException
     *             If this cache does not support setting an eviction filter
     */
	void setEvictionFilter(Filter<CacheEntry<K, V>> filter);

	/**
     * <p>
     * If this is an in-memory-only cache calling this method has the same
     * effect as calling {@link java.util.Map#remove(Object)} on the cache.
     * 
     * @param the
     *            key whose mapping is to be evicted from the cache
     */
	void evict(K key);

	/**
     * @param keys
     *            the keys whose mappings are to be evicted from the cache
     */
	void evictAll(Collection<? extends K> keys);
}
