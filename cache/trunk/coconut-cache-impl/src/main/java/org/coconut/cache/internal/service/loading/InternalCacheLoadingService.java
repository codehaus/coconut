package org.coconut.cache.internal.service.loading;

import java.util.Map;

import org.coconut.cache.CacheEntry;
import org.coconut.core.AttributeMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface InternalCacheLoadingService<K, V> {
    boolean isDummy();

    void reloadIfNeeded(CacheEntry<K, V> entry);

    /**
     * @param key
     *            the key for which a corresponding value should be loaded
     * @param attributes
     *            a list of attributes that should be passed to cache loader
     * @return a corresponding value for the specified key, or <tt>null</tt>
     *         if no such value exist
     */
    V loadBlocking(K key, AttributeMap attributes);

    Map<? super K, ? extends V> loadAllBlocking(
            Map<? extends K, AttributeMap> keysWithAttributes);
}
