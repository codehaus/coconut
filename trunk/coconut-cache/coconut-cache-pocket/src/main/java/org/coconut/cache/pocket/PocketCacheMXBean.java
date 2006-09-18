/**
 * 
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

    int getSize();

    /**
     * Resets the hit ratio.
     */
    void resetStatistics();

    void setCapacity(int newCapacity);

    void setEvictWatermark(int watermark);

    void trimToSize(int newSize);
}
