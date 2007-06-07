/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.policy.PolicyAttributes;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.core.AttributeMap;

/**
 * This class maintains a number of common attribute keys. At the moment this is very much
 * work in progress.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheAttributes extends PolicyAttributes {

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
     * expires. The time-to-live value should be a long and should be measured in nano
     * seconds. Use {@link java.util.concurrent.TimeUnit} to convert between different
     * time units.
     */
    public static final String TIME_TO_LIVE_NS = "time_to_live_ns";

    /**
     * This key can be used to indicate how long time a cache entry should live before it
     * refreshed from a cacheloader. The time-to-refresh value should be a long and should be
     * measured in nano seconds. Use {@link java.util.concurrent.TimeUnit} to convert
     * between different time units.
     */
    public static final String TIME_TO_REFRESH_NS = "time_to_refresh_ns";

    public static long getTimeToLive(AttributeMap attributes, TimeUnit unit,
            long defaultValue) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        //TODO what if time is negative
        long ttl = attributes.getLong(TIME_TO_LIVE_NS, -1);
        if (ttl == -1) {
            return defaultValue;
        } else {
            if (ttl != CacheExpirationService.NEVER_EXPIRE
                    || ttl != CacheExpirationService.DEFAULT_EXPIRATION) {
                ttl = unit.convert(ttl, TimeUnit.NANOSECONDS);
            }
            return ttl;
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
        long ttl = timeToLive;
        if (ttl != CacheExpirationService.NEVER_EXPIRE
                || ttl != CacheExpirationService.DEFAULT_EXPIRATION) {
            ttl = TimeUnit.NANOSECONDS.convert(ttl, unit);
        }
        attributes.put(TIME_TO_LIVE_NS, ttl);
        return attributes;
    }
}
