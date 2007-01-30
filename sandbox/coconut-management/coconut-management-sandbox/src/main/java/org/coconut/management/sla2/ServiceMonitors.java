/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.sla2;

import static org.coconut.filter.ComparisonFilters.*;
import static org.coconut.filter.LogicFilters.not;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.core.Clock;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class ServiceMonitors {

    static enum NormalViolated {
        NORMAL, VIOLATED;
    }

    static enum NormWarnErr {
        NORMAL, WARNING, ERROR;
    }

    static <T extends Enum> T getHighest(Class<T> e) {
        return e.getEnumConstants()[e.getEnumConstants().length - 1];
    }

    public static void main(String[] args) throws Exception {
        AtomicLong al = new AtomicLong(5);
        ServiceMonitor slm = fromLong(al, lessThen(6l));
        ServiceMonitor slm2 = outOfNormal(slm, 2, TimeUnit.SECONDS);
        al.set(7);
        // for (int i = 0; i < 40; i++) {
        // Thread.sleep(100);
        // System.out.println(slm.updateStatus());
        // System.out.println(slm2.updateStatus());
        // }
        al.set(4);
        System.out.println(slm2.updateStatus());
        al.set(8);

        ServiceMonitor<NormWarnErr> sl = fromLong(al, NormWarnErr.class, not(between(6l,
                8l)), not(between(4l, 10l)));
        System.out.println("----------------------");
        for (int i = 0; i < 14; i++) {
            al.set(i);
            System.out.println(i + ", " + sl.updateStatus());
        }

    }

    static ServiceMonitor<NormalViolated> outOfNormal(ServiceMonitor<NormalViolated> slm,
            long time, TimeUnit unit) {
        return outOf(slm, NormalViolated.NORMAL, time, unit, NormalViolated.NORMAL,
                NormalViolated.VIOLATED);
    }

    static <T extends Enum, E extends Enum> ServiceMonitor<T> outOf(
            ServiceMonitor<E> slm, E normal, long time, TimeUnit unit, T inLimits,
            T outOfLimits) {
        return new NotInForTime<T, E>(slm, normal, time, unit, inLimits, outOfLimits);
    }

    @SuppressWarnings("unchecked")
    static ServiceMonitor<NormalViolated> fromLong(Number n, Filter<? super Long> filter) {
        return fromLong(n, NormalViolated.class, new Filter[] { filter });
    }

    @SuppressWarnings("unchecked")
    static <T extends Enum> ServiceMonitor<T> fromLong(Number n, Class<T> c,
            Filter<? super Long> filter) {
        return fromLong(n, c, new Filter[] { filter });
    }

    @SuppressWarnings("unchecked")
    static <T extends Enum> ServiceMonitor<T> fromLong(Number n, Class<T> c,
            Filter<? super Long> filter1, Filter<? super Long> filter2) {
        return fromLong(n, c, new Filter[] { filter1, filter2 });
    }

    @SuppressWarnings("unchecked")
    static <T extends Enum> ServiceMonitor<T> fromLong(Number n, Class<T> c,
            Filter<? super Long>[] filters) {
        if (n == null) {
            throw new NullPointerException("n is null");
        } else if (filters == null) {
            throw new NullPointerException("filters is null");
        } else if (filters.length == 0) {
            throw new IllegalArgumentException("Must define at least one filter");
        }
        if (c.getEnumConstants().length != filters.length + 1) {
            throw new IllegalArgumentException(c.getEnumConstants().length + ","
                    + (filters.length - 1));
        }
        return new FilterSlm<T>(n, c, filters);
    }

    static class NotInForTime<T extends Enum, E extends Enum> extends
            AbstractServiceMonitor<T> {

        private final Clock clock = Clock.DEFAULT_CLOCK;

        private final ServiceMonitor<E> slm;

        private final E expected;

        private final long nanoTime;

        private final T normal;

        private final T violated;

        private final TimeUnit unit;

        /**
         * @param slm
         * @param expected
         * @param nanoTime
         * @param normal
         * @param violated
         * @param unit
         */
        public NotInForTime(final ServiceMonitor<E> slm, final E expected,
                final long nanoTime, final TimeUnit unit, final T normal, final T violated) {
            this.slm = slm;
            this.expected = expected;
            this.nanoTime = unit.toNanos(nanoTime);
            this.normal = normal;
            this.violated = violated;
            this.unit = unit;
        }

        /**
         * @see org.coconut.management.sla2.AbstractSlm#update(org.coconut.management.sla2.ServiceLevelMeasurementStatus)
         */
        @Override
        protected synchronized ServiceMonitorStatus<T> update(ServiceMonitorStatus<T> last)
                throws Exception {
            ServiceMonitorStatus<E> other = slm.lastUpdate();
            T status = normal;
            if (other.getStatus() != expected) {
                long diff = last == null ? 0 : System.currentTimeMillis()
                        - last.timestamp();
                if ((last != null && last.getStatus() == violated)
                        || TimeUnit.MILLISECONDS.toNanos(diff) > nanoTime) {
                    status = violated;
                }
            }
            return get(status, "", "");
        }
    }

    static class FilterSlm<T extends Enum> extends AbstractServiceMonitor<T> {

        private final T[] mapTo;

        private final Filter<? super Long>[] filters;

        private final Number n;

        FilterSlm(final Number n, Class<T> clazz, Filter<? super Long>... filters) {
            this.filters = filters.clone();
            mapTo = clazz.getEnumConstants();
            this.n = n;
        }

        /**
         * @see org.coconut.management.sla2.AbstractSlm#update(org.coconut.management.sla2.ServiceLevelMeasurementStatus)
         */
        @Override
        protected ServiceMonitorStatus<T> update(ServiceMonitorStatus<T> last)
                throws Exception {
            long value = n.longValue();
            for (int i = filters.length; --i >= 0;) {
                if (filters[i].accept(value)) {
                    return super.get(mapTo[i + 1], "oka",
                            "Did not fullfill predicate, x=" + n + " "
                                    + filters[i].toString().replace("$x", "x"));
                }
            }
            return super
                    .get(mapTo[0], "ok", "Value " + n + " was within accepted bounds");
        }
    }
}
