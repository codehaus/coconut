package org.coconut.cache.internal.service.event;

import org.coconut.cache.internal.service.joinpoint.AfterCacheOperation;
import org.coconut.cache.service.event.CacheEventService;

/**
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface InternalCacheEventService<K, V> extends CacheEventService<K, V>,
        AfterCacheOperation<K, V> {
    /**
     * Returns <code>true</code> if the Cache Event service is enabled, otherwise
     * <code>false</code>.
     * 
     * @return whether or not the cache event service is enabled
     */
    boolean isEnabled();
}
