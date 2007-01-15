/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management2.service.spi;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.coconut.core.EventProcessor;
import org.coconut.core.Log;
import org.coconut.core.util.Logs;
import org.coconut.management2.service.ServiceCheckLog;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultServiceCheckLog extends Logs.AbstractLogger implements
        ServiceCheckLog {

    static class LogEntry implements ServiceCheckLog.Entry {
        final Log.Level level;

        final String message;

        final long timestamp;

        LogEntry(Log.Level level, long timestamp, String message) {
            this.message = message;
            this.timestamp = timestamp;
            this.level = level;
        }

        /**
         * @return the level
         */
        public synchronized Log.Level getLevel() {
            return level;
        }

        /**
         * @return the message
         */
        public synchronized String getMessage() {
            return message;
        }

        /**
         * @return the timestamp
         */
        public synchronized long getTimestamp() {
            return timestamp;
        }

        public String toString() {
            double strip = (timestamp) / 1000;
            NumberFormat formatter = new DecimalFormat("000,000,000");
            formatter.format(strip);

            if (message.endsWith("\n")) {
                return formatter.format(strip) + " : "
                        + message.substring(0, message.length() - 2);
            } else {
                return formatter.format(strip) + " : " + message;
            }
        }
    }

    private volatile long finish;

    private volatile EventProcessor<? super ServiceCheckLog.Entry> handler;

    private final ConcurrentLinkedQueue<LogEntry> list = new ConcurrentLinkedQueue<LogEntry>();

    private final AtomicInteger size = new AtomicInteger();

    private volatile long start;

    public void addLog(StringBuilder app) {
        for (ServiceCheckLog.Entry p : this) {
            if (p.getMessage().endsWith("\n")) {
                app.append(formatTime(p.getTimestamp()) + " : " + p.getMessage());
            } else {
                app.append(formatTime(p.getTimestamp()) + " : " + p.getMessage() + "\n");
            }
        }
    }

    public synchronized void finish() {
        if (finish > 0) {
            throw new IllegalStateException("Session has already finished");
        }
        finish = System.nanoTime();
    }

    /**
     * @see org.coconut.management2.service.ServiceCheckerSession#getDuration(java.util.concurrent.TimeUnit)
     */
    public long getDuration(TimeUnit unit) {
        if (finish > 0) {
            return unit.convert(finish - start, TimeUnit.NANOSECONDS);
        } else {
            return unit.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * @return the handler
     */
    public EventProcessor<? super ServiceCheckLog.Entry> getEventHandler() {
        return handler;
    }

    /**
     * @return the finish
     */
    public long getFinish() {
        return finish;
    }

    /**
     * @return the start
     */
    public long getStart() {
        return start;
    }

    /**
     * @see org.coconut.core.Log#isEnabled(org.coconut.core.Log.Level)
     */
    public boolean isEnabled(Level level) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Entry> iterator() {
        return (Iterator) list.iterator();
    }

    public long log2(Log.Level level, String message) {
        long timeStamp = System.nanoTime();
        log(level, message, null, timeStamp);
        return timeStamp;
    }

    /**
     * @see org.coconut.core.Log#log(org.coconut.core.Log.Level,
     *      java.lang.String)
     */
    public void log(Level level, String message) {
        log(level, message, null);
    }

    /**
     * @see org.coconut.core.Log#log(org.coconut.core.Log.Level,
     *      java.lang.String, java.lang.Throwable)
     */
    public void log(Log.Level level, String message, Throwable cause) {
        log(level, message, cause, System.nanoTime());
    }

    public void log(Log.Level level, String message, Throwable cause, long timestamp) {
        LogEntry le = new LogEntry(level, timestamp - start, message);
        if (isEnabled(level)) {
            list.add(le);
            
        }
        EventProcessor<? super Entry> handler = this.handler;
        if (handler != null) {
            handler.process(le);
        }
    }

    /**
     * @param handler
     *            the handler to set
     */
    public void setEventHandler(EventProcessor<? super ServiceCheckLog.Entry> handler) {
        this.handler = handler;
    }

    /**
     * @see org.coconut.management2.service.ServiceCheckLog#size()
     */
    public int size() {
        return size.get();
    }

    public synchronized void start() {
        if (start > 0) {
            throw new IllegalStateException("Session has already started");
        }
        start = System.nanoTime();
    }

    private String formatTime(long time) {
        double strip = (time - getStart()) / 1000;
        long d = getDuration(TimeUnit.NANOSECONDS);
        NumberFormat formatter = new DecimalFormat("000,000");
        return formatter.format(strip);
    }

}
