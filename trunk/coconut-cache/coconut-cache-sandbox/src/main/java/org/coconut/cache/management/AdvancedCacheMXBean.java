/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.management;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface AdvancedCacheMXBean extends CacheMXBean {

    /* <!-- Expirimental --> */
    /**
     * Tests if the cache supports the monitoring of memory used by the cache.
     * TODO this needs to be related to size of an element in some way
     * 
     * @return true if the monitoring of used memory is supported ; false
     *         otherwise.
     */
    boolean isMemoryMonitoringSupported();

    /**
     * Returns the total amount of memory currently used by the cache, measured
     * in bytes. Or -1 if not supported.
     * <p>
     * Implementations may choose to include the overhead of the cache into the
     * returned result.
     * 
     * @return the total amount of memory currently used by the cache
     */
    long getMemoryUsage();

    long getMaximumMemoryUsage();

    void setMaximumMemoryUsage(long maxMemory);
    
    // load remove/evict/expire som entry
    // UUID getUUID(); // getHigh / log bit

    // overhead
    // getPuts
    // getGets

    // average load delay
    // average get delay

    // total getdelay

    // String[] getIDs();

    // void load(String key);
    // void loadAll(String[] keys);
    // void remove(String key);
    // void removeAll(String[] keys);
    // CacheEntryInfo
    // -toString, creationTime,TimeToLive, Deadline (creationTime+ttl)

    // void subscribeNotifications(St)
    // void unsubscribeNotifications(St)

    // void unsubscribeAllNotifications(St)

    // long getUUIDMostSignificant();
    // long getUUIDLeastSignificant();
    // setName(), getFullname();

    // is enabled
    // set enabled

}
