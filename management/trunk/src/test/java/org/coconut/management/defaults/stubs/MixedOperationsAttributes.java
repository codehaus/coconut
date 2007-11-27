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
