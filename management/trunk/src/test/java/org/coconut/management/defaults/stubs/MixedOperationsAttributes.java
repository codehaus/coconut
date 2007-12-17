/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults.stubs;

import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management.annotation.ManagedOperation;

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
