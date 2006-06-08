package org.coconut.event.bus.defaults;

import org.coconut.core.EventHandler;
import org.coconut.event.bus.AbstractEventBusTestCase;
import org.coconut.event.bus.EventBus;
import org.coconut.filter.Filter;
import org.coconut.filter.Filters;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@SuppressWarnings( { "unchecked", "unused" })
public class EventBusTypeTester extends AbstractEventBusTestCase {

    protected EventBus<Number> createNew() {
        return new DefaultEventBus<Number>();
    }

    public void testSubscribe() {
        EventBus<Number> bus = createNew();

        EventHandler<Integer> hi = null;
        EventHandler<Number> hn = null;
        EventHandler<Object> ho = null;
        EventHandler hall = null;
        EventHandler<String> hs = null;

        // bus.subscribe(hi, Filters.clazz(Integer.class));
        bus.subscribe(hn, Filters.isType(Integer.class));
        bus.subscribe(ho, Filters.isType(Integer.class));
        bus.subscribe(hall, Filters.isType(Integer.class));
        // bus.subscribe(hs, Filters.clazz(Integer.class));
    }

    public void testSubscribeFilter() {
        EventBus<Number> bus = createNew();

        EventHandler<Object> o = null;
        Filter<Integer> hi = null;
        Filter<Number> hn = null;
        Filter<Object> ho = null;
        Filter hall = null;
        Filter<String> hs = null;

        // bus.subscribe(o, hi); //shouldn't work
        bus.subscribe(o, hn);
        bus.subscribe(o, ho);
        bus.subscribe(o, hall);
        // bus.subscribe(o, hs); //shouldn't work
    }

}