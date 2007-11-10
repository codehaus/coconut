/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import org.coconut.cache.internal.service.entry.AbstractCacheEntry;
import org.coconut.cache.internal.service.entry.InternalCacheEntryService;
import org.coconut.cache.internal.service.exceptionhandling.InternalCacheExceptionService;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.core.AttributeMap;

public class UnsynchronizedCacheLoaderService<K, V> extends AbstractCacheLoadingService<K, V> {
    private final InternalCacheEntryService attributeFactory;

    public UnsynchronizedCacheLoaderService(InternalCacheEntryService attributeFactory,
            InternalCacheExceptionService<K, V> exceptionService, CacheLoadingConfiguration<K, V> loadConf,
            final LoadSupport<K, V> cache) {
        super(loadConf, attributeFactory, exceptionService, cache);
        this.attributeFactory = attributeFactory;
    }
    
    /** {@inheritDoc} */
    public AbstractCacheEntry<K, V> loadBlocking(K key, AttributeMap attributes) {
        AttributeMap map = attributeFactory.createMap(attributes);
        return loadAndAddToCache(key, map, false);
    }
}
