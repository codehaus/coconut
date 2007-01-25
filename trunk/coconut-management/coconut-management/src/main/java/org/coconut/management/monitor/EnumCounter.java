/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.monitor;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import org.coconut.core.EventProcessor;
import org.coconut.core.Named;
import org.coconut.management.spi.AbstractApm;
import org.coconut.management.spi.Described;
import org.coconut.management.spi.JMXConfigurator;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EnumCounter<T extends Enum> extends AbstractApm implements EventProcessor<T>,
        Serializable {

    private final long[] count;

    private final Class<T> clazz;

    private T latest;

    public EnumCounter(Class<? extends Enum> e, String name) {
        this(e, name, "Amount of handled " + e + "s");
    }

    public EnumCounter(Class<? extends Enum> e, String name, String description) {
        super(name, description);
        count = new long[e.getEnumConstants().length];
        this.clazz = (Class<T>) e;
    }

    /**
     * @see org.coconut.metric.jmx.PreparedForJMX#prepare(org.coconut.metric.jmx.PrepareJmx)
     */
    public void configure(JMXConfigurator jmx) {
        for (T t : clazz.getEnumConstants()) {
            jmx.addAttribute(t.toString(), getDescription(t),
                    new EnumNumber(t.ordinal()), Long.TYPE);
        }
    }

    protected String getDescription(T t) {
        return "Amount of handled " + t + "s";
    }

    public synchronized void process(T value) {
        latest = value;
        count[value.ordinal()]++;
    }

    public long getCount(T value) {
        return getCount(value.ordinal());
    }

    public Number liveCount(T value) {
        return new EnumNumber(value.ordinal());
    }

    private synchronized long getCount(int ordinal) {
        return count[ordinal];
    }

    /**
     * Return the latests reported state.
     * 
     * @return the latests reported state
     */
    public synchronized T getLatestState() {
        return latest;
    }

    /**
     * Returns a map that is sorted accordingly to the number of hits.
     */
    public SortedMap<T, Long> sort() {
        Comparator<T> c = new Comparator<T>() {
            public int compare(T o1, T o2) {
                long v1 = count[o1.ordinal()];
                long v2 = count[o2.ordinal()];
                return (v2 < v1 ? -1 : (v2 == v1 ? 0 : 1));
            }
        };
        SortedMap<T, Long> sm = new TreeMap<T, Long>(c);
        synchronized (this) {
            for (T e : clazz.getEnumConstants()) {
                sm.put((T) e, count[e.ordinal()]);
            }
        }
        return Collections.unmodifiableSortedMap(sm);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        synchronized (this) {
            SortedMap<T, Long> sm = sort();
            for (T e : clazz.getEnumConstants()) {
                sb.append(e);
                sb.append(" : ");
                sb.append(count[e.ordinal()]);
                sb.append(" (");
                sb.append(sm.headMap(e).size() + 1); // quick hack
                sb.append(")\n");
            }
            sb.append("Latest state was ");
            sb.append(getLatestState());
        }
        sb.append("\n");
        return sb.toString();
    }

    final class EnumNumber extends Number implements Callable<Number>, Named, Described {
        private final int ordinal;

        /**
         * @param ordinal
         */
        public EnumNumber(final int ordinal) {
            this.ordinal = ordinal;
        }

        /**
         * @see java.lang.Number#doubleValue()
         */
        @Override
        public double doubleValue() {
            return (double) longValue();
        }

        /**
         * @see java.lang.Number#floatValue()
         */
        @Override
        public float floatValue() {
            return (float) longValue();
        }

        /**
         * @see java.lang.Number#intValue()
         */
        @Override
        public int intValue() {
            return (int) longValue();
        }

        /**
         * @see java.lang.Number#longValue()
         */
        @Override
        public long longValue() {
            return getCount(ordinal);
        }

        /**
         * @see java.util.concurrent.Callable#call()
         */
        public Number call() {
            return Long.valueOf(getCount(ordinal));
        }

        /**
         * @see org.coconut.metric.spi.Named#getDescription()
         */
        public String getDescription() {
            return EnumCounter.this.getDescription(clazz.getEnumConstants()[ordinal]);
        }

        /**
         * @see org.coconut.metric.spi.Named#getName()
         */
        public String getName() {
            return clazz.getEnumConstants()[ordinal].toString();
        }

        /**
         * @see org.coconut.metric.PerformanceMonitor#prepare(org.coconut.metric.spi.ManagedConfigurator)
         */
        public void configure(JMXConfigurator jmx) {
            throw new UnsupportedOperationException();
        }
    }
}
