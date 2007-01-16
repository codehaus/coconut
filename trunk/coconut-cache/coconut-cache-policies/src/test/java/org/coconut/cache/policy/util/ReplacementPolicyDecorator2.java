/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.test.MockTestCase;
import org.jmock.Mock;
import org.jmock.builder.ArgumentsMatchBuilder;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class ReplacementPolicyDecorator2 extends MockTestCase {

    private static Map<Class, Object> types = new HashMap<Class, Object>();
    static {
        types.put(Integer.TYPE, 5);
        types.put(Boolean.TYPE, true);
        types.put(Object.class, new Object());
        types.put(Integer.class, 3);
        types.put(List.class, new LinkedList());
        types.put(String.class, "Foo");
    }

    public static void main(String[] args) throws Exception {

        new ReplacementPolicyDecorator2().test();
        // System.out.println(Arrays.toString(m));
    }

    void test() throws Exception {
        Object returnType = null;
        Mock mock = mock(ReplacementPolicy.class);
        Method[] methods = PolicyDecorator.class.getMethods();
        PolicyDecorator dec = new PolicyDecorator((ReplacementPolicy) mock
                .proxy());
        for (Method m : methods) {
            if (!Modifier.isFinal(m.getModifiers())) {
                ArgumentsMatchBuilder nmb = mock.expects(once()).method(m.getName());
                if (!m.getReturnType().equals(Void.TYPE)) {
                    Object r = types.get(m.getReturnType());
                    if (r == null) {
                        throw new IllegalStateException("No return types of " + m.getReturnType()
                                + " registered");
                    }
                    nmb.will(returnValue(r));
                }
                returnType = types.get(m.getReturnType());
                if (m.getParameterTypes().length == 0) {
                    Object returnValue = m.invoke(dec);
                    assertEquals(returnValue, returnType);
                } else {
                 // nmb.m
                    System.out.println("could not test " + m);
                }

            }

        }
    }
}
