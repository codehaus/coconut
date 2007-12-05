package org.coconut.cache.internal.service.servicemanager;

enum RunState {
    NOTRUNNING, STARTING, RUNNING, SHUTDOWN, STOP, TERMINATED, TIDYING;

    public boolean isShutdown() {
        return this != RUNNING && this != NOTRUNNING;
    }

    public boolean isStarted() {
        return this != NOTRUNNING;
    }

    public boolean isTerminated() {
        return this == TERMINATED;
    }

    public boolean isTerminating() {
        return this == SHUTDOWN || this == STOP;
    }

    public RunState advanceToTerminated() {
        return TERMINATED;
    }
}
