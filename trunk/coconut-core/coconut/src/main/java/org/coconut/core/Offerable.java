/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

/**
 * An Offerable represents anything that you can put items into.
 * <p>
 * 
 * @see EventHandlers
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 * @version $Id$
 */
public interface Offerable<E> {

    /**
     * Inserts the specified element into the underlying data structure, if
     * possible. Some implementations may impose insertion restrictions (for
     * example capacity bounds), which can fail to insert an element by
     * returning false. As a general rule this method is non-blocking, however,
     * this is not a strict requirement.
     * 
     * @param element
     *            the element to add.
     * @return <tt>true</tt> if it was possible to add the element to the data
     *         structure, else <tt>false</tt>
     * @throws NullPointerException
     *             if the specified element is <tt>null</tt>
     */
    boolean offer(E element);
}
