/**
 * 
 */
package org.coconut.management.monitor;

import org.coconut.management.spi.AbstractManagedNumberHolder;
import org.coconut.management.spi.JMXConfigurator;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LongWindow extends AbstractManagedNumberHolder {

    private long[] window;

    private int pointer;

    private long total;

    private int size;

    public LongWindow(Number n, int windowSize) {
        super(n);
        window = new long[windowSize];
    }

    public LongWindow(int windowSize) {
        window = new long[windowSize];
    }

    /**
     * Circularly increment i.
     */
    final int inc(int i) {
        return (++i == window.length) ? 0 : i;
    }

    /**
     * @see org.coconut.metric.spi.AbstractManagedNumber#getValue()
     */
    @Override
    protected synchronized Number getValue() {
        return getAverage();
    }

    public synchronized long getTotal() {
        return total;
    }

    public synchronized int getEntries() {
        return size;
    }

    public synchronized double getAverage() {
        return size == 0 ? Double.NaN : total / (double) size;
    }

    public synchronized long[] copyData() {
        long[] a = new long[size];
        int k = 0;
        int i = pointer;
        while (k < size) {
            a[k++] = window[i];
            i = inc(i);
        }
        return a;
    }

    /**
     * @see org.coconut.metric.spi.AbstractManagedNumberHolder#update(double)
     */
    @Override
    protected synchronized void update(Number value) {
        long v = value.longValue();
        long old = window[pointer];
        total = total - old + v;
        int windowLength = window.length;
        if (size < windowLength) {
            size++;
        }
        pointer = inc(pointer);
    }

    public synchronized int getWindowSize() {
        return window.length;
    }

    public static void main(String[] args) {
        LongWindow lw = new LongWindow(4);
        lw.process(2);
        lw.process(4);
        lw.process(3);
        lw.process(7);
        System.out.println(lw.total);
        lw.process(23);
        System.out.println(lw.total);
    }

    /**
     * @see org.coconut.metric.spi.ManagedMetric#prepare(org.coconut.metric.spi.ManagedConfigurator)
     */
    public void configure(JMXConfigurator jmx) {
        // TODO Auto-generated method stub

    }

}
