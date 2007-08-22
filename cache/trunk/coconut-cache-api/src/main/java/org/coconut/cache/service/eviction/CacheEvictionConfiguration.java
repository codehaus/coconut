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

import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.cache.spi.ReplacementPolicy;
import org.coconut.internal.util.UnitOfTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to configure the eviction service bundle prior to usage.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class CacheEvictionConfiguration<K, V> extends
        AbstractCacheServiceConfiguration<K, V> {

    /** The default maximum volume of a cache unless otherwise specified. */
    public final static long DEFAULT_MAXIMUM_VOLUME = Long.MAX_VALUE;

    /** The default maximum size of a cache unless otherwise specified. */
    public final static int DEFAULT_MAXIMUM_SIZE = Integer.MAX_VALUE;

    /** The short name of this service. */
    public final static String SERVICE_NAME = "Eviction";

    /** The default settings, used when xml-serializing this configuration. */
    private final static CacheEvictionConfiguration<?, ?> DEFAULT = new CacheEvictionConfiguration<Object, Object>();

    /** XML tag for maximum volume. */
    private final static String MAXIMUM_VOLUME = "max-volume";

    /** XML tag for maximum size. */
    private final static String MAXIMUM_SIZE = "max-size";

    /** XML tag for preferable volume. */
    private final static String PREFERABLE_VOLUME = "preferable-volume";

    /** XML tag for preferable size. */
    private final static String PREFERABLE_SIZE = "preferable-size";

    /** XML tag for scheduled eviction. */
    private final static String SCHEDULE_EVICT_TAG = "schedule-evict";

    /** The maximum volume of the cache. */
    private long maximumVolume;

    /** The maximum size of the cache. */
    private int maximumSize;

    /** The preferable volume of the cache. */
    private long preferableVolume;

    /** The preferable size of the cache. */
    private int preferableSize;

    /** The replacement policy used for evicting elements. */
    private ReplacementPolicy<?> replacementPolicy;

    /** The delay between evictions. */
    private long scheduleEvictionAtFixedRateNanos;

    /**
     * Creates a new CacheEvictionConfiguration with default settings.
     */
    public CacheEvictionConfiguration() {
        super(SERVICE_NAME);
    }

    /**
     * Returns the maximum allowed volume of the cache or {@link Long#MAX_VALUE} if
     * there is no limit.
     * 
     * @return the maximum allowed volume of the cache or Long.MAX_VALUE if there is no
     *         limit.
     * @see #setMaximumVolume(long)
     */
    public long getMaximumVolume() {
        return maximumVolume;
    }

    /**
     * Returns the maximum allowed size of the cache or {@link Integer#MAX_VALUE} if there
     * is no limit.
     * 
     * @return the maximum allowed size of the cache or Integer.MAX_VALUE if there is no
     *         limit.
     * @see #setMaximumSize(int)
     */
    public int getMaximumSize() {
        return maximumSize;
    }

    /**
     * Returns the configured replacement policy or <tt>null</tt> if none has been
     * configured.
     * 
     * @return the configured replacement policy or <tt>null</tt> if none has been
     *         configured
     * @see #setPolicy(ReplacementPolicy)
     */
    public ReplacementPolicy<?> getPolicy() {
        return replacementPolicy;
    }

    public long getPreferableVolume() {
        return preferableVolume;
    }

    public int getPreferableSize() {
        return preferableSize;
    }

    public long getScheduledEvictionAtFixedRate(TimeUnit unit) {
        if (scheduleEvictionAtFixedRateNanos == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        } else {
            return unit.convert(scheduleEvictionAtFixedRateNanos, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * Sets that maximum number of elements that a cache can contain. If the limit is
     * reached the cache must evict existing elements before adding new elements. For
     * example, if the limit is 10 elements and the cache currently holds 10 elements.
     * Then, if a user tries to add a new element the cache must choose one of the 10
     * elements to remove from the cache before it inserting the new element.
     * <p>
     * The default value is Integer.MAX_VALUE. Which roughly translates to no limit on the
     * number of elements.
     * 
     * @param maximumVolume
     *            the maximum volume.
     * @return this configuration
     */
    public CacheEvictionConfiguration<K, V> setMaximumVolume(long maximumVolume) {
        if (maximumVolume < 0) {
            throw new IllegalArgumentException(
                    "maximumVolume must be a non-negative number, was " + maximumVolume);
        }
        this.maximumVolume = maximumVolume;
        return this;
    }

    /**
     * Sets that maximum number of elements that a cache can hold. If the limit is reached
     * the cache must evict an existing element(s) before adding a new element. For
     * example, if the maximum size is 10 and the cache currently holds 10 elements. Then,
     * if a user tries to add a new element the cache must choose one of the 10 elements
     * to remove from the cache before it inserts the new element. As an alternative the
     * cache might choose to keep the 10 existing elements and not add the new element.
     * For example, if it estimates that the likelihood of requesting anyone of the 10
     * elements in the near future are higher then the likelihood of requsting the new
     * element.
     * <p>
     * To indicate that a cache can hold an unlimited number of elements, specify
     * {@link Integer#MAX_VALUE}. This is also the default value.
     * <p>
     * If the specified maximum size is 0, the cache will never store any elements
     * internally.
     * 
     * @param maximumSize
     *            the maximum number of elements the cache can hold or Integer.MAX_VALUE
     *            if there is no limit
     * @throws IllegalArgumentException
     *             if the specified integer is negative
     * @return this configuration
     */
    public CacheEvictionConfiguration<K, V> setMaximumSize(int maximumSize) {
        if (maximumSize < 0) {
            throw new IllegalArgumentException(
                    "number of maximum elements must be 0 or greater, was " + maximumSize);
        }
        this.maximumSize = maximumSize;
        return this;
    }

    /**
     * Sets the replacement policy that decides which of the currently held elements are
     * evicted in order to make room for new elements. If no replacement policy is
     * specified the cache may choose which elements to evict anyway possible. Note:
     * Whatever way it chooses the elements, it must still obey the configured maximum
     * size and volume.
     * <p>
     * If a cache implementation does not allow the specification of arbitrary
     * replacements policies. But is tightly coupled with a specific implementation of a
     * replacement policy. The cache must throw an exception at construction time, if the
     * users tries to specify any value but <code>null</code>. For example, it is often
     * impossible to implement a highly concurrent cache while at the same time allowing
     * users to specify arbitrary implementations of a replacement policy. This
     * observation is generally refered to as the <code>inheritance anomaly</code>.
     * <p>
     * The cache will, unless otherwise specified, pass instances of
     * {@link org.coconut.cache.CacheEntry} to {@link ReplacementPolicy#add(Object)} and
     * {@link ReplacementPolicy#update(int, Object)}
     * 
     * @param policy
     *            the replacement policy that should select which elements to evict when
     *            the cache is full
     * @return this CacheEvictionConfiguration
     */
    public CacheEvictionConfiguration<K, V> setPolicy(ReplacementPolicy<?> policy) {
        replacementPolicy = policy;
        return this;
    }

    public CacheEvictionConfiguration<K, V> setPreferableVolume(long volume) {
        if (volume < 0) {
            throw new IllegalArgumentException("volume must greater then 0, was "
                    + volume);
        }
        preferableVolume = volume;
        return this;
    }

    public CacheEvictionConfiguration<K, V> setPreferableSize(int elements) {
        if (elements < 0) {
            throw new IllegalArgumentException(
                    "number of maximum elements must be 0 or greater, was " + elements);
        }
        preferableSize = elements;
        return this;
    }

    /**
     * 0 = automatic.
     * <p>
     * This requires that the CacheThreadingConfiguration isEnabled.
     * 
     * @param period
     * @param unit
     *            the time unit of the specified period
     * @return this configuration
     */
    public CacheEvictionConfiguration<K, V> setScheduledEvictionAtFixedRate(long period,
            TimeUnit unit) {
        if (period < 0) {
            throw new IllegalArgumentException("period must be 0 or greater, was "
                    + period);
        }
        scheduleEvictionAtFixedRateNanos = unit.toNanos(period);
        return this;
    }

    /** {@inheritDoc}  */
    @Override
    protected void fromXML(Element e) throws Exception {
        maximumVolume = readLong(getChild(MAXIMUM_VOLUME, e), maximumVolume);
        preferableVolume = readLong(getChild(PREFERABLE_VOLUME, e),
                preferableVolume);
        maximumSize = readInt(getChild(MAXIMUM_SIZE, e), maximumSize);
        preferableSize = readInt(getChild(PREFERABLE_SIZE, e), preferableSize);

        /* Eviction time */
        Element evictionTime = getChild(SCHEDULE_EVICT_TAG, e);
        long timee = UnitOfTime.fromElement(evictionTime, TimeUnit.NANOSECONDS,
                CacheExpirationService.NEVER_EXPIRE);
        setScheduledEvictionAtFixedRate(timee, TimeUnit.NANOSECONDS);
    }

    /** {@inheritDoc} */
    @Override
    protected void toXML(Document doc, Element base) throws Exception {
        writeLong(doc, base, MAXIMUM_VOLUME, maximumVolume, DEFAULT
                .getMaximumVolume());
        writeLong(doc, base, PREFERABLE_VOLUME, preferableVolume, DEFAULT
                .getPreferableVolume());
        writeInt(doc, base, MAXIMUM_SIZE, maximumSize, DEFAULT.getMaximumSize());
        writeInt(doc, base, PREFERABLE_SIZE, preferableSize, DEFAULT.getPreferableSize());

        /* Expiration Timer */
        UnitOfTime.toElementCompact(doc, base, SCHEDULE_EVICT_TAG,
                scheduleEvictionAtFixedRateNanos, TimeUnit.NANOSECONDS,
                DEFAULT.scheduleEvictionAtFixedRateNanos);

    }
}
