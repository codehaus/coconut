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

public interface Matrix /*extends Iterable<Matrix.Entry> obviously doesn't work*/ {

    /**
     * @return the number of rows in this matrix
     */
    int getNumberOfRows();

    /**
     * @return the number of columns in this matrix
     */
    int getNumberOfColumns();

    /**
     * Returns whether or not this matrix is square (has the same number of columns and
     * rows).
     * 
     * @return true if the matrix is square, otherwise false
     */
    boolean isSquare();

    interface Entry<T> {
        int getRow();

        int getColumn();

        T getValue();
    }
}
