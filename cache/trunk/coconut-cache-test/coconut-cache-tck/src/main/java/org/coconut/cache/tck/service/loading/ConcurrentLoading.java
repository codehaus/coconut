/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.loading;

import junit.framework.AssertionFailedError;

import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.tck.CacheTestBundle;
import org.coconut.core.AttributeMap;

public class ConcurrentLoading extends CacheTestBundle {

    // /**
    // * cancel(true) interrupts a running task
    // */
    // public void testCancelInterrupt() {
    // final Cache c = newCache(CacheConfiguration.newConf().setLoader(
    // new SleepLoader()));
    //
    // Exchanger<Future> e=new Exchanger<Future>();
    //            
    //        
    //
    // new Future(new Callable() {
    // public Object call() {
    // try {
    // Thread.sleep(MEDIUM_DELAY_MS);
    // threadShouldThrow();
    // } catch (InterruptedException success) {
    // }
    // return Boolean.TRUE;
    // }
    // });
    // Thread t = new Thread(new Runnable() {
    // public void run() {
    // c.load(1000);
    // }
    // });
    // t.start();
    //
    // try {
    // Thread.sleep(SHORT_DELAY_MS);
    // assertTrue(task.cancel(true));
    // t.join();
    // assertTrue(task.isDone());
    // assertTrue(task.isCancelled());
    // } catch (InterruptedException e) {
    // unexpectedException();
    // }
    // }

    class SleepLoader implements CacheLoader<Integer, String> {

        /**
         * @see org.coconut.cache.util.AbstractCacheLoader#load(java.lang.Object)
         */
        public String load(Integer key, AttributeMap ignore) {
            try {
                Thread.sleep(key.intValue());
                throw new AssertionFailedError();
            } catch (InterruptedException success) {
            }
            return "";
        }

    }
}