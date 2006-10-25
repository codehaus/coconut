/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.spi;

import java.util.ArrayList;
import java.util.List;

import org.coconut.core.EventHandler;
import org.coconut.management.ApmObserver;
import org.coconut.management.monitor.TimedAverage;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractObservableApm<T extends AbstractObservableApm> extends AbstractApm
        implements ApmObserver<T> {

//    private final List<EventHandler<T>> dependent = new ArrayList<EventHandler<T>>();
//
//    /**
//     * @see org.coconut.metric.MetricHub#addEventHandler(org.coconut.core.EventHandler)
//     */
//    public synchronized EventHandler<? super TimedAverage> addEventHandler(
//            EventHandler<? super T> e) {
//        dependent.add(e);
//        return e;
//    }
//
//    /**
//     * @see org.coconut.metric.MetricHub#getEventHandlers()
//     */
//    public synchronized List<EventHandler<? super T>> getEventHandlers() {
//        return new ArrayList<EventHandler<? super T>>(dependent);
//    }
//
//    protected void update() {
//        if (dependent.size() > 0) {
//            for (EventHandler<? super TimedAverage> e : dependent) {
//                e.handle(this);
//            }
//        }
//    }
}
