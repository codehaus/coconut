/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.management.stubs;

import org.codehaus.cake.util.management.annotation.ManagedAttribute;
import org.codehaus.cake.util.management.annotation.ManagedOperation;

public class MixedOperationsAttributes {
    private String string;

    @ManagedAttribute
    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
    
    public int invokeCount;

    @ManagedOperation
    public void method1() {
        invokeCount += 1;
    }
}
