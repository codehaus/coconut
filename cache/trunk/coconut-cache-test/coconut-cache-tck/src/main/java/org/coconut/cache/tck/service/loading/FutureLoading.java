/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.loading;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;


public class FutureLoading extends LoadingTestBundle {

    /**
     * cancel of a completed loading fails
     * 
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testCancelAfterRun() throws InterruptedException,
            ExecutionException {
        Future<?> task = service.load(0);
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
        Future<?> task = service.load(0);
        task.get();
        assertFalse(task.cancel(true));
        assertTrue(task.isDone());
        assertFalse(task.isCancelled());
    }
}
