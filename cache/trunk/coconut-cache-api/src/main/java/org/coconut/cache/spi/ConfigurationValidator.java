/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.jcip.annotations.ThreadSafe;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;

/**
 * This class is used to validate instances of CacheConfiguration.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ConfigurationValidator {
    private final static ConfigurationValidator DEFAULT = new ConfigurationValidator();

    /**
     * Returns the default instance of a XmlConfigurator.
     */
    public static ConfigurationValidator getInstance() {
        return DEFAULT;
    }

    public void verify(CacheConfiguration<?, ?> conf, Class<? extends Cache> type) {
        // preferable capacity>maxCapacity?
        // preferable size>maxSize?
        // no policy defined->cache is free to select a policy
        Executor e = conf.serviceThreading().getExecutor();
        // if (type.isAnnotationPresent(NotThreadSafe.class)) {
        // throw new IllegalCacheConfigurationException(
        // "Cannot specify an executor, since this cache is not threadsafe");
        //
        // }
        boolean isScheduled = e instanceof ScheduledExecutorService;

        if (!isScheduled
                && conf.serviceThreading().getScheduledEvictionAtFixedRate(
                        TimeUnit.NANOSECONDS) > 0) {
            if (e == null) {
                throw new IllegalCacheConfigurationException(
                        "Cannot schedule evictions, when no executor has been set");
            } else {
                throw new IllegalCacheConfigurationException(
                        "The specified executor must of type java.util.concurrent.ScheduledExecutorService to schedule evictions, the type was, "
                                + e.getClass().getCanonicalName());
            }
        }
        boolean isExecutorService = e instanceof ExecutorService;
        if (!isExecutorService && conf.serviceThreading().getShutdownExecutorService()) {
            throw new IllegalCacheConfigurationException(
                    "Can only shutdown executors of type java.util.concurrent.ExecutorService, the type of the executor was, "
                            + e.getClass().getCanonicalName());
        }
        if (!type.isAnnotationPresent(ThreadSafe.class) && e != null) {
            throw new IllegalCacheConfigurationException(
                    "Cannot specify an Executor when the cache is not thread safe. It must use the net.jcip.annotations.ThreadSafe annotation");
        }

    }

    public boolean tryVerify(CacheConfiguration<?, ?> conf,
            Class<? extends Cache<?, ?>> type) {
        try {
            verify(conf, type);
            return true;
        } catch (IllegalCacheConfigurationException e) {
            // we only swallow IllegalCacheConfiguration exceptions
        }
        return false;
    }

    public static void initializeConfiguration(AbstractCacheServiceConfiguration c,
            CacheConfiguration conf) {
        c.setConfiguration(conf);
    }

}
