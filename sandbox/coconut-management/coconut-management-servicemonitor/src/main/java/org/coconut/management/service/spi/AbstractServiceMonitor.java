/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.service.spi;

import org.coconut.core.EventProcessor;
import org.coconut.core.Log;
import org.coconut.core.util.Logs;
import org.coconut.management.service.ServiceMonitor;
import org.coconut.management.service.ServiceMonitorLog.Entry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractServiceMonitor<V> implements ServiceMonitor<V> {

    /**
     * @see org.coconut.management.service.ServiceMonitor#getDescription()
     */
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.coconut.management.service.ServiceMonitor#getName()
     */
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    private volatile Log logger;

    /**
     * @see org.coconut.management2.service.ServiceMonitor#getLogger()
     */
    public Log getLogger() {
        return logger;
    }

    /**
     * @see org.coconut.management2.service.ServiceMonitor#setLogger(org.coconut.core.Log)
     */
    public void setLogger(Log logger) {
        this.logger = logger;
    }

    /**
     * @see org.coconut.management2.service.ServiceChecker#createAndRun()
     */
    public AbstractServiceMonitorSession<V> createAndRun() {
        AbstractServiceMonitorSession<V> s = newSession();
        s.run();
        return s;
    }

    /**
     * @see org.coconut.management2.service.ServiceMonitor#create(org.coconut.core.EventHandler)
     */
    public AbstractServiceMonitorSession<V> createAndRun(EventProcessor<? super Entry> e) {
        AbstractServiceMonitorSession<V> s = newSession();
        s.getLog().setEventHandler(e);
        s.run();
        return s;
    }

    /**
     * @see org.coconut.management2.service.ServiceChecker#createAndRun()
     */
    public final AbstractServiceMonitorSession<V> newSession() {
        AbstractServiceMonitorSession<V> s = createSession();
        Log l = logger;
        if (l == null) {
            l = Logs.nullLog();
        }
        s.logger = l;
        return s;
    }

    protected abstract AbstractServiceMonitorSession<V> createSession();

    /**
     * @see org.coconut.management2.service.ServiceMonitor#create(org.coconut.core.EventHandler)
     */
    public AbstractServiceMonitorSession<V> create(EventProcessor<? super Entry> e) {
        AbstractServiceMonitorSession<V> s = newSession();
        s.getLog().setEventHandler(e);
        return s;
    }
}
