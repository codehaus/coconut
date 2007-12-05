/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import java.util.Arrays;
import java.util.Collection;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.core.EventProcessor;
import org.coconut.event.bus.EventSubscription;
import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Predicate;
import org.coconut.test.MockTestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class EventServiceBus extends AbstractEventTestBundle {

    Mockery context = new JUnit4Mockery();

    @Before
    public void init() {
        c = newCache(newConf().event().setEnabled(true));
    }

    @Test
    public void subscribe() {
        final EventProcessor mock = context.mock(EventProcessor.class);
        context.checking(new Expectations() {
            {
                one(mock).process(new DefaultCacheEvent("a1"));
                one(mock).process(new DefaultCacheEvent("a2"));
                one(mock).process(new DefaultCacheEvent("a3"));
                one(mock).process(new DefaultCacheEvent("a4"));
            }
        });

        EventSubscription es = event().subscribe(mock);
        assertEquals(1, event().getSubscribers().size());
        assertTrue(event().getSubscribers().contains(es));
        assertSame(mock, es.getEventProcessor());
        assertNotNull(es.getFilter());
        assertNotNull(es.getName());
        assertTrue(es.isValid());

        assertTrue(event().offer(new DefaultCacheEvent("a1")));
        event().process(new DefaultCacheEvent("a2"));
        assertTrue(event().offerAll(
                (Collection) Arrays
                        .asList(new DefaultCacheEvent("a3"), new DefaultCacheEvent("a4"))));

        es.unsubscribe();
        assertEquals(0, event().getSubscribers().size());
        assertFalse(es.isValid());
        assertTrue(event().offer(new DefaultCacheEvent("a1")));
    }

    @Test
    public void subscribeFiltered() {
        final EventProcessor mock = context.mock(EventProcessor.class);
        context.checking(new Expectations() {
            {
                one(mock).process(new DefaultCacheEvent("a1"));
                one(mock).process(new DefaultCacheEvent("a2"));
                one(mock).process(new DefaultCacheEvent("a3"));
                one(mock).process(new DefaultCacheEvent("a4"));
            }
        });
        Predicate<CacheEvent<Integer, String>> p = new Predicate<CacheEvent<Integer, String>>() {
            public boolean evaluate(CacheEvent<Integer, String> element) {
                return element.getName().startsWith("a");
            }
        };
        EventSubscription es = event().subscribe(mock, p);

        assertEquals(1, event().getSubscribers().size());
        assertTrue(event().getSubscribers().contains(es));
        assertSame(mock, es.getEventProcessor());
        assertEquals(p, es.getFilter());
        assertNotNull(es.getName());
        assertTrue(es.isValid());

        assertTrue(event().offer(new DefaultCacheEvent("a1")));
        assertTrue(event().offer(new DefaultCacheEvent("b1")));
        event().process(new DefaultCacheEvent("b2"));
        event().process(new DefaultCacheEvent("a2"));
        assertTrue(event().offerAll(
                (Collection) Arrays.asList(new DefaultCacheEvent("a3"),
                        new DefaultCacheEvent("b3"), new DefaultCacheEvent("a4"),
                        new DefaultCacheEvent("b6"))));

        es.unsubscribe();
        assertEquals(0, event().getSubscribers().size());
        assertFalse(es.isValid());
        assertTrue(event().offer(new DefaultCacheEvent("a1")));
    }

    @Test
    public void subscribeFilteredNamed() {
        final EventProcessor mock = context.mock(EventProcessor.class);
        context.checking(new Expectations() {
            {
                one(mock).process(new DefaultCacheEvent("a1"));
                one(mock).process(new DefaultCacheEvent("a2"));
                one(mock).process(new DefaultCacheEvent("a3"));
                one(mock).process(new DefaultCacheEvent("a4"));
            }
        });

        Predicate<CacheEvent<Integer, String>> p = new Predicate<CacheEvent<Integer, String>>() {
            public boolean evaluate(CacheEvent<Integer, String> element) {
                return element.getName().startsWith("a");
            }
        };
        EventSubscription es = event().subscribe(mock, p, "fooName");

        assertEquals(1, event().getSubscribers().size());
        assertTrue(event().getSubscribers().contains(es));
        assertSame(mock, es.getEventProcessor());
        assertEquals(p, es.getFilter());
        assertEquals("fooName", es.getName());
        assertTrue(es.isValid());

        assertTrue(event().offer(new DefaultCacheEvent("a1")));
        assertTrue(event().offer(new DefaultCacheEvent("b1")));
        event().process(new DefaultCacheEvent("b2"));
        event().process(new DefaultCacheEvent("a2"));
        assertTrue(event().offerAll(
                (Collection) Arrays.asList(new DefaultCacheEvent("a3"),
                        new DefaultCacheEvent("b3"), new DefaultCacheEvent("a4"),
                        new DefaultCacheEvent("b6"))));

        es.unsubscribe();
        assertEquals(0, event().getSubscribers().size());
        assertFalse(es.isValid());
        assertTrue(event().offer(new DefaultCacheEvent("a1")));
    }

    @Test
    public void unsubscribeAll() {
        EventSubscription e1 = event().subscribe(MockTestCase.mockDummy(EventProcessor.class));
        EventSubscription e2 = event().subscribe(MockTestCase.mockDummy(EventProcessor.class),
                Predicates.TRUE);
        EventSubscription e3 = event().subscribe(MockTestCase.mockDummy(EventProcessor.class),
                Predicates.TRUE, "ddd");
        assertEquals(3, event().getSubscribers().size());
        assertTrue(event().getSubscribers().contains(e1));
        assertTrue(event().getSubscribers().contains(e2));
        assertTrue(event().getSubscribers().contains(e3));
        event().unsubscribeAll();
        assertEquals(0, event().getSubscribers().size());
    }

    static class DefaultCacheEvent<K, V> implements CacheEvent<K, V> {
        static Cache c = MockTestCase.mockDummy(Cache.class);

        String name;

        public DefaultCacheEvent(String name) {
            this.name = name;
        }

        public Cache<K, V> getCache() {
            return c;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            return name.equals(((CacheEvent) obj).getName());
        }
    }
}
