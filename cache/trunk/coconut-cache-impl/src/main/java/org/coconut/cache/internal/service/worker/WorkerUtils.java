/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.worker;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.cache.CacheEntry;
import org.coconut.cache.service.loading.CacheLoadingConfiguration;
import org.coconut.cache.service.loading.CacheLoadingMXBean;
import org.coconut.cache.service.loading.CacheLoadingService;
import org.coconut.cache.service.worker.CacheWorkerManager;
import org.coconut.core.AttributeMap;
import org.coconut.core.Transformer;
import org.coconut.filter.Filter;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

/**
 * Various utilities used for the worker service.
 * <p>
 * NOTICE: This is an internal class and should not be directly referred. No guarantee is
 * made to the compatibility of this class between different releases of Coconut Cache.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
final class WorkerUtils {

    /** Cannot instantiate. */
    private WorkerUtils() {}


    
//
//    /**
//     * Wraps a CacheLoadingService in a CacheLoadingMXBean.
//     * 
//     * @param service
//     *            the CacheLoadingService to wrap
//     * @return the wrapped CacheLoadingMXBean
//     */
//    public static CacheLoadingMXBean wrapMXBean(CacheLoadingService<?, ?> service) {
//        return new DelegatedCacheLoadingMXBean(service);
//    }
//
//    /**
//     * Wraps a CacheLoadingService implementation such that only methods from the
//     * CacheLoadingService interface is exposed.
//     * 
//     * @param service
//     *            the CacheLoadingService to wrap
//     * @return a wrapped service that only exposes CacheLoadingService methods
//     */
//    public static <K, V> CacheWorkerManager wrapService(
//            CacheWorkerManager service) {
//        return new DelegatedCacheLoadingService(service);
//    }

    
    static class DefaultThreadFactory implements ThreadFactory {
        static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

        final ThreadGroup group;

        final AtomicInteger threadNumber = new AtomicInteger(1);

        final String namePrefix;

        DefaultThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
                    .getThreadGroup();
            namePrefix = name + "-" + POOL_NUMBER.getAndIncrement();
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
