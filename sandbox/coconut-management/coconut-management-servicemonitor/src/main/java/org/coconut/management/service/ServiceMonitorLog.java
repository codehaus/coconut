/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.service;

import org.coconut.core.EventProcessor;
import org.coconut.core.Log;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ServiceMonitorLog extends Iterable<ServiceMonitorLog.Entry> {

    int size();

    // maybe we an offerable instead, return false=>don't add it to the log
    void setEventHandler(EventProcessor<? super ServiceMonitorLog.Entry> e);

    EventProcessor<? super ServiceMonitorLog.Entry> getEventHandler();

    interface Entry {
        /**
         * The number of nanoseconds relative to the start of the check.
         */
        long getTimestamp();

        Log.Level getLevel();

        String getMessage();
    }
}
