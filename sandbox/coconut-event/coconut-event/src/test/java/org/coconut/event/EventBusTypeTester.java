/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event;

import org.coconut.core.EventProcessor;
import org.coconut.event.impl.DefaultEventBus;
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

        EventProcessor<Integer> hi = null;
        EventProcessor<Number> hn = null;
        EventProcessor<Object> ho = null;
        EventProcessor hall = null;
        EventProcessor<String> hs = null;

        // bus.subscribe(hi, Filters.clazz(Integer.class));
        bus.subscribe(hn, Filters.isType(Integer.class));
        bus.subscribe(ho, Filters.isType(Integer.class));
        bus.subscribe(hall, Filters.isType(Integer.class));
        // bus.subscribe(hs, Filters.clazz(Integer.class));
    }

    public void testSubscribeFilter() {
        EventBus<Number> bus = createNew();

        EventProcessor<Object> o = null;
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
