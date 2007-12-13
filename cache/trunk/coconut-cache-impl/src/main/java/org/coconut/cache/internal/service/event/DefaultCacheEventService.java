/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.event;

import static org.coconut.cache.internal.service.event.InternalEntryEvent.added;
import static org.coconut.cache.internal.service.event.InternalEntryEvent.evicted;
import static org.coconut.cache.internal.service.event.InternalEntryEvent.expired;
import static org.coconut.cache.internal.service.event.InternalEntryEvent.removed;
import static org.coconut.cache.internal.service.event.InternalEntryEvent.updated;
import static org.coconut.cache.internal.service.event.InternalEvent.cleared;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.servicemanager.InternalCacheServiceManager;
import org.coconut.cache.service.event.CacheEntryEvent;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEventConfiguration;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.core.EventProcessor;
import org.coconut.core.Offerable;
import org.coconut.event.bus.EventSubscription;
import org.coconut.operations.Ops.Predicate;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class DefaultCacheEventService<K, V> extends AbstractCacheLifecycle implements
        InternalCacheEventService<K, V> {

    private final boolean doAdd;

    private final boolean doClear;

    private final boolean doEvict;

    private final boolean doExpire;

    private final boolean doRemove;

    private final boolean doStart;

    private final boolean doStopped;

    private final boolean doUpdate;

    private final CacheEventBus<CacheEvent<K, V>> eb = new CacheEventBus<CacheEvent<K, V>>();

    private final InternalCacheServiceManager manager;

    private final Offerable<CacheEvent<K, V>> offerable;

    public DefaultCacheEventService(InternalCacheServiceManager manager, CacheEventConfiguration co) {
        this.manager = manager;
        this.offerable = eb;
        this.doAdd = isIncluded(co, CacheEntryEvent.ItemAdded.class);
        this.doClear = isIncluded(co, CacheEvent.CacheCleared.class);
        this.doRemove = isIncluded(co, CacheEntryEvent.ItemRemoved.class);
        this.doExpire = isIncluded(co, CacheEntryEvent.ItemRemoved.class);
        this.doEvict = isIncluded(co, CacheEntryEvent.ItemRemoved.class);
        this.doUpdate = isIncluded(co, CacheEntryEvent.ItemUpdated.class);
        this.doStart = isIncluded(co, CacheEvent.CacheStarted.class);
        this.doStopped = isIncluded(co, CacheEvent.CacheStopped.class);
    }

    private boolean isIncluded(CacheEventConfiguration co, Class c) {
        return co.isIncluded(c);
    }

    /** {@inheritDoc} */
    public void afterCacheClear(Cache<K, V> cache, long timestamp,
            Collection<? extends CacheEntry<K, V>> entries, long previousVolume) {
        if (doRemove) {
            for (CacheEntry<K, V> entry : entries) {
                dispatch(removed(cache, entry));
            }
        }
        if (doClear && entries.size() > 0) {
            dispatch(cleared(cache, entries.size(), previousVolume));
        }
    }

    /** {@inheritDoc} */
    public void afterPurge(Cache<K, V> cache, Collection<? extends CacheEntry<K, V>> expired) {
        doExpireAll(cache, expired);
    }

    /** {@inheritDoc} */
    public void afterPut(Cache<K, V> cache, long ignoreStarted,
            Collection<? extends CacheEntry<K, V>> entries, AbstractCacheEntry<K, V> prev,
            AbstractCacheEntry<K, V> newEntry) {
        doEvictAll(cache, entries);
        put(cache, prev, newEntry);
    }

    /** {@inheritDoc} */
    public void afterPutAll(Cache<K, V> cache, long ignoreStarted,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            Map<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> entries) {
        doEvictAll(cache, evictedEntries);
        for (Map.Entry<AbstractCacheEntry<K, V>, AbstractCacheEntry<K, V>> entry : entries
                .entrySet()) {
            put(cache, entry.getValue(), entry.getKey());
        }
    }

    /** {@inheritDoc} */
    public void afterRemove(Cache<K, V> cache, long ignoreStarted, CacheEntry<K, V> entry) {
        if (doRemove && entry != null) {
            dispatch(removed(cache, entry));
        }
    }

    /** {@inheritDoc} */
    public void afterRemoveAll(Cache<K, V> cache, long started, Collection<CacheEntry<K, V>> entries) {
        for (CacheEntry<K, V> entry : entries) {
            afterRemove(cache, started, entry);
        }

    }

    /** {@inheritDoc} */
    public void afterStart(Cache<K, V> cache) {
        if (doStart) {
            dispatch(InternalEvent.started(cache));
        }
    }

    /** {@inheritDoc} */
    public void afterTrimCache(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries, int previousSize, int newSize,
            long previousVolume, long newVolume) {
        doEvictAll(cache, evictedEntries);
    }

    public void dexpired(Cache<K, V> cache, long started, CacheEntry<K, V> entry) {
        dispatch(expired(cache, entry));
    }

    /** {@inheritDoc} */
    public Collection<EventSubscription<CacheEvent<K, V>>> getSubscribers() {
        return eb.getSubscribers();
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(CacheLifecycle.Initializer cli) {
        cli.registerService(CacheEventService.class, this);
    }

    /** {@inheritDoc} */
    public boolean offer(CacheEvent<K, V> element) {
        return eb.offer(element);
    }

    /** {@inheritDoc} */
    public boolean offerAll(Collection<? extends CacheEvent<K, V>> c) {
        return eb.offerAll(c);
    }

    /** {@inheritDoc} */
    public void process(CacheEvent<K, V> event) {
        eb.process(event);
    }

    @Override
    public void shutdown(Shutdown shutdown) {
        eb.setShutdown();
    }

    /** {@inheritDoc} */
    public EventSubscription<CacheEvent<K, V>> subscribe(
            EventProcessor<? super CacheEvent<K, V>> eventHandler) {
        return eb.subscribe(eventHandler);
    }

    /** {@inheritDoc} */
    public EventSubscription<CacheEvent<K, V>> subscribe(
            EventProcessor<? super CacheEvent<K, V>> eventHandler,
            Predicate<? super CacheEvent<K, V>> filter) {
        return eb.subscribe(eventHandler, filter);
    }

    /** {@inheritDoc} */
    public EventSubscription<CacheEvent<K, V>> subscribe(
            EventProcessor<? super CacheEvent<K, V>> listener,
            Predicate<? super CacheEvent<K, V>> filter, String name) {
        return eb.subscribe(listener, filter, name);
    }

    /** {@inheritDoc} */
    public Collection<EventSubscription<CacheEvent<K, V>>> unsubscribeAll() {
        return eb.unsubscribeAll();
    }

    private void doEvictAll(Cache<K, V> cache, Iterable<? extends CacheEntry<K, V>> entries) {
        if (doEvict) {
            for (CacheEntry<K, V> entry : entries) {
                dispatch(evicted(cache, entry));
            }
        }
    }

    private void doExpireAll(Cache<K, V> cache, Iterable<? extends CacheEntry<K, V>> entries) {
        if (doExpire) {
            for (CacheEntry<K, V> entry : entries) {
                dispatch(expired(cache, entry));
            }
        }
    }

    protected void dispatch(CacheEvent<K, V> event) {
        offerable.offer(event);
    }

    void put(Cache<K, V> cache, AbstractCacheEntry<K, V> prev, AbstractCacheEntry<K, V> newEntry) {
        if (prev == null) {
            if (doAdd && newEntry != null && newEntry.getPolicyIndex() >= 0) {
                dispatch(added(cache, newEntry));
            }
        } else if (prev.getPolicyIndex() >= 0 && newEntry != null
                && newEntry.getPolicyIndex() == -1) {
            if (doRemove) {
                dispatch(removed(cache, prev));
            }
        } else if (doUpdate) {
            dispatch(updated(cache, newEntry, prev.getValue(), false));
        }
    }

    public String toString() {
        return "Event Service";
    }
}
