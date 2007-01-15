/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.sla2;

import java.util.concurrent.atomic.AtomicLong;

import org.coconut.management.monitor.DateSampler;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractServiceMonitor<T extends Enum> implements ServiceMonitor<T> {

    private final AtomicLong nextChange = new AtomicLong();

    private final DateSampler ds = new DateSampler("foo", "boo");

    private volatile ServiceMonitorStatus<T> last;

    private volatile Exception lastException;

    /**
     * @see org.coconut.management.sla2.ServiceLevelMeasurementService#getLatestUpdate()
     */
    public long getLastUpdateTime() {
        return ds.get();
    }

    /**
     * @see org.coconut.management.sla2.ServiceLevelMeasurementService#lastUpdate()
     */
    public ServiceMonitorStatus<T> lastUpdate() {
        return last;
    }

    /**
     * @see org.coconut.management.sla2.ServiceLevelMeasurement#updateStatus()
     */
    public ServiceMonitorStatus<T> updateStatus() {
        try {
            synchronized (this) {
                last = update(last);
            }
        } catch (Exception e) {
            lastException = e;
            last = null;
        }
        ds.run();
        return last;
    }

    protected ServiceMonitorStatus<T> get(T status, final String description,
            final String toString) {
        return new Status<T>(status, description, toString, nanos(last, status), id(last,
                status));
    }

    private long getNext() {
        return nextChange.incrementAndGet();
    }

    protected abstract ServiceMonitorStatus<T> update(ServiceMonitorStatus<T> last)
            throws Exception;

    private long id(ServiceMonitorStatus<T> last, T status) {
        if (last == null) {
            return getNext();
        } else {
            return last.getStatus() == status ? last.statusId() : getNext();
        }
    }

    private long nanos(ServiceMonitorStatus<T> last, T status) {
        if (last == null) {
            return System.currentTimeMillis();
        } else {
            return last.getStatus() == status ? last.timestamp() : System
                    .currentTimeMillis();
        }
    }

    static class Status<T extends Enum> implements ServiceMonitorStatus<T> {
        private final String description;

        private final T status;

        private final String toString;

        private final long id;

        private final long nanos;

        private final Throwable t;

        /**
         * @param description
         * @param status
         */
        public Status(final T status, final String description, final String toString,
                long nanos, long id) {
            this.description = description;
            this.status = status;
            this.toString = toString;
            this.nanos = nanos;
            this.id = id;
            t = null;
        }

        /**
         * @param description
         * @param status
         */
        public Status(final T status, final String description, final String toString,
                long nanos, long id, Throwable t) {
            this.description = description;
            this.status = status;
            this.toString = toString;
            this.nanos = nanos;
            this.id = id;
            this.t = t;
        }

        /**
         * @see org.coconut.management.sla2.ServiceLevelMeasurementStatus#getStatus()
         */
        public T getStatus() {
            return status;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String getDescription() {
            return description;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return status + " : " + toString;
        }

        public long timestamp() {
            return nanos;
        }

        /**
         * @see org.coconut.management.sla2.ServiceLevelMeasurementStatus#statusId()
         */
        public long statusId() {
            return id;
        }

        /**
         * @see org.coconut.management.sla2.ServiceMonitorStatus#getException()
         */
        public Throwable getException() {
            return t;
        }
    }
}
