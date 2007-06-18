/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import org.coconut.cache.internal.service.attribute.InternalCacheAttributeService;
import org.coconut.cache.internal.service.expiration.DefaultCacheExpirationService;
import org.coconut.cache.internal.service.loading.AbstractCacheLoadingService;
import org.coconut.cache.service.exceptionhandling.AbstractCacheExceptionHandler;
import org.coconut.core.AttributeMap;
import org.coconut.core.Clock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class UnsynchronizedEntryFactoryService<K, V> extends
        AbstractCacheEntryFactoryService<K, V> {
    private final InternalCacheAttributeService attributeService;

    public UnsynchronizedEntryFactoryService(Clock clock,
            AbstractCacheExceptionHandler<K, V> exceptionHandler,
            InternalCacheAttributeService attributeService,
            DefaultCacheExpirationService<K, V> expirationService,
            AbstractCacheLoadingService<K, V> loadingService) {
        super(clock, exceptionHandler, expirationService, loadingService);
        this.attributeService = attributeService;
    }

    /**
     * @see org.coconut.cache.internal.service.entry.CacheEntryFactoryService#createEntry(java.lang.Object,
     *      java.lang.Object, org.coconut.core.AttributeMap, org.coconut.cache.CacheEntry)
     */
    public AbstractCacheEntry<K, V> createEntry(K key, V value, AttributeMap attributes,
            AbstractCacheEntry<K, V> existing) {
        if (attributes == null) {
            attributes = attributeService.createMap();
        }
        long expirationTime = getTimeToLive(key, value, attributes, existing);
        double cost = getCost(key, value, attributes, existing);
        long size = getSize(key, value, attributes, existing);
        long creationTime = getCreationTime(key, value, attributes, existing);
        long lastUpdate = getLastModified(key, value, attributes, existing);
        long refreshTime = getTimeToRefresh(key, value, attributes, existing);
        UnsynchronizedCacheEntry<K, V> newEntry = new UnsynchronizedCacheEntry<K, V>(
                this, key, value, cost, creationTime, lastUpdate, size, refreshTime);
        newEntry.setExpirationTime(expirationTime);

        if (existing != null) {
            newEntry.setPolicyIndex(existing.getPolicyIndex());
        }
        return newEntry;
    }
}
