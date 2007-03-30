/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.core.AttributeMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface InternalCacheLoadingService<K, V> extends CacheLoadingService<K, V> {
    boolean canLoad();

    void reloadIfNeeded(CacheEntry<K, V> entry);

    V loadBlocking(K key, AttributeMap attributes);
}
