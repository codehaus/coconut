/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;


/**
 * A mix-in style interface for marking objects that can be registered with a
 * {@link ManagedGroup}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface ManagedLifecycle {

    /**
     * Registers this object with the specified {@link ManagedGroup}.
     * 
     * @param parent
     *            the group to register with
     */
    void manage(ManagedGroup parent);
}
