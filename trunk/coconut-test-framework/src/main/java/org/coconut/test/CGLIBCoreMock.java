/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.test;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.jmock.core.AbstractDynamicMock;
import org.jmock.core.Invocation;
import org.jmock.core.InvocationDispatcher;
import org.jmock.core.LIFOInvocationDispatcher;

class CGLIBCoreMock extends AbstractDynamicMock implements MethodInterceptor {
    private Object proxy = null;

    public CGLIBCoreMock(Class mockedType, String name) {
        this(mockedType, name, new LIFOInvocationDispatcher());
    }

    public CGLIBCoreMock(Class mockedType, String name,
            Class[] constructorArgumentTypes, Object[] constructorArguments) {
        this(mockedType, name, constructorArgumentTypes, constructorArguments,
                new LIFOInvocationDispatcher());
    }

    public CGLIBCoreMock(Class mockedType, String name,
            InvocationDispatcher invocationDispatcher) {
        this(mockedType, name, new Class[0], new Object[0],
                invocationDispatcher);
    }

    public CGLIBCoreMock(Class mockedType, String name,
            Class[] constructorArgumentTypes, Object[] constructorArguments,
            InvocationDispatcher invocationDispatcher) {
        super(mockedType, name, invocationDispatcher);
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(mockedType.getClassLoader());
        enhancer.setSuperclass(mockedType);
        enhancer.setCallback(this);
        this.proxy = enhancer.create(constructorArgumentTypes,
                constructorArguments);
    }

    public Object proxy() {
        return this.proxy;
    }

    public Object intercept(Object thisProxy, Method method, Object[] args,
            MethodProxy superProxy) throws Throwable {
        if (proxy == null) {
            return superProxy.invokeSuper(thisProxy, args);
        } else {
            return mockInvocation(new Invocation(proxy, method, args));
        }
    }
}