/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.servicemanager.CacheLifecycle;

/**
 * The purpose of this class is to have one central place where all exceptions that arise
 * within a cache or one of its associated services are handled. One implementation of
 * this class might shutdown the cache for any raised exception. This is often usefull in
 * development environments. Another implementation might just log the exception and
 * continue serving other requests. To allow for easily extending this class with new
 * methods at a later time this class is an abstract class instead of an interface.
 * {@link CacheExceptionHandlers} defines a number of predefined exception handlers.
 * <p>
 * There are 4 basis <tt>general</tt> methods for handling failures occuring in the
 * cache.
 * <ul>
 * <li>{@link #handleError(CacheExceptionContext, Error)} which is called, on a best
 * effort basis, whenever an Error is raised within the cache. No reasonable application
 * should not try to handle this, except for writing as much debug information as
 * possible.
 * <li>{@link #handleException(CacheExceptionContext, Exception)} which is called
 * whenever a condition arises that a reasonable application might want to handle. For
 * example, if a {@link CacheLoader} fails to load a value for some specified key. In most
 * situations these should just be logged and the cache should continue as nothing has
 * happend.
 * <li>{@link #handleRuntimeException(CacheExceptionContext, RuntimeException)} which is
 * called when a programmatic error arises from which an application cannot normally
 * recover. This could, for example, be some user provided callback that fails in some
 * mysterious way. Or even worse that the cache implementation contains a bug. Of course,
 * this is highly unlikely if using one of the default implementation provided by Coconut
 * Cache;).
 * <li>{@link #handleWarning(CacheExceptionContext, String)} which is called whenever a
 * some kind of inconsistency arrises in the system. Normally this always indicates a
 * non-critical problem that should be fixed at some time. For example, if a CacheLoader
 * tries to set the creation time of a newly loaded element to a negative value.
 * </ul>
 * <p>
 * In addition to these general methods there are also a number of <tt>specialized</tt>
 * methods that handle a particular type of failure. The idea is that all common exception
 * points has a corresponding method in CacheExceptionHandler. For example, whenever an
 * exception occurs while loading an element in a cache loader the
 * {@link #loadingFailed(CacheExceptionContext, CacheLoader, Object, AttributeMap, Throwable)}
 * method is called. In addition to the exception that was raised a number of additional
 * information is provided to this method. For example, the key for which the load failed,
 * the cache in which the cache occured as well as other relevant information. The default
 * implementation provided in this class just calls the
 * {@link #handleException(CacheExceptionContext, Exception)} method with the provided
 * exception.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public abstract class CacheExceptionHandler<K, V> {

    /**
     * Handles an error.
     * 
     * @param context
     *            an CacheExceptionContext containing the default logger configured for
     *            this cache
     * @param cause
     *            the error to handle
     */
    public abstract void handleError(CacheExceptionContext<K, V> context, Error cause);

    /**
     * Handles an exception.
     * 
     * @param context
     *            an CacheExceptionContext containing the default logger configured for
     *            this cache
     * @param cause
     *            the exception to handle
     */
    public abstract void handleException(CacheExceptionContext<K, V> context, Exception cause);

    /**
     * Handles a runtime exception.
     * 
     * @param context
     *            an CacheExceptionContext containing the default logger configured for
     *            this cache
     * @param cause
     *            the runtime exception to handle
     */
    public abstract void handleRuntimeException(CacheExceptionContext<K, V> context,
            RuntimeException cause);

    /**
     * Handles the generic throwable.
     * 
     * @param context
     *            an CacheExceptionContext containing the default logger configured for
     *            this cache
     * @param cause
     *            the throwable to handle
     */
    public void handleThrowable(CacheExceptionContext<K, V> context, Throwable cause) {
        if (cause instanceof RuntimeException) {
            handleRuntimeException(context, (RuntimeException) cause);
        } else if (cause instanceof Exception) {
            handleException(context, (Exception) cause);
        } else if (cause instanceof Error) {
            handleError(context, (Error) cause);
        } else {
            // hmm
            handleRuntimeException(context, new CacheException(cause));
        }
    }

    /**
     * Handles a warning from the cache. The default implementation ignores warning.
     * 
     * @param context
     *            an CacheExceptionContext containing the default logger configured for
     *            this cache
     * @param warning
     *            the warning to handle
     */
    public void handleWarning(CacheExceptionContext<K, V> context, String warning) {}

    /**
     * Called to initialize the CacheExceptionHandler. This method must be called as the
     * first operation from within the constructor of the cache. Exceptions thrown by this
     * method will not be handled by the cache.
     * 
     * @param configuration
     *            the configuration of the cache
     */
    public void initialize(CacheConfiguration<K, V> configuration) {}

    /**
     * This method is called when the
     * {@link CacheLifecycle#initialize(org.coconut.cache.service.servicemanager.CacheLifecycle.Initializer)}
     * method of a cache service fails.
     * <p>
     * The
     * {@link CacheLifecycle#initialize(org.coconut.cache.service.servicemanager.CacheLifecycle.Initializer)}
     * method is always called from the constructor of the cache. Any Exception thrown
     * from this method will override the cause.
     * 
     * @param configuration
     *            the configuration of the cache
     * @param cacheType
     *            the type of cache
     * @param service
     *            the service that failed
     * @param cause
     *            the cause of the failure
     */
    public void lifecycleInitializationFailed(CacheConfiguration<K, V> configuration,
            Class<? extends Cache> cacheType, CacheLifecycle service, RuntimeException cause) {}

    /**
     * Called if the cache fails to shutdown all service properly.
     * 
     * @param context
     *            an CacheExceptionContext containing the default logger configured for
     *            this cache
     * @param lifecycle
     *            the service that failed
     * @param cause
     *            the cause of the failure
     */
    public void lifecycleShutdownFailed(CacheExceptionContext<K, V> context,
            CacheLifecycle lifecycle, RuntimeException cause) {}

    /**
     * Called when a service fails to start properly.
     * 
     * @param configuration
     *            the configuration of the cache
     * @param cacheType
     *            the type of cache
     * @param service
     *            the service that failed
     * @param cause
     *            the cause of the failure
     */
    public void lifecycleStartFailed(CacheConfiguration<K, V> configuration,
            Class<? extends Cache> cacheType, Object service, Exception cause) {}

    /**
     * Called whenever a CacheLoader fails while trying to load a value.
     * <p>
     * If this method chooses to throw a {@link RuntimeException} and the cache loader was
     * invoked through a synchronous method, for example, {@link Cache#get(Object)} the
     * exception will be propagated to the callee. If the cache loader was invoked through
     * an asynchronous method, for example, {@link CacheLoadingService#load(Object)} any
     * exception throw from this method will not be visible to the user.
     * 
     * @param context
     *            an CacheExceptionContext containing the default logger configured for
     *            this cache
     * @param loader
     *            the cacheloader that failed to load a value
     * @param key
     *            the key for which the load failed
     * @param map
     *            a map of attributes used while trying to load
     * @param cause
     *            the exception that was raised.
     * @return a value that can be used instead of the value that couldn't be loaded. If
     *         <code>null</code> returned no entry will be added to the cache for the
     *         given key
     */
    public V loadingFailed(CacheExceptionContext<K, V> context, CacheLoader<? super K, ?> loader,
            K key, AttributeMap map, Throwable cause) {
        handleThrowable(context, cause);
        return null;
    }

    /**
     * Called as the last action by the cache once it has terminated. The map argument
     * contains a mapping from any service that failed to properly
     * {@link CacheLifecycle#terminated() terminate} to the corresponding cause. If all
     * services was succesfully terminated, the map is empty.
     * 
     * @param terminationFailures
     *            a map of services that failed or the empty map if all services
     *            terminated succesfully.
     */
    public void terminated(Map<? extends CacheLifecycle, RuntimeException> terminationFailures) {}
}
