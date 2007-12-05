/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.statistics;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
import junit.framework.*;
import java.util.concurrent.atomic.*;
import java.io.*;

public class AtomicDoubleTest extends TestCase {

    /**
     * constructor initializes to given value
     */
    public void testConstructor() {
        AtomicDouble ai = new AtomicDouble(1);
        assertEquals(1.0, ai.get());
    }

    /**
     * default constructed initializes to zero
     */
    public void testConstructor2() {
        AtomicDouble ai = new AtomicDouble();
        assertEquals(0.0, ai.get());
    }

    /**
     * get returns the last value set
     */
    public void testGetSet() {
        AtomicDouble ai = new AtomicDouble(1);
        assertEquals(1.0, ai.get());
        ai.set(2.5);
        assertEquals(2.5, ai.get());
        ai.set(-3.5);
        assertEquals(-3.5, ai.get());

    }

    /**
     * compareAndSet succeeds in changing value if equal to expected else fails
     */
    public void testCompareAndSet() {
        AtomicDouble ai = new AtomicDouble(1.0);
        assertTrue(ai.compareAndSet(1.0, 2.5));
        assertTrue(ai.compareAndSet(2.5, -4.3));
        assertEquals(-4.3, ai.get());
        assertFalse(ai.compareAndSet(-4.2, 7));
        assertFalse((7.0 == ai.get()));
        assertTrue(ai.compareAndSet(-4.3, 7));
        assertEquals(7.0, ai.get());
    }

    /**
     * compareAndSet in one thread enables another waiting for value to succeed
     */
    public void testCompareAndSetInMultipleThreads() throws Exception {
        final AtomicDouble ai = new AtomicDouble(1);
        Thread t = new Thread(new Runnable() {
            public void run() {
                while (!ai.compareAndSet(2.0, 3.0))
                    Thread.yield();
            }
        });

        t.start();
        assertTrue(ai.compareAndSet(1, 2));
        t.join(100);
        assertFalse(t.isAlive());
        assertEquals(ai.get(), 3.0);

    }

    /**
     * repeated weakCompareAndSet succeeds in changing value when equal to
     * expected
     */
    public void testWeakCompareAndSet() {
        AtomicDouble ai = new AtomicDouble(1);
        while (!ai.weakCompareAndSet(1, 2))
            ;
        while (!ai.weakCompareAndSet(2, -4))
            ;
        assertEquals(-4.0, ai.get());
        while (!ai.weakCompareAndSet(-4, 7))
            ;
        assertEquals(7.0, ai.get());
    }

    /**
     * getAndSet returns previous value and sets to given value
     */
    public void testGetAndSet() {
        AtomicDouble ai = new AtomicDouble(1);
        assertEquals(1.0, ai.getAndSet(0));
        assertEquals(0.0, ai.getAndSet(-10));
        assertEquals(-10.0, ai.getAndSet(1));
    }

    /**
     * getAndAdd returns previous value and adds given value
     */
    public void testGetAndAdd() {
        AtomicDouble ai = new AtomicDouble(1);
        assertEquals(1.0, ai.getAndAdd(2));
        assertEquals(3.0, ai.get());
        assertEquals(3.0, ai.getAndAdd(-4));
        assertEquals(-1.0, ai.get());
    }

    public static void main(String[] args) {
        AtomicLong al=new AtomicLong();
        al.getAndAdd(2);
        System.out.println(AtomicDouble.c(5));
        System.out.println(AtomicDouble.c(4));
        System.out.println(AtomicDouble.c(9));
        
        System.out.println(al); 
    }
    /**
     * getAndDecrement returns previous value and decrements
     */
    public void testGetAndDecrement() {
        AtomicDouble ai = new AtomicDouble(1);
        assertEquals(1.0, ai.getAndDecrement());
        assertEquals(0.0, ai.getAndDecrement());
        assertEquals(-1.0, ai.getAndDecrement());
    }

    /**
     * getAndIncrement returns previous value and increments
     */
    public void testGetAndIncrement() {
        AtomicDouble ai = new AtomicDouble(1);
        assertEquals(1.0, ai.getAndIncrement());
        assertEquals(2.0, ai.get());
        ai.set(-2);
        assertEquals(-2.0, ai.getAndIncrement());
        assertEquals(-1.0, ai.getAndIncrement());
        assertEquals(0.0, ai.getAndIncrement());
        assertEquals(1.0, ai.get());
    }

    /**
     * addAndGet adds given value to current, and returns current value
     */
    public void testAddAndGet() {
        AtomicDouble ai = new AtomicDouble(1);
        assertEquals(3.0, ai.addAndGet(2));
        assertEquals(3.0, ai.get());
        assertEquals(-1.0, ai.addAndGet(-4));
        assertEquals(-1.0, ai.get());
    }

    /**
     * decrementAndGet decrements and returns current value
     */
    public void testDecrementAndGet() {
        AtomicDouble ai = new AtomicDouble(1);
        assertEquals(0.0, ai.decrementAndGet());
        assertEquals(-1.0, ai.decrementAndGet());
        assertEquals(-2.0, ai.decrementAndGet());
        assertEquals(-2.0, ai.get());
    }

    /**
     * incrementAndGet increments and returns current value
     */
    public void testIncrementAndGet() {
        AtomicDouble ai = new AtomicDouble(1);
        assertEquals(2.0, ai.incrementAndGet());
        assertEquals(2.0, ai.get());
        ai.set(-2);
        assertEquals(-1.0, ai.incrementAndGet());
        assertEquals(0.0, ai.incrementAndGet());
        assertEquals(1.0, ai.incrementAndGet());
        assertEquals(1.0, ai.get());
    }

    /**
     * a deserialized serialized atomic holds same value
     */
    public void testSerialization() throws Exception {
        AtomicDouble l = new AtomicDouble();

        l.set(-22);
        ByteArrayOutputStream bout = new ByteArrayOutputStream(10000);
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
        out.writeObject(l);
        out.close();

        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(bin));
        AtomicDouble r = (AtomicDouble) in.readObject();
        assertEquals(l.get(), r.get());

    }

    /**
     * toString returns current value.
     */
    public void testToString() {
        AtomicDouble ai = new AtomicDouble();
        for (long i = -12; i < 6; ++i) {
            ai.set(i);
            assertEquals(ai.toString(), Double.toString(i));
        }
    }

    /**
     * intValue returns current value.
     */
    public void testIntValue() {
        AtomicDouble ai = new AtomicDouble();
        for (int i = -12; i < 6; ++i) {
            ai.set(i);
            assertEquals(i, ai.intValue());
        }
    }
    /**
     * longValue returns current value.
     */
    public void testLongValue() {
        AtomicDouble ai = new AtomicDouble();
        for (int i = -12; i < 6; ++i) {
            ai.set(i);
            assertEquals(i, ai.longValue());
        }
    }

    /**
     * floatValue returns current value.
     */
    public void testFloatValue() {
        AtomicDouble ai = new AtomicDouble();
        for (int i = -12; i < 6; ++i) {
            ai.set(i);
            assertEquals((float) i, ai.floatValue());
        }
    }

    /**
     * doubleValue returns current value.
     */
    public void testDoubleValue() {
        AtomicDouble ai = new AtomicDouble();
        for (int i = -12; i < 6; ++i) {
            ai.set(i);
            assertEquals((double) i, ai.doubleValue());
        }
    }

}
