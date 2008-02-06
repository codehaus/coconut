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

import jsr166y.forkjoin.Ops.DoubleProcedure;
import jsr166y.forkjoin.Ops.DoubleReducer;

public interface DoubleMatrix extends Matrix /*, Iterable<DoubleMatrix.Entry> */ {

    /**
     * Adds the scalar <tt>s</tt> to each entry of this matrix.
     * 
     * @param s
     *            the scalar to add to each entry
     * @return this matrix
     */
    DoubleMatrix add(double s);

    /**
     * Applies the given procedure to all elements in the matrix
     * 
     * @param procedure
     *            the procedure
     */
    DoubleMatrix apply(DoubleProcedure procedure);

    /**
     * Calculates the matrix trace. Which is defined as the sum of the elements on the
     * main diagonal.
     * 
     * @return the matrix trace
     */
    double calculateTrace();

    /**
     * @return a (deep) copy of this matrix
     */
    DoubleMatrix copy();

    /**
     * @return a 2 dimensional array with the contents of this matrix
     */
    double[][] toArray();

    /**
     * Divides each entry of this matrix with the specified scalar <tt>s</tt>.
     * 
     * @param s
     *            the scalar to divide each entry with
     * @return this matrix
     */
    DoubleMatrix divide(double s);

    /**
     * Returns the entry in the specified row and column.
     * <p>
     * 
     * @param m
     *            row location of entry to be fetched
     * @param n
     *            column location of entry to be fetched
     * @return entry in row,column
     * @throws IllegalArgumentException
     *             if the row or column index is not valid
     */
    double getEntry(int m, int n);

    /**
     * Multiplies each entry of this matrix with the specified scalar <tt>s</tt>.
     * 
     * @param s
     *            the scalar to multiply each entry with
     * @return this matrix
     */
    DoubleMatrix multiply(double s);

    /**
     * Returns reduction of all elements in the matrix
     * 
     * @param reducer
     *            the reducer
     * @param base
     *            the result for an empty matrix
     * @return reduction
     */
    double reduce(DoubleReducer reducer, double base);

    /**
     * Substracts the scalar <tt>s</tt> to each entry of this matrix.
     * 
     * @param s
     *            the scalar to substract from each entry
     * @return this matrix
     */
    DoubleMatrix subtract(double s);

    interface Entry extends Matrix.Entry<Double>{
        double getDouble();
    }
}
