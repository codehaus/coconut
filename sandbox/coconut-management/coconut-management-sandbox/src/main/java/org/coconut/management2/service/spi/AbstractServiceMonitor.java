/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management2.service.spi;

import org.coconut.core.EventProcessor;
import org.coconut.core.Log;
import org.coconut.core.util.Logs;
import org.coconut.management2.service.ServiceMonitor;
import org.coconut.management2.service.ServiceCheckLog.Entry;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractServiceMonitor<V> implements ServiceMonitor<V> {

    private volatile Log logger;

    /**
     * @see org.coconut.management2.service.ServiceMonitor#getLogger()
     */
    public Log getLogger() {
        // TODO Auto-generated method stub
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
    public AbstractServiceCheckerSession<V> createAndRun() {
        AbstractServiceCheckerSession<V> s = create();
        s.run();
        return s;
    }

    /**
     * @see org.coconut.management2.service.ServiceMonitor#create(org.coconut.core.EventHandler)
     */
    public AbstractServiceCheckerSession<V> createAndRun(EventProcessor<? super Entry> e) {
        AbstractServiceCheckerSession<V> s = create();
        s.getLog().setEventHandler(e);
        s.run();
        return s;
    }

    /**
     * @see org.coconut.management2.service.ServiceChecker#createAndRun()
     */
    public final AbstractServiceCheckerSession<V> create() {
        AbstractServiceCheckerSession<V> s = newSession();
        Log l = logger;
        if (l == null) {
            l = Logs.nullLog();
        }
        s.logger = l;
        return s;
    }

    protected abstract AbstractServiceCheckerSession<V> newSession();

    /**
     * @see org.coconut.management2.service.ServiceMonitor#create(org.coconut.core.EventHandler)
     */
    public AbstractServiceCheckerSession<V> create(EventProcessor<? super Entry> e) {
        AbstractServiceCheckerSession<V> s = create();
        s.getLog().setEventHandler(e);
        return s;
    }
}
