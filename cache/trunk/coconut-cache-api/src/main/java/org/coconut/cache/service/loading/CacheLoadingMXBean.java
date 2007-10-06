/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import org.coconut.cache.service.management.CacheManagementConfiguration;

/**
 * The management interface for the loading service.
 * <p>
 * This managed bean is only available at runtime if a cache loader has been set using
 * {@link CacheLoadingConfiguration#setLoader(CacheLoader)} and
 * {@link CacheManagementConfiguration#setEnabled(boolean)} has been set to
 * <code>true</code>.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheLoadingMXBean {

    /**
     * Returns the default time to refresh in milliseconds for new elements that are added
     * to the cache.
     * 
     * @return the default time to refresh in milliseconds for new elements that are added
     *         to the cache
     * @see CacheLoadingService#getDefaultTimeToRefresh(java.util.concurrent.TimeUnit)
     */
    long getDefaultTimeToRefreshMs();

    /**
     * Sets the default time to refresh in milliseconds for new elements that are added to
     * the cache.
     * 
     * @param defaultTimeToRefreshMs
     *            the default time to refresh in milliseconds for new elements that are
     *            added to the cache
     * @see CacheLoadingService#setDefaultTimeToRefresh(long,
     *      java.util.concurrent.TimeUnit)
     */
    void setDefaultTimeToRefreshMs(long defaultTimeToRefreshMs);

    /**
     * Attempts to reload all entries that are currently held in the cache.
     */
    void forceLoadAll();

    /**
     * Attempts to reload all entries that are either expired or which needs refreshing.
     */
    void loadAll();
}
