/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.service.spi;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.coconut.core.Log;
import org.coconut.core.Log.Level;
import org.coconut.management.service.ServiceMonitorLog;
import org.coconut.management.service.ServiceMonitorSession;
import org.coconut.management.service.ServiceMonitorStatus;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractServiceMonitorSession<V> implements
        ServiceMonitorSession<V> {

    static class MyFuture<V> extends FutureTask<V> {
        MyFuture(Callable c) {
            super(c);
        }

        /**
         * @see java.util.concurrent.FutureTask#set(java.lang.Object)
         */
        @Override
        protected void set(V v) {
            super.set(v);
        }

        /**
         * @see java.util.concurrent.FutureTask#setException(java.lang.Throwable)
         */
        @Override
        protected void setException(Throwable t) {
            super.setException(t);
        }
    }

    class MyRunnable implements Callable<V> {

        /**
         * @see java.util.concurrent.Callable#call()
         */
        public V call() throws Exception {
            return run0();
        }

    }

    private final CountDownLatch cdl = new CountDownLatch(1);

    private volatile Throwable exception;

    private final DefaultServiceMonitorLog log = new DefaultServiceMonitorLog();

    private volatile ServiceMonitorStatus status = ServiceMonitorStatus.UNKNOWN;

    MyFuture<V> future = new MyFuture<V>(new MyRunnable());

    /**
     * Blocks until all tasks have completed execution after a shutdown request,
     * or the timeout occurs, or the current thread is interrupted, whichever
     * happens first.
     * 
     * @param timeout
     *            the maximum time to wait
     * @param unit
     *            the time unit of the timeout argument
     * @return <tt>true</tt> if this executor terminated and <tt>false</tt>
     *         if the timeout elapsed before termination
     * @throws InterruptedException
     *             if interrupted while waiting
     */
    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        return cdl.await(timeout, unit);
    }

    /**
     * @param mayInterruptIfRunning
     * @return
     * @see java.util.concurrent.FutureTask#cancel(boolean)
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    /**
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @see java.util.concurrent.FutureTask#get()
     */
    public V get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    /**
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     * @see java.util.concurrent.FutureTask#get(long,
     *      java.util.concurrent.TimeUnit)
     */
    public V get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    /**
     * @see org.coconut.management2.service.ServiceCheckerSession#getDuration(java.util.concurrent.TimeUnit)
     */
    public long getDuration(TimeUnit unit) {
        return log.getDuration(unit);
    }

    /**
     * @return the exception
     */
    public Throwable getException() {
        return exception;
    }

    public ServiceMonitorLog getLog() {
        return log;
    }

    protected Log getLogger() {
        return logger;
    }

    /**
     * @see org.coconut.management2.service.ServiceCheckerSession#getStatus()
     */
    public ServiceMonitorStatus getStatus() {
        return status;
    }

    /**
     * @return
     * @see java.util.concurrent.FutureTask#isCancelled()
     */
    public boolean isCancelled() {
        return future.isCancelled();
    }

    /**
     * @return
     * @see java.util.concurrent.FutureTask#isDone()
     */
    public boolean isDone() {
        return future.isDone();
    }

    public void run() {
        future.run();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        // sb.append("isDone " + isDone() + "\n");
        // sb.append("Status " + getStatus() + "\n");
        // sb.append("Duration " + getDuration(TimeUnit.MILLISECONDS) + " ms" +
        // "\n");
        log.addLog(sb);
        if (exception != null && isCancelled()) {
            exception.printStackTrace();
        }
        return sb.toString();
    }

    protected abstract V doRun();

    protected void log(Log.Level level, long timestamp, String message) {
        log.log(level, message, null, timestamp);
    }

    protected long log(Log.Level level, String message) {
        return log.log2(level, message);
    }

    protected long log(Log.Level level, String message, Exception e) {
        return log.log2(level, message);
    }

    protected void setError(String message) {
        setError(message, null);
    }

    protected void setError(String message, Exception e) {
        log(Log.Level.Error, message);
        setStatus(ServiceMonitorStatus.ERROR);
        setException(e);
        throw new CheckTerminatedException();
    }

    /**
     * @param exception
     *            the exception to set
     */
    protected void setException(Throwable exception) {
        this.exception = exception;
    }

    protected void setOk(String message) {
        log(Log.Level.Info, message);
        setStatus(ServiceMonitorStatus.OK);
    }

    protected void setStatus(ServiceMonitorStatus status) {
        this.status = status;
    }

    protected void setStatus(ServiceMonitorStatus status, Exception e) {
        this.status = status;
        setException(e);
    }

    protected void setUnknown(String message) {
        setUnknown(message, null);
    }

    protected void setUnknown(String message, Exception e) {
        log(Log.Level.Fatal, message);
        setStatus(ServiceMonitorStatus.UNKNOWN);
        setException(e);
        throw new CheckTerminatedException();
    }

    protected void setWarning(String message) {
        log(Log.Level.Warn, message);
        setStatus(ServiceMonitorStatus.WARNING);
        throw new CheckTerminatedException();
    }

    V run0() {
        try {
            log.start();
            log.log(Level.Info, "Starting at " + new Date(), null, log.getStart());
            try {
                return doRun();
            } catch (CheckTerminatedException ignore) {
                return null;
            } finally {
                log.finish();
                log.log(Level.Info, "Stopped at " + new Date(), null, log.getFinish());
            }
        } finally {
            cdl.countDown();
        }
    }

    volatile Log logger;

}
