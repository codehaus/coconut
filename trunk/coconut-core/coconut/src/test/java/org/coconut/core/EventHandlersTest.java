/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import java.io.PrintStream;
import java.util.Queue;

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
        EventHandler eh = EventHandlers.fromOfferable((Offerable) mock.proxy());
        eh.handle(0);
    }

    public void testToHandlerNull() {
        try {
            EventHandlers.fromOfferable((Offerable) null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testToHandlerFromQueue() {
        Mock mock = mock(Queue.class);
        mock.expects(once()).method("offer").with(eq(0))
                .will(returnValue(true));
        EventHandler<Integer> eh = EventHandlers
                .fromQueue((Queue) mock.proxy());
        eh.handle(0);
    }

    public void testToQueueNull() {
        try {
            EventHandlers.fromQueue((Queue) null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testToOfferable() {
        Mock mock = mock(EventHandler.class);
        mock.expects(once()).method("handle").with(eq(0));
        Offerable<Integer> eh = EventHandlers.toOfferable((EventHandler) mock
                .proxy());
        assertTrue(eh.offer(0));
    }

    public void testToOfferableNull() {
        try {
            EventHandlers.toOfferable(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testToOfferableSafe() {
        Mock mock = mock(EventHandler.class);
        mock.expects(once()).method("handle").with(eq(0));
        Offerable<Integer> eh = EventHandlers
                .toOfferableSafe((EventHandler) mock.proxy());
        assertTrue(eh.offer(0));
    }

    public void testToOfferableErroneous() {
        Mock mock = mock(EventHandler.class);
        mock.expects(once()).method("handle").with(eq(0)).will(
                throwException(new IllegalArgumentException()));
        Offerable<Integer> eh = EventHandlers
                .toOfferableSafe((EventHandler) mock.proxy());
        assertFalse(eh.offer(0));
    }

    public void testToOfferableSafeNull() {
        try {
            EventHandlers.toOfferableSafe(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testIgnoreFalse() {
        assertFalse(EventHandlers.ignoreFalse().offer(this));
    }

    public void testIgnoreTrue() {
        assertTrue(EventHandlers.ignoreTrue().offer(this));
    }

    public void testIgnoreEventHandler() {
        EventHandler<?> e = EventHandlers.ignoreEventHandler();
        e.handle(null); // ignore
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
            EventHandlers.toPrintStream(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testPrintToSafe() {
        try {
            EventHandlers.toPrintStreamSafe(null);
            fail("Did not fail with NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testPrintToSystemOut() {
        PrintStream out = System.out;
        try {
            EventHandler eh = EventHandlers.toSystemOut();
            assertNotNull(eh);
        } finally {
            System.setOut(out);
        }
    }

    public void testPrintToSystemOutSafe() {
        EventHandler eh = EventHandlers.toSystemOutSafe();
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
