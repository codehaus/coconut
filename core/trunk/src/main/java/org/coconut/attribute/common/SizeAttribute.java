/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.common;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.AttributeMaps;
import org.coconut.attribute.spi.LongAttribute;

/**
 * The <tt>Size</tt> attribute indicates the <tt>size</tt> of some element. The mapped
 * value must is of type <tt>long</tt> and between 0 and {@link Long#MAX_VALUE}.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class SizeAttribute extends LongAttribute {

    /** The default value of this attribute. */
    public static final long DEFAULT_VALUE = 1;

    /** The singleton instance of this attribute. */
    public final static SizeAttribute INSTANCE = new SizeAttribute();

    /** The name of this attribute. */
    public static final String NAME = "size";

    /** serialVersionUID. */
    private static final long serialVersionUID = -2353351535602223603L;

    /** Creates a new SizeAttribute. */
    private SizeAttribute() {
        super(NAME, DEFAULT_VALUE);
    }

    /** {@inheritDoc} */
    @Override
    public void checkValid(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("invalid size (size = " + size + ")");
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isValid(long size) {
        return size >= 0;
    }

    /** @return Preserves singleton property */
    private Object readResolve() {
        return INSTANCE;
    }

    public static long get(AttributeMap attributes) {
        return INSTANCE.getPrimitive(attributes);
    }

    public static AttributeMap set(AttributeMap attributes, long value) {
        return INSTANCE.setAtttribute(attributes, value);
    }

    public static AttributeMap singleton(long value) {
        return AttributeMaps.singleton(INSTANCE, value);
    }
}
