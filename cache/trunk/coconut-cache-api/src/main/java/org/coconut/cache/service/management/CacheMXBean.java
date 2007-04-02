/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.service.management;

import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

/**
 * The management interface for a {@link org.coconut.cache.Cache}. Some cache
 * implementations might define additional methods in addition to those defined
 * in this interface. However, all implementations that has JMX support must as
 * a minimum support this interface.
 * <p>
 * The default ObjectName a cache is registered under is
 * <code>org.coconut.cache:name=cache_name,service=General</code>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 */
public interface CacheMXBean {

    /**
     * The default domain used when registering a cache.
     */
    static final String DEFAULT_JMX_DOMAIN = "org.coconut.cache";

    /**
     * Returns the current number of elements in the cache.
     * <p>
     * This method is equivalent to calling
     * {@link org.coconut.cache.Cache#size()}.
     * 
     * @return the current number of elements in the cache
     */
    @ManagedAttribute(defaultValue = "size", description = "The number of elements contained in the cache")
    int getSize();

    /**
     * Returns the current used capacity of the cache.
     * <p>
     * This method is equivalent to calling
     * {@link org.coconut.cache.Cache#getCapacity()}.
     * 
     * @return the current number of elements in the cache
     */
    @ManagedAttribute(defaultValue = "capacity", description = "The total size of all elements contained in the cache")
    long getCapacity();

    /**
     * Returns the name of the cache.
     * 
     * @return the name of the cache
     */
    @ManagedAttribute(defaultValue = "Name", description = "The name of the cache")
    String getName();

    /**
     * Clears and removes any element in the cache.
     * <p>
     * Calling this method is equivalent to calling
     * {@link org.coconut.cache.Cache#clear()}.
     */
    @ManagedOperation(defaultValue = "clear", description = "Clears the cache")
    void clear();

    /**
     * Evict expired items and do any necessary housekeeping.
     * <p>
     * Calling this method is equivalent to calling
     * {@link org.coconut.cache.Cache#evict()}.
     */
    @ManagedOperation(defaultValue = "evict", description = "Evicts expired entries and performs housekeeping on the cache")
    void evict();

    /**
     * Keep evicting entries until the number of elements in the cache has
     * reached the specified size. If the cache does not have a configured
     * replacement policy the cache may remove the elements in any order.
     * 
     * @param newSize
     *            the number of elements that the cache should hold
     */
    @ManagedOperation(defaultValue = "trimToSize", description = "Trims the cache to this size")
    void trimToSize(int newSize);
}
