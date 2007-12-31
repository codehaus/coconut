/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.eviction;

import static org.coconut.internal.util.XmlUtil.addTypedElement;
import static org.coconut.internal.util.XmlUtil.attributeBooleanGet;
import static org.coconut.internal.util.XmlUtil.attributeBooleanSet;
import static org.coconut.internal.util.XmlUtil.contentIntGet;
import static org.coconut.internal.util.XmlUtil.contentIntSet;
import static org.coconut.internal.util.XmlUtil.contentLongGet;
import static org.coconut.internal.util.XmlUtil.contentLongSet;
import static org.coconut.internal.util.XmlUtil.getChild;
import static org.coconut.internal.util.XmlUtil.loadChildObject;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.policy.paging.ClockPolicy;
import org.coconut.cache.policy.paging.FIFOPolicy;
import org.coconut.cache.policy.paging.LFUPolicy;
import org.coconut.cache.policy.paging.LIFOPolicy;
import org.coconut.cache.policy.paging.LRUPolicy;
import org.coconut.cache.policy.paging.MRUPolicy;
import org.coconut.cache.policy.paging.RandomPolicy;
import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.coconut.cache.spi.CacheSPI;
import org.coconut.internal.util.XmlUtil;
import org.coconut.operations.Ops.Predicate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Used for configuring the eviction service prior to usage.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class CacheEvictionConfiguration<K, V> extends AbstractCacheServiceConfiguration<K, V> {

    /** The default maximum size of a cache unless otherwise specified. */
    public final static int DEFAULT_MAXIMUM_SIZE = Integer.MAX_VALUE;

    /** The default maximum volume of a cache unless otherwise specified. */
    public final static long DEFAULT_MAXIMUM_VOLUME = Long.MAX_VALUE;

    /** The short name of this service. */
    public final static String SERVICE_NAME = "eviction";

    /** The default settings, used when xml-serializing this configuration. */
    private final static CacheEvictionConfiguration<?, ?> DEFAULT = new CacheEvictionConfiguration<Object, Object>();

    /** XML tag for maximum volume. */
    private final static String IS_CACHEABLE_TAG = "isCacheable";

    /** XML tag for maximum volume. */
    private final static String IS_DISABLED = "disabled";

    /** XML tag for maximum size. */
    private final static String MAXIMUM_SIZE = "max-size";

    /** XML tag for maximum volume. */
    private final static String MAXIMUM_VOLUME = "max-volume";

    /** XML tag for policy. */
    private final static String POLICY = "policy";

    /** A filter used for filtering what items should be cached. */
    private Predicate<CacheEntry<K, V>> isCacheableFilter;

    /** Whether or not caching is disabled. */
    private boolean isDisabled;

    /** The maximum size of the cache. */
    private int maximumSize;

    /** The maximum volume of the cache. */
    private long maximumVolume;

    /** The replacement policy used for evicting elements. */
    private ReplacementPolicy<?> replacementPolicy;

    /**
     * Creates a new CacheEvictionConfiguration with default settings.
     */
    public CacheEvictionConfiguration() {
        super(SERVICE_NAME);
    }

    /**
     * Returns the Predicate that determinds if a given key and value should be cached.
     *
     * @return the IsCacheable predicate configured or <code>null</code> if no predicate
     *         has been set
     * @see #setIsCacheableFilter(Predicate)
     */
    public Predicate<CacheEntry<K, V>> getIsCacheableFilter() {
        return isCacheableFilter;
    }

    /**
     * Returns the maximum allowed size of the cache or {@link Integer#MAX_VALUE} if there
     * is no upper limit.
     *
     * @return the maximum allowed size of the cache or Integer.MAX_VALUE if there is no
     *         limit.
     * @see #setMaximumSize(int)
     */
    public int getMaximumSize() {
        return maximumSize;
    }

    /**
     * Returns the maximum allowed volume of the cache or {@link Long#MAX_VALUE} if there
     * is no upper limit.
     *
     * @return the maximum allowed volume of the cache or Long.MAX_VALUE if there is no
     *         limit.
     * @see #setMaximumVolume(long)
     */
    public long getMaximumVolume() {
        return maximumVolume;
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

    /**
     * Returns whether or not caching is disabled.
     *
     * @return <code>true</code> if caching is disabled, otherwise <code>false</code>
     */
    public boolean isDisabled() {
        return isDisabled;
    }

    /**
     * Sets whether or not caching is disabled. If caching is disabled, the cache will not
     * cache any items added. This can sometimes be useful while testing.
     *
     * @param isDisabled
     *            whether or not caching is disabled
     * @return this configuration
     */
    public CacheEvictionConfiguration<K, V> setDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
        return this;
    }

    /**
     * Sets a Predicate that the cache will use to determind if a cache entry can be
     * cached. For example,
     *
     * @param predicate
     *            the predicate that decides if a given key, value combination can be
     *            added to the cache
     * @return this configration
     */
    public CacheEvictionConfiguration<K, V> setIsCacheableFilter(
            Predicate<CacheEntry<K, V>> predicate) {
        this.isCacheableFilter = predicate;
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
     * elements in the near future are higher then the likelihood of new element being
     * requested.
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
     * Sets that maximum volume of the cache. The total volume of the cache is the sum of
     * all the individual element sizes (sum of all elements {@link CacheEntry#getSize()}.
     * If the limit is reached the cache must evict existing elements before adding new
     * elements.
     * <p>
     * To indicate that a cache can have an unlimited volume, {@link Long#MAX_VALUE}
     * should be specified.
     *
     * @param maximumVolume
     *            the maximum volume.
     * @return this configuration
     */
    public CacheEvictionConfiguration<K, V> setMaximumVolume(long maximumVolume) {
        if (maximumVolume < 0) {
            throw new IllegalArgumentException("maximumVolume must be a non-negative number, was "
                    + maximumVolume);
        }
        this.maximumVolume = maximumVolume;
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
     * users to specify arbitrary implementations of a replacement policy. This mismatch
     * between the inheritance and concurrency is generally refered to as the
     * <code>inheritance anomaly</code>.
     * <p>
     * The cache will, unless otherwise specified, pass instances of
     * {@link org.coconut.cache.CacheEntry} to
     * {@link ReplacementPolicy#add(Object, org.coconut.attribute.AttributeMap)} and
     * {@link ReplacementPolicy#update(int, Object, org.coconut.attribute.AttributeMap)}
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

    /** {@inheritDoc} */
    @Override
    protected void fromXML(Element e) throws Exception {
        setDisabled(attributeBooleanGet(e, IS_DISABLED, false));
        setPolicy(getPolicy(e));
        setMaximumSize(contentIntGet(getChild(MAXIMUM_SIZE, e), maximumSize));
        setMaximumVolume(contentLongGet(getChild(MAXIMUM_VOLUME, e), maximumVolume));
        setIsCacheableFilter(loadChildObject(e, IS_CACHEABLE_TAG, Predicate.class));
    }

    /** {@inheritDoc} */
    @Override
    protected void toXML(Document doc, Element e) throws Exception {
        attributeBooleanSet(e, IS_DISABLED, isDisabled, false);
        setPolicyAttribute(doc, e);
        contentLongSet(doc, e, MAXIMUM_VOLUME, maximumVolume, DEFAULT.getMaximumVolume());
        contentIntSet(doc, e, MAXIMUM_SIZE, maximumSize, DEFAULT.getMaximumSize());
        addTypedElement(doc, e, IS_CACHEABLE_TAG, CacheSPI.DEFAULT_CACHE_BUNDLE, getClass(),
                "saveOfIsCacheableFilterFailed", isCacheableFilter);
    }

    ReplacementPolicy getPolicy(Element e) throws Exception {
        String value = e.getAttribute(POLICY);
        if (value != null && !value.equals("")) {
            if (value.equalsIgnoreCase(ClockPolicy.NAME)) {
                return new ClockPolicy();
            } else if (value.equalsIgnoreCase(FIFOPolicy.NAME)) {
                return new FIFOPolicy();
            } else if (value.equalsIgnoreCase(LIFOPolicy.NAME)) {
                return new LIFOPolicy();
            } else if (value.equalsIgnoreCase(LFUPolicy.NAME)) {
                return new LFUPolicy();
            } else if (value.equalsIgnoreCase(LRUPolicy.NAME)) {
                return new LRUPolicy();
            } else if (value.equalsIgnoreCase(MRUPolicy.NAME)) {
                return new MRUPolicy();
            } else if (value.equalsIgnoreCase(RandomPolicy.NAME)) {
                return new RandomPolicy();
            } else {
                return (ReplacementPolicy<?>) Class.forName(value).newInstance();
            }
        }
        return null;
    }

    void setPolicyAttribute(Document doc, Element e) {
        if (replacementPolicy != null) {
            final String value;
            if (replacementPolicy.getClass().equals(ClockPolicy.class)) {
                value = ClockPolicy.NAME;
            } else if (replacementPolicy.getClass().equals(FIFOPolicy.class)) {
                value = FIFOPolicy.NAME;
            } else if (replacementPolicy.getClass().equals(LIFOPolicy.class)) {
                value = LIFOPolicy.NAME;
            } else if (replacementPolicy.getClass().equals(LFUPolicy.class)) {
                value = LFUPolicy.NAME;
            } else if (replacementPolicy.getClass().equals(LRUPolicy.class)) {
                value = LRUPolicy.NAME;
            } else if (replacementPolicy.getClass().equals(MRUPolicy.class)) {
                value = MRUPolicy.NAME;
            } else if (replacementPolicy.getClass().equals(RandomPolicy.class)) {
                value = RandomPolicy.NAME;
            } else {
                try {
                    replacementPolicy.getClass().getConstructor((Class[]) null);
                    value = replacementPolicy.getClass().getName();
                } catch (NoSuchMethodException e1) {
                    XmlUtil.addComment(doc, CacheSPI.DEFAULT_CACHE_BUNDLE, getClass(),
                            "saveOfReplacementPolicyFailed", e, replacementPolicy
                                    .getClass().getName());
                    return;
                }
            }
            e.setAttribute(POLICY, value);
        }
    }
}
