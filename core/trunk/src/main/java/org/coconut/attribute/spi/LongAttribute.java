/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.attribute.spi;

import org.coconut.attribute.AttributeMap;

public abstract class LongAttribute extends AbstractAttribute<Long> {

    private final long defaultValue;

    public LongAttribute(String name, Long defaultValue) {
        super(name, Long.TYPE, defaultValue);
        this.defaultValue = defaultValue;
    }

    public Long fromString(String str) {
        return Long.parseLong(str);
    }

    public long getPrimitive(AttributeMap attributes) {
        return attributes.getLong(this,defaultValue);
    }

    public long getPrimitive(AttributeMap attributes, long defaultValue) {
        return attributes.getLong(this, defaultValue);
    }

    public final boolean isValid(Long value) {
        return isValid(value.longValue());
    }

    public boolean isValid(long value) {
        return true;
    }

    public AttributeMap setAttribute(AttributeMap attributes, long value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.putLong(this, value);
        return attributes;
    }

    @Override
    public final void checkValid(Long o) {
        checkValid(o.longValue());
    }

    public void checkValid(long d) {  /* default ok */}
}
