/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.event;

import static org.coconut.cache.internal.service.event.InternalEntryEvent.added;
import static org.coconut.cache.internal.service.event.InternalEntryEvent.evicted;
import static org.coconut.cache.internal.service.event.InternalEntryEvent.expired;
import static org.coconut.cache.internal.service.event.InternalEntryEvent.hit;
import static org.coconut.cache.internal.service.event.InternalEntryEvent.miss;
import static org.coconut.cache.internal.service.event.InternalEntryEvent.removed;
import static org.coconut.cache.internal.service.event.InternalEntryEvent.updated;
import static org.coconut.cache.internal.service.event.InternalEvent.cleared;
import static org.coconut.cache.internal.service.event.InternalEvent.evicted;

import java.util.Collection;
import java.util.Map;

import javax.management.Notification;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.servicemanager.CacheServiceManager;
import org.coconut.cache.service.event.CacheEntryEvent;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.event.CacheEventConfiguration;
import org.coconut.cache.service.event.CacheEventService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
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
public class DefaultCacheEventService<K, V> extends AbstractCacheLifecycle implements
        InternalCacheEventService<K, V> {

    private final boolean doAdd;

    private final boolean doCacheEvict;

    private final boolean doClear;

    private final boolean doEvict;

    private final boolean doExpire;

    private final boolean doHit;

    private final boolean doMiss;

    private final boolean doRemove;

    private final boolean doUpdate;

    private final EventBus<CacheEvent<K, V>> eb = new DefaultEventBus<CacheEvent<K, V>>();

    private final boolean isEnabled;

    private final CacheServiceManager manager;

    private final Offerable<CacheEvent<K, V>> offerable;

    public DefaultCacheEventService(CacheServiceManager manager,
            CacheEventConfiguration co) {
        super(CacheEventConfiguration.SERVICE_NAME);
        isEnabled = co.isEnabled();
        this.manager = manager;
        this.offerable = eb;
        this.doHit = co.isIncluded(CacheEntryEvent.ItemAccessed.class);
        this.doMiss = doHit;
        this.doAdd = co.isIncluded(CacheEntryEvent.ItemAdded.class);
        this.doClear = co.isIncluded(CacheEvent.CacheCleared.class);
        this.doRemove = co.isIncluded(CacheEntryEvent.ItemRemoved.class);
        this.doCacheEvict = co.isIncluded(CacheEvent.CacheEvicted.class);
        this.doExpire = co.isIncluded(CacheEntryEvent.ItemRemoved.class);
        this.doEvict = co.isIncluded(CacheEntryEvent.ItemRemoved.class);
        this.doUpdate = co.isIncluded(CacheEntryEvent.ItemUpdated.class);
    }

    /** {@inheritDoc} */
    public void afterCacheClear(Cache<K, V> cache, long ignoreStarted, int size,
            long capacity, Collection<? extends CacheEntry<K, V>> entries) {
        if (entries != null && doRemove) {
            for (CacheEntry<K, V> entry : entries) {
                removed(cache, entry);
            }
        }
        if (doClear) {
            dispatch(cleared(cache, size, capacity));
        }
    }

    /** {@inheritDoc} */
    public void afterCacheEvict(Cache<K, V> cache, long started, int size,
            int previousSize, long capacity, long previousCapacity,
            Collection<? extends CacheEntry<K, V>> evicted,
            Collection<? extends CacheEntry<K, V>> expired) {
        doEvictAll(cache, evicted);
        doExpireAll(cache, expired);
        if (doCacheEvict) {
            dispatch(evicted(cache, size, previousSize, capacity, previousCapacity));
        }
    }

    /** {@inheritDoc} */
    public void afterGet(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries, K key,
            CacheEntry<K, V> prev, CacheEntry<K, V> newEntry, boolean isExpired) {
        doEvictAll(cache, evictedEntries);
        if (prev == null) {
            if (doMiss) {
                dispatch(miss(cache, key));
            }
            if (newEntry != null && doAdd) {
                dispatch(added(cache, newEntry, true));
            }
        } else {
            if (isExpired) {
                if (doMiss) {
                    dispatch(miss(cache, key));
                }
            } else {
                if (doHit) {
                    dispatch(hit(cache, prev));
                }
            }
        }
    }

    /** {@inheritDoc} */
    public void afterPut(Cache<K, V> cache, long ignoreStarted,
            Collection<? extends CacheEntry<K, V>> entries, CacheEntry<K, V> newEntry,
            CacheEntry<K, V> prev) {
        doEvictAll(cache, entries);
        processRemoved(cache, newEntry, prev);
    }

    /** {@inheritDoc} */
    public void afterPutAll(Cache<K, V> cache, long ignoreStarted,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            Collection<? extends CacheEntry<K, V>> prev,
            Collection<? extends CacheEntry<K, V>> added) {
        doEvictAll(cache, evictedEntries);
        CacheEntry<K, V>[] p = prev.toArray(new CacheEntry[prev.size()]);
        CacheEntry<K, V>[] n = added.toArray(new CacheEntry[added.size()]);
        for (int i = 0; i < p.length; i++) {
            processRemoved(cache, n[i], p[i]);
        }
    }

    /** {@inheritDoc} */
    public void afterRemove(Cache<K, V> cache, long ignoreStarted, CacheEntry<K, V> entry) {
        if (doRemove && entry != null) {
            dispatch(removed(cache, entry));
        }
    }

    /** {@inheritDoc} */
    public void afterReplace(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries,
            CacheEntry<K, V> oldEntry, CacheEntry<K, V> newEntry) {
        doEvictAll(cache, evictedEntries);
        processRemoved(cache, newEntry, oldEntry);
    }

    /** {@inheritDoc} */
    public void afterTrimToSize(Cache<K, V> cache, long started,
            Collection<? extends CacheEntry<K, V>> evictedEntries) {
        doEvictAll(cache, evictedEntries);
    }

    /** {@inheritDoc} */
    public Collection<EventSubscription<CacheEvent<K, V>>> getSubscribers() {
        return eb.getSubscribers();
    }

    /** {@inheritDoc} */
    @Override
    public void registerServices(Map<Class<?>, Object> serviceMap) {
        if (isEnabled) {
            serviceMap.put(CacheEventService.class, this);
        }
    }

    /** {@inheritDoc} */
    public boolean isEnabled() {
        return isEnabled;
    }

    /** {@inheritDoc} */
    public boolean offer(CacheEvent<K, V> element) {
        manager.lazyStart(false);
        return eb.offer(element);
    }

    /** {@inheritDoc} */
    public boolean offerAll(Collection<? extends CacheEvent<K, V>> c) {
        manager.lazyStart(false);
        return eb.offerAll(c);
    }

    /** {@inheritDoc} */
    public void process(CacheEvent<K, V> event) {
        manager.lazyStart(false);
        eb.process(event);
    }

    /** {@inheritDoc} */
    public EventSubscription<CacheEvent<K, V>> subscribe(
            EventProcessor<? super CacheEvent<K, V>> eventHandler) {
        manager.lazyStart(false);
        return eb.subscribe(eventHandler);
    }

    /** {@inheritDoc} */
    public EventSubscription<CacheEvent<K, V>> subscribe(
            EventProcessor<? super CacheEvent<K, V>> eventHandler,
            Filter<? super CacheEvent<K, V>> filter) {
        manager.lazyStart(false);
        return eb.subscribe(eventHandler, filter);
    }

    /** {@inheritDoc} */
    public EventSubscription<CacheEvent<K, V>> subscribe(
            EventProcessor<? super CacheEvent<K, V>> listener,
            Filter<? super CacheEvent<K, V>> filter, String name) {
        manager.lazyStart(false);
        return eb.subscribe(listener, filter, name);
    }

    /** {@inheritDoc} */
    public Collection<EventSubscription<CacheEvent<K, V>>> unsubscribeAll() {
        manager.lazyStart(false);
        return eb.unsubscribeAll();
    }

    private void doEvictAll(Cache<K, V> cache,
            Iterable<? extends CacheEntry<K, V>> entries) {
        if (entries != null && doEvict) {
            for (CacheEntry<K, V> entry : entries) {
                dispatch(evicted(cache, entry));
            }
        }
    }

    private void doExpireAll(Cache<K, V> cache,
            Iterable<? extends CacheEntry<K, V>> entries) {
        if (entries != null && doExpire) {
            for (CacheEntry<K, V> entry : entries) {
                dispatch(expired(cache, entry));
            }
        }
    }

    private void processRemoved(Cache<K, V> cache, CacheEntry<K, V> newEntry,
            CacheEntry<K, V> prev) {
        if (prev == null) {
            if (newEntry != null && doAdd) {
                dispatch(added(cache, newEntry, true));
            }
        } else if (newEntry == null) {
            if (doRemove) {
                dispatch(removed(cache, prev));
            }
        } else if (doUpdate) {
            dispatch(updated(cache, newEntry, prev.getValue()));
        }
    }

    protected void dispatch(CacheEvent<K, V> event) {
        offerable.offer(event);
    }

    public interface NotificationTransformer {
        Notification notification(Object source);
    }
}
