/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

/**
 * The typical usage of an <code>EventHandler</code> is for processing specific
 * events in an event-driven architecture.
 * <p>
 * Usage example. This class process events (numbers) by taking the square and
 * printing it to <tt>System.out</tt>.
 * 
 * <pre>
 * public class SquareHandler implements EventHandler&lt;Integer&gt; {
 *     public void handle(Integer n) {
 *         System.out.println(n * n);
 *     }
 * }
 * </pre>
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public interface EventHandler<E> {

    /**
     * Handles an event.
     * 
     * @param event
     *            The event that the EventHandler must process
     * @throws ClassCastException
     *             the class of the specified element prevents it from handled by
     *             this event-handler.
     * @throws NullPointerException
     *             if the specified element is null and this event-handler does
     *             not support null elements.
     * @throws IllegalArgumentException
     *             some aspect of this element prevents it from being
     *             handled by this event-handler.
     */
    void handle(E event);
}
