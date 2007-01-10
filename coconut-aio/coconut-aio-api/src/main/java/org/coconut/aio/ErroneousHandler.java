/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import org.coconut.core.EventHandler;

/**
 * Extend EventHandler with a method for handling exceptions and errors.
 * TODO remove this.
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface ErroneousHandler<T> extends EventHandler<T> {
    
    /**
     * @param element the element that was attempted to be handled.
     * @param cause the cause of the failure.
     */
    void handleFailed(T element, Throwable cause);
}
