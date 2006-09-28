package org.coconut.event.bus.defaults;

import static org.coconut.filter.LogicFilters.TRUE;

import org.coconut.core.EventHandler;
import org.coconut.event.bus.AbstractEventBusTestCase;
import org.coconut.event.bus.EventBus;
import org.coconut.event.bus.EventSubscription;
import org.coconut.filter.Filters;
import org.jmock.Mock;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class EventBusTest extends AbstractEventBusTestCase {

    private static EventHandler<Number> trueOfferable = new EventHandler<Number>() {
        public void handle(Number element) {
        }
    };

    private static EventHandler<Number> otherEventHandler = new EventHandler<Number>() {
        public void handle(Number element) {
        }
    };

    protected EventBus<Number> createNew() {
        return new DefaultEventBus<Number>();
    }

    @SuppressWarnings("unchecked")
    public void testSubcribe1FilterNull() {
        EventBus<Number> bus = createNew();
        try {
            bus.subscribe(null, TRUE);
        } catch (NullPointerException npe) {
            return;
        }
        fail("Did not throw NullPointerException");
    }

    public void testSubcribe1EventHandlerNull() {
        EventBus<Number> bus = createNew();
        try {
            bus.subscribe(trueOfferable, null);
        } catch (NullPointerException npe) {
            return;
        }
        fail("Did not throw NullPointerException");
    }

    @SuppressWarnings("unchecked")
    public void testSubcribe2FilterNull() {
        EventBus<Number> bus = createNew();
        try {
            bus.subscribe(null, TRUE, "s");
        } catch (NullPointerException npe) {
            return;
        }
        fail("Did not throw NullPointerException");
    }

    public void testSubcribe2EventHandlerNull() {
        EventBus<Number> bus = createNew();
        try {
            bus.subscribe(trueOfferable, null, "s");
        } catch (NullPointerException npe) {
            return;
        }
        fail("Did not throw NullPointerException");
    }

    @SuppressWarnings("unchecked")
    public void testSubcribe2NameNull() {
        EventBus<Number> bus = createNew();
        try {
            bus.subscribe(trueOfferable, TRUE, null);
        } catch (NullPointerException npe) {
            return;
        }
        fail("Did not throw NullPointerException");
    }

    @SuppressWarnings("unchecked")
    public void testSubcribe() {
        EventBus<Number> bus = createNew();
        assertEquals(0, bus.getSubscribers().size());

        EventSubscription s = bus.subscribe(trueOfferable, Filters
                .isType(Integer.class));
        assertEquals(1, bus.getSubscribers().size());
        assertTrue(bus.getSubscribers().contains(s));

        EventSubscription s2 = bus.subscribe(trueOfferable, Filters
                .isType(Long.class));
        assertEquals(2, bus.getSubscribers().size());
        assertTrue(bus.getSubscribers().contains(s2));
    }

    @SuppressWarnings("unchecked")
    public void testUnsubscribe() {
        EventBus<Number> bus = createNew();
        EventSubscription s = bus.subscribe(trueOfferable, Filters
                .isType(Integer.class));
        EventSubscription s1 = bus.subscribe(otherEventHandler, Filters
                .isType(Integer.class));

        s1.cancel();
        assertFalse(bus.getSubscribers().contains(s1));
        assertEquals(1, bus.getSubscribers().size());

        s.cancel();
        assertFalse(bus.getSubscribers().contains(s));
        assertEquals(0, bus.getSubscribers().size());
    }

    @SuppressWarnings("unchecked")
    public void testUnsubscribeAll() {
        EventBus<Number> bus = createNew();
        bus.subscribe(trueOfferable, Filters.isType(Integer.class));
        bus.subscribe(otherEventHandler, Filters.isType(Integer.class));
        assertEquals(2, bus.getSubscribers().size());
        bus.unsubscribeAll();
        assertEquals(0, bus.getSubscribers().size());
    }

    @SuppressWarnings("unchecked")
    public void testOffer() {
        EventBus<Number> bus = createNew();
        Mock mock = mock(EventHandler.class);
        mock.expects(once()).method("handle").with(eq(0));
        mock.expects(once()).method("handle").with(eq(1));
        bus.subscribe((EventHandler<? super Number>) mock.proxy(), Filters
                .isType(Integer.class));

        assertTrue(bus.offer(0));
        assertTrue(bus.offer(1));
    }

    @SuppressWarnings("unchecked")
    public void testOfferNoSubscription() {
        EventBus<Number> bus = createNew();
        Mock mock = mock(EventHandler.class);
        mock.expects(once()).method("handle").with(eq(0));
        bus.subscribe((EventHandler<? super Number>) mock
                .proxy(), Filters.isType(Integer.class));
        assertTrue(bus.offer(0.6));
        assertTrue(bus.offer(0));
        assertTrue(bus.offer(1.8));
    }

    @SuppressWarnings("unchecked")
    public void testOffer3Subscribers() {

        EventBus<Number> bus = createNew();

        Mock mock = mock(EventHandler.class);
        mock.expects(once()).method("handle").with(eq(0));
        mock.expects(once()).method("handle").with(eq(1));
        bus.subscribe((EventHandler<? super Number>) mock.proxy(), Filters
                .isType(Integer.class));

        Mock mock1 = mock(EventHandler.class);
        mock1.expects(once()).method("handle").with(eq(0.5));
        bus.subscribe((EventHandler<? super Number>) mock1.proxy(), Filters
                .isType(Double.class));

        Mock mock2 = mock(EventHandler.class);
        mock2.expects(once()).method("handle").with(eq(0));
        mock2.expects(once()).method("handle").with(eq(0.5));
        mock2.expects(once()).method("handle").with(eq(1));
        bus.subscribe((EventHandler<? super Number>) mock2.proxy(), Filters
                .isType(Number.class));

        assertTrue(bus.offer(0));
        assertTrue(bus.offer(0.5));
        assertTrue(bus.offer(1));
    }

    @SuppressWarnings("unchecked")
    public void testOfferNull() {
        EventBus<Number> bus = createNew();
        bus.subscribe(trueOfferable, Filters.isType(Integer.class)); // dummy
        try {
            bus.offer(null);
        } catch (NullPointerException npe) {
            return;
        }
        fail("did not throw NullPointerException");
    }

    @SuppressWarnings("unchecked")
    public void testOfferMany() {
        EventBus<Number> bus = createNew();
        Mock mock = mock(EventHandler.class);
        mock.expects(once()).method("handle").with(eq(0));
        mock.expects(once()).method("handle").with(eq(1));
        mock.expects(once()).method("handle").with(eq(2));
        bus.subscribe((EventHandler<? super Number>) mock
                .proxy(), Filters.isType(Integer.class));

        assertTrue(bus.offerAll(0));
        assertTrue(bus.offerAll(1, 2));
    }

    @SuppressWarnings("unchecked")
    public void testOfferManyNull() {
        EventBus<Number> bus = createNew();
        bus.subscribe(trueOfferable, Filters.isType(Integer.class)); // dummy
        try {
            bus.offerAll(null, null);
        } catch (NullPointerException npe) {
            return;
        }
        fail("did not throw NullPointerException");
    }
}
