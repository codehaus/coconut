/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import java.util.Map;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ImmutableCacheEntry<K, V> implements CacheEntry<K, V> {

	private final K key;

	private final V value;

	private final long expirationTime;

	private final long creationTime;

	private final long hits;

	private final long lastAccessTime;

	private final long lastUpdateTime;

	private final long version;

	private final double cost;

	private final long size;

	/**
     * @param key
     * @param value
     */
	public ImmutableCacheEntry(AbstractCacheEntry<K, V> entry) {
		this.cost = entry.getCost();
		this.creationTime = entry.getCreationTime();
		this.expirationTime = entry.getExpirationTime();
		this.hits = entry.getHits();
		this.key = entry.getKey();
		this.lastAccessTime = entry.getLastAccessTime();
		this.lastUpdateTime = entry.getLastUpdateTime();
		this.size = entry.getSize();
		this.value = entry.getValue();
		this.version = entry.getVersion();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Map.Entry))
			return false;
		Map.Entry e = (Map.Entry) o;
		Object k1 = getKey();
		Object k2 = e.getKey();
		// we keep null checks, might later want to use Map.Entry instead of
		// Entry to compare with
		if (k1 == k2 || (k1 != null && k1.equals(k2))) {
			Object v1 = value;
			Object v2 = e.getValue();
			if (v1 == v2 || (v1 != null && v1.equals(v2)))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return key.hashCode() ^ value.hashCode();
	}

	/**
     * @return the cost
     */
	public double getCost() {
		return cost;
	}

	/**
     * @return the creationTime
     */
	public long getCreationTime() {
		return creationTime;
	}

	/**
     * @return the expirationTime
     */
	public long getExpirationTime() {
		return expirationTime;
	}

	/**
     * @return the hits
     */
	public long getHits() {
		return hits;
	}

	/**
     * @return the key
     */
	public K getKey() {
		return key;
	}

	/**
     * @return the lastAccessTime
     */
	public long getLastAccessTime() {
		return lastAccessTime;
	}

	/**
     * @return the lastUpdateTime
     */
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	/**
     * @return the size
     */
	public long getSize() {
		return size;
	}

	/**
     * @return the value
     */
	public V getValue() {
		return value;
	}

	/**
     * @see java.util.Map.Entry#setValue(java.lang.Object)
     */
	public V setValue(V value) {
		throw new UnsupportedOperationException("setValue not supported");
	}

}
