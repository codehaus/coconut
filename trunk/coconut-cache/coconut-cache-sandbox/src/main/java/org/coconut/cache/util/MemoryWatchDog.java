/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.util;

import com.sun.corba.se.impl.orbutil.closure.Future;

import java.awt.List;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.coconut.cache.spi.AbstractCache;

/**
 * This would probably not work, if a cache used soft references. 
 * The only 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class MemoryWatchDog implements Runnable, NotificationListener {
    private final MemoryMXBean mxBean = ManagementFactory.getMemoryMXBean();

    private AtomicInteger lows;
    private AtomicInteger highs;
    private volatile double low_threshold = .85;

    private volatile double target = .9;

    private volatile double high_threshold = .95;

    private volatile int lastPartion = -1;

    private final static int LOW = 0;

    private final static int TARGET = 1;

    private final static int HIGH = 2;

    private AbstractCache cache;

    /**
     * @see javax.management.NotificationListener#handleNotification(javax.management.Notification,
     *      java.lang.Object)
     */
    public void handleNotification(Notification notification, Object handback) {
        System.out.println(notification);
    }

    public static void main(String[] args) throws InterruptedException,
            ExecutionException {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        MemoryWatchDog dog = new MemoryWatchDog();
        dog.f = ses.scheduleAtFixedRate(dog, 0, 1000, TimeUnit.MILLISECONDS);
        for (;;) {
            Thread.sleep(500);
            System.out.println(dog.f.get());
        }
        
    }
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain");
    }
    protected void update() {

    }

    protected float getPctUsage() {
        MemoryUsage mu = mxBean.getHeapMemoryUsage();
        long used = mu.getUsed();
        long max = mu.getMax();
        return (float) used / max;
    }

    java.util.LinkedList al = new LinkedList();

    private ScheduledFuture f;

    /**
     * @see java.lang.Runnable#run()
     */
    public synchronized void run() {
        System.out.println(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
        for (int i = 0; i < 10000; i++) {
            al.add(new Object());
        }
        float reading = getPctUsage();
        final int p;
        if (reading <= low_threshold) {
            p = LOW;
        } else if (reading > high_threshold) {
            p = HIGH;
        } else {
            p = TARGET;
        }
        if (p == LOW) {
            low();
        } else if (p == HIGH) {
            high();
        }
        lastPartion = p;
    }

    private void miniAdjust() {
        //idea is to call seldomly..
        //for example once a minute to make small changes
        //
    }
    protected void low() {
        System.out.println("low " + getPctUsage());
        //if cache.size>=cache.capacity
        //  cache.size+=some percentage (be conservative)
        //conversative-> because we might have other datastructures
        //that uses this pattern as well, and we don't wan't to 
        //take it all ourself
    }
    protected void high() {
        System.out.println("high");
        Random r = new Random();
        int f = r.nextInt(50000);
        for (int i = 0; i < f; i++) {
            al.removeFirst();
        }
    }
    protected void lowCross() {
        Random r = new Random();
        int f = r.nextInt(50000);
        for (int i = 0; i < f; i++) {
            al.removeFirst();
        }
    }

    protected int getCurrentCapacity() {
        return cache.size();
    }

    protected void setNewCapacity(int newCapacity) {

    }

}
