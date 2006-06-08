/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under the academic free
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event.seda.management;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class StageStatistics {

    private final String name;

    private final long eventsAccepted;

    private final long eventsProcessed;

    private final long eventsFailed;

    /* Millies? */

    private final long totalTime;

    private final long totalCpuTime;

    private final long userTime;

    private final long pendingEvents;

    private final double averageEventsPerSecond;

    // long getEventsProcessed();
    //
    // long getCpuTime();
    // long getFailedEvents();
    // long getWallclockTime();

    public long getEventsAccepted() {
        return eventsAccepted;
    }

    public long getEventsFailed() {
        return eventsFailed;
    }

    public long getEventsProcessed() {
        return eventsProcessed;
    }

    public String getName() {
        return name;
    }

    public long getPendingEvents() {
        return pendingEvents;
    }

    public long getTotalCpuTime() {
        return totalCpuTime;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public long getUserTime() {
        return userTime;
    }

    public double getAverageEventsPerSecond() {
        return averageEventsPerSecond;
    }

    public StageStatistics(String name, long eventsAccepted, long eventsProcessed,
            long eventsFailed, long totalTime, long totalCpuTime,
            long userTime, long pendingEvents, double averageEventsPerSecond) {
        this.name = name;
        this.eventsAccepted = eventsAccepted;
        this.eventsProcessed = eventsProcessed;
        this.eventsFailed = eventsFailed;
        this.totalCpuTime = totalCpuTime;
        this.userTime = userTime;
        this.totalTime = totalTime;
        this.pendingEvents = pendingEvents;
        this.averageEventsPerSecond = averageEventsPerSecond;
    }

    // system, user time
    // long getEventsAccepted
    // long pendingevents=eventsAccepted-eventsprocessed-failedEvents

}
