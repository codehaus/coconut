package org.coconut.cache.test.attributes;

import org.coconut.attribute.spi.AbstractAttribute;

public class StringAttribute2 extends AbstractAttribute<String> {

    public static final StringAttribute2 INSTANCE = new StringAttribute2();

    /** serialVersionUID. */
    private static final long serialVersionUID = 1821856356464961171L;

    private StringAttribute2() {
        super("StringAttribute2", String.class, "string2");
    }

}
