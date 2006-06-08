package org.coconut.cache.analyzer.management;

/**
 * @author kni
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface CacheAnalyzerMBean {
    int getNumberOfMonitors();
    String[] getAvailableCachePolicies();
    void addPolicy(String name);
    String[] getCacheMonitors();    
}
