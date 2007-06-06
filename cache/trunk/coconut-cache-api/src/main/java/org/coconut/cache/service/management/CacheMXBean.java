/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.service.management;

import javax.management.MBeanServer;

import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

/**
 * The management interface for a {@link org.coconut.cache.Cache}. Some cache
 * implementations might define additional methods in addition to those defined in this
 * interface. However, all implementations, supporting JMX, must as a minimum support this
 * interface.
 * <p>
 * If no domain is specified using {@link CacheManagementConfiguration#setDomain(String)}
 * and no special {@link MBeanServer} is specified using
 * {@link CacheManagementConfiguration#setMBeanServer(MBeanServer)}. This MXBean will be
 * registered under <code>org.coconut.cache:name=$CACHE_NAME$,service=General</code>
 * where <code>$CACHE_NAME$</code> is replaced by the
 * {@link org.coconut.cache.Cache#getName() name} of the cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheMXBean {

    /**
     * The default domain used when registering a cache.
     */
    static final String DEFAULT_JMX_DOMAIN = "org.coconut.cache";

    /**
     * Returns the current number of elements in the cache.
     * <p>
     * This method is equivalent to calling {@link org.coconut.cache.Cache#size()}.
     * 
     * @return the current number of elements in the cache
     */
    int getSize();

    /**
     * Returns the current used capacity of the cache.
     * <p>
     * This method is equivalent to calling {@link org.coconut.cache.Cache#getCapacity()}.
     * 
     * @return the current number of elements in the cache
     */
    long getCapacity();

    /**
     * Returns the name of the cache.
     * <p>
     * This method is equivalent to calling {@link org.coconut.cache.Cache#getName()}.
     * 
     * @return the name of the cache
     */
    String getName();

    /**
     * Clears and removes any element in the cache.
     * <p>
     * Calling this method is equivalent to calling
     * {@link org.coconut.cache.Cache#clear()}.
     */
    void clear();

    /**
     * Evict expired items and do any necessary housekeeping.
     * <p>
     * Calling this method is equivalent to calling
     * {@link org.coconut.cache.Cache#evict()}.
     */
    void evict();
}
