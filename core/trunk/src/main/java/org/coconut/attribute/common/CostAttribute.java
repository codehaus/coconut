/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.Attributes;
import org.coconut.attribute.spi.AbstractDoubleAttribute;

/**
 * The <tt>Cost</tt> attribute indicates the <tt>cost</tt> of retrieving or
 * calculating an element. The mapped value must be of a type <tt>double</tt> and can be
 * any value except {@link Double#NaN}, {@link Double#NEGATIVE_INFINITY} or
 * {@link Double#POSITIVE_INFINITY}
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class CostAttribute extends AbstractDoubleAttribute {

    /** The default value of the Cost attribute. */
    public static final double DEFAULT_VALUE = 1.0;

    /** The singleton instance of this attribute. */
    public final static CostAttribute INSTANCE = new CostAttribute();

    /** The name of this attribute. */
    public static final String NAME = "cost";

    /** serialVersionUID. */
    private static final long serialVersionUID = -2353351535602223603L;

    /** Creates a new CostAttribute. */
    private CostAttribute() {
        super(NAME, DEFAULT_VALUE);
    }

    /** @return Preserves singleton property */
    private Object readResolve() {
        return INSTANCE;
    }

    /**
     * Returns the value of this attribute in the specified attribute map, or
     * DEFAULT_VALUE if the attribute is not mapped to any value in the specified
     * attribute map.
     *
     * @param attributes
     *            the attribute map to return the value from
     * @return the value of this attribute in the specified attribute map, or
     *         DEFAULT_VALUE if the attribute is not mapped to any value in the specified
     *         attribute map
     */
    public static double get(AttributeMap attributes) {
        return INSTANCE.getPrimitive(attributes);
    }

    /**
     * Sets the value of this attribute in the specified attribute map.
     *
     * @param attributes
     *            the attribute map to set set specified value in
     * @param value
     *            the value that this attribute should be set to
     * @return the specified attribute map
     */
    public static AttributeMap set(AttributeMap attributes, double value) {
        return INSTANCE.setAttribute(attributes, value);
    }

    /**
     * Returns an AttributeMap containing only this attribute mapping to the specified
     * value.
     *
     * @param value
     *            the value to map to
     * @return an AttributeMap containing only this attribute mapping to the specified
     *         value
     */
    public static AttributeMap singleton(double value) {
        INSTANCE.checkValid(value);
        return Attributes.singleton(INSTANCE, value);
    }
}
