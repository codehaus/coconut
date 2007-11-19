/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.Arrays;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.management.CacheManagementService;

/**
 * This class is used to validate instances of CacheConfiguration at runtime. While a lot
 * of checks can be made
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ConfigurationValidator {
    /** A default instance of ConfigurationValidator. */
    private final static ConfigurationValidator DEFAULT = new ConfigurationValidator();

    /**
     * Returns the default instance of a XmlConfigurator.
     * 
     * @return the default instance of a XmlConfigurator
     */
    public static ConfigurationValidator getInstance() {
        return DEFAULT;
    }

    /**
     * Returns whether or not the specified service is supported by the specified cache.
     * 
     * @param cache
     *            the cache to check
     * @param service
     *            the service to check for
     * @return whether or not the specified service is supported by the specified cache
     */
    private boolean isSupported(Class<? extends Cache> cache, Class<?> service) {
        CacheServiceSupport support = cache.getAnnotation(CacheServiceSupport.class);
        if (support == null) {
            throw new IllegalArgumentException(String.format(
                    "class '%s' must have a CacheServiceSupport annotation", cache.getName()));
        }
        return Arrays.asList(support.value()).contains(service);
    }

    /**
     * Verifies the CacheConfiguration for the specified type of.
     * 
     * @param conf
     *            the configuration to verify
     * @param cacheType
     *            the type of cache to verify
     * @throw IllegalCacheConfigurationException if the cache does not fully support the
     *        specified configuration
     */
    public void verify(CacheConfiguration<?, ?> conf, Class<? extends Cache> cacheType) {
        if (!isSupported(cacheType, CacheManagementService.class) && conf.management().isEnabled()) {
            throw new IllegalCacheConfigurationException(
                    "class '"
                            + cacheType
                            + "' does not support management (enable via CacheManagementConfiguration.setEnabled())");
        }
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
