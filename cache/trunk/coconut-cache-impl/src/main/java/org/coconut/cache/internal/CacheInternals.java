package org.coconut.cache.internal;

import java.util.ResourceBundle;

import org.coconut.cache.internal.SynchronizedInternalCache.SynchronizedInternalCacheFactory;
import org.coconut.cache.internal.UnsynchronizedInternalCache.UnsynchronizedInternalCacheFactory;
import org.coconut.internal.util.ResourceBundleUtil;

public class CacheInternals {

    public final static InternalCacheFactory DEFAULT_UNSYNCHRONIZED_CACHE = new UnsynchronizedInternalCacheFactory();

    public final static InternalCacheFactory DEFAULT_SYNCHRONIZED_CACHE = new SynchronizedInternalCacheFactory();

    private static final String BUNDLE_NAME = "org.coconut.cache.messagesimpl";//$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private CacheInternals() {}

    // /CLOVER:ON

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
        String k = key.replace(' ', '_');
        return ResourceBundleUtil.lookupKey(RESOURCE_BUNDLE, c.getSimpleName() + "." + k, o);
    }
}
