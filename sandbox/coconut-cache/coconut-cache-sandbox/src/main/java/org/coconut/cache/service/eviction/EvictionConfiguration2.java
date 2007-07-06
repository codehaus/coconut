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

    }
}
