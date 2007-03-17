/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.util;

import java.util.concurrent.TimeUnit;

import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationService;
import org.coconut.cache.spi.XmlConfigurator;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class InternalCacheutil {

    public static long convert(long timeout, TimeUnit unit) {
        if (timeout == CacheExpirationService.NEVER_EXPIRE) {
            return Long.MAX_VALUE;
        } else {
            long newTime = unit.toNanos(timeout);
            if (newTime == Long.MAX_VALUE) {
                throw new IllegalArgumentException(
                        "Overflow for specified expiration time, was " + timeout + " "
                                + unit);
            }
            return newTime;
        }
    }
    
    public static boolean isThreadSafe(CacheConfiguration conf) {
        Class c = null;
        String s = (String) conf.getProperty(XmlConfigurator.CACHE_INSTANCE_TYPE);
        try {
            c = Class.forName(s);
        } catch (ClassNotFoundException e1) {
            throw new Error("Could not find class " + s + ", this is highly irregular");
        }
        return c.isAnnotationPresent(ThreadSafe.class);
    }
}
