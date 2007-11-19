package org.coconut.management.defaults;

import java.lang.reflect.Method;

import javax.management.MBeanParameterInfo;

public class ManagementUtil {
    public static String filterString(Object o, String str) {
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
}
