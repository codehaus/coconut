/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.management.stubs;

import java.io.IOException;

import org.codehaus.cake.management.annotation.ManagedOperation;

public class OperationStub {
    public int invokeCount;

    @ManagedOperation
    public void method1() {
        invokeCount += 1;
    }

    @ManagedOperation(defaultValue = "mymethod", description = "desc")
    public void method2() {
        invokeCount += 2;
    }

    @ManagedOperation
    public void method3(Boolean arg) {
        invokeCount += 16;
    }

    @ManagedOperation
    public void method3(boolean arg) {
        invokeCount += 8;
    }

    @ManagedOperation
    public void method3(String arg) {
        invokeCount += 4;
    }

    @ManagedOperation
    public String method4() {
        invokeCount += 32;
        return "" + invokeCount;
    }

    @ManagedOperation
    public int method5() {
        invokeCount += 64;
        return invokeCount;
    }

    @ManagedOperation
    public void throwError() {
        throw new LinkageError();
    }

    @ManagedOperation
    public void throwException() throws Exception {
        throw new IOException();
    }

    @ManagedOperation
    public void throwRuntimeException() {
        throw new IllegalMonitorStateException();
    }

}
