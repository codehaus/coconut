package org.coconut.cache.tck.loading;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.coconut.cache.tck.CacheTestBundle;
import org.junit.Test;


public class FutureLoading extends CacheTestBundle {

    /**
     * cancel of a completed loading fails
     * 
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testCancelAfterRun() throws InterruptedException,
            ExecutionException {
        Future<?> task = loadableEmptyCache.load(0);
        task.get();
        assertFalse(task.cancel(false));
        assertTrue(task.isDone());
        assertFalse(task.isCancelled());
    }

    /**
     * cancel of a completed loading fails
     * 
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testCancelAfterRun2() throws InterruptedException,
            ExecutionException {
        Future<?> task = loadableEmptyCache.load(0);
        task.get();
        assertFalse(task.cancel(true));
        assertTrue(task.isDone());
        assertFalse(task.isCancelled());
    }
    

}
