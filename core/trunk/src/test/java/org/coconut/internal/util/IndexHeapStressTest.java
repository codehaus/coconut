/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

/**
 * An indexed heap stress test.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class IndexHeapStressTest {

    private PriorityQueue<E> pq = new PriorityQueue<E>();

    private IndexedHeap<E> ih = new IndexedHeap<E>(4);

    private ArrayList<E> list = new ArrayList<E>();

    private Random rnd = new Random(8);

    // private Random rnd = new Random(14);
    private int debug = 2;

    private void setup() {
        list = new ArrayList<E>();
        ih = new IndexedHeap<E>(4);
        pq = new PriorityQueue<E>();
    }

    @Test
    @Ignore
    public void ff() {
        debug = 0;
        int i = 0;
        try {
            while (i < 10000) {
                rnd = new Random(i);
                setup();
                tt();
                i++;
            }
        } catch (AssertionError ae) {
            System.out.println(i);
            throw ae;
        }
    }

    // @Test
    public void tt() {
        if (debug > 1) {
            ih.print();
        }
        for (int i = 1; i < 500; i++) {
            if (rnd.nextDouble() < 0.8) {
                if (debug > 0) {
                    System.out.println("add");
                }
                add(i);
                if (debug > 1) {
                    ih.print();
                }
            }
            if (rnd.nextDouble() < 0.4) {
                if (debug > 0) {
                    System.out.println("remove");
                }
                removeRandom();
                if (debug > 1) {
                    ih.print();
                }
            }
            if (rnd.nextDouble() < 0.1) {
                if (debug > 0) {
                    System.out.println("poll");
                }
                 poll();
                if (debug > 1) {
                    ih.print();
                }
            }
        }
        check();
    }

    private boolean isError;

    private void poll() {
        if (pq.size() > 0) {
            E e = pq.poll();
            E e2 = ih.poll();
            assertEquals(e, e2);
            list.remove(e);
        }
    }

    private void removeRandom() {
        if (list.size() > 0) {
            if (isError) {

            }
            E e = list.remove(rnd.nextInt(list.size()));
            // System.out.println("removing " + e);
            assertTrue(pq.remove(e));
            assertEquals(e, ih.remove(e.index));
            isError = true;
        }
    }

    private void add(long value) {
        E e = new E(value);
        pq.add(e);
        e.index = ih.add(e, value);
        list.add(e);
    }

    public void check() {
        while (!pq.isEmpty()) {
            E ePQ = pq.poll();
            E eIH = ih.poll();
            // System.out.println(ePQ + ", " + eIH);
            assertEquals(ePQ, eIH);
        }
        assertTrue(pq.isEmpty());
        assertTrue(ih.isEmpty());
    }

    static class E implements Comparable<E> {
        public E(long value) {
            this.value = value;
        }

        public int index;

        public long value;

        public int compareTo(E o) {
            return Long.valueOf(value).compareTo(o.value);
        }

        @Override
        public String toString() {
            return "" + value + ", index " + index;
        }
    }
}
