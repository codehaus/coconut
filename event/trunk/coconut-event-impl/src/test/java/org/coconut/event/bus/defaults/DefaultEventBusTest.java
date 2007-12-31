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

import org.coconut.event.bus.EventBus;
import org.coconut.event.bus.EventSubscription;
import org.coconut.operations.Predicates;
import org.coconut.operations.Procedures;
import org.coconut.operations.StringPredicates;
import org.coconut.operations.Ops.Procedure;
import org.coconut.test.SystemErrCatcher;
import org.coconut.test.TestUtil;
import org.coconut.test.throwables.RuntimeException1;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Ignore;
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

// @Test(expected = NullPointerException.class)
// public void constructorNPE() {
// new DefaultEventBus(null);
// }

    @Test
    public void subscribeSpecial() {
        String name = bus.subscribe(Procedures.NOOP).getName();
        bus = new DefaultEventBus<String>();
        bus.subscribe(Procedures.NOOP, Predicates.TRUE, name);
        assertFalse(bus.subscribe(Procedures.NOOP).getName().equals(name));
    }

    @Test
    public void exceptionHandling() {
        bus = new DefaultEventBus<String>();
        bus.subscribe(new Procedure() {
            public void apply(Object t) {
                throw new RuntimeException1();
            }
        });
        SystemErrCatcher sec = SystemErrCatcher.get();
        try {
            bus.apply("foo");
        } finally {
            sec.terminate();
        }
        assertTrue(sec.toString().contains(RuntimeException1.class.getName()));
    }

    @Test
    public void subscribe() {
        Procedure ep = TestUtil.dummy(Procedure.class);
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
        Procedure ep = TestUtil.dummy(Procedure.class);
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
        Procedure ep = TestUtil.dummy(Procedure.class);
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
        bus.subscribe(TestUtil.dummy(Procedure.class), Predicates.FALSE, "foo");
        bus.subscribe(TestUtil.dummy(Procedure.class), Predicates.FALSE, "foo");
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
        bus.subscribe(TestUtil.dummy(Procedure.class), null);
    }

    @Test(expected = NullPointerException.class)
    public void subscribe3NPE1() {
        bus.subscribe(null, Predicates.TRUE, "foo");
    }

    @Test(expected = NullPointerException.class)
    public void subscribe3NPE2() {
        bus.subscribe(TestUtil.dummy(Procedure.class), null, "foo");
    }

    @Test(expected = NullPointerException.class)
    public void subscribe3NPE3() {
        bus.subscribe(TestUtil.dummy(Procedure.class), Predicates.TRUE, null);
    }

//    @Test(expected = NullPointerException.class)
//    public void offerNPE() {
//        bus.offer(null);
//    }

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
        bus.apply(null);
    }

    @Ignore
    @Test
    public void offer() {
        final Procedure ep1 = context.mock(Procedure.class);
        final Procedure ep2 = context.mock(Procedure.class);
        context.checking(new Expectations() {
            {
                one(ep1).apply("foob");
                one(ep2).apply("foob");
                one(ep1).apply("fof");
            }
        });
        bus.subscribe(ep1, StringPredicates.startsWith("fo"));
        bus.subscribe(ep2, StringPredicates.startsWith("foo"));
        assertEquals(2, bus.getSubscribers().size());
     //   assertTrue(bus.offer("foob"));
  //      assertTrue(bus.offer("fof"));
    }

    @Test
    public void process() {
        final Procedure ep1 = context.mock(Procedure.class);
        final Procedure ep2 = context.mock(Procedure.class);
        context.checking(new Expectations() {
            {
                one(ep1).apply("foob");
                one(ep2).apply("foob");
                one(ep1).apply("fof");
            }
        });
        bus.subscribe(ep1, StringPredicates.startsWith("fo"));
        bus.subscribe(ep2, StringPredicates.startsWith("foo"));
        assertEquals(2, bus.getSubscribers().size());
        bus.apply("foob");
        bus.apply("fof");
       // bus.offer("fof");
    }

    @Test
    public void offerAll() {
        final Procedure ep1 = context.mock(Procedure.class);
        final Procedure ep2 = context.mock(Procedure.class);
        context.checking(new Expectations() {
            {
                one(ep1).apply("foob");
                one(ep2).apply("foob");
                one(ep1).apply("fof");
            }
        });
        bus.subscribe(ep1, StringPredicates.startsWith("fo"));
        bus.subscribe(ep2, StringPredicates.startsWith("foo"));
        assertEquals(2, bus.getSubscribers().size());
        assertTrue(bus.offerAll(Arrays.asList("foob", "fof")));
    }

    @Test
    public void unsubscribe() {
        final Procedure ep1 = context.mock(Procedure.class);
        final Procedure ep2 = context.mock(Procedure.class);
        final Procedure ep3 = context.mock(Procedure.class);
        context.checking(new Expectations() {
            {
                one(ep1).apply("foo");
                one(ep2).apply("foo");
                one(ep3).apply("foo");
                one(ep1).apply("fooo");
                one(ep2).apply("fooo");
            }
        });
        bus.subscribe(ep1);
        bus.subscribe(ep2);
        EventSubscription<String> es3 = bus.subscribe(ep3);
        assertEquals(3, bus.getSubscribers().size());
        bus.apply("foo");

        es3.unsubscribe();
        assertFalse(es3.isValid());
        assertEquals(2, bus.getSubscribers().size());
        bus.apply("fooo");
        bus.unsubscribeAll();
        bus.apply("foooo");
    }

// @Test
// public void noReentrent() {
// bus = new DefaultEventBus<String>((EventBusConfiguration)
// EventBusConfiguration.create()
// .setCheckReentrant(true));
// bus.subscribe(new EventProcessor<String>() {
// public void process(String event) {
// bus.offer(event);
// }
// });
// bus.offer("foo");
// }
}
