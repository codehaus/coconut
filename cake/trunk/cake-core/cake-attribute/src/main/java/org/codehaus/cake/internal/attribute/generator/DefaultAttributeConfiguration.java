package org.codehaus.cake.internal.attribute.generator;

import org.codehaus.cake.attribute.Attribute;

public class DefaultAttributeConfiguration implements AttributeConfiguration {
    private final Attribute a;

    private final boolean isHidden;

    private final boolean isMutable;

    public DefaultAttributeConfiguration(Attribute a, boolean isMutable, boolean isHidden) {
        if (a == null) {
            throw new NullPointerException("a is null");
        }
        this.a = a;
        this.isMutable = isMutable;
        this.isHidden = isHidden;
    }

    public DefaultAttributeConfiguration(DefaultAttributeConfiguration other) {
        this.a = other.a;
        this.isHidden = other.isHidden;
        this.isMutable = other.isMutable;
    }

    @Override
    public boolean equals(Object obj) {
        DefaultAttributeConfiguration c = (DefaultAttributeConfiguration) obj;
        return c.a == a && c.isHidden == isHidden && c.isMutable == isMutable;
    }

    public Attribute getAttribute() {
        return a;
    }

    @Override
    public int hashCode() {
        return a.hashCode();
    }

    public boolean isHidden() {
        return isHidden;
    }

    public boolean isMutable() {
        return isMutable;
    }

    public String toString() {
        return a + ", isMut=" + isMutable + ", isHid=" + isHidden;
    }
}
