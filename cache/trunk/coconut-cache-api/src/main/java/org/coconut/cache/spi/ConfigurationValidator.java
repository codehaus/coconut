/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;

/**
 * This class is used to validate instances of CacheConfiguration at runtime. While a lot
 * of checks can be made
 * <p>
 * 1.Make sure we check that if eviction-scheduling is enabled that a
 * CacheThreadingService is enabled.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ConfigurationValidator {
    private final static ConfigurationValidator DEFAULT = new ConfigurationValidator();

    /**
     * Returns the default instance of a XmlConfigurator.
     */
    public static ConfigurationValidator getInstance() {
        return DEFAULT;
    }

    /**
     * Verifies the CacheConfiguration for the specified type of.
     * 
     * @param conf
     *            the configuration to verify
     * @param type
     *            the type of cache to verify
     */
    public void verify(CacheConfiguration<?, ?> conf, Class<? extends Cache> type) {
    // preferable capacity>maxCapacity?
    // preferable size>maxSize?
    // no policy defined->cache is free to select a policy
// Executor e = conf.threading().getExecutor();
// // if (type.isAnnotationPresent(NotThreadSafe.class)) {
// // throw new IllegalCacheConfigurationException(
// // "Cannot specify an executor, since this cache is not threadsafe");
// //
// // }
// boolean isScheduled = e instanceof ScheduledExecutorService;
//
// if (!isScheduled
// && conf.eviction().getScheduledEvictionAtFixedRate(TimeUnit.NANOSECONDS) !=
// Long.MAX_VALUE) {
// if (e == null) {
// throw new IllegalCacheConfigurationException(
// "Cannot schedule evictions, when no executor has been set");
// } else {
// throw new IllegalCacheConfigurationException(
// "The specified executor must of type java.util.concurrent.ScheduledExecutorService to
// schedule evictions, the type was, "
// + e.getClass().getCanonicalName());
// }
// }
// boolean isExecutorService = e instanceof ExecutorService;
// if (!isExecutorService && conf.threading().getShutdownExecutorService()) {
// throw new IllegalCacheConfigurationException(
// "Can only shutdown executors of type java.util.concurrent.ExecutorService, the type of
// the executor was, "
// + e.getClass().getCanonicalName());
// }
// if (!type.isAnnotationPresent(ThreadSafe.class) && e != null) {
// throw new IllegalCacheConfigurationException(
// "Cannot specify an Executor when the cache is not thread safe. It must use the
// net.jcip.annotations.ThreadSafe annotation");
// }

    }

    /**
     * Tries to verify the configuration.
     * 
     * @param conf
     *            the configuration to verify
     * @param type
     *            the type of cache to verify
     * @return true if the configuration is ok otherwise false
     */
    public boolean tryVerify(CacheConfiguration<?, ?> conf, Class<? extends Cache<?, ?>> type) {
        try {
            verify(conf, type);
            return true;
        } catch (IllegalCacheConfigurationException e) {
            // we only swallow IllegalCacheConfigurationException exceptions
        }
        return false;
    }

}
