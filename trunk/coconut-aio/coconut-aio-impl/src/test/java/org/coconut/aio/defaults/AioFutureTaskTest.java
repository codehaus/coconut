package org.coconut.aio.defaults;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.coconut.aio.AioTestCase;
import org.coconut.aio.impl.util.AioFutureTask;
import org.coconut.core.Offerable;
/**
 * Primarily nicked from j.u.c.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class AioFutureTaskTest extends AioTestCase {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
        return new TestSuite(AioFutureTaskTest.class);
    }
    static class PublicFutureTask extends AioFutureTask {
        public PublicFutureTask(Executor e, Offerable o) {
            super(e, o);
        }
        public void set(Object x) {
            super.set(x);
        }
        public void setException(Throwable t) {
            super.setException(t);
        }
        protected int getColor()
        {
            return 0;
        }
        protected void deliverFailure(Offerable o, Throwable t) {
            throw new UnsupportedOperationException(); //TODO fix
        }
        public void setDestination(Offerable arg0) {
            throw new UnsupportedOperationException(); //TODO fix
        }
    }
    static class PublicFutureCallableTask extends AioFutureTask {
        public PublicFutureCallableTask(Executor e, Offerable o) {
            super(e, o);
        }
        public void set(Object x) {
            super.set(x);
        }
        public void setException(Throwable t) {
            super.setException(t);
        }
        protected int getColor()
        {
            return 0;
        }
        protected void deliverFailure(Offerable o, Throwable t) {
            throw new UnsupportedOperationException(); //TODO fix
        }
        /** 
         * @see org.coconut.aio.AioFuture#setDestination(org.coconut.core.Offerable)
         */
        public void setDestination(Offerable arg0) {
            throw new UnsupportedOperationException(); //TODO fix
        }
    }

    /**
     * Creating a future with a null callable throws NPE
     */
    public void testConstructor() {
        PublicFutureTask task = new PublicFutureTask(null, null);
        assertFalse(task.isCancelled());
        assertFalse(task.isDone());
    }
    public void testIsDone() throws InterruptedException, ExecutionException, IOException {
        PublicFutureTask task = new PublicFutureTask(null, null) {
            public Object call() throws Exception {
                return IGNORE_READ_HANDLER;
            }
        };
        task.run();
        assertFalse(task.isCancelled());
        assertTrue(task.isDone());
        assertEquals(IGNORE_READ_HANDLER, task.get());
        assertEquals(IGNORE_READ_HANDLER, task.getIO());
    }

    public void testSet() throws InterruptedException, ExecutionException, IOException {
        PublicFutureTask task = new PublicFutureTask(null, null);
        task.set(IGNORE_READ_HANDLER);
        assertFalse(task.isCancelled());
        assertTrue(task.isDone());
        assertEquals(IGNORE_READ_HANDLER, task.get());
        assertEquals(IGNORE_READ_HANDLER, task.getIO());
    }
    public void testSetException() throws InterruptedException, IOException {
        Exception e = new IllegalStateException();
        PublicFutureTask task = new PublicFutureTask(null, null);
        task.setException(e);
        try {
            task.get();
            fail("should throw");
        } catch (ExecutionException ee) {
            Throwable cause = ee.getCause();
            assertEquals(e, cause);
        }

        try {
            task.getIO();
            fail("should throw");
        } catch (IllegalStateException ise) {
            assertEquals(e, ise);
        }
    }

    public void testCancelBeforeRun() {
        PublicFutureTask task = new PublicFutureTask(null, null);
        assertTrue(task.cancel(false));
        task.run();
        assertTrue(task.isDone());
        assertTrue(task.isCancelled());
    }

    /**
     * Cancel(true) before run succeeds
     */
    public void testCancelBeforeRun2() {
        PublicFutureTask task = new PublicFutureTask(null, null);
        assertTrue(task.cancel(true));
        task.run();
        assertTrue(task.isDone());
        assertTrue(task.isCancelled());
    }

    /**
     * cancel of a completed task fails
     * 
     * @throws InterruptedException
     * @throws IOException
     */
    public void testCancelAfterRun() throws InterruptedException, IOException {
        PublicFutureTask task = new PublicFutureTask(null, null);
        task.run();
        assertFalse(task.cancel(false));
        assertTrue(task.isDone());
        assertFalse(task.isCancelled());

        try {
            task.get();
            fail("should throw");
        } catch (ExecutionException ee) {
            Throwable cause = ee.getCause();
            assertTrue(cause instanceof IllegalStateException);
        }

        try {
            task.getIO();
            fail("should throw");
        } catch (IllegalStateException ise) {
            //ok
        }
    }

    /**
     * cancel(true) interrupts a running task
     * 
     * @throws InterruptedException
     */
    public void testCancelInterrupt() throws InterruptedException {
        PublicFutureTask task = new PublicFutureTask(null, null) {
            public Object call() {
                try {
                    Thread.sleep(MEDIUM_DELAY_MS);
                    threadShouldThrow();
                } catch (InterruptedException success) {
                }
                return Boolean.TRUE;
            }
        };
        Thread t = new Thread(task);
        t.start();

        Thread.sleep(SHORT_DELAY_MS);
        assertTrue(task.cancel(true));
        t.join();
        assertTrue(task.isDone());
        assertTrue(task.isCancelled());
    }

    /**
     * cancel(false) does not interrupt a running task
     * 
     * @throws InterruptedException
     */
    public void testCancelNoInterrupt() throws InterruptedException {
        PublicFutureTask task = new PublicFutureTask(null, null) {
            public Object call() {
                try {
                    Thread.sleep(MEDIUM_DELAY_MS);
                } catch (InterruptedException success) {
                    threadFail("should not interrupt");
                }
                return Boolean.TRUE;
            }
        };
        Thread t = new Thread(task);
        t.start();

        Thread.sleep(SHORT_DELAY_MS);
        assertTrue(task.cancel(false));
        t.join();
        assertTrue(task.isDone());
        assertTrue(task.isCancelled());
    }

    public void testGet1() throws InterruptedException {
        final PublicFutureTask ft = new PublicFutureTask(null, null) {
            public Object call() {
                try {
                    Thread.sleep(MEDIUM_DELAY_MS);
                } catch (InterruptedException e) {
                    threadUnexpectedException();
                }
                return Boolean.TRUE;
            }
        };
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    ft.get();
                } catch (Exception e) {
                    threadUnexpectedException();
                }
            }
        });

        assertFalse(ft.isDone());
        assertFalse(ft.isCancelled());
        t.start();
        Thread.sleep(SHORT_DELAY_MS);
        ft.run();
        t.join();
        assertTrue(ft.isDone());
        assertFalse(ft.isCancelled());
    }
    public void testGetIO1() throws InterruptedException {
        final PublicFutureTask ft = new PublicFutureTask(null, null) {
            public Object call() {
                try {
                    Thread.sleep(MEDIUM_DELAY_MS);
                } catch (InterruptedException e) {
                    threadUnexpectedException();
                }
                return Boolean.TRUE;
            }
        };
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    ft.get();
                } catch (Exception e) {
                    threadUnexpectedException();
                }
            }
        });

        assertFalse(ft.isDone());
        assertFalse(ft.isCancelled());
        t.start();
        Thread.sleep(SHORT_DELAY_MS);
        ft.run();
        t.join();
        assertTrue(ft.isDone());
        assertFalse(ft.isCancelled());
    }
    public void testTimedGet1() throws InterruptedException {
        final PublicFutureTask ft = new PublicFutureTask(null, null) {
            public Object call() {
                try {
                    Thread.sleep(MEDIUM_DELAY_MS);
                } catch (InterruptedException e) {
                    threadUnexpectedException();
                }
                return Boolean.TRUE;
            }
        };
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    ft.get(SHORT_DELAY_MS, TimeUnit.MILLISECONDS);
                } catch (TimeoutException success) {
                } catch (Exception e) {
                    threadUnexpectedException();
                }
            }
        });
        assertFalse(ft.isDone());
        assertFalse(ft.isCancelled());
        t.start();
        ft.run();
        t.join();
        assertTrue(ft.isDone());
        assertFalse(ft.isCancelled());
    }
    public void testTimedGetIO1() throws InterruptedException {
        final PublicFutureTask ft = new PublicFutureTask(null, null) {
            public Object call() {
                try {
                    Thread.sleep(MEDIUM_DELAY_MS);
                } catch (InterruptedException e) {
                    threadUnexpectedException();
                }
                return Boolean.TRUE;
            }
        };
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    ft.getIO(SHORT_DELAY_MS, TimeUnit.MILLISECONDS);
                } catch (TimeoutException success) {
                } catch (Exception e) {
                    threadUnexpectedException();
                }
            }
        });
        assertFalse(ft.isDone());
        assertFalse(ft.isCancelled());
        t.start();
        ft.run();
        t.join();
        assertTrue(ft.isDone());
        assertFalse(ft.isCancelled());
    }

    /**
     * Cancelling a task causes timed get in another thread to throw
     * CancellationException
     * 
     * @throws InterruptedException
     */
    public void testTimedGet_Cancellation() throws InterruptedException {
        final PublicFutureTask ft = new PublicFutureTask(null, null) {
            public Object call() {
                try {
                    Thread.sleep(SMALL_DELAY_MS);
                    threadShouldThrow();
                } catch (InterruptedException e) {
                }
                return Boolean.TRUE;
            }
        };

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    ft.get(MEDIUM_DELAY_MS, TimeUnit.MILLISECONDS);
                    threadShouldThrow();
                } catch (CancellationException success) {
                } catch (Exception e) {
                    threadUnexpectedException();
                }
            }
        });
        Thread t2 = new Thread(ft);
        t1.start();
        t2.start();
        Thread.sleep(SHORT_DELAY_MS);
        ft.cancel(true);
        t1.join();
        t2.join();
    }

    public void testTimedGetIO_Cancellation() throws InterruptedException {
        final PublicFutureTask ft = new PublicFutureTask(null, null) {
            public Object call() {
                try {
                    Thread.sleep(SMALL_DELAY_MS);
                    threadShouldThrow();
                } catch (InterruptedException e) {
                }
                return Boolean.TRUE;
            }
        };

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    ft.getIO(MEDIUM_DELAY_MS, TimeUnit.MILLISECONDS);
                    threadShouldThrow();
                } catch (CancellationException success) {
                } catch (Exception e) {
                    threadUnexpectedException();
                }
            }
        });
        Thread t2 = new Thread(ft);
        t1.start();
        t2.start();
        Thread.sleep(SHORT_DELAY_MS);
        ft.cancel(true);
        t1.join();
        t2.join();
    }

    /**
     * Cancelling a task causes get in another thread to throw
     * CancellationException
     * 
     * @throws InterruptedException
     */
    public void testGet_Cancellation() throws InterruptedException {
        final PublicFutureTask ft = new PublicFutureTask(null, null) {
            public Object call() {
                try {
                    Thread.sleep(MEDIUM_DELAY_MS);
                    threadShouldThrow();
                } catch (InterruptedException e) {
                }
                return Boolean.TRUE;
            }
        };

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    ft.get();
                    threadShouldThrow();
                } catch (CancellationException success) {
                } catch (Exception e) {
                    threadUnexpectedException();
                }
            }
        });
        Thread t2 = new Thread(ft);
        t1.start();
        t2.start();
        Thread.sleep(SHORT_DELAY_MS);
        ft.cancel(true);
        t1.join();
        t2.join();
    }

    /**
     * Cancelling a task causes get in another thread to throw
     * CancellationException
     * 
     * @throws InterruptedException
     */
    public void testGetIO_Cancellation() throws InterruptedException {
        final PublicFutureTask ft = new PublicFutureTask(null, null) {
            public Object call() {
                try {
                    Thread.sleep(MEDIUM_DELAY_MS);
                    threadShouldThrow();
                } catch (InterruptedException e) {
                }
                return Boolean.TRUE;
            }
        };

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    ft.getIO();
                    threadShouldThrow();
                } catch (CancellationException success) {
                } catch (Exception e) {
                    threadUnexpectedException();
                }
            }
        });
        Thread t2 = new Thread(ft);
        t1.start();
        t2.start();
        Thread.sleep(SHORT_DELAY_MS);
        ft.cancel(true);
        t1.join();
        t2.join();
    }

    /**
     * A runtime exception in task causes get to throw ExecutionException
     * 
     * @throws InterruptedException
     */
    public void testGet_ExecutionException() throws InterruptedException {
        final PublicFutureTask ft = new PublicFutureTask(null, null) {
            public Object call() {
                int i = 5 / 0;
                return i;
            }
        };
        try {
            ft.run();
            ft.get();
            fail("should throw");
        } catch (ExecutionException success) {
        }
    }
    public void testGetIO_ExecutionException() throws IOException {
        final PublicFutureTask ft = new PublicFutureTask(null, null) {
            public Object call() {
                int i = 5 / 0;
                return i;
            }
        };
        try {
            ft.run();
            ft.getIO();
            fail("should throw");
        } catch (ArithmeticException success) {

        }
    }
    /*
     * A runtime exception in task causes timed get to throw ExecutionException
     */
    public void testTimedGet_ExecutionException2() throws InterruptedException {
        final PublicFutureTask ft = new PublicFutureTask(null, null) {
            public Object call() {
                int i = 5 / 0;
                return i;
            }
        };
        try {
            ft.run();
            ft.get(SHORT_DELAY_MS, TimeUnit.MILLISECONDS);
            fail("should throw");
        } catch (ExecutionException success) {
        } catch (TimeoutException success) {
        } // unlikely but OK
    }
    /*
     * A runtime exception in task causes timed get to throw ExecutionException
     */
    public void testTimedGetIO_ExecutionException2() throws IOException {
        final PublicFutureTask ft = new PublicFutureTask(null, null) {
            public Object call() {
                int i = 5 / 0;
                return i;
            }
        };
        try {
            ft.run();
            ft.getIO(SHORT_DELAY_MS, TimeUnit.MILLISECONDS);
            fail("should throw");
        } catch (ArithmeticException success) {
        } catch (TimeoutException success) {
        } // unlikely but OK
    }

    /**
     * Interrupting a waiting get causes it to throw InterruptedException
     * 
     * @throws InterruptedException
     */
    public void testGet_InterruptedException() throws InterruptedException {
        final PublicFutureTask ft = new PublicFutureTask(null, null);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    ft.get();
                    threadShouldThrow();
                } catch (InterruptedException success) {
                } catch (Exception e) {
                    threadUnexpectedException();
                }
            }
        });

        t.start();
        Thread.sleep(SHORT_DELAY_MS);
        t.interrupt();
        t.join();
    }

    /**
     * Interrupting a waiting get causes it to throw InterruptedException
     * 
     * @throws InterruptedException
     */
    public void testGetIO_InterruptedException() throws InterruptedException {
        final PublicFutureTask ft = new PublicFutureTask(null, null);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    ft.getIO();
                    threadShouldThrow();
                } catch (InterruptedIOException success) {
                } catch (Exception e) {
                    threadUnexpectedException();
                }
            }
        });

        t.start();
        Thread.sleep(SHORT_DELAY_MS);
        t.interrupt();
        t.join();
    }

    /**
     * Interrupting a waiting timed get causes it to throw InterruptedException
     * 
     * @throws InterruptedException
     */
    public void testTimedGet_InterruptedException2() throws InterruptedException {
        final PublicFutureTask ft = new PublicFutureTask(null, null);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    ft.get(LONG_DELAY_MS, TimeUnit.MILLISECONDS);
                    threadShouldThrow();
                } catch (InterruptedException success) {
                } catch (Exception e) {
                    threadUnexpectedException();
                }
            }
        });

        t.start();
        Thread.sleep(SHORT_DELAY_MS);
        t.interrupt();
        t.join();

    }

    /**
     * Interrupting a waiting timed get causes it to throw InterruptedException
     * 
     * @throws InterruptedException
     */
    public void testTimedGetIO_InterruptedException2() throws InterruptedException {
        final PublicFutureTask ft = new PublicFutureTask(null, null);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    ft.getIO(LONG_DELAY_MS, TimeUnit.MILLISECONDS);
                    threadShouldThrow();
                } catch (InterruptedIOException success) {
                } catch (Exception e) {
                    threadUnexpectedException();
                }
            }
        });

        t.start();
        Thread.sleep(SHORT_DELAY_MS);
        t.interrupt();
        t.join();

    }

    /**
     * A timed out timed get throws TimeoutException
     * 
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void testGet_TimeoutException() throws InterruptedException, ExecutionException {
        try {
            final PublicFutureTask ft = new PublicFutureTask(null, null);
            ft.get(1, TimeUnit.MILLISECONDS);
            fail("should throw");
        } catch (TimeoutException success) {
        }
    }

    /**
     * A timed out timed get throws TimeoutException
     * 
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void testGetIO_TimeoutException() throws IOException {
        try {
            final PublicFutureTask ft = new PublicFutureTask(null, null);
            ft.getIO(1, TimeUnit.MILLISECONDS);
            fail("should throw");
        } catch (TimeoutException success) {
        }
    }
}