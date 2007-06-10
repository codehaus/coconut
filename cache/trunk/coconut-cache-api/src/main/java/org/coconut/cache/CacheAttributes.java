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
public class CacheAttributes  {
    public static final String HITS = "hits";

    public static final String COST = "cost";

    public static final String SIZE = "size";
    /**
     * Type Map<K,AttributeMap> can be used in getAll/removeAll/
     */
    public static final String ATTRIBUTE_MAP_TRANSFORMER = "attributemap_transformer";

    public static final String CREATION_TIME = "creation_time";

    public static final String LAST_MODIFIED_TIME = "last_modified";

    /**
     * Whether or not any events will be raised. A Boolean value
     */
    public static final String NO_EVENTS = "post_event";

    /**
     * This key can be used to indicate how long time a cache entry should live in memory
     * before it is evicted to secondary storage such as a disk. The time-to-idle value
     * should be a long and should be measured in nano seconds. Use
     * {@link java.util.concurrent.TimeUnit} to convert between different time units.
     */
    public static final String TIME_TO_IDLE_NS = "time_to_idle_ns";

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

    public static AttributeMap setSize(AttributeMap attributes, long size) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (size < 0) {
            throw new IllegalArgumentException("invalid size (size = " + size + ")");
        }
        attributes.putLong(SIZE, size);
        return attributes;
    }

    public static long getLastModified(AttributeMap attributes, Clock clock) {
        if (attributes == null) {
            throw new NullPointerException("attributes, clock is null");
        } else if (clock == null) {
            throw new NullPointerException("clock is null");
        }
        long time = attributes.getLong(CacheAttributes.LAST_MODIFIED_TIME);
        if (time < 0) {
            throw new IllegalArgumentException(
                    "lastModified was negative (lastModified = " + time + ")");
        } else if (time == 0) {
            return clock.timestamp();
        } else {
            return time;
        }
    }

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

    public static double getCost(AttributeMap attributes) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        double cost = attributes.getDouble(COST, ReplacementPolicy.DEFAULT_COST);
        if (Double.isNaN(cost)) {
            throw new IllegalArgumentException("invalid cost (cost = Nan)");
        } else if (Double.isInfinite(cost)) {
            throw new IllegalArgumentException("invalid cost (cost = Infinity)");
        }
        return cost;
    }

    public static long getTimeToLive(AttributeMap attributes, TimeUnit unit,
            long defaultValue) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
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
}
