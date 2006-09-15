/**
 * 
 */
package org.coconut.cache.pocket;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface PocketCacheMXBean {

    int getSize();

    void clear();

    void evict();

    long getNumberOfHits();

    /**
     * Resets the hit ratio.
     * 
     * @throws UnsupportedOperationException
     *             if the cache does not allow resetting the cache statistics
     *             (read-only cache)
     */
    void resetStatistics();
    
    long getNumberOfMisses();

    double getHitRatio();
    
    int getCapacity();
    void setCapacity(int newCapacity);
    void trimToSize(int newSize);
    
    void setEvictWatermark(int trimSize);
    
    int getDefaultTrimSize();
}
