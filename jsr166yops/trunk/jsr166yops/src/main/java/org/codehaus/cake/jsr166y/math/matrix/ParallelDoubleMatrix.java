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

import java.text.NumberFormat;

import jsr166y.forkjoin.ForkJoinExecutor;
import jsr166y.forkjoin.ParallelDoubleArray;
import jsr166y.forkjoin.Ops.DoubleGenerator;
import jsr166y.forkjoin.Ops.DoubleProcedure;
import jsr166y.forkjoin.Ops.DoubleReducer;

import org.codehaus.cake.jsr166y.ops.DoubleOps;

public class ParallelDoubleMatrix extends AbstractParallelAnyMatrix implements DoubleMatrix {

    /** Array[][] for internal storage of elements. */
    private final double[] a;

    private final ParallelDoubleArray pda;

    private ParallelDoubleMatrix(ParallelDoubleMatrix matrix) {
        this(matrix.ex, new double[matrix.a.length], matrix.m, matrix.n);
        System.arraycopy(matrix.a, 0, a, 0, a.length);
    }

    ParallelDoubleMatrix(ForkJoinExecutor ex, double[] a, int m, int n) {
        super(ex, m, n);
        this.a = a;
        pda = ParallelDoubleArray.createUsingHandoff(a, ex);
    }

    public ParallelDoubleMatrix add(double s) {
        pda.replaceWithMapping(DoubleOps.add(s));
        return this;
    }

    /**
     * Matrix addition: Computes the sum of this and the specified matrix.
     * <p>
     * If this matrix is represented with <tt>A</tt> and the specified matrix is
     * represented with <tt>B</tt>. This method is equivalent to <tt>A = A + B</tt>.
     * 
     * @param b
     *            matrix to be added
     * @return this
     * @throws IllegalArgumentException
     *             if the specified matrix does not have the same number of rows and
     *             columns as this
     */
    public ParallelDoubleMatrix add(ParallelDoubleMatrix b) {
        checkSameDimensions(b);
        pda.replaceWithMapping(DoubleOps.binaryAdd(), b.a);
        return this;
    }

    /**
     * Applies the given procedure to elements
     * 
     * @param procedure
     *            the procedure
     */
    public ParallelDoubleMatrix apply(DoubleProcedure procedure) {
        pda.apply(procedure);
        return this;
    }

    public double calculateTrace() {
        double t = 0;
        int nextIndex = 0;
        // ineffective
        while (nextIndex <= a.length) {
            t += a[nextIndex];
            nextIndex += n + 1;
        }
        return t;
    }

    public ParallelDoubleMatrix all() {
        return new ParallelDoubleMatrix(this);
    }

    public ParallelDoubleMatrix divide(double s) {
        pda.replaceWithMapping(DoubleOps.divide(s));
        return this;
    }

    public ParallelDoubleMatrix fill(double s) {
        pda.replaceWithValue(s);
        return this;
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
        return a[translate(m, n)];
    }

    /**
     * Returns the executor used for computations
     * 
     * @return the executor
     */
    public ForkJoinExecutor getExecutor() {
        return ex;
    }

    public ParallelDoubleMatrix multiply(double s) {
        pda.replaceWithMapping(DoubleOps.multiply(s));
        return this;
    }

    public ParallelDoubleMatrix multiply(ParallelDoubleMatrix B) {
        throw new UnsupportedOperationException();
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
        return pda.reduce(reducer, base);
    }

    /**
     * Returns true if all elements at the same relative positions of this and other
     * matrix are equal.
     * 
     * @param other
     *            the other matrix
     * @return true if equal
     */
    public boolean hasAllEqualElements(ParallelDoubleMatrix other) {
        return pda.hasAllEqualElements(other.pda);
    }

    public ParallelDoubleMatrix subtract(double s) {
        return add(-s);
    }

    /**
     * Matrix subtraction; Computes the difference between this and the specified matrix.
     * <p>
     * If this matrix is represented with <tt>A</tt> and the specified matrix is
     * represented with <tt>B</tt>. This method is equivalent to <tt>A = A - B</tt>.
     * 
     * @param b
     *            matrix to be added
     * @return this (to simplify use in expressions)
     * @throws IllegalArgumentException
     *             if the specified matrix does not have the same number of rows and
     *             columns as this
     */
    public ParallelDoubleMatrix subtract(ParallelDoubleMatrix B) {
        checkSameDimensions(B);
        pda.replaceWithMapping(DoubleOps.binarySubtract(), B.a);
        return this;
    }

    /**
     * Replaces elements with the results of applying the given generator. For example, to
     * fill the matrix with uniform random values, use
     * <tt>replaceWithGeneratedValue(Ops.doubleRandom())</tt>
     * 
     * @param generator
     *            the generator
     * @return this (to simplify use in expressions)
     */
    public ParallelDoubleMatrix replaceWithGeneratedValue(DoubleGenerator generator) {
        pda.replaceWithGeneratedValue(generator);
        return this;
    }

    public double[][] toArray() {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        // need a better to string
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < m; i++) {
            res.append("| ");
            for (int j = 0; j < n; j++) {
                res.append(a[i * n + j]);
                res.append(" ");
            }
            res.append(" |\n");
        }
        return res.toString();
    }

    //temporarily method until we find something better.
    public String toString(NumberFormat formatter) {
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < m; i++) {
            res.append("| ");
            for (int j = 0; j < n; j++) {
                res.append(formatter.format(a[i * n + j]));
                res.append(" ");
            }
            res.append("|\n");
        }
        return res.toString();
    }        
    public ParallelDoubleMatrix transpose() {
        // do we support inplace transpose or?
        // if yes, we probably need a transpose(ParallelDoubleMatrix)
        // to transpose into
        throw new UnsupportedOperationException();
    }

    private int translate(int m, int n) {
        return (n - 1) + (m - 1) * this.n;
    }

    public static ParallelDoubleMatrix create(int rowColumnSize, ForkJoinExecutor executor) {
        return create(rowColumnSize, rowColumnSize, executor);
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
        double[] a = new double[numberOfRows * numberOfColumns];
        return new ParallelDoubleMatrix(executor, a, numberOfRows, numberOfColumns);
    }

    public static ParallelDoubleMatrix createIdentity(int rowColumnSize, ForkJoinExecutor executor) {
        return createIdentity(rowColumnSize, rowColumnSize, executor);
    }

    public static ParallelDoubleMatrix createIdentity(int numberOfRows, int numberOfColumns,
            ForkJoinExecutor executor) {
        double[] a = new double[numberOfRows * numberOfColumns];
        int nextIndex = 0;
        // ineffective
        while (nextIndex <= a.length) {
            a[nextIndex] = 1;
            nextIndex += numberOfColumns + 1;
        }
        return new ParallelDoubleMatrix(executor, a, numberOfRows, numberOfColumns);
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
