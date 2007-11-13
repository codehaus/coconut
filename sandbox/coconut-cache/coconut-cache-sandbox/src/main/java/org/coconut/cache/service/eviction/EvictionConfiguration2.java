import static org.coconut.internal.util.XmlUtil.addAndsaveObject;
import static org.coconut.internal.util.XmlUtil.getChild;
import static org.coconut.internal.util.XmlUtil.loadOptional;
import static org.coconut.internal.util.XmlUtil.readInt;
import static org.coconut.internal.util.XmlUtil.readLong;
import static org.coconut.internal.util.XmlUtil.writeInt;
import static org.coconut.internal.util.XmlUtil.writeLong;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.eviction.CacheEvictionConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.filter.Filter;
import org.coconut.internal.util.UnitOfTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EvictionConfiguration2 {

    private final static String DEFAULT_IDLE_TIME = "default-idle-time";

    private final static String IDLE_FILTER = "idle-filter";

    /** The default idle time for new elements. */
    private long defaultIdleTimeNs = Long.MAX_VALUE;

    private Filter<CacheEntry<K, V>> idleFilter;

  /** XML tag for preferable volume. */
  private final static String PREFERABLE_VOLUME = "preferable-volume";

  /** XML tag for preferable size. */
  private final static String PREFERABLE_SIZE = "preferable-size";

     /** XML tag for scheduled eviction. */
     private final static String SCHEDULE_EVICT_TAG = "schedule-evict";

     /** The delay between evictions. */
     private long scheduleEvictionAtFixedRateNanos;
     

   /** The preferable volume of the cache. */
   private long preferableVolume;

   /** The preferable size of the cache. */
   private int preferableSize;
     
    /**
     * Returns the default expiration time for entries. If entries never expire,
     * {@link #NEVER_EXPIRE} is returned.
     * 
     * @param unit
     *            the time unit that should be used for returning the default expiration
     * @return the default expiration time for entries, or {@link #NEVER_EXPIRE} if
     *         entries never expire
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
     * Sets the default time idle time for new objects that are added to the cache. Items
     * are evicted according to their idle time on a best effort basis.
     * 
     * @param idleTime
     *            the time from insertion to the point where the entry should be evicted
     *            from the cache
     * @param unit
     *            the time unit of the timeToLive argument
     * @throws IllegalArgumentException
     *             if the specified time to live is negative (<0)
     * @throws NullPointerException
     *             if the specified time unit is <tt>null</tt>
     * @see #getDefaultTimeToLive(TimeUnit)
     */
    public CacheEvictionConfiguration<K, V> setDefaultIdleTime(long idleTime,
            TimeUnit unit) {
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
     * Sets a filter that the cache can use to determine if a given cache entry should be
     * evicted. Usage of this method only makes sense if the cache stores entries in a
     * background store. For example, a file on the disk.
     * <p>
     * This method is similar to the #setExpirationFilter(Filter) except that this is only
     * used as a tempory
     * <p>
     * If this cache does
     * 
     * @param filter
     *            the filter to check entries against
     * @see #getIdleFilter()
     * @throws UnsupportedOperationException
     *             If this cache does not support setting an eviction filter
     */
    public CacheEvictionConfiguration<K, V> setIdleFilter(Filter<CacheEntry<K, V>> filter) {
        idleFilter = filter;
        return this;
    }

    @Override
    protected void fromXML(Element e) throws Exception {

        /* Idle time */
        Element eTime = getChild(DEFAULT_IDLE_TIME, e);
        long time = UnitOfTime.fromElement(eTime, DEFAULT_TIME_UNIT,
                CacheExpirationService.NEVER_EXPIRE);
        setDefaultIdleTime(time, DEFAULT_TIME_UNIT);
        /* Idle filter. */
        idleFilter = loadOptional(e, IDLE_FILTER, Filter.class);

        preferableVolume = readLong(getChild(PREFERABLE_VOLUME, e), preferableVolume);
        preferableSize = readInt(getChild(PREFERABLE_SIZE, e), preferableSize);

        /* Eviction time */
        Element evictionTime = getChild(SCHEDULE_EVICT_TAG, e);
        long timee = UnitOfTime.fromElement(evictionTime, TimeUnit.NANOSECONDS,
                CacheExpirationService.NEVER_EXPIRE);
        setScheduledEvictionAtFixedRate(timee, TimeUnit.NANOSECONDS);

    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#toXML(org.w3c.dom.Document,
     *      org.w3c.dom.Element)
     */
    @Override
    protected void toXML(Document doc, Element base) throws Exception {

        /* Filter */
        addAndsaveObject(doc, base, IDLE_FILTER, getResourceBundle(),
                "expiration.saveOfExpirationFilterFailed", idleFilter);
        /* Expiration Timer */
        UnitOfTime.toElementCompact(doc, base, SCHEDULE_EVICT_TAG,
                scheduleEvictionAtFixedRateNanos, DEFAULT_TIME_UNIT,
                DEFAULT.scheduleEvictionAtFixedRateNanos);
        writeLong(doc, base, PREFERABLE_VOLUME, preferableVolume, DEFAULT
                .getPreferableVolume());
                writeInt(doc, base, PREFERABLE_SIZE, preferableSize, DEFAULT.getPreferableSize());

                       /* Expiration Timer */
                       UnitOfTime.toElementCompact(doc, base, SCHEDULE_EVICT_TAG,
                               scheduleEvictionAtFixedRateNanos, TimeUnit.NANOSECONDS,
                               DEFAULT.scheduleEvictionAtFixedRateNanos);

    }
    

    public long getScheduledEvictionAtFixedRate(TimeUnit unit) {
        if (scheduleEvictionAtFixedRateNanos == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        } else {
            return unit.convert(scheduleEvictionAtFixedRateNanos, TimeUnit.NANOSECONDS);
        }
    }
//  /**
//   * 0 = automatic.
//   * <p>
//   * This requires that the CacheThreadingConfiguration isEnabled.
//   * 
//   * @param period
//   * @param unit
//   *            the time unit of the specified period
//   * @return this configuration
//   */
//  public CacheEvictionConfiguration<K, V> setScheduledEvictionAtFixedRate(long period,
//          TimeUnit unit) {
//      if (period < 0) {
//          throw new IllegalArgumentException("period must be 0 or greater, was "
//                  + period);
//      }
//      scheduleEvictionAtFixedRateNanos = unit.toNanos(period);
//      return this;
//  }

// public CacheEvictionConfiguration<K, V> setPreferableVolume(long volume) {
// if (volume < 0) {
// throw new IllegalArgumentException("volume must greater then 0, was "
// + volume);
// }
// preferableVolume = volume;
// return this;
// }
//
// public CacheEvictionConfiguration<K, V> setPreferableSize(int elements) {
// if (elements < 0) {
// throw new IllegalArgumentException(
// "number of maximum elements must be 0 or greater, was " + elements);
// }
// preferableSize = elements;
// return this;
// }
//
// public long getPreferableVolume() {
// return preferableVolume;
// }
//
// public int getPreferableSize() {
// return preferableSize;
// }
}
