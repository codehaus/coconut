/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.management;

import org.coconut.management.ManagedGroup;

/**
 * This is the main interface for controlling the remote management of a cache at runtime.
 * <p>
 * Currently, you cannot do much with it.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheManagementService {

    /**
     * Returns the root ManagedGroup.
     * 
     * @return the root ManagedGroup
     */
    ManagedGroup getRoot();
}
