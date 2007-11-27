/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import org.coconut.attribute.AttributeMap;

public interface InternalCacheEntryService<K, V> {

    AbstractCacheEntry<K, V> createEntry(K key, V value, AttributeMap attributes,
            AbstractCacheEntry<K, V> existing);

    /**
     * Creates a new AttributeMap populated containing the entries specified in the
     * provided attribute map.
     * 
     * @param copyFrom
     *            the map to copy entries from
     * @return a new AttributeMap populated containing the entries specified in the
     *         provided attribute map
     */
    AttributeMap createMap(AttributeMap copyFrom);

    /**
     * Creates a new empty AttributeMap.
     * 
     * @return a new empty AttributeMap
     */
    AttributeMap createMap();

    void setDefaultTimeToLiveNs(long nanos);

    long getDefaultTimeToLiveTimeNs();

    void setTimeToRefreshNs(long nanos);

    long getTimeToRefreshNs();
    long getAccessTimeStamp(AbstractCacheEntry<K, V> entry);
}
