package org.coconut.jobs;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.coconut.core.Priority;

public class MyExecutor implements JobService {

    public <T> JobFuture<T> submit(Callable<T> task) {
        return null;
    }

    public void shutdown() {
        // TODO Auto-generated method stub

    }

    public List<Runnable> shutdownNow() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isShutdown() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isTerminated() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }

    public void execute(Runnable command) {

    }

    public <T> JobFuture<T> submit(Callable<T> task, Priority priority) {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> JobFuture<T> submit(Runnable task, Priority priority) {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> Future<T> submit(Runnable task, T result) {
        // TODO Auto-generated method stub
        return null;
    }

    public Future<?> submit(Runnable task) {
        // TODO Auto-generated method stub
        return null;
    }

//    public <T> List<Future<T>> invokeAll(Collection<Callable<T>> arg0, long arg1, TimeUnit arg2) throws InterruptedException {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    public <T> T invokeAny(Collection<Callable<T>> arg0) throws InterruptedException, ExecutionException {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    public <T> T invokeAny(Collection<Callable<T>> arg0, long arg1, TimeUnit arg2) throws InterruptedException, ExecutionException, TimeoutException {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    public <T> List<Future<T>> invokeAll(Collection<Callable<T>> arg0) throws InterruptedException {
//        // TODO Auto-generated method stub
//        return null;
//    }

    /**
     * @see org.coconut.jobs.JobService#submit(java.lang.Class)
     */
    public <S, T extends Callable<S>> JobFuture<S> submit(Class<T> task) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see java.util.concurrent.ExecutorService#invokeAll(java.util.Collection)
     */
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> arg0) throws InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see java.util.concurrent.ExecutorService#invokeAll(java.util.Collection, long, java.util.concurrent.TimeUnit)
     */
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> arg0, long arg1, TimeUnit arg2) throws InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see java.util.concurrent.ExecutorService#invokeAny(java.util.Collection)
     */
    public <T> T invokeAny(Collection<? extends Callable<T>> arg0) throws InterruptedException, ExecutionException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see java.util.concurrent.ExecutorService#invokeAny(java.util.Collection, long, java.util.concurrent.TimeUnit)
     */
    public <T> T invokeAny(Collection<? extends Callable<T>> arg0, long arg1, TimeUnit arg2) throws InterruptedException, ExecutionException, TimeoutException {
        // TODO Auto-generated method stub
        return null;
    }

//    public <T> List<JobFuture<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
//        // TODO Auto-generated method stub
//        return null;
//    }
}
