package org.coconut.cache.internal.service.attribute;

import org.coconut.cache.CacheEntry;
import org.coconut.filter.Filter;

/**
 * but but but but whyyy?
 * 
 * @param <K>
 * @param <V>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface UserSettings<K, V> {

    void setExpirationTimeNanos(long nanos);

    long getExpirationTimeNanos();

}
