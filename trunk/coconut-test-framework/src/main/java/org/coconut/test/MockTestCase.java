package org.coconut.test;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.DynamicMock;
import org.jmock.core.MockObjectSupportTestCase;

public class MockTestCase extends MockObjectTestCase {

    protected volatile boolean threadFailed;

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
        return new CGLIBCoreMock(mockedClass, roleName,
                constructorArgumentTypes, constructorArguments);
    }

}
