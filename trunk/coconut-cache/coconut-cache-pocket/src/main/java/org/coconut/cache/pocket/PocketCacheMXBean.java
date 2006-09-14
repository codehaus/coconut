/**
 * 
 */
package org.coconut.cache.pocket;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface PocketCacheMXBean {

    int size();

    void clear();

    void evict();

    long getNumberOfHits();

    long getNumberOfMisses();

    double getHitRatio();
    
    void trimToSize(int newSize);
    
    void setEvictWatermark(int trimSize);
    
    int getDefaultTrimSize();
}
