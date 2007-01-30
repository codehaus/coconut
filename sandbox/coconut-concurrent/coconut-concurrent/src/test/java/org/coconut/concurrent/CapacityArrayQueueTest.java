/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.coconut.concurrent.CapacityArrayQueue;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CapacityArrayQueueTest extends TestCase {

    public void testCapacity() throws InterruptedException {
        BlockingQueue cq = new CapacityArrayQueue(4);
        // BlockingQueue cq = new ArrayBlockingQueue(40);
        Thread t = new Thread(new TakeMe(cq));
        t.start();
        for (int i = 0; i < 100; i++) {
            assertTrue(cq.offer(i, 10, TimeUnit.SECONDS));
        }
        t.join();
    }

    static class TakeMe implements Runnable {

        private final BlockingQueue q;

        /**
         * @param q
         */
        public TakeMe(final BlockingQueue q) {
            this.q = q;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    q.poll(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            // TODO Auto-generated method stub

        }

    }
}
