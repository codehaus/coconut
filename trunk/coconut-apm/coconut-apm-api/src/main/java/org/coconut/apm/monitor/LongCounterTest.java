/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.apm.monitor;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LongCounterTest extends TestCase {

    public static void main(String[] args) throws InterruptedException {
        //LongCounter lc = LongCounter.newConcurrent("", "");
        LongCounter lc = LongCounter.newUnsynchronized("", "");
        for (int i = 0; i < 1000000; i++) {
            lc.addAndGet(i);
        }
        Thread.sleep(400);
        long start = System.nanoTime();
        lc.set(0);
        for (int i = 0; i < 100000000; i++) {
            lc.addAndGet(i);
        }
        long finish = System.nanoTime();
        System.out.println(lc);
        System.out.println((finish - start) / 100000000.0);
    }

    public void testUnsynchronized() {
        check(LongCounter.newUnsynchronized("foo", "voo"));
    }

    public void testConcurrent() {
        check(LongCounter.newConcurrent("foo", "voo"));
    }

    private void check(LongCounter lc) {
        assertEquals(0l, lc.get());
        lc.set(4);
        assertEquals(4l, lc.get());
        assertEquals(4d, lc.doubleValue());
        assertEquals(4f, lc.floatValue());
        assertEquals(4, lc.intValue());
        assertEquals(4l, lc.longValue());
        assertEquals((byte) 4, lc.byteValue());
        assertEquals((short) 4, lc.shortValue());
        assertEquals(3l, lc.decrementAndGet());

        assertEquals(3l, lc.getAndAdd(4));
        assertEquals(7l, lc.getAndDecrement());
        assertEquals(6l, lc.getAndIncrement());
        assertEquals(7l, lc.getAndSet(10));
        assertEquals(10l, lc.getLatest());
        assertEquals(10l, lc.getValue());
        assertEquals(11l, lc.incrementAndGet());
        assertEquals(21l, lc.addAndGet(10));
        lc.reset();
        assertEquals(0l, lc.get());
    }
}
