/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.spi;

import java.lang.management.ManagementFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.coconut.cache.Cache;
import org.coconut.cache.defaults.support.JMXSupport;
import org.coconut.test.MockTestCase;


/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class JMXCacheMXBeanNotificationTest extends MockTestCase
        implements NotificationListener {

    Cache<Integer, Integer> c;

    BlockingQueue<Notification> events;

    
    protected void setUp() throws Exception {
        super.setUp();
        events = new LinkedBlockingQueue<Notification>();
        c =null; // Caches.newFastMemoryCache(Policies.newClock(), 5);
        ObjectName name = JMXSupport.registerCache(c, "test");
        ManagementFactory.getPlatformMBeanServer().addNotificationListener(
                name, this, null, null);
        // JMXRegistrant.getProxy(ManagementFactory.getPlatformMBeanServer(),
        // "test");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        JMXSupport.unregisterCache("test");
    }

    public void verify() {
        super.verify();
        Object event = null;
        try {
            event = events.poll(100, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (event != null) {
            System.err.println(event);
            fail();
        }
    }

    private void consumeItem(String type, long sequenceId) {
        try {
            Notification event = events.poll(1, TimeUnit.SECONDS);
            assertNotNull(event);

            assertEquals(sequenceId, event.getSequenceNumber());
            assertEquals(type, event.getType());
        } catch (InterruptedException e) {
            throw new IllegalStateException("Thread was interrupted", e);
        }
    }

    private void consumeItem() {
        try {
            Notification event = events.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Thread was interrupted", e);
        }
    }

    /**
     * @see javax.management.NotificationListener#handleNotification(javax.management.Notification,
     *      java.lang.Object)
     */
    public void handleNotification(Notification notification, Object handback) {
        events.add(notification);
    }

    public void testNoTests() {
        
    }
    
//    public void testAccessed() {
//        c.get(0);
//        c.put(0, 1);
//        consumeItem();
//        c.get(0);
//        consumeItem(CacheItemEvent.Accessed.NAME, 2);
//    }
//
//    public void testAdded() {
//        c.put(0, 1);
//        consumeItem(CacheItemEvent.Added.NAME, 1);
//    }
//
//    public void testEvicted() {
//        for (int i = 0; i < 5; i++) {
//            c.put(i, i);
//            consumeItem();
//        }
//        c.put(6, 6);
//        consumeItem(CacheItemEvent.Evicted.NAME, 6);
//        consumeItem(); // item added message
//    }
//
//    public void testExpired() {
//        // No test yet
//    }
//
//    public void testRemoved() {
//        c.put(1, 0);
//        consumeItem();
//        c.remove(1);
//        consumeItem(CacheItemEvent.Removed.NAME, 2);
//    }

}
