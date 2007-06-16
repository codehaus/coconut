/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.eviction;

import org.coconut.cache.CacheServices;

/**
 * This interface contains various eviction-based methods that are available at runtime.
 * <p>
 * An instance of this interface can be retrieved either by using the cache instance to
 * look it up.
 * 
 * <pre>
 * Cache&lt;?, ?&gt; c = anCache;
 * 
 * CacheEvictionService&lt;?, ?&gt; ces = c.getService(CacheEvictionService.class);
 * </pre>
 * 
 * Or using {@link CacheServices} to look it up
 * 
 * <pre>
 * Cache&lt;?, ?&gt; c = anCache;
 * 
 * CacheEvictionService&lt;?, ?&gt; ces = CacheServices.eviction(c);
 * </pre>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheEvictionService<K, V> {
    
    // TODO copy javadoc from MXBean
    void trimToSize(int size);

    void trimToCapacity(long capacity);

    long getMaximumCapacity();

    int getMaximumSize();

    void setMaximumCapacity(long maximumCapacity);

    void setMaximumSize(int maximumSize);
}
