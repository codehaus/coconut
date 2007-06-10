/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.numbers.spi;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class BinaryDoubleOperand extends DoubleNumber {

    private final Number first;

    private final Number second;

    BinaryDoubleOperand(Number first, Number second) {
        if (first == null) {
            throw new NullPointerException("first is null");
        } else if (second == null) {
            throw new NullPointerException("second is null");
        }
        this.first = first;
        this.second = second;
    }

    public Number getFirst() {
        return first;
    }

    public Number getSecond() {
        return second;
    }
}
