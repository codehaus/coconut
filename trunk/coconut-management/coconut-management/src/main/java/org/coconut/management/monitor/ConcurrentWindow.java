/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.monitor;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ConcurrentWindow {
    private final int windowSize;

    private final AtomicLong[] values;

    private final AtomicLong[] lastUpdate;

    private final AtomicLong total = new AtomicLong();

    private final AtomicLong nextTimestamp = new AtomicLong();

    private final AtomicLong update = new AtomicLong();

    ConcurrentWindow(int windowSize) {
        this.windowSize = windowSize;
        values = new AtomicLong[windowSize];
        lastUpdate = new AtomicLong[windowSize];
        for (int i = 0; i < windowSize; i++) {
            values[i] = new AtomicLong();
            lastUpdate[i] = new AtomicLong();
        }
    }

    public void update(long value) {
        long n = nextTimestamp.incrementAndGet();
        int index = (int) ((n - 1) % windowSize);
        long l = lastUpdate[index].get();
        while (l != 0 && l != n - windowSize) {
            Thread.yield();
            l = lastUpdate[index].get();
        }
        total.addAndGet(-values[index].get());
        total.addAndGet(value);
        values[index].set(value);
        lastUpdate[index].set(n);
        update.incrementAndGet();
    }

    public double getAverage() {
        for (;;) {
            long timestamp = nextTimestamp.get();
            long samlings = Math.min(windowSize, timestamp);
            double avg = samlings == 0 ? Double.NaN : ((double) total.get()) / samlings;
            if (update.get() == timestamp && nextTimestamp.get() == timestamp) {
                return avg;
            }
        }
    }

    public static void main(String[] args) {
        ConcurrentWindow cw = new ConcurrentWindow(130);
        for (int i = 0; i < 101; i++) {
            cw.update(i);
        }
        System.out.println(cw.getAverage());
        System.out.println(Arrays.toString(cw.values));
    }
}
