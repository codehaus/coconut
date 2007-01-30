/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.AssertionFailedError;

import org.coconut.core.Offerable;
import org.coconut.test.MockTestCase;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class AbstractEventBusTestCase extends MockTestCase {

    public Offerable createQueueOfferableOnce(final BlockingQueue<Object> queue) {
        return new Offerable() {
            private AtomicBoolean hasBeenCalled = new AtomicBoolean();

            public boolean offer(Object o) {
                if (hasBeenCalled.get()) {
                    (new Exception()).printStackTrace();
                    throw new AssertionFailedError(
                            "createQueueOfferableOnce called twice");
                }
                hasBeenCalled.getAndSet(true);
                queue.add(o);
                return true;
            }
        };
    }
}
