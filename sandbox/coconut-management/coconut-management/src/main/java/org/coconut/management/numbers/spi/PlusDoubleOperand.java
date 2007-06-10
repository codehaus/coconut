/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.numbers.spi;

import org.coconut.filter.Filter;
import org.coconut.filter.Filters;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class PlusDoubleOperand extends BinaryDoubleOperand implements Comparable<Number> {

    /**
     * @param first
     * @param second
     */
    PlusDoubleOperand(Number first, Number second) {
        super(first, second);
    }

    /**
     * @see java.lang.Number#doubleValue()
     */
    @Override
    public double doubleValue() {
        return getFirst().doubleValue() + getSecond().doubleValue();

    }

    public static void main(String[] args) {
        Double d = 4.40;
        Number n = new PlusDoubleOperand(4, 5);
        Filter<Number> f=Filters.greatherThenOrEqual(n);
        System.out.println(f.accept(8.9));
        System.out.println(f.accept(9));
        System.out.println(f.accept(9.4));
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Number o) {
        return Double.compare(doubleValue(), o.doubleValue());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Double.toString(doubleValue());
    }
}
