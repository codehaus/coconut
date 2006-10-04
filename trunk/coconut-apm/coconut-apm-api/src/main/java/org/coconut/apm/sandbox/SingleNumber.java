/**
 * 
 */
package org.coconut.apm.sandbox;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface SingleNumber {
    /**
     * Returns the value of the specified number as an <code>int</code>. This
     * may involve rounding or truncation.
     * 
     * @return the numeric value represented by this object after conversion to
     *         type <code>int</code>.
     */
    int intValue();

    /**
     * Returns the value of the specified number as a <code>long</code>. This
     * may involve rounding or truncation.
     * 
     * @return the numeric value represented by this object after conversion to
     *         type <code>long</code>.
     */
    long longValue();

    /**
     * Returns the value of the specified number as a <code>float</code>.
     * This may involve rounding.
     * 
     * @return the numeric value represented by this object after conversion to
     *         type <code>float</code>.
     */
    float floatValue();

    /**
     * Returns the value of the specified number as a <code>double</code>.
     * This may involve rounding.
     * 
     * @return the numeric value represented by this object after conversion to
     *         type <code>double</code>.
     */
    double doubleValue();
}
