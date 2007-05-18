/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.eviction;

import static org.coconut.internal.util.XmlUtil.getChild;
import static org.coconut.internal.util.XmlUtil.readInt;
import static org.coconut.internal.util.XmlUtil.readLong;
import static org.coconut.internal.util.XmlUtil.writeInt;
import static org.coconut.internal.util.XmlUtil.writeLong;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.filter.Filter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheEvictionConfiguration<K, V> extends
		AbstractCacheServiceConfiguration<K, V> {

	private final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.NANOSECONDS;

	private final static String DEFAULT_IDLE_TIME = "default-idle-time";
	
	/**
     * The default maximum capacity of a cache unless otherwise specified.
     */
	public final static long DEFAULT_MAXIMUM_CAPACITY = Long.MAX_VALUE;

	/**
     * The default maximum size of a cache unless otherwise specified.
     */
	public final static int DEFAULT_MAXIMUM_SIZE = Integer.MAX_VALUE;

	private final static CacheEvictionConfiguration DEFAULT = new CacheEvictionConfiguration();

	public final static String SERVICE_NAME = "eviction";

	private final static String MAXIMUM_CAPACITY = "max-capacity";

	private final static String MAXIMUM_SIZE = "max-size";

	private final static String PREFERABLE_CAPACITY = "preferable-capacity";

	private final static String PREFERABLE_SIZE = "preferable-size";

	private long maximumCapacity = DEFAULT_MAXIMUM_CAPACITY;

	private int maximumSize = DEFAULT_MAXIMUM_SIZE;

	private long preferableCapacity = DEFAULT_MAXIMUM_CAPACITY;

	private int preferableSize = DEFAULT_MAXIMUM_SIZE;

	private ReplacementPolicy replacementPolicy;

	private Filter<CacheEntry<K, V>> evictionFilter;

	/** Time between evicts. */
	private long scheduledEvictTime;
	/**
     * @param tag
     * @param c
     */
	public CacheEvictionConfiguration() {
		super(SERVICE_NAME, CacheEvictionService.class);
	}

	/**
     * Returns the maximum allowed capacity of the cache or
     * {@link Long#MAX_VALUE} if there is no limit.
     * 
     * @return the maximum allowed capacity of the cache or Long.MAX_VALUE if
     *         there is no limit.
     * @see #setMaximumCapacity(long)
     */
	public long getMaximumCapacity() {
		return maximumCapacity;
	}

	/**
     * Returns the specified expiration filter or <tt>null</tt> if no filter
     * has been specified.
     */
	public Filter<CacheEntry<K, V>> getEvictionFilter() {
		return evictionFilter;
	}

	/**
     * Sets a specific expiration that can be used in <tt>addition</tt> to the
     * time based expiration filter to check if items has expired. If no filter
     * has been set items are expired according to their registered expiration
     * time. If an expiration filter is set cache entries are first checked
     * against that filter then against the time based expiration times.
     */
	public CacheEvictionConfiguration setEvictionFilter(Filter<CacheEntry<K, V>> filter) {
		evictionFilter = filter;
		return this;
	}

	/**
     * Returns the maximum allowed size of the cache or
     * {@link Integer#MAX_VALUE} if there is no limit.
     * 
     * @return the maximum allowed size of the cache or Integer.MAX_VALUE if
     *         there is no limit.
     * @see #setMaximumSize(int)
     */
	public int getMaximumSize() {
		return maximumSize;
	}

	/**
     * Returns the specified replacement policy or <tt>null</tt> if none has
     * been specified.
     * 
     * @return the specified replacement policy or <tt>null</tt> if none has
     *         been specified
     * @see #setPolicy(ReplacementPolicy)
     */
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
     * limit is reached the cache must evict existing elements before adding new
     * elements.
     * <p>
     * The default value is Integer.MAX_VALUE. Which
     * 
     * @param elements
     *            the maximum capacity.
     */
	public CacheEvictionConfiguration setMaximumCapacity(long maximumCapacity) {
		if (maximumCapacity <= 0) {
			throw new IllegalArgumentException("capacity must greater then 0, was "
					+ maximumCapacity);
		}
		this.maximumCapacity = maximumCapacity;
		return this;
	}

	/**
     * Sets that maximum number of elements that the cache is allowed to
     * contain. If the limit is reached the cache must evict existing elements
     * before adding new elements.
     * <p>
     * To indicate that a cache can hold an unlimited number of items,
     * {@link Integer#MAX_VALUE} should be specified. This is also refered to as
     * an unlimited cache.
     * <p>
     * If the specified maximum capacity is 0, the cache will never store any
     * elements internally.
     * 
     * @param maximumSize
     *            the maximum number of elements the cache can hold or
     *            Integer.MAX_VALUE if there is no limit
     * @throws IllegalArgumentException
     *             if the specified maximum size is negative
     */
	public CacheEvictionConfiguration setMaximumSize(int maximumSize) {
		if (maximumSize < 0) {
			throw new IllegalArgumentException(
					"number of maximum elements must be 0 or greater, was " + maximumSize);
		}
		this.maximumSize = maximumSize;
		return this;
	}

	/**
     * Sets the replacement policy that should be used when the cache needs to
     * evict entries to make room for new entries. The replacement policy will
     * unless otherwise specified pass instances of
     * {@link org.coconut.cache.CacheEntry} to its
     * {@link ReplacementPolicy#add(Object)) and {@link ReplacementPolicy#update(int, Object)}
     * methods.
     * <p>
     * If no policy has been specified, the cache is free to choose any avilable
     * method to decide which entries to replace when the maximum size or
     * capacity is reached. Including choosing elements at random. However
     * 
     * @param policy
     *            the replacement policy that should be used when the cache
     *            needs to evict entries
     * @return this CacheEvictionConfiguration
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

	private long defaultIdleTimeNs = Long.MAX_VALUE;

	private Filter<CacheEntry<K, V>> idleFilter;

	/**
     * Returns the default expiration time for entries. If entries never expire,
     * {@link #NEVER_EXPIRE} is returned.
     * 
     * @param unit
     *            the time unit that should be used for returning the default
     *            expiration
     * @return the default expiration time for entries, or {@link #NEVER_EXPIRE}
     *         if entries never expire
     */
	public long getDefaultIdleTime(TimeUnit unit) {
		if (defaultIdleTimeNs == Long.MAX_VALUE) {
			return Long.MAX_VALUE;
		} else {
			return unit.convert(defaultIdleTimeNs, TimeUnit.NANOSECONDS);
		}
	}

	/**
     * @return
     */
	public Filter<CacheEntry<K, V>> getIdleFilter() {
		return idleFilter;
	}

	/**
     * Sets the default time idle time for new objects that are added to the
     * cache.
     * 
     * @param timeToLive
     *            the time from insertion to the point where the entry should be
     *            evicted from the cache
     * @param unit
     *            the time unit of the timeToLive argument
     * @throws IllegalArgumentException
     *             if the specified time to live is negative (<0)
     * @throws NullPointerException
     *             if the specified time unit is <tt>null</tt>
     * @see #getDefaultTimeToLive(TimeUnit)
     */
	public CacheEvictionConfiguration setDefaultIdleTime(long idleTime, TimeUnit unit) {
		if (idleTime <= 0) {
			throw new IllegalArgumentException("idleTime must be greather then 0, was "
					+ idleTime);
		} else if (unit == null) {
			throw new NullPointerException("unit is null");
		}
		if (idleTime == Long.MAX_VALUE) {
			defaultIdleTimeNs = Long.MAX_VALUE;
			// don't convert relative to time unit
		} else {
			defaultIdleTimeNs = unit.toNanos(idleTime);
		}
		return this;
	}

	/**
     * Sets a filter that the cache can use to determine if a given cache entry
     * should be evicted. Usage of this method only makes sense if the cache
     * stores entries in a background store. For example, a file on the disk.
     * <p>
     * This method is similar to the #setExpirationFilter(Filter) except that
     * this is only used as a tempory
     * <p>
     * If this cache does
     * 
     * @param filter
     *            the filter to check entries against
     * @see #getEvictionFilter()
     * @throws UnsupportedOperationException
     *             If this cache does not support setting an eviction filter
     */
	public CacheEvictionConfiguration setIdleFilter(Filter<CacheEntry<K, V>> filter) {
		idleFilter = filter;
		return this;
	}
}
