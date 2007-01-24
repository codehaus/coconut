/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

import static org.coconut.test.TestUtil.assertEqual;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.internal.util.DieMutableCacheEntry;
import org.coconut.test.MockTestCase;

public class MutableCacheEntryTest extends MockTestCase {

    private ExecutorService e;

    private DieMutableCacheEntry<Integer, String> notLoaded;

    private DieMutableCacheEntry<Integer, String> loaded;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        notLoaded = new DieMutableCacheEntry<Integer, String>(1, 123);
        loaded = new DieMutableCacheEntry<Integer, String>(1, "A", 123);
    }

    @Override
    protected void tearDown() throws Exception {
        if (e != null) {
            e.shutdown();
        }

        super.tearDown();
    }

    public void testConstructor1() {
        DieMutableCacheEntry<Integer, String> entry = new DieMutableCacheEntry<Integer, String>(
                1, 123);
        assertEqual(1, entry.getKey());
        assertNull(entry.peek());
        assertEquals(123l, entry.getCreationTime());
        assertFalse(entry.isValid());
    }

    public void testConstructor2() {
        DieMutableCacheEntry<Integer, String> entry = new DieMutableCacheEntry<Integer, String>(
                1, "A", 123);
        assertEqual(1, entry.getKey());
        assertEquals("A", entry.getValue());
        assertEquals("A", entry.peek());
        assertEquals(123l, entry.getCreationTime());
        assertTrue(entry.isValid());
    }

    public void testSetValue() {
        assertEquals("A", loaded.setValue("B"));
        assertTrue(loaded.isValid());
        assertEquals("B", loaded.getValue());
    }

    public void testLoad() {
        assertTrue(notLoaded.tryPrepareLoad(false));
        assertEquals(null, notLoaded.peek());
        assertFalse(notLoaded.isValid());
        notLoaded.loadFinished("B");
        assertEquals("B", notLoaded.getValue());
        assertTrue(notLoaded.isValid());
    }

    public void testLoad1() {
        assertTrue(notLoaded.tryPrepareLoad(true));
        assertEquals(null, notLoaded.peek());
        assertFalse(notLoaded.isValid());
        notLoaded.loadFinished("B");
        assertEquals("B", notLoaded.getValue());
        assertTrue(notLoaded.isValid());
    }

    public void testLoad2() {
        assertTrue(loaded.tryPrepareLoad(false));
        assertEquals("A", loaded.peek());
        assertTrue(loaded.isValid());
        loaded.loadFinished("B");
        assertEquals("B", loaded.getValue());
        assertTrue(loaded.isValid());
    }

    public void testLoad3() {
        assertTrue(loaded.tryPrepareLoad(true));
        assertEquals(null, loaded.peek());
        assertFalse(loaded.isValid());
        loaded.loadFinished("B");
        assertEquals("B", loaded.getValue());
        assertTrue(loaded.isValid());
    }

    public void testFailPrepareLoad() {
        assertTrue(notLoaded.tryPrepareLoad(false));
        assertFalse(notLoaded.tryPrepareLoad(false));
        assertTrue(loaded.tryPrepareLoad(false));
        assertFalse(loaded.tryPrepareLoad(false));
    }

    public void testFailPrepareLoad1() {
        assertTrue(notLoaded.tryPrepareLoad(false));
        assertFalse(notLoaded.tryPrepareLoad(true));
        assertTrue(loaded.tryPrepareLoad(false));
        assertFalse(loaded.tryPrepareLoad(true));
    }

    public void testFailPrepareLoad2() {
        assertTrue(notLoaded.tryPrepareLoad(true));
        assertFalse(notLoaded.tryPrepareLoad(false));
        assertTrue(loaded.tryPrepareLoad(true));
        assertFalse(loaded.tryPrepareLoad(false));
    }

    public void testFailPrepareLoad3() {
        assertTrue(notLoaded.tryPrepareLoad(true));
        assertFalse(notLoaded.tryPrepareLoad(true));
        assertTrue(loaded.tryPrepareLoad(true));
        assertFalse(loaded.tryPrepareLoad(true));
    }

    public void testReuseEntry() {
        assertTrue(loaded.tryPrepareLoad(false));
        loaded.loadFinished("B");
        assertTrue(loaded.isValid());
        assertTrue(loaded.tryPrepareLoad(true));
        loaded.loadFinished("B");
        assertTrue(loaded.isValid());
        assertTrue(loaded.tryPrepareLoad(false));
        loaded.loadFinished("B");
        assertTrue(loaded.isValid());
    }

    public void testLoadTwoThread() throws InterruptedException,
            ExecutionException {
        e = new FailureExecutor(5);
        final CountDownLatch s = new CountDownLatch(1);

        assertTrue(notLoaded.tryPrepareLoad(false));
        Future<?> f = e.submit(new Callable<Object>() {
            public Object call() throws Exception {
                assertNull(notLoaded.peek());
                assertFalse(notLoaded.tryPrepareLoad(false));
                s.countDown();
                assertEquals("B", notLoaded.getValue());
                assertEquals("B", notLoaded.peek());
                return null;
            }
        });

        s.await(); // don't finish loading before
        notLoaded.loadFinished("B");
        assertEquals("B", notLoaded.getValue());
        assertEquals("B", notLoaded.peek());
        f.get();
    }

    public void testManyThreads() throws InterruptedException,
            ExecutionException {
        int threads = 10;
        e = new FailureExecutor(threads);
        final CountDownLatch s = new CountDownLatch(threads);
        final DieMutableCacheEntry<Integer, Integer> entry = new DieMutableCacheEntry<Integer, Integer>(
                0, 0);
        assertTrue(notLoaded.tryPrepareLoad(false));

        Callable<?> c = new Callable<Object>() {
            public Object call() throws Exception {
                assertNull(notLoaded.peek());
                assertFalse(notLoaded.tryPrepareLoad(false));
                s.countDown();
                assertEquals("B", notLoaded.getValue());
                assertEquals("B", notLoaded.peek());
                return null;
            }
        };
        Future<?>[] futures = new Future[threads];
        for (int i = 0; i < futures.length; i++) {
            futures[i] = e.submit(c);
        }
        s.await();
        notLoaded.loadFinished("B");
        assertEquals("B", notLoaded.getValue());
        assertEquals("B", notLoaded.peek());

        for (Future<?> f : futures) {
            f.get();
        }

    }

    class FailureExecutor extends ThreadPoolExecutor {

        public FailureExecutor(int number) {
            super(number, number, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            if (t != null) {
                threadFailed();
                t.printStackTrace();
            }
            super.afterExecute(r, t);
        }
    }
}
