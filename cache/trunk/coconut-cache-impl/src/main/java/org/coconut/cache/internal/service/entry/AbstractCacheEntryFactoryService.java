/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import org.coconut.cache.CacheAttributes;
import org.coconut.cache.CacheEntry;
import org.coconut.cache.CacheErrorHandler;
import org.coconut.cache.policy.PolicyAttributes;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.core.AttributeMap;
import org.coconut.core.Clock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheEntryFactoryService<K, V> {
    private Clock clock;

    private CacheErrorHandler<K, V> errorHandler;

    public V putVersion(K key, V value, long version) {
        return value;
    }
    public abstract AbstractCacheEntry<K, V> createEntry(K key, V value,
            AttributeMap attributes, AbstractCacheEntry<K, V> existing);

    long getSize(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        long size = attributes.getLong(PolicyAttributes.SIZE,
                ReplacementPolicy.DEFAULT_SIZE);
        if (size < 0) {
            errorHandler.warning("negative size (size = " + size
                    + ") was added for key = " + key);
            size = ReplacementPolicy.DEFAULT_SIZE;
        }
        return size;
    }

    double getCost(K key, V value, AttributeMap attributes, CacheEntry<K, V> existing) {
        double cost = attributes.getDouble(PolicyAttributes.COST,
                ReplacementPolicy.DEFAULT_COST);
        if (Double.isNaN(cost)) {
            errorHandler.warning("invalid cost (cost = NaN) was added for key = " + key);
            cost = ReplacementPolicy.DEFAULT_COST;
        }
        return cost;
    }

    long getLastModified(K key, V value, AttributeMap attributes,
            CacheEntry<K, V> existing) {
        long time = attributes.getLong(CacheAttributes.LAST_MODIFIED_TIME);
        if (time < 0) {
            errorHandler.warning("Must specify a positive modification time [Attribute="
                    + CacheAttributes.LAST_MODIFIED_TIME + " , modificationtime = "
                    + time + " for key = " + key);
        }
        if (time > 0) {
            return time;
        } else {
            return clock.timestamp();
        }
    }

    long getCreationTime(K key, V value, AttributeMap attributes,
            CacheEntry<K, V> existing) {
        long creationTime = attributes.getLong(CacheAttributes.CREATION_TIME);
        if (creationTime < 0) {
            errorHandler.warning("Must specify a positive creation time [Attribute="
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
