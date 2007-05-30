/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache;

import java.util.concurrent.TimeUnit;

import org.coconut.cache.policy.PolicyAttributes;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.core.AttributeMap;

/**
 * This class maintains a number of common attribute keys.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheAttributes implements PolicyAttributes {

    /**
     * This key can be used to indicate that how long time a cache entry should live
     * before it expires. The time-to-live value should be a long and should be measured
     * in nano seconds. Use {@link java.util.concurrent.TimeUnit} to convert between
     * different time units.
     */
    public static final String TIME_TO_LIVE_NANO = "time_to_live_ns";

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
        attributes.put(TIME_TO_LIVE_NANO, ttl);
        return attributes;
    }

    public static long getTimeToLive(AttributeMap attributes, TimeUnit unit,
            long defaultValue) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        } else if (unit == null) {
            throw new NullPointerException("unit is null");
        }
        long ttl = attributes.getLong(TIME_TO_LIVE_NANO, -1);
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

    public static final String TIME_TO_IDLE_NANO = "time_to_idle_ns";

    public static final String TIME_TO_REFRESH_NANO = "time_to_refresh_ns";

    public static final String CREATION_TIME = "creation_time";

    public static final String LAST_MODIFIED_TIME = "last_modified";

    /**
     * Whether or not any events will be raised. A Boolean value
     */
    public static final String POST_EVENT = "post_event";

    /**
     * Type Map<K,AttributeMap> can be used in getAll/removeAll/
     */
    public static final String ATTRIBUTE_MAP_TRANSFORMER = "attributemap_transformer";
}
