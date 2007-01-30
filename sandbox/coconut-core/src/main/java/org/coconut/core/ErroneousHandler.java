/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package coconut.core;

/**
 * Bla bla bla
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ErroneousHandler.java,v 1.2 2005/01/19 16:25:10 kasper Exp $
 */
public interface ErroneousHandler<T> extends EventHandler<T>
{
    /**
     * @param element
     * @param cause
     */
    void handleFailed(T element, Throwable cause);
}
