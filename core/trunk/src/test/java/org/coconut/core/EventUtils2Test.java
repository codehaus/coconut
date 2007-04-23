/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Queue;

import org.coconut.core.LogsTest.InnerPrintStream;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@SuppressWarnings("unchecked")
public class EventUtils2Test {

    @Test(expected = NullPointerException.class)
    public void testToHandlerNull() {
        EventUtils.fromOfferable((Offerable) null);
    }

    @Test(expected = NullPointerException.class)
    public void testToQueueNull() {
        EventUtils.fromQueue((Queue) null);
    }

    @Test(expected = NullPointerException.class)
    public void testToOfferableNull() {
        EventUtils.toOfferable(null);
    }

    @Test(expected = NullPointerException.class)
    public void testToOfferableSafeNull() {
        EventUtils.toOfferableSafe(null);
    }

    @Test
    public void testIgnoreFalse() {
        assertFalse(EventUtils.ignoreFalse().offer(this));
    }

    @Test
    public void testIgnoreTrue() {
        assertTrue(EventUtils.ignoreTrue().offer(this));
    }

    @Test
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

    @Test(expected = NullPointerException.class)
    public void testToPrintStreamNull() {
        EventUtils.toPrintStream(null);
    }

    @Test(expected = NullPointerException.class)
    public void testPrintToSafe() {
        EventUtils.toPrintStreamSafe(null);
    }

    @Test
    public void testPrintToSystemOut() {
        InnerPrintStream str = InnerPrintStream.get();
        try {
            EventProcessor eh = EventUtils.toSystemOut();
            eh.process(234);
            assertEquals("234\r\n", str.getFromLast(0));
        } finally {
            str.terminate();
        }
    }
    @Test
    public void testPrintToSystemOutSafe() {
        InnerPrintStream str = InnerPrintStream.get();
        try {
            EventProcessor eh = EventUtils.toSystemOutSafe();
            eh.process(234);
            assertEquals("234\r\n", str.getFromLast(0));
        } finally {
            str.terminate();
        }
    }

}
