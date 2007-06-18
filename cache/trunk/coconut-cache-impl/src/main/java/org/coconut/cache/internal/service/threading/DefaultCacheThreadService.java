/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.threading;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.service.servicemanager.AbstractCacheService;
import org.coconut.cache.service.threading.CacheServiceThreadManage;
import org.coconut.cache.service.threading.CacheThreadManager;
import org.coconut.cache.spi.XmlConfigurator;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheThreadService<K, V> extends AbstractCacheService implements
         InternalCacheThreadingService {

    private final CacheThreadManager executorFactory;
    /**
     * @param manager
     * @param conf
     */
    public DefaultCacheThreadService(
            CacheConfiguration<K, V> conf) {
        super("threading");
        executorFactory=conf.threading().getExecutorFactory();
        String s = (String) conf.getProperty(XmlConfigurator.CACHE_INSTANCE_TYPE);
        Class c = null;
        try {
            c = Class.forName(s);
        } catch (ClassNotFoundException e1) {
            throw new CacheException("Could not find cache of type " + s);
        }
        // check that cache is async
        // c
        // .isAnnotationPresent(ThreadSafe.class) ? threadManager
        // : ThreadUtils.SAME_THREAD_EXECUTOR
    }

    /**
     * @see org.coconut.cache.internal.service.AbstractCacheService#shutdown(java.lang.Runnable)
     */
    public void shutdown(Executor callback) /* throws Exception */{
        if (true) {
            final ThreadPoolExecutor tpe = (ThreadPoolExecutor) null;
            tpe.shutdown();
            if (!tpe.isTerminated()) {
                Runnable r = new Runnable() {
                    public void run() {
                        try {
                            while (!tpe.isTerminated()) {
                                tpe
                                        .awaitTermination(Long.MAX_VALUE,
                                                TimeUnit.NANOSECONDS);
                            }
                        } catch (InterruptedException ignore) {}
                    }
                };
                callback.execute(r);
                return;
            }
        }
    }

    /**
     * @see org.coconut.cache.internal.service.threading.InternalThreadManager#isEnabled()
     */
    public boolean isAsync() {
        return executorFactory != null;
    }


    /**
     * @see org.coconut.cache.internal.service.threading.InternalThreadManager#executeShutdown(org.coconut.cache.internal.service.InternalCacheService,
     *      java.lang.Runnable)
     */
    public void executeShutdown(Runnable r) {
        new Thread(r).start();
    }

    /**
     * @see org.coconut.cache.internal.service.CacheServiceLifecycle#doStart()
     */
    public void doStart() {
    // ignore
    }

    /**
     * @see org.coconut.cache.internal.service.threading.InternalCacheThreadingService#isActive()
     */
    public boolean isActive() {
        return isAsync();
    }

    /**
     * @see org.coconut.cache.internal.service.InternalCacheService#isDummy()
     */
    public boolean isDummy() {
        // TODO Auto-generated method stub
        return false;
    }

    public CacheServiceThreadManage getExecutor(Class<?> service) {
        throw new UnsupportedOperationException();
    }

}
