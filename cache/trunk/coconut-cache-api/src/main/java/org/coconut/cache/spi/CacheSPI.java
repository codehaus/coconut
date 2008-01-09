/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.event.CacheEventConfiguration;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandlingConfiguration;
import org.coconut.cache.service.expiration.CacheExpirationConfiguration;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.management.CacheManagementConfiguration;
import org.coconut.cache.service.memorystore.CacheEvictionConfiguration;
import org.coconut.cache.service.servicemanager.CacheServiceManagerConfiguration;
import org.coconut.cache.service.statistics.CacheStatisticsConfiguration;
import org.coconut.cache.service.worker.CacheWorkerConfiguration;
import org.coconut.internal.util.ResourceBundleUtil;

/**
 * This class is used internally. It should not be referenced by user code.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class CacheSPI {

    /** A message indicating a highly irregular error. */
    public static final String BUNDLE_NAME = "org.coconut.cache.messages";//$NON-NLS-1$

    /** A message indicating a highly irregular error. */
    public static final ResourceBundle DEFAULT_CACHE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    /** A list of all default service configuration types. */
    public final static List<Class<? extends AbstractCacheServiceConfiguration>> DEFAULT_CONFIGURATIONS = Collections
            .unmodifiableList(Arrays.asList(CacheEventConfiguration.class,
                    CacheEvictionConfiguration.class, CacheExceptionHandlingConfiguration.class,
                    CacheExpirationConfiguration.class, CacheLoadingConfiguration.class,
                    CacheManagementConfiguration.class, CacheServiceManagerConfiguration.class,
                    CacheStatisticsConfiguration.class, CacheWorkerConfiguration.class));

    /** A message indicating a highly irregular error. */
    public static final String HIGHLY_IRREGULAR_MSG = "This is a highly irregular exception, and most likely means that the jar containing this class is corrupt";

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private CacheSPI() {}

    // /CLOVER:ON

    /**
     * Sets the cacheconfiguration returned by
     * {@link AbstractCacheServiceConfiguration#c()}.
     * 
     * @param c
     *            the AbstractCacheServiceConfiguration to initialize
     * @param conf
     *            the cache configuration
     * @param <K>
     *            the type of keys maintained by the cache
     * @param <V>
     *            the type of mapped values
     */
    public static <K, V> void initializeConfiguration(AbstractCacheServiceConfiguration<K, V> c,
            CacheConfiguration<K, V> conf) {
        c.setConfiguration(conf);
    }

    /**
     * Looksup a message in the default bundle.
     * 
     * @param c
     *            the class looking up the value
     * @param key
     *            the message key
     * @param o
     *            additional parameters
     * @return a message from the default bundle.
     */
    public static String lookup(Class<?> c, String key, Object... o) {
        return ResourceBundleUtil.lookupKey(DEFAULT_CACHE_BUNDLE,c.getSimpleName() + "." + key, o);
    }
}
