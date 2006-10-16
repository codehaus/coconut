/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.defaults.support;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheEvent;
import org.coconut.cache.spi.CacheEventDispatcher;
import org.coconut.event.bus.EventBus;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EventSupport<K, V> {
    private final CacheEventDispatcher<K, V> ed = null;

    private long eventId;

    /**
     * Returns the next id used for sequencing events.
     * 
     * @return the next id used for sequencing events.
     */
    private long nextSequenceId() {
        return ++eventId;
    }

    public EventBus<CacheEvent<K, V>> getEventBus() {
        return null;
    }

    public EventSupport(CacheConfiguration<K, V> conf) {

    }

    public void cleared(Cache<K, V> cache, int size) {

    }

    public void evicted(Cache<K, V> cache, int size) {

    }
    public void put(Cache<K, V> cache, CacheEntry<K, V> newEntry, CacheEntry<K,V> prev) {
        V preVal = prev == null ? null : prev.getValue();

        if (prev == null) {
            if (ed.doNotifyAdded()) {
                ed.notifyAdded(nextSequenceId(), newEntry.getKey(), newEntry.getValue(),
                        newEntry);
            }
        } else {
            if (ed.doNotifyChanged() && !newEntry.getValue().equals(preVal)) {
                ed.notifyChanged(eventId++, newEntry.getKey(), newEntry.getValue(),
                        preVal, newEntry);
            }
        }

    }
    public void getHit(Cache<K, V> cache, CacheEntry<K, V> entry) {
        ed.notifyAccessed(nextSequenceId(), key, entry.getValue(), entry, true);
    }

    public void expiredAndGet(Cache<K, V> cache, K key, CacheEntry<K, V> entry) {
        if (entry == null) {
            ed.notifyAccessed(nextSequenceId(), key, null, null, false);

        } else {
            ed.notifyChanged(nextSequenceId(), key, loadEntry.getValue(), entry
                    .getValue(), newEntry);
        }
    }

    public void getAndLoad(Cache<K, V> cache, K key, CacheEntry<K, V> entry) {
        ed.notifyAccessed(nextSequenceId(), key, value, entry, false);
        if (entry != null) {
            ed.notifyAdded(nextSequenceId(), key, value, entry);
        }
    }

    public void expired(Cache<K, V> cache, CacheEntry<K, V> entry) {
        V value = entry == null ? null : entry.getValue();
        if (value != null && ed.doNotifyRemoved()) {
            ed.notifyRemoved(nextSequenceId(), entry.getKey(), value, true, entry);
        }
    }

    public void removed(Cache<K, V> cache, CacheEntry<K, V> entry) {
        V value = entry == null ? null : entry.getValue();
        if (value != null && ed.doNotifyRemoved()) {
            ed.notifyRemoved(nextSequenceId(), entry.getKey(), value, false, entry);
        }
    }
}
