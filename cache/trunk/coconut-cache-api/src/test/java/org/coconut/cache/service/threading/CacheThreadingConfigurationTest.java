/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.threading;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.spi.XmlConfigurator;
import org.coconut.test.MockTestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheThreadingConfigurationTest {

    CacheThreadingConfiguration t;


    CacheThreadingConfiguration DEFAULT = new CacheThreadingConfiguration();

    private final static Executor e = Executors.newCachedThreadPool();

    private final static ScheduledExecutorService ses = Executors
            .newSingleThreadScheduledExecutor();

    @Before
    public void setUp() {
        t = new CacheThreadingConfiguration();
    }

    @Test
    public void testExecutor() {
        assertNull(t.getExecutor());
        assertEquals(t, t.setExecutor(e));
        assertEquals(e, t.getExecutor());
    }

    @Test
    public void testShutdownExecutorService() {
        assertFalse(t.getShutdownExecutorService());
        assertEquals(t, t.setShutdownExecutorService(true));
        assertTrue(t.getShutdownExecutorService());
    }

    @Test
    public void testScheduledEvictionAtFixedRate() {
        t.setExecutor(ses);
        assertEquals(t, t.setScheduledEvictionAtFixedRate(4, TimeUnit.MICROSECONDS));
        assertEquals(4000, t.getScheduledEvictionAtFixedRate(TimeUnit.NANOSECONDS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetScheduledEvictionAtFixedRateIAE() {
        t.setExecutor(ses);
        t.setScheduledEvictionAtFixedRate(-1, TimeUnit.MICROSECONDS);
    }

    // @Test(expected = IllegalStateException.class)
    // public void testSetScheduledEvictionAtFixedRateISE1() {
    // t.setScheduledEvictionAtFixedRate(4, TimeUnit.MICROSECONDS);
    // }
    //
    // @Test(expected = IllegalStateException.class)
    // public void testSetScheduledEvictionAtFixedRateISE2() {
    // t.setExecutor(e);
    // t.setScheduledEvictionAtFixedRate(4, TimeUnit.MICROSECONDS);
    // }

    static CacheThreadingConfiguration rw(CacheThreadingConfiguration conf)
            throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        CacheConfiguration cc = CacheConfiguration.create();
        cc.addService(conf);
        XmlConfigurator.getInstance().to(cc, os);
        cc = XmlConfigurator.getInstance().from(
                new ByteArrayInputStream(os.toByteArray()));
        return (CacheThreadingConfiguration) cc
                .getServiceConfiguration(CacheThreadingConfiguration.class);
    }

    @Test
    public void testNoop() throws Exception {
        t = rw(t);
        assertNull(t.getExecutor());
        assertEquals(DEFAULT.getScheduledEvictionAtFixedRate(
                TimeUnit.NANOSECONDS), t.getScheduledEvictionAtFixedRate(
                TimeUnit.NANOSECONDS));
        assertEquals(DEFAULT.getShutdownExecutorService(), t
                .getShutdownExecutorService());
    }

    @Test
    public void testThreading() throws Exception {
        t.setShutdownExecutorService(!DEFAULT.getShutdownExecutorService());
        t.setScheduledEvictionAtFixedRate(360000, TimeUnit.MILLISECONDS);
        t.setExecutor(MockTestCase.mockDummy(Executor.class));
        t = rw(t);
        assertEquals(!DEFAULT.getShutdownExecutorService(), t
                .getShutdownExecutorService());
        assertEquals(360, t.getScheduledEvictionAtFixedRate(TimeUnit.SECONDS));
        assertNull(t.getExecutor());
    }

    @Test
    public void testThreading_ThreadPool() throws Exception {
        t.setExecutor(Executors.newFixedThreadPool(5));
        t = rw(t);
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) t.getExecutor();
        assertEquals(5, tpe.getCorePoolSize());
        assertEquals(5, tpe.getMaximumPoolSize());
        assertEquals(0, tpe.getKeepAliveTime(TimeUnit.NANOSECONDS));
        assertTrue(tpe.getRejectedExecutionHandler() instanceof ThreadPoolExecutor.AbortPolicy);
        assertTrue(tpe.getThreadFactory().getClass().equals(
                Executors.defaultThreadFactory().getClass()));
        assertTrue(tpe.getQueue() instanceof LinkedBlockingQueue);
    }

    @Test
    public void testThreading_ThreadPool1() throws Exception {
        ThreadPoolExecutor tpe1 = new ThreadPoolExecutor(10, 20, 30,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(200),
                new ThreadPoolExecutor.CallerRunsPolicy());
        tpe1.setThreadFactory(new Tf1());
        t.setExecutor(tpe1);
        t = rw(t);
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) t.getExecutor();
        assertEquals(10, tpe.getCorePoolSize());
        assertEquals(20, tpe.getMaximumPoolSize());
        assertEquals(30000, tpe.getKeepAliveTime(TimeUnit.MICROSECONDS));
        assertTrue(tpe.getRejectedExecutionHandler() instanceof CallerRunsPolicy);
        assertTrue(tpe.getThreadFactory() instanceof Tf1);
        assertTrue(tpe.getQueue() instanceof ArrayBlockingQueue);
        assertEquals(200, ((ArrayBlockingQueue) tpe.getQueue()).remainingCapacity());
    }

    @Test
    public void testThreading_ThreadPool2() throws Exception {
        ThreadPoolExecutor tpe1 = new ThreadPoolExecutor(10, 20, 30,
                TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        t.setExecutor(tpe1);
        t = rw(t);
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) t.getExecutor();
        assertTrue(tpe != tpe1);
        assertEquals(10, tpe.getCorePoolSize());
        assertEquals(20, tpe.getMaximumPoolSize());
        assertEquals(30000, tpe.getKeepAliveTime(TimeUnit.MICROSECONDS));
        assertTrue(tpe.getRejectedExecutionHandler() instanceof DiscardOldestPolicy);
        assertTrue(tpe.getQueue() instanceof PriorityBlockingQueue);
        assertNull(((PriorityBlockingQueue) tpe.getQueue()).comparator());
    }

    @Test
    public void testThreading_ThreadPool3() throws Exception {
        ThreadPoolExecutor tpe1 = new ThreadPoolExecutor(10, 20, 30,
                TimeUnit.MILLISECONDS, new PriorityBlockingQueue(11, new Comp()),
                new ThreadPoolExecutor.DiscardPolicy());
        t.setExecutor(tpe1);
        t = rw(t);
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) t.getExecutor();
        assertTrue(tpe != tpe1);
        assertTrue(tpe.getRejectedExecutionHandler() instanceof DiscardPolicy);
        assertTrue(tpe.getQueue() instanceof PriorityBlockingQueue);
        assertTrue(((PriorityBlockingQueue) tpe.getQueue()).comparator() instanceof Comp);
    }

    @Test
    public void testThreading_ThreadPool4() throws Exception {
        ThreadPoolExecutor tpe1 = new ThreadPoolExecutor(10, 20, 30,
                TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(), new MyREH1());
        t.setExecutor(tpe1);
        t = rw(t);
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) t.getExecutor();
        assertTrue(tpe != tpe1);
        assertTrue(tpe.getRejectedExecutionHandler() instanceof MyREH1);
        assertTrue(tpe.getQueue() instanceof SynchronousQueue);
    }

    @Test
    public void testThreading_ThreadPool5() throws Exception {
        ThreadPoolExecutor tpe1 = new ThreadPoolExecutor(10, 20, 30,
                TimeUnit.MILLISECONDS, new MyQueue(), new AbortPolicy());
        t.setExecutor(tpe1);
        t = rw(t);

        ThreadPoolExecutor tpe = (ThreadPoolExecutor) t.getExecutor();
        assertTrue(tpe != tpe1);
        assertTrue(tpe.getQueue() instanceof MyQueue);
    }

    @Test
    public void testThreading_ThreadFactoryNotSerializable() throws Exception {
        ThreadPoolExecutor tpe1 = new ThreadPoolExecutor(10, 20, 30,
                TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        tpe1.setThreadFactory(new Tf2(""));
        t.setExecutor(tpe1);
        t = rw(t);
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) t.getExecutor();
        assertEquals(10, tpe.getCorePoolSize());
        assertEquals(20, tpe.getMaximumPoolSize());
        assertEquals(30000, tpe.getKeepAliveTime(TimeUnit.MICROSECONDS));
        assertTrue(tpe.getThreadFactory().getClass().equals(
                Executors.defaultThreadFactory().getClass()));
    }

    @Test
    public void testThreading_REHNotSerializable() throws Exception {
        ThreadPoolExecutor tpe1 = new ThreadPoolExecutor(10, 20, 30,
                TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(), new MyREH(
                        ""));
        t.setExecutor(tpe1);
        t = rw(t);
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) t.getExecutor();
        assertEquals(10, tpe.getCorePoolSize());
        assertEquals(20, tpe.getMaximumPoolSize());
        assertEquals(30000, tpe.getKeepAliveTime(TimeUnit.MICROSECONDS));
        assertTrue(tpe.getRejectedExecutionHandler() instanceof AbortPolicy);
    }

    @Test
    public void testThreading_CompNotSerializable() throws Exception {
        ThreadPoolExecutor tpe1 = new ThreadPoolExecutor(10, 20, 30,
                TimeUnit.MILLISECONDS, new PriorityBlockingQueue(11, new Comp2("")),
                new AbortPolicy());
        t.setExecutor(tpe1);
        t = rw(t);
        assertNull(t.getExecutor());
    }

    @Test
    public void testThreading_QueueNotSerializable() throws Exception {
        ThreadPoolExecutor tpe1 = new ThreadPoolExecutor(10, 20, 30,
                TimeUnit.MILLISECONDS, new MyQueue2(""), new AbortPolicy());
        t.setExecutor(tpe1);
        t = rw(t);
        assertNull(t.getExecutor());
    }

    @Test
    public void testThreading_ScheduledPool1() throws Exception {
        ScheduledThreadPoolExecutor tpe1 = new ScheduledThreadPoolExecutor(12, new Tf1(),
                new MyREH1());
        t.setExecutor(tpe1);
        t = rw(t);
        ScheduledThreadPoolExecutor tpe = (ScheduledThreadPoolExecutor) t.getExecutor();
        assertTrue(tpe != tpe1);

        assertEquals(12, tpe.getCorePoolSize());
        assertTrue(tpe.getRejectedExecutionHandler() instanceof MyREH1);
        assertTrue(tpe.getThreadFactory() instanceof Tf1);

    }

    public static class MyQueue extends LinkedBlockingQueue {

    }

    public static class MyQueue2 extends LinkedBlockingQueue {
        MyQueue2(Object o) {

        }
    }

    public static class Comp implements Comparator {

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {
            return 0;
        }

    }

    public static class Comp2 implements Comparator {
        Comp2(Object o) {

        }

        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object o1, Object o2) {
            return 0;
        }

    }

    public static class MyREH implements RejectedExecutionHandler {
        public MyREH(Object o) {

        }

        /**
         * @see java.util.concurrent.RejectedExecutionHandler#rejectedExecution(java.lang.Runnable,
         *      java.util.concurrent.ThreadPoolExecutor)
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        }
    }

    public static class MyREH1 implements RejectedExecutionHandler {

        /**
         * @see java.util.concurrent.RejectedExecutionHandler#rejectedExecution(java.lang.Runnable,
         *      java.util.concurrent.ThreadPoolExecutor)
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        }
    }

    public static class Tf1 implements ThreadFactory {

        /**
         * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
         */
        public Thread newThread(Runnable r) {
            return null;
        }
    }

    public static class Tf2 implements ThreadFactory {
        Tf2(Object o) {

        }

        /**
         * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
         */
        public Thread newThread(Runnable r) {
            return null;
        }
    }

}