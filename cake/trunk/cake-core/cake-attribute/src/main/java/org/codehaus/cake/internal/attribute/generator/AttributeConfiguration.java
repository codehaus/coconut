package org.codehaus.cake.internal.attribute.generator;

import org.codehaus.cake.attribute.Attribute;

public interface AttributeConfiguration {
    Attribute getAttribute();

    boolean isHidden();

    boolean isMutable();
}
