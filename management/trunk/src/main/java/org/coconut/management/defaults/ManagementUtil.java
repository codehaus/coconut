/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import java.lang.reflect.Method;

import javax.management.MBeanParameterInfo;

/**
 * Various utility functions.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
final class ManagementUtil {

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private ManagementUtil() {}

    // /CLOVER:ON
    /**
     * Filters the specified string, currently does nothing.
     * 
     * @param o
     *            the object to filter
     * @param str
     *            the string to filter
     * @return the specified string
     */
    static String filterString(Object o, String str) {
        // if (o instanceof Named) {
        // Named n = (Named) o;
        // str = str.replace("$name", n.getName());
        // // System.out.println(n.getName());
        // }
        // if (o instanceof Described) {
        // Described n = (Described) o;
        // str = str.replace("$description", n.getDescription());
        // }
        return str;
    }

    /**
     * Returns information about the parameters of the specified method.
     * 
     * @param method
     *            the method to return parameter info about
     * @return information about the parameters of the specified method
     */
    static MBeanParameterInfo[] methodSignature(Method method) {
        Class[] classes = method.getParameterTypes();
        MBeanParameterInfo[] params = new MBeanParameterInfo[classes.length];

        for (int i = 0; i < classes.length; i++) {
            String parameterName = "p" + (i + 1);
            params[i] = new MBeanParameterInfo(parameterName, classes[i].getName(), "");
        }

        return params;
    }

    static String[] methodStringSignature(Method method) {
        Class[] classes = method.getParameterTypes();
        String[] params = new String[classes.length];

        for (int i = 0; i < classes.length; i++) {
            params[i] = classes[i].getName();
        }
        return params;
    }
}
