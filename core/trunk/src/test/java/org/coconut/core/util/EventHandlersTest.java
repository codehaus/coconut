/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core.util;

import java.io.PrintStream;
import java.util.Queue;

import org.coconut.core.EventProcessor;
import org.coconut.core.Offerable;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@SuppressWarnings("unchecked")
public class EventHandlersTest extends MockTestCase {
//TODO check serializeable
    public void testToHandler() {
        Mock mock = mock(Offerable.class);
        mock.expects(once()).method("offer").with(eq(0))
                .will(returnValue(true));
        EventProcessor eh = EventUtils.fromOfferable((Offerable) mock.proxy());
        eh.process(0);
    }

    public void testToHandlerNull() {
        try {
            EventUtils.fromOfferable((Offerable) null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testToHandlerFromQueue() {
        Mock mock = mock(Queue.class);
        mock.expects(once()).method("offer").with(eq(0))
                .will(returnValue(true));
        EventProcessor<Integer> eh = EventUtils
                .fromQueue((Queue) mock.proxy());
        eh.process(0);
    }

    public void testToQueueNull() {
        try {
            EventUtils.fromQueue((Queue) null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testToOfferable() {
        Mock mock = mock(EventProcessor.class);
        mock.expects(once()).method("process").with(eq(0));
        Offerable<Integer> eh = EventUtils.toOfferable((EventProcessor) mock
                .proxy());
        assertTrue(eh.offer(0));
    }

    public void testToOfferableNull() {
        try {
            EventUtils.toOfferable(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testToOfferableSafe() {
        Mock mock = mock(EventProcessor.class);
        mock.expects(once()).method("process").with(eq(0));
        Offerable<Integer> eh = EventUtils
                .toOfferableSafe((EventProcessor) mock.proxy());
        assertTrue(eh.offer(0));
    }

    public void testToOfferableErroneous() {
        Mock mock = mock(EventProcessor.class);
        mock.expects(once()).method("process").with(eq(0)).will(
                throwException(new IllegalArgumentException()));
        Offerable<Integer> eh = EventUtils
                .toOfferableSafe((EventProcessor) mock.proxy());
        assertFalse(eh.offer(0));
    }

    public void testToOfferableSafeNull() {
        try {
            EventUtils.toOfferableSafe(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testIgnoreFalse() {
        assertFalse(EventUtils.ignoreFalse().offer(this));
    }

    public void testIgnoreTrue() {
        assertTrue(EventUtils.ignoreTrue().offer(this));
    }

    public void testIgnoreEventHandler() {
        EventProcessor<?> e = EventUtils.ignoreEventHandler();
        e.process(null); // ignore
    }

    public void testToPrintStream() {
        // we need a jmock that can take constructor parameters
        // PrintStreamTester tester=new PrintStreamTester()
        // Mock mock = mock(PrintStreamTester.class);
        // mock.expects(once()).method("println").with(eq("1"));
        // EventHandlers.toPrintStream((PrintStream) mock.proxy());
    }

    public void testToPrintStreamNull() {
        try {
            EventUtils.toPrintStream(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testPrintToSafe() {
        try {
            EventUtils.toPrintStreamSafe(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testPrintToSystemOut() {
        PrintStream out = System.out;
        try {
            EventProcessor eh = EventUtils.toSystemOut();
            assertNotNull(eh);
        } finally {
            System.setOut(out);
        }
    }

    public void testPrintToSystemOutSafe() {
        EventProcessor eh = EventUtils.toSystemOutSafe();
        assertNotNull(eh);
    }

    // class PrintStreamTester extends PrintStream {
    // final BlockingQueue q=new LinkedBlockingQueue();
    // public PrintStreamTester() {
    // super(new DummyOutputStream());
    // }
    // public void println(String x) {
    // q.add(x);
    // }
    // }
    // class DummyOutputStream extends OutputStream {
    // public void write(int b) throws IOException {
    // throw new UnsupportedOperationException();
    // }
    // }
}
