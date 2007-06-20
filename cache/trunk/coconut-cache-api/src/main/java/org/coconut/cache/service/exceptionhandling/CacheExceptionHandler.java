/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.core.AttributeMap;
import org.coconut.event.EventSubscription;

/**
 * The purpose of this class is to have one central place where all exceptions are
 * handled. This class is abstract but CacheHandlers defines a number of default exception
 * handling policies. An exception handling policy where any exception that occurs within
 * the cache will shutdow cache will be shutdowned
 * <p>
 * There are 4 basis <tt>General</tt> methods for handling exceptions. handleException,
 * handleRuntimeException, handleError. handleException is called when the system catches
 * a checked exception. For example, if a call to load fails with some exception. In most
 * situations these should just be logged and the cache should continue as nothing has
 * happend. handleRuntimeExceptions. If a runtime exception occurs within the cache it is
 * normally a serious situation. This could, for example, be some user provided callback
 * that fails in some mysterious way. Or even worse that the cache implementation contains
 * a bug. Of course, this is highly unlikely if using one of the default. Finally there is
 * handleError, a call to this method indicates serious problems that a reasonable
 * application should not try to handle.
 * <p>
 * This class also contains a handleWarning. This always indicates a non-critical problem
 * that should be fixed. For example, if a CacheLoader tries to set the size of a newly
 * loaded element to a negative number.
 * <p>
 * In addition to these 4 general methods there are also a number of specialized methods
 * such as .... The idea is that all common exception points has a corresponding method in
 * CacheExceptionHandler. For example, whenever an exception occurs while loading an
 * element in a cache loader the #load method is call. In addition to the exception that
 * was raised a number of additional information is provided to this method. For example,
 * the key for which the load failed, the cache in which the cache occured as well as
 * other relevant information. The default implementation provided in this class just
 * calls the handleException method with the provided exception.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class CacheExceptionHandler<K, V> {
    public V loadFailed(CacheExceptionContext<K, V> context,
            CacheLoader<? super K, ?> loader, K key, AttributeMap map, boolean isGet,
            Exception cause) {
        handleException(context, cause);
        return null;
    }

    public boolean eventDeliveryFailed(CacheExceptionContext<K, V> context,
            CacheEvent<K, V> event, EventSubscription<CacheEvent<K, V>> destination,
            RuntimeException cause) {
        handleRuntimeException(context, cause);
        return false;
    }

    public abstract void handleException(CacheExceptionContext<K, V> context,
            Exception cause);

    public abstract void handleRuntimeException(CacheExceptionContext<K, V> context,
            RuntimeException cause);

    public abstract void handleError(CacheExceptionContext<K, V> context, Error cause);

    public abstract void warning(CacheExceptionContext<K, V> context, String warning);
}
