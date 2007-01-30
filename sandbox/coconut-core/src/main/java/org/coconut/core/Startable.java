/*
 * Copyright 2004 - 2005 Kasper Nielsen <kasper@codehaus.org> Licensed under a
 * MIT compatible license, see LICENSE.txt or
 * http://coconut.codehaus.org/license for details.
 */

package coconut.core;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 */
public interface Startable {
    
    /**
     * Start this component. Called initially at the begin of the lifecycle. It can be called again after a stop.
     */
    void start();

    /**
     * Stop this component. Called near the end of the lifecycle. It can be called again after a further start. Implement
     * {@link Disposable} if you need a single call at the definite end of the lifecycle.
     */
    void stop();
}
