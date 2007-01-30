/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.aio;

import org.coconut.core.EventProcessor;

/**
 * Extend EventHandler with a method for handling exceptions and errors.
 * TODO remove this.
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface ErroneousHandler<T> extends EventProcessor<T> {
    
    /**
     * @param element the element that was attempted to be handled.
     * @param cause the cause of the failure.
     */
    void handleFailed(T element, Throwable cause);
}
