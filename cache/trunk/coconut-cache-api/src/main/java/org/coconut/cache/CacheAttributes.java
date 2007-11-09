/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.concurrent.TimeUnit;

import org.coconut.core.AttributeMap;
import org.coconut.core.Clock;

/**
 * The main purpose of a cache attribute is to support custom metadata associated with
 * each element in the cache. This cache can used set and retrieve predefined cache
 * attributes from an AttributeMap in a typesafe manner. Currently these attributes are
 * only in use for cache loaders. See
 * {@link org.coconut.cache.service.loading.CacheLoader} for examples.
 * <p>
 * The following is a list of the default provided attributes
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @see $HeadURL$
 */
public final class CacheAttributes {

    /**
     * The <tt>Cost</tt> attribute indicates the <tt>cost</tt> of retrieving or
     * calculating an element. The mapped value must be of a type <tt>double</tt> and
     * can be any value except {@link Double#NaN}, {@link Double#NEGATIVE_INFINITY} or
     * {@link Double#POSITIVE_INFINITY}
     * 
     * @see #setCost(AttributeMap, double)
     * @see #getCost(AttributeMap)
     */
    public static final String COST = "cost";

    /**
     * The <tt>Creation time</tt> attribute indicates the creation time of a cache
     * element. The mapped value must be of a type <tt>long</tt> between 1 and
     * {@link Long#MAX_VALUE}.
     * 
     * @see #setCreationTime(AttributeMap, long)
     * @see #getCreationTime(AttributeMap)
     * @see #getCreationTime(AttributeMap, Clock)
     */
    public static final String CREATION_TIME = "creation_time";

    /**
     * The <tt>Hits</tt> attribute indicates the number of hits for a cache element. The
     * mapped value must be of a type <tt>long</tt> between 0 and {@link Long#MAX_VALUE}.
     * 
     * @see #setHits(AttributeMap, long)
     * @see #getHits(AttributeMap)
     */
    public static final String HITS = "hits";

    /**
     * The <tt>Last modified time</tt> attribute indicates when a cache element was last
     * modified. The mapped value must be of a type <tt>long</tt> between 1 and
     * {@link Long#MAX_VALUE}.
     * 
     * @see #setLastModifiedTime(AttributeMap, long)
     * @see #getLastModified(AttributeMap, Clock)
     */
    public static final String LAST_MODIFIED_TIME = "last_modified";

    /**
     * The <tt>Size</tt> attribute indicates the <tt>size</tt> of a cache element. The
     * mapped value must be of a type <tt>long</tt> between 1 and {@link Long#MAX_VALUE}.
     * <p>
     * TODO It might make sense to allow a size of 0 indicating free storage.
     * 
     * @see #setSize(AttributeMap, long)
     * @see #getSize(AttributeMap)
     */
    public static final String SIZE = "size";

    /**
     * This key can be used to indicate how long time a cache entry should live before it
     * expires. The time-to-live value should be a long between 1 and
     * {@link Long#MAX_VALUE} measured in nanoseconds. Use
     * {@link java.util.concurrent.TimeUnit} to convert between different time units.
     * 
     * @see org.coconut.cache.service.expiration.CacheExpirationService
     */
    public static final String TIME_TO_LIVE_NS = "time_to_live_ns";

    /**
     * This key can be used to indicate how long time a cache entry should live before it
     * refreshed from a cacheloader. The time-to-refresh value should be a long and should
     * be measured in nano seconds. Use {@link java.util.concurrent.TimeUnit} to convert
     * between different time units.
     */
    public static final String TIME_TO_REFRESH_NS = "time_to_refresh_ns";

    /** The default value of the {@link #COST} attribute. */
    public static final double DEFAULT_COST = 1.0;

    /** The default value of the {@link #SIZE} attribute. */
    public static final long DEFAULT_SIZE = 1;

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private CacheAttributes() {}
    // /CLOVER:ON
    
    /**
     * Returns the value that the specified AttributeMap maps the {@link #COST} attribute
     * to or {@link CacheAttributes#DEFAULT_COST} if no such mapping exist.
     * 
     * @param attributes
     *            the map to retrieve the value of the cost attribute from
     * @return returns the value that the specified AttributeMap maps the cost attribute
     *         to or {@value CacheAttributes#DEFAULT_COST} if no such mapping exist
     * @throws NullPointerException
     *             if the specified attributeMap is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified attributeMap returns {@link Double#NaN},
     *             {@link Double#NEGATIVE_INFINITY} or {@link Double#POSITIVE_INFINITY} as
     *             a value for the cost attribute
     * @see #setCost(AttributeMap, double)
     * @see #COST
     */
    public static double getCost(AttributeMap attributes) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        double cost = attributes.getDouble(COST, CacheAttributes.DEFAULT_COST);
        if (Double.isNaN(cost)) {
            throw new IllegalArgumentException("invalid cost (cost = Nan)");
        } else if (Double.isInfinite(cost)) {
            throw new IllegalArgumentException("invalid cost (cost = Infinity)");
        }
        return cost;
    }

    /**
     * Returns the number of hits for the {@link #HITS} attribute.
     * 
     * @param attributes
     *            the map to retrieve the value of the hit attribute from
     * @return the number of hits
     * @throws IllegalArgumentException
     *             if the specified attributeMap returns a negative number for the HITS
     *             attribute
     */
    public static long getHits(AttributeMap attributes) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        long hits = attributes.getLong(HITS);
        if (hits < 0) {
            throw new IllegalArgumentException("invalid hit count (hits = " + hits + ")");
        }
        return hits;
    }

    /**
     * Returns the value that the specified AttributeMap maps the {@link #CREATION_TIME}
     * attribute to or the return value from a call to {@link Clock#timestamp()} on the
     * specified clock if no such mapping exist.
     * 
     * @param attributes
     *            the map to retrieve the value of the creation time attribute from
     * @param clock
     *            the clock to retrieve the timestamp from
     * @return returns the value that the specified AttributeMap maps the
     *         {@link #CREATION_TIME} attribute to or the return value from a call to
     *         {@link Clock#timestamp()} on the specified clock if no such mapping exist
     * @throws NullPointerException
     *             if the specified attributeMap or clock is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified attributeMap returns a negative number or if the
     *             specified clock returns a negative number when calling
     *             {@link Clock#timestamp()}
     * @see #setCreationTime(AttributeMap, long)
     * @see #CREATION_TIME
     */
    public static long getCreationTime(AttributeMap attributes, Clock clock) {
        if (clock == null) {
            throw new NullPointerException("clock is null");
        }
        long time = getCreationTime(attributes);
        if (time == 0) {
            time = clock.timestamp();
            if (time < 0) {
                throw new IllegalArgumentException(
                        "the timestamp returned by the specified clock was negative (creationTime = "
                                + time + ")");
            }
        }
        return time;
    }

    /**
     * Returns the value that the specified AttributeMap maps the {@link #CREATION_TIME}
     * attribute to .
     * 
     * @param attributes
     *            the map to retrieve the value of the creation time attribute from
     * @return returns the value that the specified AttributeMap maps the
     *         {@link #CREATION_TIME} attribute to
     * @throws NullPointerException
     *             if the specified attributeMap is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified attributeMap returns a negative number
     * @see #setCreationTime(AttributeMap, long)
     * @see #CREATION_TIME
     */
    public static long getCreationTime(AttributeMap attributes) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        long time = attributes.getLong(CacheAttributes.CREATION_TIME);
        if (time < 0) {
            throw new IllegalArgumentException("creationTime was negative (creationTime = " + time
                    + ")");
        }
        return time;
    }

    /**
     * Returns the value that the specified AttributeMap maps the
     * {@link #LAST_MODIFIED_TIME} attribute to or the return value from a call to
     * {@link Clock#timestamp()} on the specified clock if no such mapping exist.
     * 
     * @param attributes
     *            the map to retrieve the value of the creation time attribute from
     * @param clock
     *            the clock to retrieve the timestamp from
     * @return returns the value that the specified AttributeMap maps the
     *         {@link #LAST_MODIFIED_TIME} attribute to or the return value from a call to
     *         {@link Clock#timestamp()} on the specified clock if no such mapping exist
     * @throws NullPointerException
     *             if the specified attributeMap or clock is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified attributeMap returns a negative number or if the
     *             specified clock returns a negative number when calling
     *             {@link Clock#timestamp()}
     * @see #setLastModifiedTime(AttributeMap, long)
     * @see #LAST_MODIFIED_TIME
     */
    public static long getLastModified(AttributeMap attributes, Clock clock) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (clock == null) {
            throw new NullPointerException("clock is null");
        }
        long time = attributes.getLong(CacheAttributes.LAST_MODIFIED_TIME);
        if (time < 0) {
            throw new IllegalArgumentException("lastModified was negative (lastModified = " + time
                    + ")");
        } else if (time == 0) {
            time = clock.timestamp();
            if (time < 0) {
                throw new IllegalArgumentException(
                        "the timestamp returned by the specified clock was negative (lastModified = "
                                + time + ")");
            }
        }
        return time;
    }

    /**
     * Returns the value that the specified AttributeMap maps the {@link #SIZE} attribute
     * to or {@link CacheAttributes#DEFAULT_SIZE} if no such mapping exist.
     * 
     * @param attributes
     *            the map to retrieve the value of the size attribute from
     * @return returns the value that the specified AttributeMap maps the size attribute
     *         to or {@value CacheAttributes#DEFAULT_SIZE} if no such mapping exist
     * @throws NullPointerException
     *             if the specified attributeMap is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified attributeMap returns a negative number
     * @see #setSize(AttributeMap, long)
     * @see #SIZE
     */
    public static long getSize(AttributeMap attributes) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        long size = attributes.getLong(SIZE, CacheAttributes.DEFAULT_SIZE);
        if (size < 0) {
            throw new IllegalArgumentException("invalid size (size = " + size + ")");
        }
        return size;
    }

    /**
     * Returns the value that the specified AttributeMap maps the {@link #TIME_TO_LIVE_NS}
     * attribute to or the default specified value if no such mapping exist.
     * 
     * @param attributes
     *            the map to retrieve the value of the time to live attribute from
     * @param unit
     *            the unit that the time should be returned in
     * @param defaultValue
     *            the value that should be returned if a mapping for the time to live
     *            attribute does not exist in the specified attribute map
     * @return returns the value that the specified AttributeMap maps the
     *         {@link #TIME_TO_LIVE_NS} attribute to default specified value if no such
     *         mapping exist
     * @throws NullPointerException
     *             if the specified attributeMap is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified attributeMap returns a negative number
     * @see #setTimeToLive(AttributeMap, long, TimeUnit)
     * @see #TIME_TO_LIVE_NS
     */
    public static long getTimeToLive(AttributeMap attributes, TimeUnit unit, long defaultValue) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        } else if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue must be a positive value");
        }
        long ttl = attributes.getLong(TIME_TO_LIVE_NS);
        if (ttl < 0) {
            throw new IllegalArgumentException("invalid timeToLive (timeToLiveNs = " + ttl + ")");
        }
        if (ttl == 0) {
            return defaultValue;
        } else if (ttl == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        } else {
            return unit.convert(ttl, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * Returns the value that the specified AttributeMap maps the
     * {@link #TIME_TO_REFRESH_NS} attribute to or the default specified value if no such
     * mapping exist.
     * 
     * @param attributes
     *            the map to retrieve the value of the time to refresh attribute from
     * @param unit
     *            the unit that the time should be returned in
     * @param defaultValue
     *            the value that should be returned if a mapping for the time to live
     *            attribute does not exist in the specified attribute map
     * @return returns the value that the specified AttributeMap maps the
     *         {@link #TIME_TO_REFRESH_NS} attribute to, or the default specified value if
     *         no such mapping exist
     * @throws NullPointerException
     *             if the specified attributeMap is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified attributeMap returns a negative number for the time to
     *             refresh attribute
     * @see #setTimeToRefresh(AttributeMap, long, TimeUnit)
     * @see #TIME_TO_REFRESH_NS
     */
    public static long getTimeToRefresh(AttributeMap attributes, TimeUnit unit, long defaultValue) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        } else if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue must be a positive value");
        }
        long ttl = attributes.getLong(TIME_TO_REFRESH_NS);
        if (ttl < 0) {
            throw new IllegalArgumentException("invalid refreshTime (refreshTimeNs = " + ttl + ")");
        }
        if (ttl == 0) {
            return defaultValue;
        } else if (ttl == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        } else {
            return unit.convert(ttl, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * Sets a value for the {@link #COST} attribute in the specified AttributeMap.
     * 
     * @param attributes
     *            the map of attributes to set the cost attribute in
     * @param cost
     *            the cost to set the cost attribute to
     * @return the specified attribute map
     * @throws NullPointerException
     *             if the specified attributeMap is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified cost is {@link Double#NaN},
     *             {@link Double#NEGATIVE_INFINITY} or {@link Double#POSITIVE_INFINITY}
     * @see #getCost(AttributeMap)
     * @see #COST
     */
    public static AttributeMap setCost(AttributeMap attributes, double cost) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (Double.isNaN(cost)) {
            throw new IllegalArgumentException("invalid cost (cost = Nan)");
        } else if (Double.isInfinite(cost)) {
            throw new IllegalArgumentException("invalid cost (cost = Infinity)");
        }
        attributes.putDouble(COST, cost);
        return attributes;
    }

    /**
     * Sets a value for the {@link #LAST_MODIFIED_TIME} attribute in the specified
     * AttributeMap.
     * 
     * @param attributes
     *            the map of attributes to set the last modified time attribute in
     * @param lastModifiedTime
     *            the last modified time
     * @return the specified attribute map
     * @throws NullPointerException
     *             if the specified attributeMap is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified last modified time is a negative number
     * @see #getLastModified(AttributeMap, Clock)
     * @see #LAST_MODIFIED_TIME
     */
    public static AttributeMap setLastModifiedTime(AttributeMap attributes, long lastModifiedTime) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (lastModifiedTime < 0) {
            throw new IllegalArgumentException("invalid creationTime (creationTime = "
                    + lastModifiedTime + ")");
        }
        attributes.putLong(LAST_MODIFIED_TIME, lastModifiedTime);
        return attributes;
    }

    /**
     * Sets a value for the {@link #CREATION_TIME} attribute in the specified
     * AttributeMap.
     * 
     * @param attributes
     *            the map of attributes to set the creation time attribute in
     * @param creationTime
     *            the creation time
     * @return the specified attribute map
     * @throws NullPointerException
     *             if the specified attributeMap is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified creation time is a negative number
     * @see #getCreationTime(AttributeMap)
     * @see #CREATION_TIME
     */
    public static AttributeMap setCreationTime(AttributeMap attributes, long creationTime) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (creationTime < 0) {
            throw new IllegalArgumentException("invalid creationTime (creationTime = "
                    + creationTime + ")");
        }
        attributes.putLong(CREATION_TIME, creationTime);
        return attributes;
    }

    /**
     * Sets a value for the {@link #SIZE} attribute in the specified AttributeMap.
     * 
     * @param attributes
     *            the map of attributes to set the size attribute in
     * @param size
     *            the size to set the size attribute to
     * @return the specified attribute map
     * @throws NullPointerException
     *             if the specified attributeMap is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified size is a negative number
     * @see #getSize(AttributeMap)
     * @see #SIZE
     */
    public static AttributeMap setSize(AttributeMap attributes, long size) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (size < 0) {
            throw new IllegalArgumentException("invalid size (size = " + size + ")");
        }
        attributes.putLong(SIZE, size);
        return attributes;
    }

    /**
     * Sets a value for the {@link #HITS} attribute in the specified AttributeMap.
     * 
     * @param attributes
     *            the map of attributes to set the cost attribute in
     * @param hits
     *            the number of hits to set the hit attribute to
     * @return the specified attribute map
     * @throws NullPointerException
     *             if the specified attributeMap is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified hits is negative
     * @see #getHits(AttributeMap)
     * @see #HITS
     */
    public static AttributeMap setHits(AttributeMap attributes, long hits) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (hits < 0) {
            throw new IllegalArgumentException("invalid hits (hits = " + hits + ")");
        }
        attributes.putLong(HITS, hits);
        return attributes;
    }

    /**
     * Sets a value for the {@link #TIME_TO_LIVE_NS} attribute in the specified
     * AttributeMap.
     * 
     * @param attributes
     *            the map of attributes to set the cost attribute in
     * @param timeToLive
     *            the time to live
     * @param unit
     *            the unit of the specified time to live
     * @return the specified attribute map
     * @throws NullPointerException
     *             if the specified attributeMap or time unit is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified time to live is a negative number
     * @see #getTimeToLive(AttributeMap, TimeUnit, long)
     * @see #TIME_TO_LIVE_NS
     */
    public static AttributeMap setTimeToLive(AttributeMap attributes, long timeToLive, TimeUnit unit) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (timeToLive < 0) {
            throw new IllegalArgumentException("timeToLive must not be negative, was " + timeToLive);
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        if (timeToLive == 0) {
            // ignore
        } else if (timeToLive == Long.MAX_VALUE) {
            attributes.putLong(TIME_TO_LIVE_NS, Long.MAX_VALUE);
        } else {
            long ttl = TimeUnit.NANOSECONDS.convert(timeToLive, unit);
            attributes.putLong(TIME_TO_LIVE_NS, ttl);
        }
        return attributes;
    }

    /**
     * Sets a value for the {@link #TIME_TO_REFRESH_NS} attribute in the specified
     * AttributeMap.
     * 
     * @param attributes
     *            the map of attributes to set the cost attribute in
     * @param timeToRefresh
     *            the time to refresh
     * @param unit
     *            the unit of the specified time to refresh
     * @return the specified attribute map
     * @throws NullPointerException
     *             if the specified attributeMap or time unit is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified time to refresh is a negative number
     * @see #getTimeToRefresh(AttributeMap, TimeUnit, long)
     * @see #TIME_TO_REFRESH_NS
     */
    public static AttributeMap setTimeToRefresh(AttributeMap attributes, long timeToRefresh,
            TimeUnit unit) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (timeToRefresh < 0) {
            throw new IllegalArgumentException("timeToRefresh must not be negative, was "
                    + timeToRefresh);
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        if (timeToRefresh == 0) {
            // ignore
        } else if (timeToRefresh == Long.MAX_VALUE) {
            attributes.putLong(TIME_TO_REFRESH_NS, Long.MAX_VALUE);
        } else {
            long ttl = TimeUnit.NANOSECONDS.convert(timeToRefresh, unit);
            attributes.putLong(TIME_TO_REFRESH_NS, ttl);
        }
        return attributes;
    }
}
