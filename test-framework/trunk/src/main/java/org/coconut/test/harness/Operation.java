package org.coconut.test.harness;

public class Operation {
    public final String operation;

    public long invocations;

    public Operation(String operation) {
        this.operation = operation;
    }
    
    public String toString() {
        return operation + " " + invocations + " invocations";
    }
}
