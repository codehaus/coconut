/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.eviction;

import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheEvictionConfiguration<K, V> extends
        AbstractCacheServiceConfiguration<K, V> {

    /**
     * The default maximum size of a cache unless otherwise specified.
     */
    public final static int DEFAULT_MAXIMUM_SIZE = Integer.MAX_VALUE;

    public final static String EVICTION_TAG = "eviction";

    public final static String MAXIMUM_CAPACITY = "max-capacity";

    public final static String MAXIMUM_SIZE = "max-size";

    public final static String PREFERABLE_CAPACITY = "preferable-capacity";

    public final static String PREFERABLE_SIZE = "preferable-size";

    private final static CacheEvictionConfiguration DEFAULT = new CacheEvictionConfiguration();

    private long maximumCapacity = Long.MAX_VALUE;

    private int maximumSize = DEFAULT_MAXIMUM_SIZE;

    private long preferableCapacity = Long.MAX_VALUE;

    private int preferableSize = DEFAULT_MAXIMUM_SIZE;

    private ReplacementPolicy replacementPolicy;

    /**
     * @param tag
     * @param c
     */
    public CacheEvictionConfiguration() {
        super(EVICTION_TAG, CacheEvictionService.class);
    }

    /**
     * Integer.MAX_VALUE = unlimited
     * 
     * @return the maximum number of elements.
     */
    public long getMaximumCapacity() {
        return maximumCapacity;
    }

    /**
     * Integer.MAX_VALUE = unlimited
     * 
     * @return the maximum number of elements.
     */
    public int getMaximumSize() {
        return maximumSize;
    }

    public ReplacementPolicy getPolicy() {
        return replacementPolicy;

    }

    public long getPreferableCapacity() {
        return preferableCapacity;
    }

    public int getPreferableSize() {
        return preferableSize;
    }

    /**
     * Sets that maximum number of elements that a cache can contain. If the
     * limit is reached the cache should evict elements according to the cache
     * policy specified in setExpirationStrategy.
     * <p>
     * The default value is Integer.MAX_VALUE.
     * 
     * @param elements
     *            the maximum capacity.
     */
    public CacheEvictionConfiguration setMaximumCapacity(long capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must greater then 0, was "
                    + capacity);
        }
        maximumCapacity = capacity;
        return this;
    }

    /**
     * Sets that maximum number of elements that the cache can contain. If the
     * limit is reached the cache should evict elements according to the cache
     * policy specified in {@link #setPolicy(ReplacementPolicy)}.
     * <p>
     * The default value is Integer.MAX_VALUE. If 0 is specified the cache will
     * never retain any elements. An effective method for disabling caching.
     * 
     * @param elements
     *            the maximum capacity.
     */
    public CacheEvictionConfiguration setMaximumSize(int elements) {
        if (elements < 0) {
            throw new IllegalArgumentException(
                    "number of maximum elements must be 0 or greater, was " + elements);
        }
        maximumSize = elements;
        return this;
    }

    /**
     * policies are passed a CacheEntry, because it is the easiest way to
     * support cost/sized based policies.
     * 
     * @param policy
     * @return
     */
    public CacheEvictionConfiguration setPolicy(ReplacementPolicy policy) {
        replacementPolicy = policy;
        return this;
    }

    public CacheEvictionConfiguration setPreferableCapacity(long capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must greater then 0, was "
                    + capacity);
        }
        preferableCapacity = capacity;
        return this;
    }

    public CacheEvictionConfiguration setPreferableSize(int elements) {
        if (elements < 0) {
            throw new IllegalArgumentException(
                    "number of maximum elements must be 0 or greater, was " + elements);
        }
        preferableSize = elements;
        return this;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#fromXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void fromXML(Document doc, Element e) throws Exception {
        maximumCapacity = readLong(getChild(MAXIMUM_CAPACITY, e), maximumCapacity);
        preferableCapacity = readLong(getChild(PREFERABLE_CAPACITY, e),
                preferableCapacity);
        maximumSize = readInt(getChild(MAXIMUM_SIZE, e), maximumSize);
        preferableSize = readInt(getChild(PREFERABLE_SIZE, e), preferableSize);
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#toXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void toXML(Document doc, Element base) throws Exception {
        writeLong(doc, base, MAXIMUM_CAPACITY, maximumCapacity, DEFAULT
                .getMaximumCapacity());
        writeLong(doc, base, PREFERABLE_CAPACITY, preferableCapacity, DEFAULT
                .getPreferableCapacity());
        writeInt(doc, base, MAXIMUM_SIZE, maximumSize, DEFAULT.getMaximumSize());
        writeInt(doc, base, PREFERABLE_SIZE, preferableSize, DEFAULT.getPreferableSize());
    }

}
