/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.jsr166ymath.matrix;

import jsr166y.forkjoin.ForkJoinExecutor;
import jsr166y.forkjoin.Ops.DoubleProcedure;
import jsr166y.forkjoin.Ops.DoubleReducer;

public class ParallelDoubleMatrix extends AbstractParallelAnyMatrix {
    
    //should we use double[][] or double[]
    //pro double[][]:
    //this is probably what most people expect
    //can supply a double[][] at creation time
    //can return the double[][] array at any time
    //no limititations of 2^31 elements
    
    
    //pro double[]::
    //we can use ParallelDoubleArray for add, scalar multiplication
    //apply, reduce
    //we can create a Matrix interface that can be implemented, for example,
    //by ParallelSparseDoubleMatrix
    //if we don't support a getArrayOfArrays() method
    //we can support a very efficient transpose method
    //by just having a switch indicating whether we are using
    //row-column-order or column-row order
    /** Array[][] for internal storage of elements. */
    private final double[][] A;

    public ParallelDoubleMatrix(ParallelDoubleMatrix matrix) {
        super(matrix);
        A = new double[m][n];
        for (int i = originM; i < m; i++) {
            for (int j = originN; j < n; j++) {
                A[i][j] = matrix.A[i][j];
            }
        }
    }

    ParallelDoubleMatrix(ForkJoinExecutor ex, double[][] A) {
        super(ex, 0, 0, A[0].length, A.length);
        this.A = A;
    }

    /**
     * Returns the executor used for computations
     * 
     * @return the executor
     */
    public ForkJoinExecutor getExecutor() {
        return ex;
    }

    /**
     * Applies the given procedure to elements
     * 
     * @param procedure
     *            the procedure
     */
    public void apply(DoubleProcedure procedure) {
        for (int i = originM; i < m; i++) {
            for (int j = originN; j < n; j++) {
                procedure.op(A[i][j]);
            }
        }
    }

    /**
     * Returns reduction of elements
     * 
     * @param reducer
     *            the reducer
     * @param base
     *            the result for an empty matrix
     * @return reduction
     */
    public double reduce(DoubleReducer reducer, double base) {
        double b = base;
        for (int i = originM; i < m; i++) {
            for (int j = originN; j < n; j++) {
                b = reducer.op(A[i][j], b);
            }
        }
        return b;
    }

    /**
     * Compute the sum of this and the specified matrix.
     * 
     * @param B
     *            matrix to be added
     * @return this
     * @throws IllegalArgumentException
     *             if the specified matrix is not the same size as this
     */
    public ParallelDoubleMatrix add(ParallelDoubleMatrix B) {
        checkSameDimensions(B);
        for (int i = originM; i < m; i++) {
            for (int j = originN; j < n; j++) {
                A[i][j] += B.A[i][j];
            }
        }
        return this;
    }

    public ParallelDoubleMatrix subtract(double s) {
        return add(-s);
    }

    public ParallelDoubleMatrix subtract(ParallelDoubleMatrix B) {
        checkSameDimensions(B);
        for (int i = originM; i < m; i++) {
            for (int j = originN; j < n; j++) {
                A[i][j] -= B.A[i][j];
            }
        }
        return this;
    }

    public static void main(String[] args) {
        ParallelDoubleMatrix pdm = ParallelDoubleMatrix.create(3, defaultExecutor());
        pdm.add(3);
        pdm.multiply(2);
        System.out.println(pdm);
    }

    /**
     * Returns the entry in the specified row and column.
     * <p>
     * 
     * @param row
     *            row location of entry to be fetched
     * @param column
     *            column location of entry to be fetched
     * @return entry in row,column
     * @throws IllegalArgumentException
     *             if the row or column index is not valid
     */
    public double getEntry(int m, int n) {
        checkValidRowColumn(m, n);
        return A[m + originM][n + originN];
    }

    public ParallelDoubleMatrix transpose() {
        // do we want to support inplace transpose when m=n?
        double[][] transpose = new double[n][m];
        ParallelDoubleMatrix out = new ParallelDoubleMatrix(ex, transpose);
        for (int i = originM; i < m; i++) {
            for (int j = originN; j < n; j++) {
                transpose[j][i] = A[i][j];
            }
        }
        return out;
    }

    public ParallelDoubleMatrix add(double s) {
        for (int i = originM; i < m; i++) {
            for (int j = originN; j < n; j++) {
                A[i][j] += s;
            }
        }
        return this;
    }

    public ParallelDoubleMatrix multiply(ParallelDoubleMatrix B) {
        double[][] c = new double[n][B.m];
        final double[] Bcolj = new double[B.n];
        for (int j = B.originM; j < B.m; j++) {
            for (int k = B.originN; k < B.originN; k++) {
                Bcolj[k] = B.A[k][j];
            }
            for (int i = originN; i < n; i++) {
                final double[] Arowi = A[i];
                double s = 0;
                for (int k = B.originN; k < B.n; k++) {
                    s += Arowi[k] * Bcolj[k];
                }
                c[i][j] = s;
            }
        }
        return new ParallelDoubleMatrix(ex, c);
    }

    public ParallelDoubleMatrix multiply(double s) {
        for (int i = originM; i < m; i++) {
            for (int j = originN; j < n; j++) {
                A[i][j] *= s;
            }
        }
        return this;
    }

    public String toString() {
        //need a better to string
        StringBuffer res = new StringBuffer();
        for (int i = originM; i < m; i++) {
            res.append("| ");
            for (int j = originN; j < n; j++) {
                res.append(A[i][j]);
                res.append(" ");
            }
            res.append(" |\n");
        }
        return res.toString();
    }

    public static ParallelDoubleMatrix create(int size, ForkJoinExecutor executor) {
        return create(size, size, executor);
    }

    /**
     * Creates a new ParallelDoubleMatrix using the given executor and a matrix with the
     * given number of rows and columns.
     * 
     * @param executor
     *            the executor
     */
    public static ParallelDoubleMatrix create(int numberOfRows, int numberOfColumns,
            ForkJoinExecutor executor) {
        double[][] A = new double[numberOfRows][numberOfColumns];
        return new ParallelDoubleMatrix(executor, A);
    }

    public static ParallelDoubleMatrix create(double[][] matrix, ForkJoinExecutor executor) {
        int n = matrix[0].length;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i].length != n) {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
        }
        return new ParallelDoubleMatrix(executor, matrix);
    }

    public static ParallelDoubleMatrix createIdentity(int size, ForkJoinExecutor executor) {
        return createIdentity(size, size, executor);
    }

    public static ParallelDoubleMatrix createIdentity(int numberOfRows, int numberOfColumns,
            ForkJoinExecutor executor) {
        double[][] A = new double[numberOfRows][numberOfColumns];
        for (int i = 0; i < Math.min(numberOfRows, numberOfColumns); i++) {
            A[i][i] = 1.0;
        }
        return new ParallelDoubleMatrix(executor, A);
    }

    /**
     * Returns a common default executor for use in ParallelMatrixs. This executor
     * arranges enough parallelism to use most, but not necessarily all, of the avaliable
     * processors on this system.
     * 
     * @return the executor
     */
    public static ForkJoinExecutor defaultExecutor() {
        return AbstractParallelAnyMatrix.defaultExecutor();
    }
}
