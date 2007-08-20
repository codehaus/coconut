/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.attribute.DefaultAttributes;
import org.coconut.cache.internal.service.exceptionhandling.CacheExceptionService;
import org.coconut.cache.internal.service.expiration.DefaultCacheExpirationService;
import org.coconut.cache.internal.service.loading.AbstractCacheLoadingService;
import org.coconut.core.AttributeMap;
import org.coconut.core.Clock;

/**
 * An AbstractCacheEntryFactoryService is responsible for creating cache entry instances.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public abstract class AbstractCacheEntryFactoryService<K, V> {
    /** Used for calculating timestamps. */
    private final Clock clock;

    private final CacheExceptionService<K, V> errorHandler;

    private final DefaultCacheExpirationService<K, V> expirationService;

    private final AbstractCacheLoadingService<K, V> loadingService;

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
            CacheExceptionService<K, V> exceptionHandler,
            DefaultCacheExpirationService<K, V> expirationService,
            AbstractCacheLoadingService<K, V> loadingService) {
        this.clock = clock;
        this.errorHandler = exceptionHandler;
        this.expirationService = expirationService;
        this.loadingService = loadingService;
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
    public abstract AbstractCacheEntry<K, V> createEntry(K key, V value,
            AttributeMap attributes, AbstractCacheEntry<K, V> existing);

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
            errorHandler.getExceptionHandler().handleWarning(
                    errorHandler.createContext(),
                    iae.getMessage() + " was added for key = " + key);
            return CacheAttributes.DEFAULT_SIZE;
        }
    }

    long getTimeToLive(DefaultAttributes atr, K key, V value, AttributeMap attributes,
            CacheEntry<K, V> existing) {
        long time = atr.getExpirationTimeNanos();
        try {
            time = CacheAttributes.getTimeToLive(attributes, TimeUnit.NANOSECONDS, time);
        } catch (IllegalArgumentException iae) {
            errorHandler.getExceptionHandler().handleWarning(
                    errorHandler.createContext(),
                    iae.getMessage() + " was added for key = " + key);
        }

        return time == Long.MAX_VALUE ? Long.MAX_VALUE : clock.getDeadlineFromNow(time,
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
        try {
            return CacheAttributes.getCost(attributes);
        } catch (IllegalArgumentException iae) {
            errorHandler.getExceptionHandler().handleWarning(
                    errorHandler.createContext(),
                    iae.getMessage() + " was added for key = " + key);
            return CacheAttributes.DEFAULT_COST;
        }
    }

    long getHits(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        try {
            return CacheAttributes.getHits(attributes);
        } catch (IllegalArgumentException iae) {
            errorHandler.getExceptionHandler().handleWarning(
                    errorHandler.createContext(),
                    iae.getMessage() + " was added for key = " + key);
            return clock.timestamp();
        }
    }

    long getLastModified(K key, V value, AttributeMap attributes,
            CacheEntry<K, V> existing) {
        try {
            return CacheAttributes.getLastModified(attributes, clock);
        } catch (IllegalArgumentException iae) {
            errorHandler.getExceptionHandler().handleWarning(
                    errorHandler.createContext(),
                    iae.getMessage() + " was added for key = " + key);
            return clock.timestamp();
        }
    }

    long getTimeToRefresh(DefaultAttributes atr, K key, V value, AttributeMap attributes,
            CacheEntry<K, V> existing) {
        long time = atr.getTimeToRefreshNanos();
        try {
            time = CacheAttributes.getTimeToRefresh(attributes, TimeUnit.NANOSECONDS,
                    time);
        } catch (IllegalArgumentException iae) {
            errorHandler.getExceptionHandler().handleWarning(
                    errorHandler.createContext(),
                    iae.getMessage() + " was added for key = " + key);
        }
        return clock.getDeadlineFromNow(time, TimeUnit.NANOSECONDS);
    }

    long getCreationTime(K key, V value, AttributeMap attributes,
            CacheEntry<K, V> existing) {
        long creationTime = attributes.getLong(CacheAttributes.CREATION_TIME);
        if (creationTime < 0) {
            errorHandler.getExceptionHandler().handleWarning(
                    errorHandler.createContext(),
                    "Must specify a positive creation time [Attribute="
                            + CacheAttributes.CREATION_TIME + " , creationtime = "
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

    long getAccessTimeStamp(AbstractCacheEntry<K, V> entry) {
        return clock.timestamp();
    }
}
