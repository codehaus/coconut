/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.core.EventProcessor;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

/**
 * noget federe end det åndsvage event handling.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class LongCounter 
      {

    /**
     * @param numberType
     */
    public LongCounter(String name) {
        //super(name, "No Description of " + name);
    }

    /**
     * @param numberType
     */
    public LongCounter(String name, String description) {
        //super(name, description);
    }

    public static LongCounter newUnsynchronized(String name, String description) {
        return new UnsynchronizedLongCounter(name, description);
    }

    /**
     * Atomically adds the given value to the current value.
     * 
     * @param delta
     *            the value to add
     * @return the updated value
     */
    public abstract long addAndGet(long delta);

    /**
     * Atomically decrements by one the current value.
     * 
     * @return the updated value
     */
    public abstract long decrementAndGet();

    /**
     * Gets the current value.
     * 
     * @return the current value
     */
    public abstract long get();

    /**
     * Atomically adds the given value to the current value.
     * 
     * @param delta
     *            the value to add
     * @return the previous value
     */
    public abstract long getAndAdd(long delta);

    /**
     * Atomically decrements by one the current value.
     * 
     * @return the previous value
     */
    public abstract long getAndDecrement();

    /**
     * Atomically increments by one the current value.
     * 
     * @return the previous value
     */
    public abstract long getAndIncrement();

    /**
     * Atomically sets to the given value and returns the old value.
     * 
     * @param newValue
     *            the new value
     * @return the previous value
     */
    public abstract long getAndSet(long newValue);

    /**
     * Atomically increments by one the current value.
     * 
     * @return the updated value
     */
    public abstract long incrementAndGet();

    /**
     * Sets to the given value.
     * 
     * @param newValue
     *            the new value
     */
    public abstract void set(long newValue);

    /**
     * Returns the String representation of the current value.
     * 
     * @return the String representation of the current value.
     */
    public String toString() {
        return Long.toString(get());
    }

    @ManagedAttribute(defaultValue = "$name Total", description = "Total $description")
    public final long getLatest() {
        return get();
    }

    /**
     * @see org.coconut.metric.spi.SingleJMXNumber#getNumberClass()
     */
  
    protected final Class<? extends Number> getNumberClass() {
        return Long.TYPE;
    }

    /**
     * @see org.coconut.metric.spi.AbstractManagedNumber#getValue()
     */
 
    protected final Number getValue() {
        return get();
    }

    @ManagedOperation(defaultValue = "reset $name", description = "Sets the value of $name to 0")
    public void reset() {
        set(0);
    }



    public void process(Number n) {
        set(n.longValue());
    }

    static EventProcessor<? super LongCounter>[] add(
            EventProcessor<? super LongCounter>[] prev,
            EventProcessor<? super LongCounter> add) {
        ArrayList<EventProcessor<? super LongCounter>> al = new ArrayList<EventProcessor<? super LongCounter>>(
                Arrays.asList(prev));
        al.add(add);
        return al.toArray(new EventProcessor[prev.length + 1]);
    }

    static EventProcessor<? super LongCounter>[] remove(
            EventProcessor<? super LongCounter>[] prev,
            EventProcessor<?> remove) {
        ArrayList<EventProcessor<? super LongCounter>> al = new ArrayList<EventProcessor<? super LongCounter>>(
                Arrays.asList(prev));
        al.remove(remove);
        // very pessimistic, don't want an array that is to long
        return al.toArray(new EventProcessor[0]);
    }

    public final static class SynchronizedLongCounter extends LongCounter {
        private final Object mutex;

        private long value;

        private EventProcessor<? super LongCounter>[] array = new EventProcessor[0];

        SynchronizedLongCounter(String name, String description) {
            super(name, description);
            this.mutex = this;
        }

        SynchronizedLongCounter(Object mutex, String name, String description) {
            super(name, description);
            this.mutex = mutex;
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#doubleValue()
         */
     
        public double doubleValue() {
            synchronized (mutex) {
                return (double) value;
            }
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#floatValue()
         */
      
        public float floatValue() {
            synchronized (mutex) {
                return (float) value;
            }
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#intValue()
         */
    
        public int intValue() {
            synchronized (mutex) {
                return (int) value;
            }
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#longValue()
         */

        public long longValue() {
            synchronized (mutex) {
                return value;
            }
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#reset()
         */
        @Override
        public void reset() {
            synchronized (mutex) {
                value = 0;
            }
        }

        /**
         * Atomically adds the given value to the current value.
         * 
         * @param delta
         *            the value to add
         * @return the updated value
         */
        public long addAndGet(long delta) {
            synchronized (mutex) {
                return ++value;
            }
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#decrementAndGet()
         */
        @Override
        public long decrementAndGet() {
            synchronized (mutex) {
                return --value;
            }
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#get()
         */
        @Override
        public long get() {
            synchronized (mutex) {
                return value;
            }
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#getAndAdd(long)
         */
        @Override
        public long getAndAdd(long delta) {
            synchronized (mutex) {
                long prev = value;
                value += delta;
                return prev;
            }
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#getAndDecrement()
         */
        @Override
        public long getAndDecrement() {
            synchronized (mutex) {
                return value--;
            }
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#getAndIncrement()
         */
        @Override
        public long getAndIncrement() {
            synchronized (mutex) {
                return value++;
            }
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#getAndSet(long)
         */
        @Override
        public long getAndSet(long newValue) {
            synchronized (mutex) {
                long prev = value;
                value = newValue;
                return prev;
            }
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#incrementAndGet()
         */
        @Override
        public long incrementAndGet() {
            synchronized (mutex) {
                return ++value;
            }
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#set(long)
         */
        @Override
        public void set(long newValue) {
            synchronized (mutex) {
                value = newValue;
            }
        }

        /**
         * @see org.coconut.metric.MetricHub#addEventHandler(org.coconut.core.EventHandler)
         */
        public EventProcessor<? super LongCounter> addEventProcessor(
                EventProcessor<? super LongCounter> e) {
            synchronized (mutex) {
                array = add(array, e);
                return e;
            }
        }

        /**
         * @see org.coconut.metric.MetricHub#getEventHandlers()
         */
        public List<EventProcessor<? super LongCounter>> getEventProcessors() {
            synchronized (mutex) {
                return new ArrayList<EventProcessor<? super LongCounter>>(Arrays
                        .asList(array));
            }
        }

        /**
         * @see org.coconut.metric.MetricUpdateBus#removeEventHandler(org.coconut.core.EventHandler)
         */
        public boolean removeEventProcessor(EventProcessor<?> e) {
            synchronized (mutex) {
                EventProcessor<? super LongCounter>[] prev = array;
                array = remove(array, e);
                return prev.length != array.length;
            }
        }
    }

    final static class ConcurrentLongCounter extends LongCounter {

        private final AtomicLong l = new AtomicLong();

        private volatile EventProcessor<? super LongCounter>[] array = new EventProcessor[0];

        private final static boolean doUpdate = true;

        private final Object mutex = new Object();

        /**
         * @param name
         * @param description
         */
        ConcurrentLongCounter(String name, String description) {
            super(name, description);
        }


        @ManagedOperation(defaultValue = "reset $name", description = "Sets the value of $name to 0")
        public synchronized void reset() {
            l.set(0);
        }

        private void update() {
            if (doUpdate) {
                EventProcessor<? super LongCounter>[] a = array;
                if (a.length > 0) {
                    for (int i = 0; i < a.length; i++) {
                        a[i].process(this);
                    }
                }
            }
        }

        /**
         * Atomically adds the given value to the current value.
         * 
         * @param delta
         *            the value to add
         * @return the updated value
         */
        public long addAndGet(long delta) {
            long result = l.addAndGet(delta);
            update();
            return result;
        }

        /**
         * Atomically decrements by one the current value.
         * 
         * @return the updated value
         */
        public long decrementAndGet() {
            return l.decrementAndGet();
        }

        /**
         * @see java.lang.Number#doubleValue()
         */
        public double doubleValue() {
            return l.doubleValue();
        }

        /**
         * @see java.lang.Number#floatValue()
         */
        public float floatValue() {
            return l.floatValue();
        }

        /**
         * Gets the current value.
         * 
         * @return the current value
         */
        public long get() {
            return l.get();
        }

        /**
         * Atomically adds the given value to the current value.
         * 
         * @param delta
         *            the value to add
         * @return the previous value
         */
        public long getAndAdd(long delta) {
            return l.getAndAdd(delta);
        }

        /**
         * Atomically decrements by one the current value.
         * 
         * @return the previous value
         */
        public long getAndDecrement() {
            return l.getAndDecrement();
        }

        /**
         * Atomically increments by one the current value.
         * 
         * @return the previous value
         */
        public long getAndIncrement() {
            return l.getAndIncrement();
        }

        /**
         * Atomically sets to the given value and returns the old value.
         * 
         * @param newValue
         *            the new value
         * @return the previous value
         */
        public long getAndSet(long newValue) {
            return l.getAndSet(newValue);
        }

        /**
         * Atomically increments by one the current value.
         * 
         * @return the updated value
         */
        public long incrementAndGet() {
            return l.incrementAndGet();
        }

        /**
         * @see java.lang.Number#intValue()
         */
        public int intValue() {
            return l.intValue();
        }

        /**
         * @see java.lang.Number#longValue()
         */
        public long longValue() {
            return l.longValue();
        }

        /**
         * Sets to the given value.
         * 
         * @param newValue
         *            the new value
         */
        public void set(long newValue) {
            l.set(newValue);
        }

        /**
         * @see org.coconut.metric.MetricHub#addEventHandler(org.coconut.core.EventHandler)
         */
        public EventProcessor<? super LongCounter> addEventProcessor(
                EventProcessor<? super LongCounter> e) {
            synchronized (mutex) {
                array = add(array, e);
                return e;
            }
        }

        /**
         * @see org.coconut.metric.MetricHub#getEventHandlers()
         */
        public List<EventProcessor<? super LongCounter>> getEventProcessors() {
            synchronized (mutex) {
                return new ArrayList<EventProcessor<? super LongCounter>>(Arrays
                        .asList(array));
            }
        }

        /**
         * @see org.coconut.metric.MetricUpdateBus#removeEventHandler(org.coconut.core.EventHandler)
         */
        public boolean removeEventProcessor(EventProcessor<?> e) {
            synchronized (mutex) {
                EventProcessor<? super LongCounter>[] prev = array;
                array = remove(array, e);
                return prev.length != array.length;
            }
        }

    }

    static final class UnsynchronizedLongCounter extends LongCounter {

        private EventProcessor<? super LongCounter>[] array = new EventProcessor[0];

        private long value;

        /**
         * @param name
         * @param description
         */
        UnsynchronizedLongCounter(String name, String description) {
            super(name, description);
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#doubleValue()
         */
     
        public double doubleValue() {
            return (double) value;
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#floatValue()
         */
       
        public float floatValue() {
            return (float) value;
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#intValue()
         */

        public int intValue() {
            return (int) value;
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#longValue()
         */
    
        public long longValue() {
            return value;
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#reset()
         */
        @Override
        public void reset() {
            value = 0;
            update();
        }

        private void update() {
            EventProcessor<? super LongCounter>[] a = array;
            if (a.length > 0) {
                for (int i = 0; i < a.length; i++) {
                    a[i].process(this);
                }
            }
        }

        /**
         * Atomically adds the given value to the current value.
         * 
         * @param delta
         *            the value to add
         * @return the updated value
         */
        public long addAndGet(long delta) {
            value += delta;
            update();
            return value;
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#decrementAndGet()
         */
        @Override
        public long decrementAndGet() {
            --value;
            update();
            return value;
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#get()
         */
        @Override
        public long get() {
            return value;
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#getAndAdd(long)
         */
        @Override
        public long getAndAdd(long delta) {
            long prev = value;
            value += delta;
            update();
            return prev;
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#getAndDecrement()
         */
        @Override
        public long getAndDecrement() {
            update();
            return value--;
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#getAndIncrement()
         */
        @Override
        public long getAndIncrement() {
            update();
            return value++;
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#getAndSet(long)
         */
        @Override
        public long getAndSet(long newValue) {
            long prev = value;
            update();
            value = newValue;
            return prev;
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#incrementAndGet()
         */
        @Override
        public long incrementAndGet() {
            ++value;
            update();
            return value;
        }

        /**
         * @see org.coconut.metric.impl.LongCounter#set(long)
         */
        @Override
        public void set(long newValue) {
            value = newValue;
            update();
        }

        /**
         * @see org.coconut.metric.MetricHub#addEventHandler(org.coconut.core.EventHandler)
         */
        public EventProcessor<? super LongCounter> addEventProcessor(
                EventProcessor<? super LongCounter> e) {
            array = add(array, e);
            return e;

        }

        /**
         * @see org.coconut.metric.MetricHub#getEventHandlers()
         */
        public List<EventProcessor<? super LongCounter>> getEventProcessors() {
            return new ArrayList<EventProcessor<? super LongCounter>>(Arrays.asList(array));
        }

        /**
         * @see org.coconut.metric.MetricUpdateBus#removeEventHandler(org.coconut.core.EventHandler)
         */
        public boolean removeEventProcessor(EventProcessor<?> e) {
            EventProcessor<? super LongCounter>[] prev = array;
            array = remove(array, e);
            return prev.length != array.length;
        }
    }

    /**
     * @param string
     * @param string2
     * @return
     */
    public static LongCounter newConcurrent(String string, String string2) {
        return new ConcurrentLongCounter(string, string2);
    }
}
