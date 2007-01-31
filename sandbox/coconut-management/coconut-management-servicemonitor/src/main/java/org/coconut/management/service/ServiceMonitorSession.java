/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.service;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ServiceMonitorSession<V> extends Future<V> {

    ServiceMonitorLog getLog();

    long getDuration(TimeUnit unit);

    /**
     * Returns the status of this check.
     * <p>
     * If this check has not been started or it has been cancelled
     * {@link ServiceStatus.Unknown} is returned.
     * 
     * @return the status of this check
     */
    ServiceMonitorStatus getStatus();
}
