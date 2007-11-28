/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import org.coconut.attribute.spi.DoubleAttribute;

/**
 * The <tt>Cost</tt> attribute indicates the <tt>cost</tt> of retrieving or
 * calculating an element. The mapped value must be of a type <tt>double</tt> and can be
 * any value except {@link Double#NaN}, {@link Double#NEGATIVE_INFINITY} or
 * {@link Double#POSITIVE_INFINITY}
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CostAttribute extends DoubleAttribute {

    /** The default value of the Cost attribute. */
    public static final double DEFAULT_COST = 1.0;

    public final static CostAttribute INSTANCE = new CostAttribute();

    private CostAttribute() {
        super("Cost", DEFAULT_COST);
    }

    // Preserves singleton property
    private Object readResolve() {
        return INSTANCE;
    }
}
