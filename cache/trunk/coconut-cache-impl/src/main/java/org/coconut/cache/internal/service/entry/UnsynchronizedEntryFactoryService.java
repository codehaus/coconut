/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.entry;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.internal.service.expiration.AbstractExpirationService;
import org.coconut.core.AttributeMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class UnsynchronizedEntryFactoryService<K, V> extends
        AbstractCacheEntryFactoryService<K, V> {

    private AbstractExpirationService<K, V> expirationService;

    UnsynchronizedEntryFactoryService(
            AbstractExpirationService<K, V> expirationService

    ) {
        this.expirationService = expirationService;
    }

    /**
     * @see org.coconut.cache.internal.service.entry.CacheEntryFactoryService#createEntry(java.lang.Object,
     *      java.lang.Object, org.coconut.core.AttributeMap,
     *      org.coconut.cache.CacheEntry)
     */
    public AbstractCacheEntry<K, V> createEntry(K key, V value, AttributeMap attributes,
            AbstractCacheEntry<K, V> existing) {
        AbstractCacheEntry newEntry = null;
        long expirationTime = expirationService.innerGetExpirationTime(key, value, attributes);
        // TODO Auto-generated method stub
        if (existing != null) {
            newEntry.setPolicyIndex(existing.getPolicyIndex());
        }
        return null;
    }

}
