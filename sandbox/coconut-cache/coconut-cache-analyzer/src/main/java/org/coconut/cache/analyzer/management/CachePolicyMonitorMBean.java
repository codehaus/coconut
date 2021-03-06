/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

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
