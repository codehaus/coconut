/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.bus.defaults;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;

import org.coconut.core.EventProcessor;
import org.coconut.event.bus.EventBus;
import org.coconut.event.bus.EventSubscription;
import org.coconut.operations.Predicates;
import org.coconut.operations.StringPredicates;
import org.coconut.test.TestUtil;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the {@link  DefaultEventBus} class.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheConfigurationTest.java 475 2007-11-20 17:22:26Z kasper $
 */
@RunWith(JMock.class)
public class DefaultEventBusTest {
    Mockery context = new JUnit4Mockery();

    EventBus<String> bus;

    @Before
    public void setup() {
        bus = new DefaultEventBus<String>();
    }

//    @Test(expected = NullPointerException.class)
//    public void constructorNPE() {
//        new DefaultEventBus(null);
//    }

    @Test
    public void subscribe() {
        EventProcessor ep = TestUtil.dummy(EventProcessor.class);
        EventSubscription es = bus.subscribe(ep);
        assertNotNull(es);

        assertSame(ep, es.getEventProcessor());
        assertSame(Predicates.TRUE, es.getFilter());
        assertNotNull(es.getName());
        assertTrue(es.isValid());
        assertEquals(1, bus.getSubscribers().size());
        assertTrue(bus.getSubscribers().contains(es));
    }

    @Test
    public void subscribe2() {
        EventProcessor ep =TestUtil.dummy(EventProcessor.class);
        EventSubscription es = bus.subscribe(ep, Predicates.FALSE);
        assertNotNull(es);
        assertSame(ep, es.getEventProcessor());
        assertSame(Predicates.FALSE, es.getFilter());
        assertNotNull(es.getName());
        assertTrue(es.isValid());
        assertEquals(1, bus.getSubscribers().size());
        assertTrue(bus.getSubscribers().contains(es));
    }

    @Test
    public void subscribe3() {
        EventProcessor ep = TestUtil.dummy(EventProcessor.class);
        EventSubscription es = bus.subscribe(ep, Predicates.FALSE, "foo");
        assertNotNull(es);
        assertSame(ep, es.getEventProcessor());
        assertSame(Predicates.FALSE, es.getFilter());
        assertEquals("foo", es.getName());
        assertTrue(es.isValid());
        assertEquals(1, bus.getSubscribers().size());
        assertTrue(bus.getSubscribers().contains(es));
    }

    @Test(expected = IllegalArgumentException.class)
    public void subscribe3IAE() {
        bus.subscribe(TestUtil.dummy(EventProcessor.class), Predicates.FALSE, "foo");
        bus.subscribe(TestUtil.dummy(EventProcessor.class), Predicates.FALSE, "foo");
    }

    @Test(expected = NullPointerException.class)
    public void subscribe1NPE() {
        bus.subscribe(null);
    }

    @Test(expected = NullPointerException.class)
    public void subscribe2NPE1() {
        bus.subscribe(null, Predicates.TRUE);
    }

    @Test(expected = NullPointerException.class)
    public void subscribe2NPE2() {
        bus.subscribe(TestUtil.dummy(EventProcessor.class), null);
    }

    @Test(expected = NullPointerException.class)
    public void subscribe3NPE1() {
        bus.subscribe(null, Predicates.TRUE, "foo");
    }

    @Test(expected = NullPointerException.class)
    public void subscribe3NPE2() {
        bus.subscribe(TestUtil.dummy(EventProcessor.class), null, "foo");
    }

    @Test(expected = NullPointerException.class)
    public void subscribe3NPE3() {
        bus.subscribe(TestUtil.dummy(EventProcessor.class), Predicates.TRUE, null);
    }

    @Test(expected = NullPointerException.class)
    public void offerNPE() {
        bus.offer(null);
    }

    @Test(expected = NullPointerException.class)
    public void offerAllNPE() {
        bus.offerAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void offerAllNPE1() {
        bus.offerAll(Arrays.asList("1", null, "2"));
    }

    @Test(expected = NullPointerException.class)
    public void processNPE() {
        bus.process(null);
    }

    @Test
    public void offer() {
        final EventProcessor ep1 = context.mock(EventProcessor.class);
        final EventProcessor ep2 = context.mock(EventProcessor.class);
        context.checking(new Expectations() {
            {
                one(ep1).process("foob");
                one(ep2).process("foob");
                one(ep1).process("fof");
            }
        });
        bus.subscribe(ep1, StringPredicates.startsWith("fo"));
        bus.subscribe(ep2, StringPredicates.startsWith("foo"));
        assertEquals(2, bus.getSubscribers().size());
        assertTrue(bus.offer("foob"));
        assertTrue(bus.offer("fof"));
    }

    @Test
    public void process() {
        final EventProcessor ep1 = context.mock(EventProcessor.class);
        final EventProcessor ep2 = context.mock(EventProcessor.class);
        context.checking(new Expectations() {
            {
                one(ep1).process("foob");
                one(ep2).process("foob");
                one(ep1).process("fof");
            }
        });
        bus.subscribe(ep1, StringPredicates.startsWith("fo"));
        bus.subscribe(ep2, StringPredicates.startsWith("foo"));
        assertEquals(2, bus.getSubscribers().size());
        bus.process("foob");
        bus.offer("fof");
    }

    @Test
    public void offerAll() {
        final EventProcessor ep1 = context.mock(EventProcessor.class);
        final EventProcessor ep2 = context.mock(EventProcessor.class);
        context.checking(new Expectations() {
            {
                one(ep1).process("foob");
                one(ep2).process("foob");
                one(ep1).process("fof");
            }
        });
        bus.subscribe(ep1, StringPredicates.startsWith("fo"));
        bus.subscribe(ep2, StringPredicates.startsWith("foo"));
        assertEquals(2, bus.getSubscribers().size());
        assertTrue(bus.offerAll(Arrays.asList("foob", "fof")));
    }

    @Test
    public void unsubscribe() {
        final EventProcessor ep1 = context.mock(EventProcessor.class);
        final EventProcessor ep2 = context.mock(EventProcessor.class);
        final EventProcessor ep3 = context.mock(EventProcessor.class);
        context.checking(new Expectations() {
            {
                one(ep1).process("foo");
                one(ep2).process("foo");
                one(ep3).process("foo");
                one(ep1).process("fooo");
                one(ep2).process("fooo");
            }
        });
        bus.subscribe(ep1);
        bus.subscribe(ep2);
        EventSubscription<String> es3 = bus.subscribe(ep3);
        assertEquals(3, bus.getSubscribers().size());
        bus.process("foo");

        es3.unsubscribe();
        assertFalse(es3.isValid());
        assertEquals(2, bus.getSubscribers().size());
        bus.process("fooo");
        bus.unsubscribeAll();
        bus.process("foooo");
    }

//    @Test
//    public void noReentrent() {
//        bus = new DefaultEventBus<String>((EventBusConfiguration) EventBusConfiguration.create()
//                .setCheckReentrant(true));
//        bus.subscribe(new EventProcessor<String>() {
//            public void process(String event) {
//                bus.offer(event);
//            }
//        });
//        bus.offer("foo");
//    }
}
