/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.numbers.spi;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class DoubleNumber extends Number {
    /**
     * Returns the value of this <code>Double</code> as a <code>byte</code>
     * (by casting to a <code>byte</code>).
     * 
     * @return the <code>double</code> value represented by this object
     *         converted to type <code>byte</code>
     * @since JDK1.1
     */
    public byte byteValue() {
        return (byte) doubleValue();
    }

    /**
     * Returns the value of this <code>Double</code> as a <code>short</code>
     * (by casting to a <code>short</code>).
     * 
     * @return the <code>double</code> value represented by this object
     *         converted to type <code>short</code>
     * @since JDK1.1
     */
    public short shortValue() {
        return (short) doubleValue();
    }

    /**
     * Returns the value of this <code>Double</code> as an <code>int</code>
     * (by casting to type <code>int</code>).
     * 
     * @return the <code>double</code> value represented by this object
     *         converted to type <code>int</code>
     */
    public int intValue() {
        return (int) doubleValue();
    }

    /**
     * Returns the value of this <code>Double</code> as a <code>long</code>
     * (by casting to type <code>long</code>).
     * 
     * @return the <code>double</code> value represented by this object
     *         converted to type <code>long</code>
     */
    public long longValue() {
        return (long) doubleValue();
    }

    /**
     * Returns the <code>float</code> value of this <code>Double</code>
     * object.
     * 
     * @return the <code>double</code> value represented by this object
     *         converted to type <code>float</code>
     * @since JDK1.0
     */
    public float floatValue() {
        return (float) doubleValue();
    }

}
