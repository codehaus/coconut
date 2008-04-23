/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.management.stubs;

import org.codehaus.cake.management.annotation.ManagedAttribute;
import org.codehaus.cake.management.annotation.ManagedOperation;

public class MixedOperationsAttributes {
    public int invokeCount;

    private String string;

    @ManagedAttribute
    public String getString() {
        return string;
    }

    @ManagedOperation
    public void method1() {
        invokeCount += 1;
    }

    public void setString(String string) {
        this.string = string;
    }
}
