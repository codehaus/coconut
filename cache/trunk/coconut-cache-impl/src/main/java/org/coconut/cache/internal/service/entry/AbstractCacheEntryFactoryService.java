/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import java.util.concurrent.TimeUnit;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.AttributeMaps;
import org.coconut.attribute.common.CostAttribute;
import org.coconut.attribute.common.DateCreatedAttribute;
import org.coconut.attribute.common.DateLastModifiedAttribute;
import org.coconut.attribute.common.HitsAttribute;
import org.coconut.attribute.common.SizeAttribute;
import org.coconut.attribute.common.TimeToLiveAttribute;
import org.coconut.attribute.common.TimeToRefreshAttribute;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.core.Clock;

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
public abstract class AbstractCacheEntryFactoryService<K, V> extends AbstractCacheLifecycle
        implements InternalCacheEntryService<K, V> {

    /** Used for calculating timestamps. */
    private final Clock clock;

    private final InternalCacheExceptionService<K, V> exceptionService;

    /**
     * Creates a new AbstractCacheEntryFactoryService.
     * 
     * @param clock
     *            the clock used to calculate time stamps
     * @param exceptionHandler
     * @param expirationService
     * @param loadingService
     */
    public AbstractCacheEntryFactoryService(Clock clock,
            InternalCacheExceptionService<K, V> exceptionHandler) {
        this.clock = clock;
        this.exceptionService = exceptionHandler;
    }

    /**
     * Creates a new empty AttributeMap.
     * 
     * @return a new empty AttributeMap
     */
    public AttributeMap createMap() {
        return new AttributeMaps.DefaultAttributeMap();
    }

    /**
     * Creates a new AttributeMap populated containing the entries specified in the
     * provided attribute map.
     * 
     * @param copyFrom
     *            the map to copy entries from
     * @return a new AttributeMap populated containing the entries specified in the
     *         provided attribute map
     */
    public AttributeMap createMap(AttributeMap copyFrom) {
        return new AttributeMaps.DefaultAttributeMap(copyFrom);
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
            exceptionService.getExceptionHandler().handleWarning(exceptionService.createContext(),
                    "An illegal size was added for key = " + key);
            size = SizeAttribute.DEFAULT_VALUE;
        }
        return size;
    }

    long getTimeToLive(long expirationTimeNanos, K key, V value, AttributeMap attributes,
            CacheEntry<K, V> existing) {
        long nanos = TimeToLiveAttribute.INSTANCE.getPrimitive(attributes,
                TimeUnit.NANOSECONDS, expirationTimeNanos);

        if (!TimeToLiveAttribute.INSTANCE.isValid(nanos)) {
            exceptionService.getExceptionHandler().handleWarning(exceptionService.createContext(),
                    "An illegal expiration time was added for key = " + key);
            nanos = expirationTimeNanos;
        }
        return nanos == Long.MAX_VALUE ? Long.MAX_VALUE : clock.getDeadlineFromNow(nanos,
                TimeUnit.NANOSECONDS);
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
        double cost = CostAttribute.get(attributes);
        if (!CostAttribute.INSTANCE.isValid(cost)) {
            exceptionService.getExceptionHandler().handleWarning(exceptionService.createContext(),
                    "An illegal cost was added for key = " + key);
            cost = CostAttribute.DEFAULT_VALUE;
        }
        return cost;
    }

    long getHits(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        long hits = HitsAttribute.INSTANCE.getPrimitive(attributes);
        if (!HitsAttribute.INSTANCE.isValid(hits)) {
            exceptionService.getExceptionHandler().handleWarning(exceptionService.createContext(),
                    "An illegal hits was added for key = " + key);
            hits = 0;
        }
        return hits;
    }

    long getLastModified(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        long lastModified = DateLastModifiedAttribute.INSTANCE.getPrimitive(attributes);
        if (lastModified == 0) {
            lastModified = clock.timestamp();
        }
        if (!DateLastModifiedAttribute.INSTANCE.isValid(lastModified)) {
            exceptionService.getExceptionHandler().handleWarning(exceptionService.createContext(),
                    "An illegal last modified time was added for key = " + key);
            lastModified = clock.timestamp();
        }
        return lastModified;
    }

    long getTimeToRefresh(long refreshTimeNanos, K key, V value, AttributeMap attributes,
            CacheEntry<K, V> existing) {
        long nanos = TimeToRefreshAttribute.INSTANCE.getPrimitive(attributes,
                TimeUnit.NANOSECONDS, refreshTimeNanos);

        if (!TimeToRefreshAttribute.INSTANCE.isValid(nanos)) {
            exceptionService.getExceptionHandler().handleWarning(exceptionService.createContext(),
                    "An illegal time to refresh time was added for key = " + key);
            nanos = refreshTimeNanos;
        }
        return clock.getDeadlineFromNow(nanos, TimeUnit.NANOSECONDS);
    }

    long getCreationTime(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        long creationTime = DateCreatedAttribute.INSTANCE.getPrimitive(attributes);
        if (creationTime < 0) {
            exceptionService.getExceptionHandler().handleWarning(
                    exceptionService.createContext(),
                    "Must specify a positive creation time [Attribute="
                            + DateCreatedAttribute.INSTANCE + " , creationtime = "
                            + creationTime + " for key = " + key);
        }
        if (creationTime > 0) {
            return creationTime;
        } else if (existing != null) {
            return existing.getCreationTime();
        } else {
            return clock.timestamp();
        }
    }

    public long getAccessTimeStamp(AbstractCacheEntry<K, V> entry) {
        return clock.timestamp();
    }
}
