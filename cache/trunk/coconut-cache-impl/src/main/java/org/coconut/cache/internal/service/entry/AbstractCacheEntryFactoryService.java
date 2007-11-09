/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
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

    private final InternalCacheExceptionService<K, V> errorHandler;

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
        this.errorHandler = exceptionHandler;
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
     * Creates a new cache entry from the specified key, value, attribute map and existing
     * cache entry.
     * 
     * @param key
     *            the key of the cache entry that should be added
     * @param value
     *            the value of the cache entry that should be added
     * @param attributes
     *            the attributemap for the new cache entry
     * @param existing
     *            the existing cache entry, or null if this is a new entry
     * @return a new cache entry from the specified key, value, attribute map and existing
     *         cache entry
     */

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
        try {
            return CacheAttributes.getSize(attributes);
        } catch (IllegalArgumentException iae) {
            errorHandler.getExceptionHandler().handleWarning(errorHandler.createContext(),
                    iae.getMessage() + " was added for key = " + key);
            return CacheAttributes.DEFAULT_SIZE;
        }
    }

    long getTimeToLive(long expirationTimeNanos, K key, V value, AttributeMap attributes,
            CacheEntry<K, V> existing) {
        try {
            expirationTimeNanos = CacheAttributes.getTimeToLive(attributes, TimeUnit.NANOSECONDS,
                    expirationTimeNanos);
        } catch (IllegalArgumentException iae) {
            errorHandler.getExceptionHandler().handleWarning(errorHandler.createContext(),
                    iae.getMessage() + " was added for key = " + key);
        }

        return expirationTimeNanos == Long.MAX_VALUE ? Long.MAX_VALUE : clock.getDeadlineFromNow(
                expirationTimeNanos, TimeUnit.NANOSECONDS);
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
        try {
            return CacheAttributes.getCost(attributes);
        } catch (IllegalArgumentException iae) {
            errorHandler.getExceptionHandler().handleWarning(errorHandler.createContext(),
                    iae.getMessage() + " was added for key = " + key);
            return CacheAttributes.DEFAULT_COST;
        }
    }

    long getHits(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        try {
            return CacheAttributes.getHits(attributes);
        } catch (IllegalArgumentException iae) {
            errorHandler.getExceptionHandler().handleWarning(errorHandler.createContext(),
                    iae.getMessage() + " was added for key = " + key);
            return clock.timestamp();
        }
    }

    long getLastModified(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        try {
            return CacheAttributes.getLastModified(attributes, clock);
        } catch (IllegalArgumentException iae) {
            errorHandler.getExceptionHandler().handleWarning(errorHandler.createContext(),
                    iae.getMessage() + " was added for key = " + key);
            return clock.timestamp();
        }
    }

    long getTimeToRefresh(long refreshTimeNanos, K key, V value, AttributeMap attributes,
            CacheEntry<K, V> existing) {
        try {
            refreshTimeNanos = CacheAttributes.getTimeToRefresh(attributes, TimeUnit.NANOSECONDS,
                    refreshTimeNanos);
        } catch (IllegalArgumentException iae) {
            errorHandler.getExceptionHandler().handleWarning(errorHandler.createContext(),
                    iae.getMessage() + " was added for key = " + key);
        }
        return clock.getDeadlineFromNow(refreshTimeNanos, TimeUnit.NANOSECONDS);
    }

    long getCreationTime(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        long creationTime = attributes.getLong(CacheAttributes.CREATION_TIME);
        if (creationTime < 0) {
            errorHandler.getExceptionHandler().handleWarning(
                    errorHandler.createContext(),
                    "Must specify a positive creation time [Attribute="
                            + CacheAttributes.CREATION_TIME + " , creationtime = " + creationTime
                            + " for key = " + key);
        }
        if (creationTime > 0) {
            return creationTime;
        } else if (existing != null) {
            return existing.getCreationTime();
        } else {
            return clock.timestamp();
        }
    }

    long getAccessTimeStamp(AbstractCacheEntry<K, V> entry) {
        return clock.timestamp();
    }
}
