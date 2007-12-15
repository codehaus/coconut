/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import static org.coconut.test.TestUtil.assertIsSerializable;
import static org.coconut.test.TestUtil.assertNotSerializable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.coconut.test.SystemErrOutHelper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests {@link EventUtils}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
@RunWith(JMock.class)
public class EventUtilsTest {
    Mockery context = new JUnit4Mockery();

    @Test
    public void dummyEventProcessor() {
        EventUtils.dummyEventProcessor().process(null);
        assertIsSerializable(EventUtils.dummyEventProcessor());
    }

    @Test
    public void dummyOfferableFalse() {
        assertFalse(EventUtils.dummyOfferableFalse().offer(this));
        assertIsSerializable(EventUtils.dummyOfferableFalse());
    }

    @Test
    public void dummyOfferableTrue() {
        assertTrue(EventUtils.dummyOfferableTrue().offer(this));
        assertIsSerializable(EventUtils.dummyOfferableTrue());
    }

    @Test
    public void fromOfferable() {
        final Offerable subscriber = context.mock(Offerable.class);
        context.checking(new Expectations() {
            {
                one(subscriber).offer(0);
            }
        });
        EventProcessor processor = EventUtils.fromOfferable(subscriber);
        processor.process(0);
        assertNotSerializable(processor);
        assertIsSerializable(EventUtils.fromOfferable(EventUtils.dummyOfferableFalse()));
    }

    @Test(expected = NullPointerException.class)
    public void fromOfferableNPE() {
        EventUtils.fromOfferable((Offerable) null);
    }

    @Test
    public void fromQueue() {
        final Queue q = context.mock(Queue.class);
        context.checking(new Expectations() {
            {
                one(q).offer(0);
            }
        });
        EventProcessor processor = EventUtils.fromQueue(q);
        processor.process(0);
        assertNotSerializable(processor);
        assertIsSerializable(EventUtils.fromQueue(new ArrayBlockingQueue(1)));

    }

    @Test(expected = NullPointerException.class)
    public void fromQueueNPE() {
        EventUtils.fromQueue(null);
    }

    @Test
    public void toSystemOut() {
        SystemErrOutHelper str = SystemErrOutHelper.get();
        try {
            EventProcessor eh = EventUtils.toSystemOut();
            eh.process(234);
            assertEquals("234\r\n", str.getFromLast(0));
        } finally {
            str.terminate();
        }
    }

    @Test
    public void toSystemOutSafe() {
        SystemErrOutHelper str = SystemErrOutHelper.get();
        try {
            EventProcessor eh = EventUtils.toSystemOutSafe();
            eh.process(234);
            assertEquals("234\r\n", str.getFromLast(0));
        } finally {
            str.terminate();
        }
    }

    @Test
    public void toPrintStream() {
        toSystemOut();// hack
    }

    @Test
    public void toPrintStreamSafe() {
        toSystemOutSafe();// hack
    }

    @Test(expected = NullPointerException.class)
    public void toPrintStreamNPE() {
        EventUtils.toPrintStream(null);
    }

    @Test
    public void toOfferable() {
        final EventProcessor eh = context.mock(EventProcessor.class);
        context.checking(new Expectations() {
            {
                one(eh).process(0);
            }
        });
        Offerable o = EventUtils.toOfferable(eh);
        assertTrue(o.offer(0));
        assertNotSerializable(o);
        assertIsSerializable(EventUtils.toOfferable(EventUtils.dummyEventProcessor()));
    }

    @Test(expected = NullPointerException.class)
    public void toOfferableNPE() {
        EventUtils.toOfferable(null);
    }

    @Test
    public void toOfferableSafe() {
        final EventProcessor eh = context.mock(EventProcessor.class);
        context.checking(new Expectations() {
            {
                one(eh).process(0);
                one(eh).process(1);
                will(throwException(new IllegalArgumentException()));
            }
        });
        Offerable o = EventUtils.toOfferableSafe(eh);
        assertTrue(o.offer(0));
        assertFalse(o.offer(1));
        assertNotSerializable(o);
        assertIsSerializable(EventUtils.toOfferableSafe(EventUtils.dummyEventProcessor()));

    }

    @Test(expected = NullPointerException.class)
    public void toOfferableSafeNPE() {
        EventUtils.toOfferableSafe(null);
    }

    @Test(expected = NullPointerException.class)
    public void toPrintStreamSafeNPE() {
        EventUtils.toPrintStreamSafe(null);
    }

}
