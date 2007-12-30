package org.coconut.cache.test.attributes;

import org.coconut.attribute.spi.AbstractAttribute;

public class StringAttribute1 extends AbstractAttribute<String> {

    public static final StringAttribute1 INSTANCE = new StringAttribute1();

    /** serialVersionUID. */
    private static final long serialVersionUID = 1821856356464961171L;

    private StringAttribute1() {
        super("StringAttribute1", String.class, "string1");
    }
}
