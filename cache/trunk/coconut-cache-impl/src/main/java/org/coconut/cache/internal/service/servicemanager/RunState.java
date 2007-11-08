/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public enum RunState {
    NOTRUNNING, RUNNING, SHUTDOWN, STOP, TIDYING, TERMINATED, COULD_NOT_START;

    public boolean isStarted() {
        return this != NOTRUNNING && this != COULD_NOT_START;
    }

    public boolean isShutdown() {
        return this != RUNNING && this != NOTRUNNING;
    }

    public boolean isTerminating() {
        return this == SHUTDOWN || this == STOP;
    }

    public boolean isTerminated() {
        return this == TERMINATED || this == COULD_NOT_START;
    }
}
