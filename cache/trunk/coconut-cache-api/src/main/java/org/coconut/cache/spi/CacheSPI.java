/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.util.ResourceBundle;

import org.coconut.cache.CacheConfiguration;
import org.coconut.internal.util.ResourceHolder;

/**
 * This class is used internally. It should not be referenced by user code.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class CacheSPI {

    /** A message indicating a highly irregular error. */
    public static final String HIGHLY_IRREGULAR_MSG = "This is a highly irregular exception, and most likely means that the jar containing this class is corrupt";

    /** A message indicating a highly irregular error. */
    static final String BUNDLE_NAME = "org.coconut.cache.messages";//$NON-NLS-1$

    /** A message indicating a highly irregular error. */
    static final ResourceBundle DEFAULT_CACHE_BUNDLE = ResourceHolder.lookup(BUNDLE_NAME);

    /** A message indicating a highly irregular error. */
    private static final ResourceHolder RESOURCE_HOLDER = new ResourceHolder(BUNDLE_NAME);

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
        return RESOURCE_HOLDER.lookup(c.getSimpleName() + "." + key, o);
    }

}
