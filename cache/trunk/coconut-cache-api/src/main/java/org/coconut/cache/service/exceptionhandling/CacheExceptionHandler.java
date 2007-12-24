/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import java.util.Map;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.core.Logger.Level;
import org.coconut.operations.Ops.Procedure;

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
 * <li>{@link #handleWarning(CacheExceptionContext)} which is called whenever a some kind
 * of inconsistency arrises in the system. Normally this always indicates a non-critical
 * problem that should be fixed at some time. For example, if a CacheLoader tries to set
 * the creation time of a newly loaded element to a negative value.
 * </ul>
 * <p>
 * In addition to these general methods there are also a number of <tt>specialized</tt>
 * methods that handle a particular type of failure. The idea is that all common exception
 * points has a corresponding method in CacheExceptionHandler. For example, whenever an
 * exception occurs while loading an element in a cache loader the
 * {@link #loadingLoadValueFailed(CacheExceptionContext, CacheLoader, Object, AttributeMap)}
 * method is called. In addition to the exception that was raised a number of additional
 * information is provided to this method. For example, the key for which the load failed,
 * the cache in which the cache occured as well as other relevant information. The default
 * implementation provided in this class just logs the error and returns <code>null</code>.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public class CacheExceptionHandler<K, V> implements Procedure<CacheExceptionContext<K, V>> {

    /**
     * Handles a warning from the cache. The default implementation just logs the warning.
     *
     * @param context
     *            an CacheExceptionContext containing the default logger configured for
     *            this cache
     */
    public void handleWarning(CacheExceptionContext<K, V> context) {
        context.defaultLogger().log(Level.Warn, context.getMessage(), context.getCause());
    }

    /**
     * Called to initialize the CacheExceptionHandler. This method must be called as the
     * first operation from within the constructor of the cache. Exceptions thrown by this
     * method will not be handled by the cache. The default implementation does nothing
     *
     * @param configuration
     *            the configuration of the cache
     */
    public void initialize(CacheConfiguration<K, V> configuration) {}

    /**
     * Rethrows any errors.
     *
     * @param context
     *            the context
     */
    public void apply(CacheExceptionContext<K, V> context) {
        Throwable cause = context.getCause();
        if (cause instanceof RuntimeException) {
            context.defaultLogger().fatal(context.getMessage(), cause);
        } else if (cause instanceof Exception) {
            context.defaultLogger().error(context.getMessage(), cause);
        } else {
            context.defaultLogger().fatal(context.getMessage(), cause);
            if (context.getCause() instanceof Error) {
                throw (Error) context.getCause();
            }
        }
    }

    /**
     * Called whenever a CacheLoader fails while trying to load a value.
     * <p>
     * If this method chooses to throw a {@link RuntimeException} and the cache loader was
     * invoked through a synchronous method, for example, {@link Cache#get(Object)} the
     * exception will be propagated to the callee. If the cache loader was invoked through
     * an asynchronous method, for example, {@link CacheLoadingService#load(Object)} any
     * exception throw from this method will not be visible to the user.
     * <p>
     * The default implementation, will just log any exception.
     *
     * @param context
     *            an CacheExceptionContext containing the default logger configured for
     *            this cache and the cause of the failure. The exception message includes
     *            the
     * @param loader
     *            the cacheloader that failed to load a value
     * @param key
     *            the key for which the load failed
     * @param map
     *            a map of attributes used while trying to load
     * @return a value that can be used instead of the value that couldn't be loaded. If
     *         <code>null</code> returned no entry will be added to the cache for the
     *         given key
     */
    public V loadingLoadValueFailed(CacheExceptionContext<K, V> context,
            CacheLoader<? super K, ?> loader, K key, AttributeMap map) {
        apply(context);
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
