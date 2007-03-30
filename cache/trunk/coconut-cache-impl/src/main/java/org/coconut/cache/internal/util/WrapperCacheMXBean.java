/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.internal.util;

import org.coconut.cache.service.management.CacheMXBean;
import org.coconut.cache.spi.AbstractCache;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class WrapperCacheMXBean implements CacheMXBean {

    private final AbstractCache cache;

    /**
     * @param mbeanInterface
     * @throws NotCompliantMBeanException
     * @throws NotCompliantMBeanException
     */
    public WrapperCacheMXBean(AbstractCache cache) {
        this.cache = cache;
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#getSize()
     */
    @ManagedAttribute(defaultValue = "size", description = "The number of elements contained in the cache")
    public int getSize() {
        return cache.size();
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#clear()
     */
    @ManagedOperation(defaultValue = "clear", description = "Clears the cache")
    public void clear() {
        cache.clear();
    }

    @ManagedOperation(defaultValue = "evict", description = "Evicts expired entries and performs housekeeping on the cache")
    public void evict() {
        cache.evict();
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#getName()
     */
    @ManagedAttribute(defaultValue = "Name", description = "The name of the cache")
    public String getName() {
        return cache.getName();
    }

    /**
     * @see org.coconut.cache.management.CacheMXBean#trimToSize(int)
     */
    @ManagedOperation(defaultValue = "trimToSize", description = "Trims the cache to this size")
    public void trimToSize(int newSize) {
        cache.trimToSize(newSize);
    }

    /**
     * @see org.coconut.cache.service.management.CacheMXBean#getCapacity()
     */
    @ManagedAttribute(defaultValue = "capacity", description = "The total size of all elements contained in the cache")
    public long getCapacity() {
        return cache.getCapacity();
    }
}
