/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.attribute;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An abstract implementation of an {@link Attribute} mapping to a int. This implementation adds a
 * number of methods that works on primitive ints instead of their object counterpart.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class CharAttribute extends Attribute<Character> implements
        Comparator<WithAttributes> {

    static final AtomicLong NAME = new AtomicLong();
    /** The default value of this attribute. */
    private final transient char defaultIntValue;

    /**
     * Creates a new IntAttribute with a default value of <tt>0</tt>.
     * 
     * @throws IllegalArgumentException
     *             if 0 is not a valid value
     */
    public CharAttribute() {
        this((char) 0);
    }

    /**
     * Creates a new IntAttribute.
     * 
     * @param defaultValue
     *            the default value of this attribute
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value
     */
    public CharAttribute(char defaultValue) {
        this("CharAttribute" + NAME.incrementAndGet(), defaultValue);
    }

    /**
     * Creates a new IntAttribute with a default value of <tt>0</tt>.
     * 
     * @param name
     *            the name of the attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if 0 is not a valid value
     */
    public CharAttribute(String name) {
        this(name, (char) 0);
    }

    /**
     * Creates a new IntAttribute.
     * 
     * @param name
     *            the name of the attribute
     * @param defaultValue
     *            the default value of this attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value
     */
    public CharAttribute(String name, char defaultValue) {
        super(name, Character.TYPE, defaultValue);
        this.defaultIntValue = defaultValue;
    }

    /**
     * Analogous to {@link #checkValid(Char)} except taking a scalar char.
     * 
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is not valid
     */
    public void checkValid(char value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Illegal value for attribute " + getName()
                    + ", value = " + value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void checkValid(Character o) {
        checkValid(o.charValue());
    }

    /** {@inheritDoc} */
    public int compare(WithAttributes w1, WithAttributes w2) {
        char thisVal = getValue(w1);
        char anotherVal = getValue(w2);
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }

    /** {@inheritDoc} */
    public char fromString(String str) {
        if (str.length() != 1) {
            throw new IllegalArgumentException();
        }
        return str.charAt(0);
    }

    /**
     * Returns the default scalar value of this attribute. This is equivalent to calling
     * {@link #getDefault()}.
     * 
     * @return the default value of this attribute
     */
    public char getDefaultValue() {
        return defaultIntValue;
    }

    /**
     * Analogous to {@link #get(WithAttributes)} except returning a scalar <tt>char</tt>.
     * 
     * @param attributes
     *            the attribute map to retrieve the value of this attribute from
     * @return the value of this attribute
     */
    public char getValue(WithAttributes attributes) {
        return attributes.getAttributes().get(this);
    }

    public char getValue(WithAttributes attributes, char defaultValue) {
        return attributes.getAttributes().get(this, defaultValue);
    }

    /**
     * Analogous to {@link Attribute#isValid(Object)} except taking a primitive Char as parameter.
     * 
     * @param value
     *            the value to check
     * @return whether or not the value is valid
     */
    public boolean isValid(char value) {
        return true;
    }

    /** {@inheritDoc} */
    public char op(WithAttributes t) {
        return getValue(t);
    }

    /**
     * Analogous to {@link #set(AttributeMap, Char)} except taking a primitive Char as parameter.
     * 
     * @param attributes
     *            the attribute map to set the value in.
     * @param value
     *            the value that should be set
     * @return the specified attribute map
     * @throws IllegalArgumentException
     *             if the specified value is not valid accordingly to {@link #checkValid(Char)}
     */
    public AttributeMap set(AttributeMap attributes, char value) {
        if (attributes == null) {
            throw new NullPointerException("attributes is null");
        }
        checkValid(value);
        attributes.put(this, value);
        return attributes;
    }

    public AttributeMap set(WithAttributes attributes, char value) {
        return set(attributes.getAttributes(), value);
    }

    /**
     * Analogous to {@link #singleton(Char)} except taking a primitive Char as parameter.
     * 
     * @param value
     *            the value to create the singleton from
     * @return an AttributeMap containing only this attribute mapping to the specified value
     */
    public AttributeMap singleton(char value) {
        checkValid(value);
        return Attributes.singleton(this, value);
    }
}
