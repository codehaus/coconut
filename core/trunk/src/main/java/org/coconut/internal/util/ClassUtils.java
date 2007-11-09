/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class ClassUtils {
    
    ///CLOVER:OFF
    /** Cannot instantiate. */
    private ClassUtils() {}
    ///CLOVER:ON
    
    public static Class fromPrimitive(Class c) {
        if (c.equals(Integer.TYPE)) {
            return Integer.class;
        } else if (c.equals(Double.TYPE)) {
            return Double.class;
        } else if (c.equals(Byte.TYPE)) {
            return Byte.class;
        } else if (c.equals(Float.TYPE)) {
            return Float.class;
        } else if (c.equals(Long.TYPE)) {
            return Long.class;
        } else if (c.equals(Short.TYPE)) {
            return Short.class;
        } else if (c.equals(Boolean.TYPE)) {
            return Boolean.class;
        } else if (c.equals(Character.TYPE)) {
            return Character.class;
        } else {
            return null;
        }
    }

    public static boolean isNumberOrPrimitiveNumber(Class c) {
        if (Number.class.isAssignableFrom(c)) {
            return true;
        }
        Class cc = fromPrimitive(c);
        return cc != null && Number.class.isAssignableFrom(cc);
    }
}
