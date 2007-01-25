/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;

import javax.management.JMException;
import javax.management.ObjectName;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface JmxRegistrant {
    String getDomain();

    ObjectName getName(ManagedGroup mg) throws JMException;

    void registerChild(ManagedGroup mg) throws JMException;
}
