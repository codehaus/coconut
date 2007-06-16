/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.tck.service.loading;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;


public class FutureLoading extends AbstractLoadingTestBundle {

    /**
     * cancel of a completed loading fails
     * 
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testCancelAfterRun() throws InterruptedException,
            ExecutionException {
        Future<?> task = loading().load(0);
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
        Future<?> task = loading().load(0);
        task.get();
        assertFalse(task.cancel(true));
        assertTrue(task.isDone());
        assertFalse(task.isCancelled());
    }
    

//  /**
//  * isDone is true when a task completes
//  * 
//  * @throws InterruptedException
//  * @throws ExecutionException
//  */
// @Test
// public void testLoad() throws InterruptedException, ExecutionException {
//     Future<?> task = service.load(1);
//     assertNull(task.get());
//     assertTrue(task.isDone());
//     assertFalse(task.isCancelled());
//
//     assertSize(1);
//     assertPeek(M1);
//     assertTrue(containsKey(M1));
//     assertTrue(containsValue(M1));
// }
   
//   @Test
//   public void testLoadAll() throws InterruptedException, ExecutionException {
//       Future<?> task = service.loadAll(Arrays.asList(1, 2, 3, 4));
//       assertNull(task.get());
//       assertTrue(task.isDone());
//       assertFalse(task.isCancelled());
//
//       assertEquals(4, c.size());
//
//       assertPeek(M1, M2, M3, M4);
//   }
    
//    /**
//     * isDone is true when a task completes
//     * 
//     * @throws InterruptedException
//     * @throws ExecutionException
//     */
//    @Test
//    public void testLoadNull() throws InterruptedException, ExecutionException {
//        Future<?> task = service.load(M6.getKey());
//        assertNull(task.get());
//        assertTrue(task.isDone());
//        assertFalse(task.isCancelled());
//
//        assertSize(0);
//        assertFalse(containsKey(M6));
//
//        assertNull(service.load(4));
//        assertSize(1);
//    }

//    @Test
//    public void testLoadManyNull() throws InterruptedException, ExecutionException {
//        Future<?> task = service.loadAll(Arrays.asList(1, 2, 6, 7));
//        assertNull(task.get());
//        assertTrue(task.isDone());
//        assertFalse(task.isCancelled());
//
//        assertSize(2);
//
//        assertPeek(M1, M2);
//    }
}
