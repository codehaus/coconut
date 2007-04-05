/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Queue;

import org.coconut.core.EventProcessor;
import org.coconut.core.EventUtils;
import org.coconut.core.Offerable;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@SuppressWarnings("unchecked")
@RunWith(JMock.class)
public class EventUtilsTest {
    Mockery context = new JUnit4Mockery();

    @Test
    public void testToHandler() {
        final Offerable subscriber = context.mock(Offerable.class);
        context.checking(new Expectations() {
            {
                one(subscriber).offer(0);
            }
        });
        EventProcessor eh = EventUtils.fromOfferable(subscriber);
        eh.process(0);
    }

    @Test
    public void testToHandlerFromQueue() {
        final Queue q = context.mock(Queue.class);
        context.checking(new Expectations() {
            {
                one(q).offer(0);
            }
        });
        EventProcessor eh = EventUtils.fromQueue(q);
        eh.process(0);
    }

    @Test(expected = NullPointerException.class)
    public void testToOfferableNPE() {
        EventUtils.toOfferable(null);
    }

    @Test
    public void testToOfferable() {
        final EventProcessor eh = context.mock(EventProcessor.class);
        context.checking(new Expectations() {
            {
                one(eh).process(0);
            }
        });
        Offerable o = EventUtils.toOfferable(eh);
        assertTrue(o.offer(0));
    }

    @Test
    public void testToOfferableSafe() {
        final EventProcessor eh = context.mock(EventProcessor.class);
        context.checking(new Expectations() {
            {
                one(eh).process(0);
            }
        });
        Offerable o = EventUtils.toOfferableSafe(eh);
        assertTrue(o.offer(0));
    }

    @Test
    public void testToOfferableErroneous() {
        final EventProcessor eh = context.mock(EventProcessor.class);
        context.checking(new Expectations() {
            {
                one(eh).process(0);
                will(throwException(new IllegalArgumentException()));
            }
        });
        Offerable o = EventUtils.toOfferableSafe(eh);
        assertFalse(o.offer(0));
    }
    // TODO check serializeable

}
