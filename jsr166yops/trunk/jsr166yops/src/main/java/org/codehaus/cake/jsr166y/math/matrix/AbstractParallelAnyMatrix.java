/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.jsr166y.math.matrix;

import jsr166y.forkjoin.ForkJoinExecutor;
import jsr166y.forkjoin.ForkJoinPool;

import org.codehaus.cake.jsr166y.math.matrix.Matrix;

public abstract class AbstractParallelAnyMatrix implements Matrix {
    /** Global default executor */
    private static volatile ForkJoinPool defaultExecutor;

    /** Lock for on-demand initialization of defaultExecutor */
    private static final Object poolLock = new Object();

    final ForkJoinExecutor ex;

    /** Row dimensions. */
    final int m;

    /** column dimensions. */
    final int n;

    AbstractParallelAnyMatrix(AbstractParallelAnyMatrix matrix) {
        this.ex = matrix.ex;
        this.m = matrix.m;
        this.n = matrix.n;
    }

    AbstractParallelAnyMatrix(ForkJoinExecutor ex, int m, int n) {
        this.ex = ex;
        this.m = m;
        this.n = n;
    }

    public int getNumberOfColumns() {
        return n;
    }

    public int getNumberOfRows() {
        return m;
    }

    public boolean isSquare() {
        return m == n;
    }

    void checkSameDimensions(AbstractParallelAnyMatrix other) {
        if (m != other.m) {
            throw new IllegalArgumentException(
                    "The specified matrix has a incompatible number of rows [this.rows =" + m
                            + ", other.rows=" + other.m + "]");
        } else if (n != other.n) {
            throw new IllegalArgumentException(
                    "The specified matrix has a incompatible number of columns [this.rows =" + n
                            + ", other.rows=" + n + "]");
        }
    }

    void checkValidRowColumn(int n, int m) {
        if (n <= 0) {
            throw new IllegalArgumentException(
                    "The specified row must be a positive number [row = " + n + " ]");
        } else if (n > this.n) {
            throw new IllegalArgumentException();
        } else if (m <= 0) {
            throw new IllegalArgumentException(
                    "The specified column must be a positive number [column = " + n + " ]");
        } else if (m > this.m) {
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
