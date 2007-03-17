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
public class EventUtilsTest extends MockTestCase {
//TODO check serializeable
    public void testToHandler() {
        Mock mock = mock(Offerable.class);
        mock.expects(once()).method("offer").with(eq(0))
                .will(returnValue(true));
        EventProcessor eh = EventUtils.fromOfferable((Offerable) mock.proxy());
        eh.process(0);
    }

    public void testToHandlerFromQueue() {
        Mock mock = mock(Queue.class);
        mock.expects(once()).method("offer").with(eq(0))
                .will(returnValue(true));
        EventProcessor<Integer> eh = EventUtils
                .fromQueue((Queue) mock.proxy());
        eh.process(0);
    }

    public void testToOfferable() {
        Mock mock = mock(EventProcessor.class);
        mock.expects(once()).method("process").with(eq(0));
        Offerable<Integer> eh = EventUtils.toOfferable((EventProcessor) mock
                .proxy());
        assertTrue(eh.offer(0));
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
}
