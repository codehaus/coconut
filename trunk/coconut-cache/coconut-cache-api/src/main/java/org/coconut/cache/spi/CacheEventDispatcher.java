/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.cache.spi;

import org.coconut.cache.CacheEntry;

/**
 * This interface is subject to change,
 * TODO Should have the eventbus build in?
 * TODO take cache entry instead of key, value
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheEventDispatcher<K, V> {

    boolean doNotifyAdded();

    boolean doNotifyClear();

    boolean doNotifyAccessed();

    boolean doNotifyChanged();

    boolean doNotifyRemoved();

    /**
     * Whether or not to include oldValue item for notifyPut
     */
    boolean showOldValueForChanged();

    void notifyAccessed(long sequenceID, K key, V value, CacheEntry<K,V> entry, boolean wasHit);

    void notifyAdded(long sequenceID, K key, V value, CacheEntry<K,V> entry);

    void notifyChanged(long sequenceID, K key, V value, V oldValue, CacheEntry<K,V> entry);

    void notifyRemoved(long sequenceID, K key, V value, boolean isExpired, CacheEntry<K,V> entry);
}