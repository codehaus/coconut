package org.coconut.cache.analyzer.management;

/**
 * @author kni
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface CachePolicyMonitorMBean {
    long getStart();
    long getNumberOfHits();
    long getNumberOfMisses();
    float getHitRatio();
    void setMaxNumberOfElements(int maxSize);
    int getMaxNumberOfElements();
    int getNumberOfElements();
}
