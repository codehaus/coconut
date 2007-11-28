/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.management.CacheManagementService;

/**
 * This class is used to validate instances of CacheConfiguration at runtime and the given
 * {@link Cache} type that is being instantiated.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ConfigurationValidator {

    /** A default instance of ConfigurationValidator. */
    private final static ConfigurationValidator DEFAULT = new ConfigurationValidator();

    /**
     * Tries to verify the configuration.
     * 
     * @param conf
     *            the configuration to verify
     * @param type
     *            the type of cache to verify against
     * @return true if the configuration is ok otherwise false
     */
    public boolean tryVerify(CacheConfiguration<?, ?> conf, Class<? extends Cache> type) {
        try {
            verify(conf, type);
            return true;
        } catch (IllegalCacheConfigurationException e) {
            // we only swallow IllegalCacheConfigurationException exceptions
        }
        return false;
    }

    /**
     * Verifies the CacheConfiguration for the specified type of cache.
     * 
     * @param conf
     *            the configuration to verify
     * @param cacheType
     *            the type of cache to verify against
     * @throws IllegalCacheConfigurationException if the cache does not fully support the
     *        specified configuration
     */
    public void verify(CacheConfiguration<?, ?> conf, Class<? extends Cache> cacheType) {
        CacheServiceSupport support = cacheType.getAnnotation(CacheServiceSupport.class);
        if (support == null) {
            throw new IllegalCacheConfigurationException(String.format(
                    "class '%s' must have a CacheServiceSupport annotation", cacheType.getName()));
        }
        Set<Class> supportedServices = new HashSet(Arrays.asList(support.value()));

        if (!supportedServices.contains(CacheManagementService.class)
                && conf.management().isEnabled()) {
            throw new IllegalCacheConfigurationException(
                    "class '"
                            + cacheType
                            + "' does not support management (enable via CacheManagementConfiguration.setEnabled())");
        }
    }

    /**
     * Returns the default instance of a XmlConfigurator.
     * 
     * @return the default instance of a XmlConfigurator
     */
    public static ConfigurationValidator getInstance() {
        return DEFAULT;
    }

}
