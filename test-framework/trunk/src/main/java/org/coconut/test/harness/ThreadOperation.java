package org.coconut.test.harness;

public class ThreadOperation {
    public long invocations;

    public final String operation;

    public final Runnable r;

    public final int rnd;

    Throwable t;

    ThreadOperation(String operation, int rnd, Runnable r) {
        this.operation = operation;
        this.rnd = rnd;
        this.r = r;
    }
}
