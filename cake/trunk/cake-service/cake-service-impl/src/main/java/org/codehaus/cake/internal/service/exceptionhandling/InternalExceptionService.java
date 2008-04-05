/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.internal.service.exceptionhandling;

import org.codehaus.cake.container.ContainerConfiguration;
import org.codehaus.cake.container.lifecycle.DisposableService;
import org.codehaus.cake.internal.service.debug.InternalDebugService;

/**
 * An exception service available as an internal service at runtime.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: InternalCacheExceptionService.java 544 2008-01-05 01:19:03Z kasper $
 * @param <K>
 *            the type of keys maintained by the cache
 * @param <V>
 *            the type of mapped values
 */
public interface InternalExceptionService<T> extends InternalDebugService {

    void checkExceptions(boolean failIfShutdown);

    void disposeFailed(DisposableService service, Throwable cause);

    void error(String msg);

    void error(String msg, Throwable cause);
    
    void fatal(String msg);

    void fatal(String msg, Throwable cause);

    void startFailed(Object service, ContainerConfiguration<T> configuration, Throwable cause);

    boolean startupFailed();

    void terminated();

    void warning(String warning);
}
