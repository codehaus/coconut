/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.ReplacementPolicy;
import org.coconut.cache.internal.service.expiration.AbstractExpirationService;
import org.coconut.cache.service.exceptionhandling.AbstractCacheExceptionHandler;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlingConfiguration;
import org.coconut.core.AttributeMap;
import org.coconut.core.Clock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheEntryFactoryService<K, V> {
    private final Clock clock;

    private final AbstractCacheExceptionHandler<K, V> errorHandler;

    private final AbstractExpirationService<K, V> expirationService;

    public AbstractCacheEntryFactoryService(Clock clock,
            CacheExceptionHandlingConfiguration<K, V> conf,
            AbstractExpirationService<K, V> expirationService) {
        this.clock = clock;
        this.errorHandler = conf.getExceptionHandler();
        this.expirationService = expirationService;
    }

    public V putVersion(K key, V value, long version) {
        return value;
    }

    public abstract AbstractCacheEntry<K, V> createEntry(K key, V value,
            AttributeMap attributes, AbstractCacheEntry<K, V> existing);

    long getSize(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        try {
            return CacheAttributes.getSize(attributes);
        } catch (IllegalArgumentException iae) {
            errorHandler.warning(iae.getMessage() + " was added for key = " + key);
            return ReplacementPolicy.DEFAULT_SIZE;
        }
    }

    long getTimeToLive(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        long time = expirationService.innerGetExpirationTime();
        try {
            time = CacheAttributes.getTimeToLive(attributes, TimeUnit.NANOSECONDS, time);
        } catch (IllegalArgumentException iae) {
            errorHandler.warning(iae.getMessage() + " was added for key = " + key);
        }
        return clock.getDeadlineFromNow(time, TimeUnit.NANOSECONDS);
    }

    double getCost(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        try {
            return CacheAttributes.getCost(attributes);
        } catch (IllegalArgumentException iae) {
            errorHandler.warning(iae.getMessage() + " was added for key = " + key);
            return ReplacementPolicy.DEFAULT_COST;
        }
    }

    long getLastModified(K key, V value, AttributeMap attributes,
            CacheEntry<K, V> existing) {
        try {
            return CacheAttributes.getLastModified(attributes, clock);
        } catch (IllegalArgumentException iae) {
            errorHandler.warning(iae.getMessage() + " was added for key = " + key);
            return clock.timestamp();
        }
    }

    long getCreationTime(K key, V value, AttributeMap attributes,
            CacheEntry<K, V> existing) {
        long creationTime = attributes.getLong(CacheAttributes.CREATION_TIME);
        if (creationTime < 0) {
            errorHandler.warning("Must specify a positive creation time [Attribute="
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
