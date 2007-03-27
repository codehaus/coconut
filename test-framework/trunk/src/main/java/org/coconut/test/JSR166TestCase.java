/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.test;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class JSR166TestCase extends TestCase {

    public static long SHORT_DELAY_MS;
    public static long SMALL_DELAY_MS;
    public static long MEDIUM_DELAY_MS;
    public static long LONG_DELAY_MS;


    /**
     * Returns the shortest timed delay. This could
     * be reimplemented to use for example a Property.
     */
    protected long getShortDelay() {
        return 50;
    }


    /**
     * Sets delays as multiples of SHORT_DELAY.
     */
    protected  void setDelays() {
        SHORT_DELAY_MS = getShortDelay();
        SMALL_DELAY_MS = SHORT_DELAY_MS * 5;
        MEDIUM_DELAY_MS = SHORT_DELAY_MS * 10;
        LONG_DELAY_MS = SHORT_DELAY_MS * 50;
    }

    /**
     * Flag set true if any threadAssert methods fail
     */
    volatile boolean threadFailed;

    /**
     * Initializes test to indicate that no thread assertions have failed
     */
    public void setUp() {
        setDelays();
        threadFailed = false;
    }

    /**
     * Triggers test case failure if any thread assertions have failed
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
     * If expression not true, set status to indicate current testcase
     * should fail
     */
    public void threadAssertTrue(boolean b) {
        if (!b) {
            threadFailed = true;
            assertTrue(b);
        }
    }

    /**
     * If expression not false, set status to indicate current testcase
     * should fail
     */
    public void threadAssertFalse(boolean b) {
        if (b) {
            threadFailed = true;
            assertFalse(b);
        }
    }

    /**
     * If argument not null, set status to indicate current testcase
     * should fail
     */
    public void threadAssertNull(Object x) {
        if (x != null) {
            threadFailed = true;
            assertNull(x);
        }
    }

    /**
     * If arguments not equal, set status to indicate current testcase
     * should fail
     */
    public void threadAssertEquals(long x, long y) {
        if (x != y) {
            threadFailed = true;
            assertEquals(x, y);
        }
    }

    /**
     * If arguments not equal, set status to indicate current testcase
     * should fail
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
        } catch(SecurityException ok) {
            // Allowed in case test doesn't have privs
        } catch(InterruptedException ie) {
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
     * The number of elements to place in collections, arrays, etc.
     */
    static final int SIZE = 20;

    // Some convenient Integer constants

    static final Integer zero = new Integer(0);
    static final Integer one = new Integer(1);
    static final Integer two = new Integer(2);
    static final Integer three  = new Integer(3);
    static final Integer four  = new Integer(4);
    static final Integer five  = new Integer(5);
    static final Integer six = new Integer(6);
    static final Integer seven = new Integer(7);
    static final Integer eight = new Integer(8);
    static final Integer nine = new Integer(9);
    static final Integer m1  = new Integer(-1);
    static final Integer m2  = new Integer(-2);
    static final Integer m3  = new Integer(-3);
    static final Integer m4 = new Integer(-4);
    static final Integer m5 = new Integer(-5);
    static final Integer m6 = new Integer(-6);
    static final Integer m10 = new Integer(-10);


    /**
     * A security policy where new permissions can be dynamically added
     * or all cleared.
     */
    static class AdjustablePolicy extends java.security.Policy {
        Permissions perms = new Permissions();
        AdjustablePolicy() { }
        void addPermission(Permission perm) { perms.add(perm); }
        void clearPermissions() { perms = new Permissions(); }
    public PermissionCollection getPermissions(CodeSource cs) {
        return perms;
    }
    public PermissionCollection getPermissions(ProtectionDomain pd) {
        return perms;
    }
    public boolean implies(ProtectionDomain pd, Permission p) {
        return perms.implies(p);
    }
    public void refresh() {}
    }


    // Some convenient Runnable classes

    static class NoOpRunnable implements Runnable {
        public void run() {}
    }

    static class NoOpCallable implements Callable {
        public Object call() { return Boolean.TRUE; }
    }

    static final String TEST_STRING = "a test string";

    static class StringTask implements Callable<String> {
        public String call() { return TEST_STRING; }
    }

    static class NPETask implements Callable<String> {
        public String call() { throw new NullPointerException(); }
    }

    static class CallableOne implements Callable<Integer> {
        public Integer call() { return one; }
    }

    class ShortRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(SHORT_DELAY_MS);
            }
            catch(Exception e) {
                threadUnexpectedException();
            }
        }
    }

    class ShortInterruptedRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(SHORT_DELAY_MS);
                threadShouldThrow();
            }
            catch(InterruptedException success) {
            }
        }
    }

    class SmallRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(SMALL_DELAY_MS);
            }
            catch(Exception e) {
                threadUnexpectedException();
            }
        }
    }

    class SmallPossiblyInterruptedRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(SMALL_DELAY_MS);
            }
            catch(Exception e) {
            }
        }
    }

    class SmallCallable implements Callable {
        public Object call() {
            try {
                Thread.sleep(SMALL_DELAY_MS);
            }
            catch(Exception e) {
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
            }
            catch(InterruptedException success) {
            }
        }
    }


    class MediumRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(MEDIUM_DELAY_MS);
            }
            catch(Exception e) {
                threadUnexpectedException();
            }
        }
    }

    class MediumInterruptedRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(MEDIUM_DELAY_MS);
                threadShouldThrow();
            }
            catch(InterruptedException success) {
            }
        }
    }

    class MediumPossiblyInterruptedRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(MEDIUM_DELAY_MS);
            }
            catch(InterruptedException success) {
            }
        }
    }

    class LongPossiblyInterruptedRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(LONG_DELAY_MS);
            }
            catch(InterruptedException success) {
            }
        }
    }

    /**
     * For use as ThreadFactory in constructors
     */
    static class SimpleThreadFactory implements ThreadFactory{
        public Thread newThread(Runnable r){
            return new Thread(r);
        }
    }

    static class TrackedShortRunnable implements Runnable {
        volatile boolean done = false;
        public void run() {
            try {
                Thread.sleep(SMALL_DELAY_MS);
                done = true;
            } catch(Exception e){
            }
        }
    }

    static class TrackedMediumRunnable implements Runnable {
        volatile boolean done = false;
        public void run() {
            try {
                Thread.sleep(MEDIUM_DELAY_MS);
                done = true;
            } catch(Exception e){
            }
        }
    }

    static class TrackedLongRunnable implements Runnable {
        volatile boolean done = false;
        public void run() {
            try {
                Thread.sleep(LONG_DELAY_MS);
                done = true;
            } catch(Exception e){
            }
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
            } catch(Exception e){
            }
            return Boolean.TRUE;
        }
    }


    /**
     * For use as RejectedExecutionHandler in constructors
     */
    static class NoOpREHandler implements RejectedExecutionHandler{
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor){}
    }


}