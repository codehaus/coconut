/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management.defaults;

import java.util.Arrays;

public class OperationKey {
    private final String methodName;

    private final String[] signature;

    private int hashCode;

    public OperationKey(String methodName, String... signature) {
        this.methodName = methodName;
        if (signature == null) {
            signature = new String[0];
        }
        this.signature = signature.clone();
        hashCode = methodName.hashCode() ^ Arrays.hashCode(signature);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof OperationKey && equals((OperationKey) obj);
    }

    public boolean equals(OperationKey obj) {
        return methodName.equals(obj.methodName) && Arrays.equals(signature, obj.signature);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
