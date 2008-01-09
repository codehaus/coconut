/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import java.util.concurrent.TimeUnit;

import org.coconut.attribute.Attribute;
import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.DefaultAttributeMap;
import org.coconut.attribute.common.CostAttribute;
import org.coconut.attribute.common.DateCreatedAttribute;
import org.coconut.attribute.common.DateModifiedAttribute;
import org.coconut.attribute.common.HitsAttribute;
import org.coconut.attribute.common.SizeAttribute;
import org.coconut.attribute.common.TimeToLiveAttribute;
import org.coconut.attribute.common.TimeToRefreshAttribute;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.CacheInternals;
import org.coconut.cache.internal.InternalCacheEntry;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.service.memorystore.MemoryStoreConfiguration;
import org.coconut.core.Clock;
import org.coconut.operations.Ops.Predicate;

/**
 * An AbstractCacheEntryFactoryService is responsible for creating cache entry instances.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public abstract class AbstractCacheEntryFactoryService<K, V> implements
        InternalCacheEntryService<K, V> {

    /** Used for calculating timestamps. */
    private final Clock clock;

    /** The cache exception service. */
    private final InternalCacheExceptionService<K, V> exceptionService;

    private final Predicate<CacheEntry<K, V>> isCacheable;

    /**
     * Creates a new AbstractCacheEntryFactoryService.
     *
     * @param clock
     *            the clock used to calculate time stamps
     * @param exceptionHandler
     */
    public AbstractCacheEntryFactoryService(Clock clock,
            MemoryStoreConfiguration<K, V> evictionConfiguration,
            InternalCacheExceptionService<K, V> exceptionHandler) {
        this.clock = clock;
        this.isCacheable = evictionConfiguration.getIsCacheableFilter();
        this.exceptionService = exceptionHandler;
    }

    boolean isCacheable(CacheEntry<K, V> entry) {
        boolean result = isCacheable == null;
        if (!result) {
            try {
                result = isCacheable.evaluate(entry);
            } catch (RuntimeException e) {
                exceptionService.fatal("Could not determind if the object was cacheable", e);
            }
        }
        return result;
    }

    public abstract AbstractCacheEntry<K, V> createEntry(K key, V value, AttributeMap attributes,
            InternalCacheEntry<K, V> existing);

    /**
     * Creates a new empty AttributeMap.
     *
     * @return a new empty AttributeMap
     */
    public AttributeMap createMap() {
        return new DefaultAttributeMap();
    }

    /**
     * Creates a new AttributeMap populated containing the entries specified in the
     * specified attribute map.
     *
     * @param copyFrom
     *            the map to copy entries from
     * @return a new AttributeMap populated containing the entries specified in the
     *         provided attribute map
     */
    public AttributeMap createMap(AttributeMap copyFrom) {
        return new DefaultAttributeMap(copyFrom);
    }

    public long getAccessTimeStamp(InternalCacheEntry<K, V> entry) {
        return clock.timestamp();
    }

    private void illegalAttribute(Attribute a, K key, Object illegal, Object defaultValue) {
        String warning = CacheInternals.lookup(AbstractCacheEntryFactoryService.class, "ia", a, key,
                illegal.toString(), defaultValue.toString());
        exceptionService.warning(warning);
    }

    /**
     * Calculates the cost of the element that was added.
     *
     * @param key
     *            the key of the cache entry
     * @param value
     *            the value of the cache entry
     * @param attributes
     *            a map of cache entry attributes
     * @param existing
     *            the existing cache entry, or null if this is a new entry
     * @return the cost of the element that was added
     */
    double getCost(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        double cost = CostAttribute.getCost(attributes);
        if (!CostAttribute.INSTANCE.isValid(cost)) {
            illegalAttribute(CostAttribute.INSTANCE, key, cost, CostAttribute.DEFAULT_VALUE);
            cost = CostAttribute.DEFAULT_VALUE;
        }
        return cost;
    }

    long getCreationTime(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        long creationTime = DateCreatedAttribute.INSTANCE.getLong(attributes);
        final long time;
        if (creationTime > 0) {
            time = creationTime;
        } else if (existing != null) {
            time = existing.getCreationTime();
        } else {
            time = clock.timestamp();
        }
        if (!DateCreatedAttribute.INSTANCE.isValid(creationTime)) {
            illegalAttribute(DateCreatedAttribute.INSTANCE, key, creationTime, time);
        }
        return time;

    }

    long getHits(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        long hits = HitsAttribute.INSTANCE.getLong(attributes);
        if (!HitsAttribute.INSTANCE.isValid(hits)) {
            illegalAttribute(HitsAttribute.INSTANCE, key, hits, HitsAttribute.DEFAULT_VALUE);
            hits = HitsAttribute.DEFAULT_VALUE;
        }
        return hits;
    }

    long getLastModified(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        long lastModified = DateModifiedAttribute.INSTANCE.getLong(attributes);
        if (lastModified == 0) {
            lastModified = clock.timestamp();
        }
        if (!DateModifiedAttribute.INSTANCE.isValid(lastModified)) {
            long illegal = lastModified;
            lastModified = clock.timestamp();
            illegalAttribute(DateModifiedAttribute.INSTANCE, key, illegal, lastModified);
        }
        return lastModified;
    }

    /**
     * Calculates the size of the element that was added.
     *
     * @param key
     *            the key of the cache entry
     * @param value
     *            the value of the cache entry
     * @param attributes
     *            a map of cache entry attributes
     * @param existing
     *            the existing cache entry, or null if this is a new entry
     * @return the size of the element that was added
     */
    long getSize(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        long size = SizeAttribute.get(attributes);
        if (!SizeAttribute.INSTANCE.isValid(size)) {
            illegalAttribute(SizeAttribute.INSTANCE, key, size, SizeAttribute.DEFAULT_VALUE);
            size = SizeAttribute.DEFAULT_VALUE;
        }
        return size;
    }

    long getTimeToLive(long expirationTimeNanos, K key, V value, AttributeMap attributes,
            CacheEntry<K, V> existing) {
        long nanos = TimeToLiveAttribute.INSTANCE.getDuration(attributes, TimeUnit.NANOSECONDS,
                expirationTimeNanos);
        if (!TimeToLiveAttribute.INSTANCE.isValid(nanos)) {
            illegalAttribute(TimeToLiveAttribute.INSTANCE, key, nanos, expirationTimeNanos);
            nanos = expirationTimeNanos;
        }
        return nanos == Long.MAX_VALUE ? Long.MAX_VALUE : clock.getDeadlineFromNow(nanos,
                TimeUnit.NANOSECONDS);
    }

    long getTimeToRefresh(long refreshTimeNanos, K key, V value, AttributeMap attributes,
            CacheEntry<K, V> existing) {
        long nanos = TimeToRefreshAttribute.INSTANCE.getDuration(attributes, TimeUnit.NANOSECONDS,
                refreshTimeNanos);

        if (!TimeToRefreshAttribute.INSTANCE.isValid(nanos)) {
            illegalAttribute(TimeToRefreshAttribute.INSTANCE, key, nanos, refreshTimeNanos);
            nanos = refreshTimeNanos;
        }
        return nanos == Long.MAX_VALUE ? Long.MAX_VALUE : clock.getDeadlineFromNow(nanos,
                TimeUnit.NANOSECONDS);
    }

    protected boolean isCacheAttribute(Attribute a) {
        return a == CostAttribute.INSTANCE || a == DateCreatedAttribute.INSTANCE
                || a == DateModifiedAttribute.INSTANCE || a == HitsAttribute.INSTANCE
                || a == SizeAttribute.INSTANCE || a == TimeToLiveAttribute.INSTANCE
                || a == TimeToRefreshAttribute.INSTANCE;
    }
}
