/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://cake.codehaus.org/LICENSE
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.attribute;

import java.util.Comparator;

/**
 * An implementation of an {@link Attribute} mapping to a char. This implementation adds a number of
 * methods that works on primitive chars instead of their object counterpart.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CharAttribute.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class CharAttribute extends Attribute<Character> implements
        Comparator<WithAttributes> {

    /** The default value of this attribute. */
    private final transient char defaultValue;

    /**
     * Creates a new CharAttribute with a generated name and a default value of
     * <tt>$defaultValueNoCast</tt>.
     * 
     * @throws IllegalArgumentException
     *             if $defaultValueNoCast is not a valid value according to {@link #checkValid(int)}
     */
    public CharAttribute() {
        this((char) 0);
    }

    /**
     * Creates a new CharAttribute with a generated name.
     * 
     * @param defaultValue
     *            the default value of this attribute
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value according to
     *             {@link #checkValid(char)}
     */
    public CharAttribute(char defaultValue) {
        super(Character.TYPE, defaultValue);
        this.defaultValue = defaultValue;
    }

    /**
     * Creates a new CharAttribute with a default value of <tt>$defaultValueNoCast</tt>.
     * 
     * @param name
     *            the name of the attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if $defaultValueNoCast is not a valid value according to
     *             {@link #checkValid(char)}
     */
    public CharAttribute(String name) {
        this(name, (char) 0);
    }

    /**
     * Creates a new CharAttribute.
     * 
     * @param name
     *            the name of the attribute
     * @param defaultValue
     *            the default value of the attribute
     * @throws NullPointerException
     *             if the specified name is <code>null</code>
     * @throws IllegalArgumentException
     *             if the specified default value is not a valid value according to
     *             {@link #checkValid(char)}
     */
    public CharAttribute(String name, char defaultValue) {
        super(name, Character.TYPE, defaultValue);
        this.defaultValue = defaultValue;
    }

    /**
     * Analogous to {@link #checkValid(Character)} except taking a primitive char.
     * 
     * @param value
     *            the value to check
     * @throws IllegalArgumentException
     *             if the specified value is not valid
     */
    public void checkValid(char value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Illegal value for attribute [name=" + getName()
                    + ", type = " + getClass() + ", value = " + value + "]");
        }
    }

    /** {@inheritDoc} */
    @Override
    public final void checkValid(Character o) {
        checkValid(o.charValue());
    }

    /** {@inheritDoc} */
    public int compare(WithAttributes w1, WithAttributes w2) {
        char thisVal = get(w1);
        char anotherVal = get(w2);
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }

    /**
     * Creates a value instance of this attribute from the specified string.
     * 
     * @param str
     *            the string to create the value from.
     * @return a value instance from the specified string
     * @throws IllegalArgumentException
     *             if a valid value could not be created from the string.
     */
    public char fromString(String str) {
        if (str.length() != 1) {
            throw new IllegalArgumentException();
        }
        return str.charAt(0);
    }

    /**
     * Analogous to {@link #get(WithAttributes)} except returning a primitive <tt>char</tt>.
     * 
     * @param attributes
     *            an object containing an AttributeMap
     * @return the value of this attribute if this attribute is present in the map. Otherwise
     *         {@link #getDefaultValue()}
     */
    public char get(WithAttributes attributes) {
        return attributes.getAttributes().get(this);
    }

    /**
     * Analogous to {@link #get(WithAttributes)} except returning a primitive <tt>char</tt>.
     * 
     * @param attributes
     *            an object containing an AttributeMap
     * @param defaultValue
     *            the default value to return if this attribute is not present in the map
     * @return the value of this attribute if this attribute is present in the map. Otherwise the
     *         specified default value
     */
    public char get(WithAttributes attributes, char defaultValue) {
        return attributes.getAttributes().get(this, defaultValue);
    }

    /**
     * Returns the default scalar value of this attribute. This is equivalent to calling
     * {@link #getDefault()}, but returning a primitive int instead.
     * 
     * @return the default value of this attribute
     */
    public char getDefaultValue() {
        return defaultValue;
    }

    /**
     * Analogous to {@link Attribute#isValid(Object)} except taking a primitive char as parameter.
     * <p>
     * The default version returns true for all parameters
     * 
     * @param value
     *            the value to check
     * @return whether or not the value is valid
     */
    public boolean isValid(char value) {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public final boolean isValid(Character value) {
        return isValid(value.charValue());
    }

    /**
     * Analogous to {@link #set(AttributeMap, Character)} except taking a primitive char as
     * parameter.
     * 
     * @param attributes
     *            the attribute map to set the value in.
     * @param value
     *            the value that should be set
     * @return the specified attribute map
     * @throws IllegalArgumentException
     *             if the specified value is not valid accordingly to {@link #checkValid(Character)}
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
     * Analogous to {@link #singleton(Character)} except taking a primitive char as parameter.
     * 
     * @param value
     *            the value to create the singleton from
     * @return an AttributeMap containing only this attribute mapping to the specified value
     */
    public AttributeMap singleton(char value) {
        return super.singleton(value);
    }
}
