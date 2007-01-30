/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management2.active;

import org.coconut.management2.spi.AbstractPassiveNumberMonitor;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ActivePassiveNumberMonitor implements Runnable {
    private AbstractPassiveNumberMonitor mon;

    private Number n;

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        mon.process(n);
    }
}
