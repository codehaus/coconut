/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.expiration;

import org.coconut.management.annotation.ManagedAttribute;

/**
 * The management interface for the expiration system of a Cache.
 * <p>
 * A Cache has a single instance of the implementation class of this interface.
 * This instance implementing this interface is an <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/management/ManagementFactory.html#MXBean">MXBean</a>
 * that can be obtained by calling the
 * {@link ManagementFactory#getClassLoadingMXBean} method or from the
 * {@link ManagementFactory#getPlatformMBeanServer platform <tt>MBeanServer</tt>}
 * method.
 * <p>
 * The <tt>ObjectName</tt> for uniquely identifying the MXBean for the
 * expiration system and cache within an <tt>MBeanServer</tt> is: <blockquote>
 * {@link ManagementFactory#CLASS_LOADING_MXBEAN_NAME 
 * <tt>org.coconut.cache:cache=$CACHE_NAME$, type=Expiration</tt>}
 * </blockquote>
 * 
 * @see <a
 *      href="http://java.sun.com/j2se/1.5.0/docs/api/javax/management/package-summary.html">
 *      JMX Specification.</a>
 * @see <a href="package-summary.html#examples"> Ways to Access MXBeans</a>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheExpirationMXBean {
    @ManagedAttribute(defaultValue = "Default TimeToLive", description = "The default time to live for cache entries in milliseconds")
    long getDefaultTimeToLiveMs();

    @ManagedAttribute(defaultValue = "Expiration Filter", description = "toString() on the defined expiration filter")
    String getFilterAsString();

    void setDefaultTimeToLiveMs(long timeToLiveMs);

}
