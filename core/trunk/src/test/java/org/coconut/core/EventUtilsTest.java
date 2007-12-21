/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import static org.coconut.test.TestUtil.assertIsSerializable;
import static org.coconut.test.TestUtil.assertNotSerializable;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.coconut.operations.Ops.Procedure;
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
        EventUtils.dummyEventProcessor().apply(null);
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
        Procedure processor = EventUtils.fromOfferable(subscriber);
        processor.apply(0);
        assertNotSerializable(processor);
        assertIsSerializable(EventUtils.fromOfferable(EventUtils.dummyOfferableFalse()));
    }

    @Test(expected = NullPointerException.class)
    public void fromOfferableNPE() {
        EventUtils.fromOfferable((Offerable) null);
    }



    @Test
    public void toOfferable() {
        final Procedure eh = context.mock(Procedure.class);
        context.checking(new Expectations() {
            {
                one(eh).apply(0);
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
        final Procedure eh = context.mock(Procedure.class);
        context.checking(new Expectations() {
            {
                one(eh).apply(0);
                one(eh).apply(1);
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


}
