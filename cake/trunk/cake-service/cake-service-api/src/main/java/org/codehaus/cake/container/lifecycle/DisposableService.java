/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.container.lifecycle;


/**
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheLifecycle.java 511 2007-12-13 14:37:02Z kasper $
 */
public interface DisposableService {
    /**
     * Method invoked when the container has terminated. This method is invoked as the last method
     * in this lifecycle interface and is called when the container and all of it services has been
     * succesfully shutdown. This method is also called if the container failed to initialize or
     * start. But only if the service was succesfully initialized 
     * was run without failing).
     */
    void dispose();
}
