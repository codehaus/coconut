/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import java.util.Arrays;

public class CostAttributeTest extends AbstractAttributeTest {

    public CostAttributeTest() {
        super(CostAttribute.INSTANCE, Arrays.asList(Double.MIN_VALUE, -100d, -1d, 0d, 10d,
                Double.MAX_VALUE), Arrays.asList(Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, Double.NaN));
    }

}
