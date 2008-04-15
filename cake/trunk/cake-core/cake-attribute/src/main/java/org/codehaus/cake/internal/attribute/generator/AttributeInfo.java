package org.codehaus.cake.internal.attribute.generator;

import org.codehaus.cake.attribute.Attribute;

public class AttributeInfo {
    private Attribute a;
    private boolean isMutable;
    private boolean isHidden;

    public AttributeInfo(Attribute a, boolean isMutable, boolean isHidden) {
        if (a == null) {
            throw new NullPointerException("a is null");
        }
        this.a = a;
        this.isMutable = isMutable;
        this.isHidden = isHidden;
    }

    public Attribute getAttribute() {
        return a;
    }

    public boolean isMutable() {
        return isMutable;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public String toString() {
        return a + ", isMut=" + isMutable + ", isHid=" + isHidden;
    }
}
