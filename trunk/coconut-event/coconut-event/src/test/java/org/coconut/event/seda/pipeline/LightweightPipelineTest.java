/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.seda.pipeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.coconut.core.Transformer;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LightweightPipelineTest extends TestCase {

    public void testInitialSettings() {
        LightweightPipeline lp = new LightweightPipeline();
        assertNull(lp.getInputQueue());
        assertNull(lp.getOutputQueue());
        assertEquals(0, lp.getLength());
        assertFalse(lp.isShutdown());
        assertFalse(lp.isTerminated());
    }

    public void testStartFailsWithoutAnyStagesConfigured() {
        LightweightPipeline lp = new LightweightPipeline();
        try {
            lp.start();
            fail("should throw");
        } catch (IllegalStateException ignoree) {
        }
    }

    public void testSimple() throws InterruptedException {
        BlockingQueue<Integer> bq = new LinkedBlockingQueue<Integer>();
        LightweightPipeline lp = new LightweightPipeline(true, bq);
        lp.addStage("Stage1", new AddTransformer());
        lp.addStage("Stage2", new AddTransformer());

        BlockingQueue<Integer> input = (BlockingQueue) lp.getInputQueue();
        HashSet<Integer> result = new HashSet<Integer>();
        assertNotNull(input);
        lp.start();
        for (int i = 0; i < 100; i++) {
            assertTrue(input.offer(i, 10, TimeUnit.SECONDS));
            result.add(i);
        }
        lp.shutdown();
        assertTrue(lp.isShutdown());
        lp.awaitTermination(30, TimeUnit.SECONDS);
        assertTrue(lp.isTerminated());
        assertEquals(100, bq.size());
        transformCollection(result, new AddTransformer());
        transformCollection(result, new AddTransformer());

        assertEquals(new TreeSet(result), new TreeSet(bq));
    }

    static <T> void transformCollection(Collection<T> c, Transformer<T, T> t) {
        ArrayList<T> tmp = new ArrayList<T>();
        for (Iterator<? extends T> iter = c.iterator(); iter.hasNext();) {
            T element = (T) iter.next();
            iter.remove();
            tmp.add(t.transform(element));
        }
        for (T tt : tmp) {
            c.add(tt);
        }
    }

    static <F, T> void transformCollection(Collection<T> c, Collection<F> result,
            Transformer<F, T> t) {
        for (Iterator<? extends F> iter = result.iterator(); iter.hasNext();) {
            F element = (F) iter.next();
            iter.remove();
            c.add(t.transform(element));
        }
    }

    static class AddTransformer implements Transformer<Integer, Integer> {

        /**
         * @see org.coconut.core.Transformer#transform(java.lang.Object)
         */
        public Integer transform(Integer from) {
            return from + 1;
        }

    }
}
