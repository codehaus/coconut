/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.management;

import javax.management.MBeanServer;

import org.coconut.management.ManagedGroup;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface CacheManagementService {

    // List getMBeans();, management groups,..

    /**
     * Returns the MBeanServer that is used for management.
     */
    MBeanServer getMBeanServer();

    // configuration allow modification
    // what about executableGroup
    ManagedGroup getGroup();
}
