/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.jsr166y.math.matrix;

import jsr166y.forkjoin.ForkJoinExecutor;
import jsr166y.forkjoin.ForkJoinPool;

public abstract class AbstractParallelAnyMatrix {
    /** Global default executor */
    private static volatile ForkJoinPool defaultExecutor;

    /** Lock for on-demand initialization of defaultExecutor */
    private static final Object poolLock = new Object();

    final ForkJoinExecutor ex;

    /** Row dimensions. */
    final int m;

    /** column dimensions. */
    final int n;

    final int originM;

    final int originN;

    AbstractParallelAnyMatrix(AbstractParallelAnyMatrix matrix) {
        this.ex = matrix.ex;
        this.m = matrix.m;
        this.n = matrix.n;
        this.originM = matrix.originM;
        this.originN = matrix.originN;
    }

    AbstractParallelAnyMatrix(ForkJoinExecutor ex, int originM, int originN, int m, int n) {
        this.ex = ex;
        this.m = m;
        this.n = n;
        this.originM = originM;
        this.originN = originN;
    }

    public final int getNumberOfColumns() {
        return n - originN;
    }

    public final int getNumberOfRows() {
        return m - originM;
    }

    public boolean isSquare() {
        return getNumberOfColumns() == getNumberOfRows();
    }

    void checkSameDimensions(AbstractParallelAnyMatrix other) {
        if (getNumberOfRows() != other.getNumberOfRows()) {
            throw new IllegalArgumentException(
                    "The specified matrix has a incompatible number of rows [this.rows ="
                            + getNumberOfRows() + ", other.rows=" + other.getNumberOfRows() + "]");
        } else if (getNumberOfColumns() != other.getNumberOfColumns()) {
            throw new IllegalArgumentException(
                    "The specified matrix has a incompatible number of columns [this.rows ="
                            + getNumberOfColumns() + ", other.rows=" + other.getNumberOfColumns()
                            + "]");
        }
    }

    void checkValidRowColumn(int n, int m) {
        if (n < 0) {
            throw new IllegalArgumentException(
                    "The specified row must be a non negative number [row = " + n + " ]");
        } else if (m < 0) {
            throw new IllegalArgumentException(
                    "The specified column must be a non negative number [column = " + n + " ]");
        } else if (n >= getNumberOfRows() - originN) {
            throw new IllegalArgumentException();
        } else if (m >= getNumberOfColumns() - originM) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Copied from juc.PAS
     */
    static ForkJoinExecutor defaultExecutor() {
        ForkJoinPool p = defaultExecutor; // double-check
        if (p == null) {
            synchronized (poolLock) {
                p = defaultExecutor;
                if (p == null) {
                    // use ceil(7/8 * ncpus)
                    int nprocs = Runtime.getRuntime().availableProcessors();
                    int nthreads = nprocs - (nprocs >>> 3);
                    defaultExecutor = p = new ForkJoinPool(nthreads);
                }
            }
        }
        return p;
    }
}
