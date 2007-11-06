/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.worker;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.cache.service.worker.CacheWorkerManager;
import org.coconut.cache.spi.XmlConfigurator;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheThreadService<K, V> extends AbstractCacheLifecycle  {

    private final CacheWorkerManager executorFactory;
    /**
     * @param conf
     */
    public DefaultCacheThreadService(
            CacheConfiguration<K, V> conf) {
        super("threading");
        executorFactory=null;
        //conf.worker().getWorkerManager().createExecutorService(null);
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

    public void shutdown(Executor callback) /* throws Exception */{
        if (true) {
            final ThreadPoolExecutor tpe =  null;
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
                        } catch (InterruptedException ignore) {/* ignore */}
                    }
                };
                callback.execute(r);
                return;
            }
        }
    }

    public boolean isAsync() {
        return executorFactory != null;
    }



    public void executeShutdown(Runnable r) {
        new Thread(r).start();
    }


    public void doStart() {
    // ignore
    }


    public boolean isActive() {
        return isAsync();
    }


}