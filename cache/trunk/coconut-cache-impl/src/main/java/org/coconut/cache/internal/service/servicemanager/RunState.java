/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.servicemanager;

enum RunState {
    NOTRUNNING, STARTING, RUNNING, SHUTDOWN, STOP, TERMINATED;

    public boolean isShutdown() {
        return this == SHUTDOWN || this == STOP || this == TERMINATED;
    }

    public boolean isStarted() {
        return this != NOTRUNNING;
    }

    public boolean isTerminated() {
        return this == TERMINATED;
    }
    
    
//    public boolean isTerminating() {
//        return this == SHUTDOWN || this == STOP;
//    }

    public RunState advanceToTerminated() {
        return TERMINATED;
    }
}
