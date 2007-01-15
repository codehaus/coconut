/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.sla2;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ServiceMonitor<T extends Enum> {
    /**
     * Should throw an exception if the status could not be read
     * 
     * @return the current
     * @throws Exception
     */
    ServiceMonitorStatus<T> updateStatus();

    /**
     * The last time
     * 
     * @return
     */
    long getLastUpdateTime();

    ServiceMonitorStatus<T> lastUpdate();
}
