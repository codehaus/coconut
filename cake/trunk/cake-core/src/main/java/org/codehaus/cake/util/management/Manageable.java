/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org> 
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.management;


/**
 * A mix-in style interface for marking objects that can be registered with a
 * {@link ManagedGroup}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: ManagedLifecycle.java 504 2007-12-05 17:49:24Z kasper $
 */
public interface Manageable {

    /**
     * Registers this object with the specified {@link ManagedGroup}.
     * 
     * @param parent
     *            the group to register with
     */
    void manage(ManagedGroup parent);
}
