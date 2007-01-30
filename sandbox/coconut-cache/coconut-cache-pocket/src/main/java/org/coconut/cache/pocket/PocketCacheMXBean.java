/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.pocket;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface PocketCacheMXBean {

    /**
     * Clears the cache.
     */
    void clear();

    void evict();

    int getCapacity();

    int getEvictWatermark();

    double getHitRatio();

    long getNumberOfHits();

    long getNumberOfMisses();

    /**
     * Returns the number of key-value mappings in this cache. If the cache
     * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     * 
     * @return the number of key-value mappings in this cache
     */
    int getSize();

    /**
     * Sets the number of cache hits, and cache misses to 0. The hit ratio will
     * be set to {@link java.lang.Double.NaN}.
     */
    void resetStatistics();

    void setCapacity(int newCapacity);

    void setEvictWatermark(int watermark);

    void trimToSize(int newSize);
}
