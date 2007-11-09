/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.expiration;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

/**
 * The management interface for the expiration service.
 * <p>
 * A Cache has a single instance of the implementation class of this interface. This
 * instance implementing this interface is an <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/management/ManagementFactory.html#MXBean">MXBean</a>
 * that can be obtained by calling the {@link ManagementFactory#getClassLoadingMXBean}
 * method or from the
 * {@link ManagementFactory#getPlatformMBeanServer platform <tt>MBeanServer</tt>} method.
 * <p>
 * The <tt>ObjectName</tt> for uniquely identifying the MXBean for the expiration system
 * and cache within an <tt>MBeanServer</tt> is: <blockquote>
 * {@link ManagementFactory#CLASS_LOADING_MXBEAN_NAME 
 * <tt>org.coconut.cache:cache=$CACHE_NAME$, type=Expiration</tt>} </blockquote>
 * 
 * @see <a
 *      href="http://java.sun.com/j2se/1.5.0/docs/api/javax/management/package-summary.html">
 *      JMX Specification.</a>
 * @see <a href="package-summary.html#examples"> Ways to Access MXBeans</a>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface CacheExpirationMXBean {
    /**
     * Removes all expired items from the cache.
     */
    void purgeExpired();

    /**
     * Returns the default time to live in milliseconds for new elements that are added to
     * the cache.
     * 
     * @return the default time to live in milliseconds for new elements that are added to
     *         the cache
     * @see #setDefaultTimeToLiveMs(long)
     * @see CacheExpirationConfiguration#getDefaultTimeToLive(TimeUnit)
     */
    long getDefaultTimeToLiveMs();

    /**
     * Sets the default time to live in milliseconds for new elements that are added to
     * the cache.
     * 
     * @param timeToLiveMs
     *            the time from insertion or update to the point where the entry should
     *            expire in milliseconds
     * @throws IllegalArgumentException
     *             if the specified time to live is negative (<0)
     * @see #getDefaultTimeToLiveMs()
     * @see CacheExpirationConfiguration#setDefaultTimeToLive(long, TimeUnit)
     */
    void setDefaultTimeToLiveMs(long timeToLiveMs);
}
