/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.sla;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class Slop implements ServiceLevelObjective {

    private final AtomicLong updateCount = new AtomicLong();

    private Status latest;
    protected long getLatest;

    boolean isEnabled;

    public synchronized void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public synchronized boolean isEnabled() {
        return isEnabled;
    }

    public synchronized void run() {

    }

    /**
     * @see org.coconut.management.sla.ServiceLevelObjective#getStatus()
     */
    public Status getStatus() {
        return new Status();
    }

    public String getName() {
        return "Average Bytes written/s";
    }

    public String getDescription() {
        return "Average Bytes written/s must be greater then 300 Bytes/s";
    }

    class Status implements ServiceLevelObjective.Status {
        private long duration;

        private boolean isEnabled;

        private boolean isOutOfLimit;

        private String status;

        private String description;

        private long updateCount;

        public long getUpdateCount() {
            return updateCount;
        }

        /**
         * @see org.coconut.management.sla.ServiceLevelObjective.Status#getDuration()
         */
        public double getDuration(TimeUnit unit) {
            return (double) duration / unit.toNanos(1);
        }

        public String toString() {
            return description;
        }

        /**
         * @see org.coconut.management.sla.ServiceLevelObjective.Status#getSlo()
         */
        public ServiceLevelObjective getSlo() {
            return Slop.this;
        }

        /**
         * @see org.coconut.management.sla.ServiceLevelObjective.Status#getStatus()
         */
        public String getStatus() {
            return status;
        }

        /**
         * @see org.coconut.management.sla.ServiceLevelObjective.Status#isEnabled()
         */
        public boolean isEnabled() {
            return isEnabled;
        }

        /**
         * @see org.coconut.management.sla.ServiceLevelObjective.Status#isOutOfLimit()
         */
        public boolean isOutOfLimit() {
            return isOutOfLimit;
        }
    }
}
