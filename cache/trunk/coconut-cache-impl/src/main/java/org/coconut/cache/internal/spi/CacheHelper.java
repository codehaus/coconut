/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.spi;

import java.util.Collection;
import java.util.Map;

import org.coconut.cache.CacheEntry;
import org.coconut.core.AttributeMap;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheHelper<K, V> {
    // moveToExpirationService??
    // returns all keys that 1. are present in the cache, 2. not expired
    Collection<K> filterEntries(Collection<? super K> col,
            Filter<? super CacheEntry> filter);

    abstract void valueLoaded(K key, V value, AttributeMap attributes);

    abstract void valuesLoaded(Map<? super K, ? extends V> values,
            Map<? extends K, AttributeMap> keys);
}
