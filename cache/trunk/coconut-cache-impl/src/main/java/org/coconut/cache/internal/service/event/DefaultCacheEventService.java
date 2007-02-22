/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.event;

import java.util.Collection;
import java.util.List;

import javax.management.Notification;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.AbstractCacheService;
import org.coconut.cache.internal.service.event.AbstractCacheEvent.AccessedEvent;
import org.coconut.cache.internal.service.event.AbstractCacheEvent.AddedEvent;
import org.coconut.cache.internal.service.event.AbstractCacheEvent.ChangedEvent;
import org.coconut.cache.internal.service.event.AbstractCacheEvent.ClearEvent;
import org.coconut.cache.internal.service.event.AbstractCacheEvent.RemovedEvent;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.core.EventProcessor;
import org.coconut.core.Offerable;
import org.coconut.event.EventBus;
import org.coconut.event.EventSubscription;
import org.coconut.event.defaults.DefaultEventBus;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheEventService<K, V> extends AbstractCacheService<K, V> implements
        CacheEventService<K, V>, InternalEventService<K, V> {

    private final EventBus<CacheEvent<K, V>> eb = new DefaultEventBus<CacheEvent<K, V>>();

    private long eventId;

    private final Offerable<CacheEvent<K, V>> offerable;

    public DefaultCacheEventService(CacheConfiguration<K, V> conf) {
        super(conf);
        this.offerable = eb;
    }

    public void cacheCleared(Cache<K, V> cache, int size, long capacity,
            Iterable<? extends CacheEntry<K, V>> entries) {
        if (entries != null) {
            removedAll(cache, entries);
        }
        CacheEvent<K, V> e = new ClearEvent<K, V>(cache, nextSequenceId(), size);
        dispatch(e);
    }

    public void entriesEvicted(Cache<K, V> cache,
            Collection<? extends CacheEntry<K, V>> list) {

    }

    public void entryEvicted(Cache<K, V> cache, CacheEntry<K, V> entry) {
        CacheEvent<K, V> e = new RemovedEvent<K, V>(cache, entry, nextSequenceId(), entry
                .getKey(), entry.getValue(), false);
        dispatch(e);
    }

    public void expired(Cache<K, V> cache, CacheEntry<K, V> entry) {
        CacheEvent<K, V> e = new RemovedEvent<K, V>(cache, entry, nextSequenceId(), entry
                .getKey(), entry.getValue(), true);
        dispatch(e);
    }

    public void expired(Cache<K, V> cache, Collection<? extends CacheEntry<K, V>> list) {
        for (CacheEntry ce : list) {
            expired(cache, ce);
        }
    }

    public void expiredAndGet(Cache<K, V> cache, K key, CacheEntry<K, V> entry) {
        if (entry == null) {
            AccessedEvent<K, V> e = new AccessedEvent<K, V>(cache, nextSequenceId(),
                    entry, key, null, false);
            dispatch(e);
        } else {
            CacheEvent<K, V> e = new ChangedEvent<K, V>(cache, nextSequenceId(), entry,
                    key, entry.getValue(), entry.getValue());
            dispatch(e);
        }
    }

    public void getAndLoad(Cache<K, V> cache, K key, CacheEntry<K, V> entry) {
        if (entry != null) {
            AccessedEvent<K, V> e = new AccessedEvent<K, V>(cache, nextSequenceId(),
                    entry, key, entry.getValue(), false);
            dispatch(e);
            CacheEvent<K, V> ee = new AddedEvent<K, V>(cache, entry, nextSequenceId(),
                    key, entry.getValue());
            dispatch(ee);
        } else {
            AccessedEvent<K, V> e = new AccessedEvent<K, V>(cache, nextSequenceId(),
                    null, key, null, false);
            dispatch(e);
        }
    }

    public void getHit(Cache<K, V> cache, CacheEntry<K, V> entry) {
        AccessedEvent<K, V> e = new AccessedEvent<K, V>(cache, nextSequenceId(), entry,
                entry.getKey(), entry.getValue(), true);
        dispatch(e);
    }

    /**
     * @see org.coconut.event.EventBus#getSubscribers()
     */
    public Collection<EventSubscription<CacheEvent<K, V>>> getSubscribers() {
        return eb.getSubscribers();
    }

    public boolean isRemoveEventsFromClear() {
        return false;
    }

    /**
     * @see org.coconut.core.Offerable#offer(java.lang.Object)
     */
    public boolean offer(CacheEvent<K, V> element) {
        return eb.offer(element);
    }

    /**
     * @see org.coconut.event.EventBus#offerAll(java.util.Collection)
     */
    public boolean offerAll(Collection<? extends CacheEvent<K, V>> c) {
        return eb.offerAll(c);
    }

    /**
     * @see org.coconut.core.EventProcessor#process(java.lang.Object)
     */
    public void process(CacheEvent<K, V> event) {
        eb.process(event);
    }

    public void put(Cache<K, V> cache, CacheEntry<K, V> newEntry, CacheEntry<K, V> prev) {
        V preVal = prev == null ? null : prev.getValue();

        if (prev == null) {
            CacheEvent<K, V> ee = new AddedEvent<K, V>(cache, newEntry, nextSequenceId(),
                    newEntry.getKey(), newEntry.getValue());
            dispatch(ee);
        } else {
            if (!newEntry.getValue().equals(preVal)) {
                CacheEvent<K, V> e = new ChangedEvent<K, V>(cache, nextSequenceId(),
                        newEntry, newEntry.getKey(), newEntry.getValue(), prev.getValue());
                dispatch(e);
            }
        }
    }

    public void put(Cache<K, V> cache, CacheEntry<K, V> newEntry, CacheEntry<K, V> prev,
            boolean wasAccepted) {
        if (wasAccepted) {
            put(cache, newEntry, prev);
        } else if (prev != null) {
            removed(cache, prev);
        }
    }

    public void removed(Cache<K, V> cache, CacheEntry<K, V> entry) {
        CacheEvent<K, V> e = new RemovedEvent<K, V>(cache, entry, nextSequenceId(), entry
                .getKey(), entry.getValue(), false);
        dispatch(e);
    }

    public void removedAll(Cache<K, V> cache, Iterable<? extends CacheEntry<K, V>> entries) {
        for (CacheEntry<K, V> entry : entries) {
            CacheEvent<K, V> e = new RemovedEvent<K, V>(cache, entry, nextSequenceId(),
                    entry.getKey(), entry.getValue(), false);
            dispatch(e);
        }
    }

    /**
     * @see org.coconut.cache.service.event.CacheEventService#subscribe(org.coconut.core.EventProcessor)
     */
    public EventSubscription<CacheEvent<K, V>> subscribe(
            EventProcessor<? super CacheEvent<K, V>> eventHandler) {
        return eb.subscribe(eventHandler);
    }

    /**
     * @see org.coconut.cache.service.event.CacheEventService#subscribe(org.coconut.core.EventProcessor,
     *      org.coconut.filter.Filter)
     */
    public EventSubscription<CacheEvent<K, V>> subscribe(
            EventProcessor<? super CacheEvent<K, V>> eventHandler,
            Filter<? super CacheEvent<K, V>> filter) {
        return eb.subscribe(eventHandler, filter);
    }

    /**
     * @see org.coconut.event.EventBus#subscribe(org.coconut.core.EventProcessor,
     *      org.coconut.filter.Filter, java.lang.String)
     */
    public EventSubscription<CacheEvent<K, V>> subscribe(
            EventProcessor<? super CacheEvent<K, V>> listener,
            Filter<? super CacheEvent<K, V>> filter, String name) {
        return eb.subscribe(listener, filter, name);
    }

    /**
     * @see org.coconut.cache.service.event.CacheEventService#unsubscribeAll()
     */
    public Collection<EventSubscription<CacheEvent<K, V>>> unsubscribeAll() {
        return eb.unsubscribeAll();
    }

    /**
     * Returns the next id used for sequencing events.
     * 
     * @return the next id used for sequencing events.
     */
    private long nextSequenceId() {
        return ++eventId;
    }

    protected void dispatch(CacheEvent<K, V> event) {
        offerable.offer(event);
    }

    public interface NotificationTransformer {
        Notification notification(Object source);
    }

    /**
     * @see org.coconut.cache.service.event.CacheEventService#publishJMX(org.coconut.cache.service.event.CacheEvent)
     */
    public void publishJMX(CacheEvent<?, ?> event) {
        throw new UnsupportedOperationException();
    }
}
