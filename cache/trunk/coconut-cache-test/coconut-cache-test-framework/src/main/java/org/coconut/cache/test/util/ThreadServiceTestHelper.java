/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.service.worker.CacheWorkerManager;
import org.coconut.core.AttributeMap;

public class ThreadServiceTestHelper extends CacheWorkerManager {

    private final static int PERMITS = 100000;

    private Semaphore awaits = new Semaphore(PERMITS);

    public void awaitAllIdle() {
        awaits.acquireUninterruptibly(PERMITS);
        awaits.release(PERMITS);
    }

    private class MyRunnable implements Runnable {
        private final Runnable r;

        private final Exception calledFrom;

        public MyRunnable(Runnable r) {
            this.r = r;
            this.calledFrom = new Exception();
        }

        public void run() {
            try {
                r.run();
                if (r instanceof Future) {
                    Future ft = ((FutureTask) r);
                    if (ft.isDone()) {
                        try {
                            ft.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            calledFrom.printStackTrace();
                            e.printStackTrace();
                        }
                    }
                }
            }finally {
                awaits.release();
            }
        }
    }

    @Override
    public ExecutorService getExecutorService(Class<?> service, AttributeMap attributes) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>())
        {
            @Override
            public void execute(Runnable command) {
                awaits.acquireUninterruptibly();
                super.execute(new MyRunnable(command));
            }
        };
    }

    @Override
    public ScheduledExecutorService getScheduledExecutorService(Class<?> service,
            AttributeMap attributes) {
        throw new UnsupportedOperationException();
    }

}
