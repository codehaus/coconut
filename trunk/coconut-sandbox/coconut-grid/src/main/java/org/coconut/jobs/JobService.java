package org.coconut.jobs;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.coconut.core.Priority;

public interface JobService extends ExecutorService {

    /**
     * Use constructor dependency injection.
     * @param task
     * @return
     */
    <S, T extends Callable<S>> JobFuture<S> submit(Class<T> task);

    <T> JobFuture<T> submit(Callable<T> task);

    <T> JobFuture<T> submit(Callable<T> task, Priority priority);

    <T> JobFuture<T> submit(Runnable task, Priority priority);

    // <T> List<JobFuture<T>> invokeAll(Collection<Callable<T>> tasks)
    // throws InterruptedException;

    // <T> List<JobFuture<T>> invokeAll(Collection<Callable<T>> tasks)
    // throws InterruptedException;
}
