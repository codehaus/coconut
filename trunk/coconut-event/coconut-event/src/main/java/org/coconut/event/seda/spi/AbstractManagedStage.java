/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.seda.spi;

import java.util.concurrent.atomic.AtomicLong;

import org.coconut.event.seda.Stage;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public abstract class AbstractManagedStage implements Stage {

    private final String name;

    private final AtomicLong clockTime = new AtomicLong();

    private final AtomicLong cpuTime = new AtomicLong();

    private final AtomicLong userTime = new AtomicLong();

    private final AtomicLong transformations = new AtomicLong();

    private final AtomicLong failedTransformations = new AtomicLong();

    public AbstractManagedStage(String name) {
        this.name = name;
    }

    protected void incrementProcessed() {
        transformations.incrementAndGet();
    }

    protected void incrementFailed() {
        failedTransformations.incrementAndGet();
    }

    protected void addProcessed(int amount) {
        transformations.addAndGet(amount);
    }

    protected void addFailures(int amount) {
        failedTransformations.addAndGet(amount);
    }

    protected void addUserTime(long nanos) {
        userTime.addAndGet(nanos);
    }

    protected void addWallClockTime(long nanos) {
        clockTime.addAndGet(nanos);
    }

    protected void addCpuTime(long nanos) {
        cpuTime.addAndGet(nanos);
    }

    public long getProcessed() {
        return transformations.get();
    }

    public long getUserTime() {
        return userTime.get();
    }

    /**
     * Returns the total wall clock time that any thread has spent processing
     * events within the stage.
     */
    public long getTime() {
        return clockTime.get();
    }

    public long getCpuTime() {
        return cpuTime.get();
    }

    public String getName() {
        return name;
    }

    public long getFailed() {
        return failedTransformations.get();
    }

    public abstract int getActiveThreadCount();

    public abstract AbstractManagedStage[] getIncoming();
}
