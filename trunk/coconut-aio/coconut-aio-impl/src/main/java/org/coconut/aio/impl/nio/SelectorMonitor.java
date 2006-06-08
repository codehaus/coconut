package org.coconut.aio.impl.nio;

/**
 * A monitor used for dealing with a selector. The default implementation does
 * nothing.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class SelectorMonitor {
    /**
     * Opens a new Selector
     * 
     * @param throwable
     *            a throwable is provided if opening a new selector resulted in
     *            an error
     */
    public void opened(Throwable throwable) {
    }

    /**
     * Called before sleeping in a select
     * 
     * @param If
     *            positive, block for up to timeout milliseconds, more or less,
     *            while waiting for a channel to become ready; if zero, block
     *            indefinitely; if negative do not block
     */
    public void preSelect(int timeout) {
    }

    /**
     * Called after a select
     * 
     * @param count
     *            the number of channels that are ready
     * @param throwable
     *            any exception that was thrown while selecting
     */
    public void postSelect(int count, Throwable throwable) {

    }

    /**
     * The selector was explicitly woken.
     */
    public void wakeup() {
    }

    /**
     * The selector was closed
     * 
     * @param throwable
     *            any exception that caused the closing.
     */
    public void closed(Throwable throwable) {
    }
}