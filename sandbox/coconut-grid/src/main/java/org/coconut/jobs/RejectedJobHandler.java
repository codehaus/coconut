package org.coconut.jobs;

public interface RejectedJobHandler {
    void rejectedExecution(Runnable r, JobService service);
}
