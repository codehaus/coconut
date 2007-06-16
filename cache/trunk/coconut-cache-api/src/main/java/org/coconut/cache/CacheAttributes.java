/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.concurrent.TimeUnit;

import org.coconut.core.AttributeMap;
import org.coconut.core.Clock;

/**
 * This class maintains a number of common attribute keys. At the moment this is very much
 * work in progress.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheAttributes {

    /**
     * This attribute key can be used to indicate the <tt>cost</tt> of retrieving or
     * calculating an element. The mapped value must can be any <tt>double</tt> value
     * except {@link Double#NaN}, {@link Double#NEGATIVE_INFINITY} or
     * {@link Double#POSITIVE_INFINITY}
     * 
     * @see #setCost(AttributeMap, double)
     * @see #getCost(AttributeMap)
     */
    public static final String COST = "cost";

    /**
     * This attribute key can be used to indicate the creation time of a cache element.
     * The mapped value must be <tt>long</tt> between 1 and {@link Long#MAX_VALUE}.
     */
    public static final String CREATION_TIME = "creation_time";

    public static final String HITS = "hits";

    public static final String LAST_MODIFIED_TIME = "last_modified";

    /**
     * This attribute key can be used to indicate the size of an element. The mapped value
     * must be <tt>long</tt> between 1 and {@link Long#MAX_VALUE}.
     */
    /*
     * It might make sense to allow a size of 0 indicating free storage.
     */
    public static final String SIZE = "size";

    /**
     * This key can be used to indicate how long time a cache entry should live before it
     * expires. The time-to-live value should be a long between 1 and
     * {@link Long#MAX_VALUE} and should be measured in nanoseconds. Use
     * {@link java.util.concurrent.TimeUnit} to convert between different time units.
     */
    public static final String TIME_TO_LIVE_NS = "time_to_live_ns";

    /**
     * This key can be used to indicate how long time a cache entry should live before it
     * refreshed from a cacheloader. The time-to-refresh value should be a long and should
     * be measured in nano seconds. Use {@link java.util.concurrent.TimeUnit} to convert
     * between different time units.
     */
    public static final String TIME_TO_REFRESH_NS = "time_to_refresh_ns";

    /**
     * Returns the value that the specified AttributeMap maps the {@link #COST} attribute
     * maps to or {@link ReplacementPolicy#DEFAULT_COST} if no such mapping exist.
     * 
     * @param attributeMap
     *            the map of attributes to retrieve the cost from
     * @return returns the value that specified AttributeMap maps the cost attribute to or
     *         {@value ReplacementPolicy#DEFAULT_COST} if no such mapping exist
     * @throws NullPointerException
     *             if the specified attributeMap is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified attributeMap returns {@link Double#NaN},
     *             {@link Double#NEGATIVE_INFINITY} or {@link Double#POSITIVE_INFINITY} as
     *             a value for the cost attribute
     * @see #setCost(AttributeMap, double)
     * @see #COST
     */
    public static double getCost(AttributeMap attributeMap) {
        if (attributeMap == null) {
            throw new NullPointerException("attributes is null");
        }
        double cost = attributeMap.getDouble(COST, ReplacementPolicy.DEFAULT_COST);
        if (Double.isNaN(cost)) {
            throw new IllegalArgumentException("invalid cost (cost = Nan)");
        } else if (Double.isInfinite(cost)) {
            throw new IllegalArgumentException("invalid cost (cost = Infinity)");
        }
        return cost;
    }

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

    public static long getCreationTime(AttributeMap attributes, Clock clock) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (clock == null) {
            throw new NullPointerException("clock is null");
        }
        long time = attributes.getLong(CacheAttributes.CREATION_TIME);
        if (time < 0) {
            throw new IllegalArgumentException(
                    "creationTime was negative (creationTime = " + time + ")");
        } else if (time == 0) {
            time = clock.timestamp();
            if (time < 0) {
                throw new IllegalArgumentException(
                        "the timestamp returned by the specified clock was negative (creationTime = "
                                + time + ")");
            }
        }
        return time;
    }

    public static long getLastModified(AttributeMap attributes, Clock clock) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (clock == null) {
            throw new NullPointerException("clock is null");
        }
        long time = attributes.getLong(CacheAttributes.LAST_MODIFIED_TIME);
        if (time < 0) {
            throw new IllegalArgumentException(
                    "lastModified was negative (lastModified = " + time + ")");
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

    public static long getSize(AttributeMap attributes) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        long size = attributes.getLong(SIZE, ReplacementPolicy.DEFAULT_SIZE);
        if (size < 0) {
            throw new IllegalArgumentException("invalid size (size = " + size + ")");
        }
        return size;
    }

    public static long getTimeToLive(AttributeMap attributes, TimeUnit unit,
            long defaultValue) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        } else if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue must be a positive value");
        }
        long ttl = attributes.getLong(TIME_TO_LIVE_NS);
        if (ttl < 0) {
            throw new IllegalArgumentException("invalid timeToLive (timeToLiveNs = "
                    + ttl + ")");
        }
        if (ttl == 0) {
            return defaultValue;
        } else if (ttl == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        } else {
            return unit.convert(ttl, TimeUnit.NANOSECONDS);
        }
    }

    public static long getTimeToRefresh(AttributeMap attributes, TimeUnit unit,
            long defaultValue) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        } else if (defaultValue <= 0) {
            throw new IllegalArgumentException("defaultValue must be a positive value");
        }
        long ttl = attributes.getLong(TIME_TO_REFRESH_NS);
        if (ttl < 0) {
            throw new IllegalArgumentException("invalid refreshTime (refreshTimeNs = "
                    + ttl + ")");
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
     * @param attributeMap
     *            the map of attributes to set the cost attribute in
     * @param cost
     *            the cost to set the cost attribute to
     * @return the specified AttributeMap
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

    public static AttributeMap setLastModifiedTime(AttributeMap attributes,
            long lastModifiedTime) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (lastModifiedTime < 0) {
            throw new IllegalArgumentException("invalid creationTime (creationTime = "
                    + lastModifiedTime + ")");
        }
        attributes.putLong(LAST_MODIFIED_TIME, lastModifiedTime);
        return attributes;
    }

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

    public static AttributeMap setSize(AttributeMap attributes, long size) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (size < 0) {
            throw new IllegalArgumentException("invalid size (size = " + size + ")");
        }
        attributes.putLong(SIZE, size);
        return attributes;
    }

    public static AttributeMap setHits(AttributeMap attributes, long hits) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (hits < 0) {
            throw new IllegalArgumentException("invalid hits (hits = " + hits + ")");
        }
        attributes.putLong(HITS, hits);
        return attributes;
    }

    public static AttributeMap setTimeToLive(AttributeMap attributes, long timeToLive,
            TimeUnit unit) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (timeToLive < 0) {
            throw new IllegalArgumentException("timeToLive must not be negative, was "
                    + timeToLive);
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

    public static AttributeMap setTimeToRefresh(AttributeMap attributes, long timeToLive,
            TimeUnit unit) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (timeToLive < 0) {
            throw new IllegalArgumentException("timeToRefresh must not be negative, was "
                    + timeToLive);
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        if (timeToLive == 0) {
            // ignore
        } else if (timeToLive == Long.MAX_VALUE) {
            attributes.putLong(TIME_TO_REFRESH_NS, Long.MAX_VALUE);
        } else {
            long ttl = TimeUnit.NANOSECONDS.convert(timeToLive, unit);
            attributes.putLong(TIME_TO_REFRESH_NS, ttl);
        }
        return attributes;
    }
}
