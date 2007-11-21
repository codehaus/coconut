/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.bus;

import org.coconut.predicate.matcher.PredicateMatcher;

/**
 * This class is the primary class used for representing the configuration of a cache. All
 * general-purpose <tt>EventBus</tt> implementation classes should have a constructor with
 * a single argument taking a EventBus.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: EventBusConfiguration.java 415 2007-11-09 08:25:23Z kasper $
 */
public class EventBusConfiguration<E> {

    private PredicateMatcher<?, E> filterMatcher;

    private boolean reentrant;

    public boolean getCheckReentrant() {
        return reentrant;
    }

    /**
     * @return the filterMatcher
     */
    public PredicateMatcher<?, E> getFilterMatcher() {
        return filterMatcher;
    }

    /**
     * Sets whether or not the eventbus should check that an EventBus listener does not
     * call the event bus.
     * 
     * @param check
     *            check reenterent
     */
    public EventBusConfiguration<E> setCheckReentrant(boolean check) {
        this.reentrant = check;
        return this;
    }

    /**
     * @param filterMatcher
     *            the filterMatcher to set
     */
    public EventBusConfiguration<E> setFilterMatcher(PredicateMatcher<?, E> filterMatcher) {
        this.filterMatcher = filterMatcher;
        return this;
    }

    /**
     * Creates a new EventBusConfiguration with default settings.
     * 
     * @return a new EventBusConfiguration with default settings
     * @param <E>
     *            the type of events accepted by the eventbus
     */
    public static <E> EventBusConfiguration<E> create() {
        return new EventBusConfiguration<E>();
    }
}
