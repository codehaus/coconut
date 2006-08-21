package org.coconut.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.builder.NameMatchBuilder;
import org.jmock.core.DynamicMock;

public class MockTestCase extends MockObjectTestCase {

    protected volatile boolean threadFailed;

    private static final Collection dummies;

    static {
        ArrayList al = new ArrayList();
        al.addAll(Arrays.asList(new Object[] { true, false, 0, 1, 2, 3, 4, 5l, 6l, 7l,
                8l, "A", "B", "C" }));
        dummies = al;
    }

    /**
     * fail with message "should throw exception"
     */
    public void shouldThrow() {
        fail("Should throw exception");
    }

    /**
     * threadFail with message "should throw exception"
     */
    public void threadFailed() {
        threadFailed = true;
        fail("thread failed");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        threadFailed = false;
    }

    @Override
    protected void tearDown() throws Exception {
        assertFalse(threadFailed);
        super.tearDown();
    }

    @SuppressWarnings("unchecked")
    public static <V> V mockDummy(Class<V> arg) {
        return (V) new MockTestCase().mock(arg).proxy();
    }

    @Override
    public org.jmock.Mock mock(Class arg0, String arg1) {
        return super.mock(arg0, arg1);
    }

    @Override
    public org.jmock.Mock mock(Class arg0) {
        return super.mock(arg0);
    }

    public <T> void delegateTest(T front, Mock back, String... methods) throws Exception {
        for (String s : methods) {
            for (Method m : front.getClass().getMethods()) {
                if (m.getName().equals(s)) {
                    NameMatchBuilder nmb = back.expects(once());
                    Object[] parameters = new Object[m.getParameterTypes().length];
                    for (int i = 0; i < parameters.length; i++) {
                        parameters[i] = getObject(m.getParameterTypes()[i]);
                    }
                    if (m.getReturnType().equals(Void.TYPE)) {
                        m.invoke(front, parameters);
                    } else {
                        Object o = getObject(m.getReturnType());
                        nmb.will(returnValue(o));
                        assertEquals(o, m.invoke(front, parameters));
                    }
                }
            }
        }
    }

    private <T> T getObject(Class<T> type) {
        if (type.isPrimitive()) {
            type = fromPrimitive(type);
        }
        if (type.isInterface()) {
            return mockDummy(type);
        } else if (type.isEnum()) {
            return type.getEnumConstants()[0];
        } else if (type.equals(Object.class)) {
            return (T) new Object();
        } else {
            for (Object o : dummies) {
                if (o.getClass().isAssignableFrom(type)) {
                    return (T) o;
                }
            }
        }
        throw new IllegalArgumentException("Unknown type " + type);
    }

    public Mock mock(Class mockedClass, String roleName,
            Class[] constructorArgumentTypes, Object[] constructorArguments) {
        Mock newMock = new Mock(newCoreMock(mockedClass, roleName,
                constructorArgumentTypes, constructorArguments));
        registerToVerify(newMock);
        return newMock;
    }

    public Mock mock(Class mockedClass, Class[] constructorArgumentTypes,
            Object[] constructorArguments) {
        return mock(mockedClass, defaultMockNameForType(mockedClass),
                constructorArgumentTypes, constructorArguments);
    }

    protected DynamicMock newCoreMock(Class mockedType, String roleName) {
        return new CGLIBCoreMock(mockedType, roleName);
    }

    protected DynamicMock newCoreMock(Class mockedClass, String roleName,
            Class[] constructorArgumentTypes, Object[] constructorArguments) {
        return new CGLIBCoreMock(mockedClass, roleName, constructorArgumentTypes,
                constructorArguments);
    }

    private static Class fromPrimitive(Class c) {
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
            throw new Error("unknown type " + c);
        }
    }

}
