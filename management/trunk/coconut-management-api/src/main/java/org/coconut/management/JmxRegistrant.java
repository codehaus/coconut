/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;

import javax.management.JMException;
import javax.management.ObjectName;

/**
 * This class is used to provide flexible naming when registering a big
 * hierarchy of ManagedGroups.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface JmxRegistrant {
    /**
     * Returns the name under which the ManagedGroups should be registered. For
     * example <code>com.mycompany.myproduct</code>
     */
    String getDomain();

    /**
     * @param mg
     * @return
     * @throws JMException
     */
    ObjectName getName(ManagedGroup mg) throws JMException;

    void registerChild(ManagedGroup mg) throws JMException;
}
