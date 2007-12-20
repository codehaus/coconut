/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.core.Logger;

/**
 * This class should define a number of standard {@link CacheExceptionHandler}s. However,
 * currently is only defines one {@link DefaultLoggingExceptionHandler}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class CacheExceptionHandlers {

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private CacheExceptionHandlers() {}

    // /CLOVER:ON

//    /**
//     * Creates a new instance of {@link DefaultLoggingExceptionHandler}.
//     * 
//     * @return a new instance of DefaultLoggingExceptionHandler
//     * @param <K>
//     *            the type of keys maintained by the cache
//     * @param <V>
//     *            the type of mapped values
//     */
//    public static <K, V> DefaultLoggingExceptionHandler<K, V> defaultLoggingExceptionHandler() {
//        return new DefaultLoggingExceptionHandler<K, V>();
//    }


}
