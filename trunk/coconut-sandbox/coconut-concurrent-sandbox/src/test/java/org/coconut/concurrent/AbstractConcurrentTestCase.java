/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package org.coconut.concurrent;

/*
 * Written by Doug Lea with assistance from members of JCP JSR-166 Expert Group
 * and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain Other contributors include
 * Andrew Wright, Jeffrey Hayes, Pat Fisher, Mike Judd.
 */

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

/**
 * NOTE: taken from JSR166
 * http://gee.cs.oswego.edu/cgi-bin/viewcvs.cgi/jsr166/src/test/tck/JSR166TestCase.java
 * Base class for JSR166 Junit TCK tests. Defines some constants, utility
 * methods and classes, as well as a simple framework for helping to make sure
 * that assertions failing in generated threads cause the associated test that
 * generated them to itself fail (which JUnit doe not otherwise arrange). The
 * rules for creating such tests are:
 * <ol>
 * <li>All assertions in code running in generated threads must use the forms
 * {@link #threadFail},{@link #threadAssertTrue}{@link#threadAssertEquals},
 * or {@link #threadAssertNull}, (not <tt>fail</tt>,<tt>assertTrue</tt>,
 * etc.) It is OK (but not particularly recommended) for other code to use these
 * forms too. Only the most typically used JUnit assertion methods are defined
 * this way, but enough to live with.</li>
 * <li>If you override {@link #setUp}or {@link #tearDown}, make sure to
 * invoke <tt>super.setUp</tt> and <tt>super.tearDown</tt> within them.
 * These methods are used to clear and check for thread assertion failures.
 * </li>
 * <li>All delays and timeouts must use one of the constants <tt>
 * SHORT_DELAY_MS</tt>,
 * <tt> SMALL_DELAY_MS</tt>,<tt> MEDIUM_DELAY_MS</tt>,
 * <tt> LONG_DELAY_MS</tt>. The idea here is that a SHORT is always
 * discriminable from zero time, and always allows enough time for the small
 * amounts of computation (creating a thread, calling a few methods, etc) needed
 * to reach a timeout point. Similarly, a SMALL is always discriminable as
 * larger than SHORT and smaller than MEDIUM. And so on. These constants are set
 * to conservative values, but even so, if there is ever any doubt, they can all
 * be increased in one spot to rerun tests on slower platforms</li>
 * <li>All threads generated must be joined inside each test case method (or
 * <tt>fail</tt> to do so) before returning from the method. The
 * <tt> joinPool</tt> method can be used to do this when using Executors.</li>
 * </ol>
 * <p>
 * <b>Other notes </b>
 * <ul>
 * <li>Usually, there is one testcase method per JSR166 method covering
 * "normal" operation, and then as many exception-testing methods as there are
 * exceptions the method can throw. Sometimes there are multiple tests per
 * JSR166 method when the different "normal" behaviors differ significantly. And
 * sometimes testcases cover multiple methods when they cannot be tested in
 * isolation.</li>
 * <li>The documentation style for testcases is to provide as javadoc a simple
 * sentence or two describing the property that the testcase method purports to
 * test. The javadocs do not say anything about how the property is tested. To
 * find out, read the code.</li>
 * <li>These tests are "conformance tests", and do not attempt to test
 * throughput, latency, scalability or other performance factors (see the
 * separate "jtreg" tests for a set intended to check these for the most central
 * aspects of functionality.) So, most tests use the smallest sensible numbers
 * of threads, collection sizes, etc needed to check basic conformance.</li>
 * <li>The test classes currently do not declare inclusion in any particular
 * package to simplify things for people integrating them in TCK test suites.
 * </li>
 * <li>As a convenience, the <tt>main</tt> of this class (JSR166TestCase)
 * runs all JSR166 unit tests.</li>
 * </ul>
 */
public class AbstractConcurrentTestCase extends TestCase {
//    /**
//     * Runs all JSR166 unit tests using junit.textui.TestRunner
//     */
//    public static void main(String[] args) {
//        int iters = 1;
//        if (args.length > 0)
//            iters = Integer.parseInt(args[0]);
//        Test s = suite();
//        for (int i = 0; i < iters; ++i) {
//            junit.textui.TestRunner.run(s);
//            System.gc();
//            System.runFinalization();
//        }
//        System.exit(0);
//    }
//
//    /**
//     * Collects all JSR166 unit tests as one suite
//     */
//    public static Test suite() {
//        TestSuite suite = new TestSuite("JSR166 Unit Tests");
//
//        suite.addTest(new TestSuite(ColoredExecutorTest.class));
//
//        return suite;
//    }

    public static long SHORT_DELAY_MS;
    public static long SMALL_DELAY_MS;
    public static long MEDIUM_DELAY_MS;
    public static long LONG_DELAY_MS;
    public static long VERYLONG_DELAY_MS;

    /**
     * Return the shortest timed delay. This could be reimplemented to use for
     * example a Property.
     */
    protected long getShortDelay() {
        return 50;
    }

    /**
     * Set delays as multiples of SHORT_DELAY.
     */
    protected void setDelays() {
        SHORT_DELAY_MS = getShortDelay();
        SMALL_DELAY_MS = SHORT_DELAY_MS * 5;
        MEDIUM_DELAY_MS = SHORT_DELAY_MS * 10;
        LONG_DELAY_MS = SHORT_DELAY_MS * 50;
        VERYLONG_DELAY_MS = SHORT_DELAY_MS * 250;
    }

    /**
     * Flag set true if any threadAssert methods fail
     */
    volatile boolean threadFailed;

    /**
     * Initialize test to indicate that no thread assertions have failed
     */
    public void setUp() {
        setDelays();
        threadFailed = false;
    }

    /**
     * Trigger test case failure if any thread assertions have failed
     */
    public void tearDown() {
        assertFalse(threadFailed);
    }

    /**
     * Fail, also setting status to indicate current testcase should fail
     */
    public void threadFail(String reason) {
        threadFailed = true;
        fail(reason);
    }

    /**
     * If expression not true, set status to indicate current testcase should
     * fail
     */
    public void threadAssertTrue(boolean b) {
        if (!b) {
            threadFailed = true;
            assertTrue(b);
        }
    }

    /**
     * If expression not false, set status to indicate current testcase should
     * fail
     */
    public void threadAssertFalse(boolean b) {
        if (b) {
            threadFailed = true;
            assertFalse(b);
        }
    }

    /**
     * If argument not null, set status to indicate current testcase should fail
     */
    public void threadAssertNull(Object x) {
        if (x != null) {
            threadFailed = true;
            assertNull(x);
        }
    }

    /**
     * If arguments not equal, set status to indicate current testcase should
     * fail
     */
    public void threadAssertEquals(long x, long y) {
        if (x != y) {
            threadFailed = true;
            assertEquals(x, y);
        }
    }

    /**
     * If arguments not equal, set status to indicate current testcase should
     * fail
     */
    public void threadAssertEquals(Object x, Object y) {
        if (x != y && (x == null || !x.equals(y))) {
            threadFailed = true;
            assertEquals(x, y);
        }
    }

    /**
     * threadFail with message "should throw exception"
     */
    public void threadShouldThrow() {
        threadFailed = true;
        fail("should throw exception");
    }

    /**
     * threadFail with message "Unexpected exception"
     */
    public void threadUnexpectedException() {
        threadFailed = true;
        fail("Unexpected exception");
    }

    /**
     * Wait out termination of a thread pool or fail doing so
     */
    public void joinPool(ExecutorService exec) {
        try {
            exec.shutdown();
            assertTrue(exec.awaitTermination(LONG_DELAY_MS, TimeUnit.MILLISECONDS));
        } catch (SecurityException ok) {
            // Allowed in case test doesn't have privs
        } catch (InterruptedException ie) {
            fail("Unexpected exception");
        }
    }

    /**
     * fail with message "should throw exception"
     */
    public void shouldThrow() {
        fail("Should throw exception");
    }

    /**
     * fail with message "Unexpected exception"
     */
    public void unexpectedException() {
        fail("Unexpected exception");
    }

    /**
     * Computes a linear congruential random number a random number of times.
     */
    public static int compute2(int x) {
        int loops = (x >>> 4) & 7;
        while (loops-- > 0) {
            x = (x * 2147483647) % 16807;
        }
        return x;
    }

    public void waitOnLatchVeryLong(CountDownLatch latch) throws InterruptedException {
        if (!latch.await(VERYLONG_DELAY_MS, TimeUnit.MILLISECONDS))
            fail("Did not count down properly:" + latch.getCount());
    }
    public void waitOnLatchLong(CountDownLatch latch) throws InterruptedException {
        if (!latch.await(LONG_DELAY_MS, TimeUnit.MILLISECONDS))
            fail("Did not count down properly:" + latch.getCount());
    }
    public void waitOnLatchShort(CountDownLatch latch) throws InterruptedException {
        if (!latch.await(SHORT_DELAY_MS, TimeUnit.MILLISECONDS))
            fail("Did not count down properly:" + latch.getCount());
    }

    /**
     * The number of elements to place in collections, arrays, etc.
     */
    static final int SIZE = 20;

    // Some convenient Integer constants

    /**
     * A security policy where new permissions can be dynamically added or all
     * cleared.
     */
    static class AdjustablePolicy extends java.security.Policy {
        Permissions perms = new Permissions();

        void addPermission(Permission perm) {
            perms.add(perm);
        }
        void clearPermissions() {
            perms = new Permissions();
        }
        public PermissionCollection getPermissions(CodeSource cs) {
            return perms;
        }
        public PermissionCollection getPermissions(ProtectionDomain pd) {
            return perms;
        }
        public boolean implies(ProtectionDomain pd, Permission p) {
            return perms.implies(p);
        }
        public void refresh() {/* ignore */}
    }

    // Some convenient Runnable classes

    static class NoOpRunnable implements Runnable {
        public void run() {/* ignore */}
    }

    static class NoOpCallable implements Callable {
        public Object call() {
            return Boolean.TRUE;
        }
    }

    static final String TEST_STRING = "a test string";

    static class StringTask implements Callable<String> {
        public String call() {
            return TEST_STRING;
        }
    }

    static class NPETask implements Callable<String> {
        public String call() {
            throw new NullPointerException();
        }
    }

    static class CallableOne implements Callable<Integer> {
        public Integer call() {
            return 1;
        }
    }

    class ShortRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(SHORT_DELAY_MS);
            } catch (Exception e) {
                threadUnexpectedException();
            }
        }
    }

    class ShortInterruptedRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(SHORT_DELAY_MS);
                threadShouldThrow();
            } catch (InterruptedException success) {/* ignore */}
        }
    }

    class SmallRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(SMALL_DELAY_MS);
            } catch (Exception e) {
                threadUnexpectedException();
            }
        }
    }

    class SmallPossiblyInterruptedRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(SMALL_DELAY_MS);
            } catch (Exception e) {/* ignore */}
        }
    }

    class SmallCallable implements Callable {
        public Object call() {
            try {
                Thread.sleep(SMALL_DELAY_MS);
            } catch (Exception e) {
                threadUnexpectedException();
            }
            return Boolean.TRUE;
        }
    }

    class SmallInterruptedRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(SMALL_DELAY_MS);
                threadShouldThrow();
            } catch (InterruptedException success) {/* ignore */}
        }
    }

    class MediumRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(MEDIUM_DELAY_MS);
            } catch (Exception e) {
                threadUnexpectedException();
            }
        }
    }

    class MediumInterruptedRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(MEDIUM_DELAY_MS);
                threadShouldThrow();
            } catch (InterruptedException success) {/* ignore */}
        }
    }

    class MediumPossiblyInterruptedRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(MEDIUM_DELAY_MS);
            } catch (InterruptedException success) {/* ignore */}
        }
    }

    class LongPossiblyInterruptedRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(LONG_DELAY_MS);
            } catch (InterruptedException success) {/* ignore */}
        }
    }

    /**
     * For use as ThreadFactory in constructors
     */
    static class SimpleThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            return new Thread(r);
        }
    }

    static class TrackedShortRunnable implements Runnable {
        volatile boolean done = false;

        public void run() {
            try {
                Thread.sleep(SMALL_DELAY_MS);
                done = true;
            } catch (Exception e) {/* ignore */}
        }
    }

    static class TrackedMediumRunnable implements Runnable {
        volatile boolean done = false;

        public void run() {
            try {
                Thread.sleep(MEDIUM_DELAY_MS);
                done = true;
            } catch (Exception e) {/* ignore */}
        }
    }

    static class TrackedLongRunnable implements Runnable {
        volatile boolean done = false;

        public void run() {
            try {
                Thread.sleep(LONG_DELAY_MS);
                done = true;
            } catch (Exception e) {/* ignore */}
        }
    }

    static class TrackedNoOpRunnable implements Runnable {
        volatile boolean done = false;

        public void run() {
            done = true;
        }
    }

    static class TrackedCallable implements Callable {
        volatile boolean done = false;

        public Object call() {
            try {
                Thread.sleep(SMALL_DELAY_MS);
                done = true;
            } catch (Exception e) {/* ignore */}
            return Boolean.TRUE;
        }
    }

    /**
     * For use as RejectedExecutionHandler in constructors
     */
    static class NoOpREHandler implements RejectedExecutionHandler {
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {/* ignore */}
    }

}