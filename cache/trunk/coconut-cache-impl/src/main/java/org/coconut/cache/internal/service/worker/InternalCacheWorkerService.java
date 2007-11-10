/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public interface InternalCacheWorkerService {

    ExecutorService getExecutorService(Class<?> service);

    ScheduledExecutorService getScheduledExecutorService(Class<?> service);
}
