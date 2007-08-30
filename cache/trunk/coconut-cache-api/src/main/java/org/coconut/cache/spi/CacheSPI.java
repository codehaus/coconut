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

    public static final String HIGHLY_IRREGULAR = "This is a highly irregular exception, and most likely means that the jar containing this class is corrupt";

    static final String BUNDLE_NAME = "org.coconut.cache.messages";//$NON-NLS-1$

    static final ResourceBundle DEFAULT_CACHE_BUNDLE = ResourceHolder.lookup(BUNDLE_NAME);

    private static final ResourceHolder RESOURCE_HOLDER = new ResourceHolder(BUNDLE_NAME);

    /** Cannot instantiate. */
    private CacheSPI() {}

    public static String lookup(Class<?> c, String key, Object... o) {
        return RESOURCE_HOLDER.lookup(c, key, o);
    }

    public static <K, V> void initializeConfiguration(
            AbstractCacheServiceConfiguration<K, V> c, CacheConfiguration<K, V> conf) {
        c.setConfiguration(conf);
    }

}
