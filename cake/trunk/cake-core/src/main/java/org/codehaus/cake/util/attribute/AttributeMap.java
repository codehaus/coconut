/* Copyright 2004 - 2008 Kasper Nielsen <kasper@codehaus.org>
 * Licensed under the Apache 2.0 License. */
package org.codehaus.cake.util.attribute;

import java.util.Map;

/**
 * A map specifically for the storage of Attribute->Object values. Attribute-maps are
 * often only used to hold a very small amount of entries (perhaps just one). An
 * implementations of this interface might be memory-wise optimized to hold only a single
 * entry.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AttributeMap.java 415 2007-11-09 08:25:23Z kasper $
 */
public interface AttributeMap extends Map<Attribute, Object> {

    /**
     * Returns the value to which this attribute-map maps the specified key. Returns the
     * specified <tt>defaultValue</tt> if the attribute-map contains no mapping for this
     * key. A return value with the same value as defaultValue does not <i>necessarily</i>
     * indicate that the attribute-map contains no mapping for the key; it's also possible
     * that the attribute-map explicitly maps the key to <tt>defaultValue</tt>. The
     * <tt>containsKey</tt> operation may be used to distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>defaultValue</tt>.
     * (There can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @param defaultValue
     *            the value to return if this attribute-map contains no mapping for the
     *            specified key
     * @return the value to which this map maps the specified key, or the specified
     *         default value if the map contains no mapping for this key.
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    Object get(Attribute key, Object defaultValue);

    /**
     * Returns the boolean value to which this attribute-map maps the specified key.
     * Returns <tt>false</tt> if the attribute-map contains no mapping for this key. A
     * return value of <tt>false</tt> does not <i>necessarily</i> indicate that the
     * attribute-map contains no mapping for the key; it's also possible that the
     * attribute-map explicitly maps the key to <tt>false</tt>. The
     * <tt>containsKey</tt> operation may be used to distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>false</tt>.
     * (There can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or <tt>false</tt> if
     *         the map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a bool (or Boolean)
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    boolean getBoolean(Attribute<Boolean> key);

    /**
     * Returns the boolean value to which this attribute-map maps the specified key.
     * Returns the specified <tt>defaultValue</tt> if the attribute-map contains no
     * mapping for this key. A return value with the same value as defaultValue does not
     * <i>necessarily</i> indicate that the attribute-map contains no mapping for the
     * key; it's also possible that the attribute-map explicitly maps the key to
     * <tt>defaultValue</tt>. The <tt>containsKey</tt> operation may be used to
     * distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>defaultValue</tt>.
     * (There can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @param defaultValue
     *            the value to return if this attribute-map contains no mapping for the
     *            specified key
     * @return the value to which this map maps the specified key, or the specified
     *         default value if the map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a boolean (or Boolean)
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    boolean getBoolean(Attribute<Boolean> key, boolean defaultValue);

    /**
     * Returns the byte value to which this attribute-map maps the specified key. Returns
     * <tt>0</tt> if the attribute-map contains no mapping for this key. A return value
     * of <tt>0</tt> does not <i>necessarily</i> indicate that the attribute-map
     * contains no mapping for the key; it's also possible that the attribute-map
     * explicitly maps the key to <tt>0</tt>. The <tt>containsKey</tt> operation may
     * be used to distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>0</tt>. (There
     * can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or <tt>0</tt> if the
     *         map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a byte (or Byte)
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    byte getByte(Attribute<Byte> key);

    /**
     * Returns the byte value to which this attribute-map maps the specified key. Returns
     * the specified <tt>defaultValue</tt> if the attribute-map contains no mapping for
     * this key. A return value with the same value as defaultValue does not
     * <i>necessarily</i> indicate that the attribute-map contains no mapping for the
     * key; it's also possible that the attribute-map explicitly maps the key to
     * <tt>defaultValue</tt>. The <tt>containsKey</tt> operation may be used to
     * distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>defaultValue</tt>.
     * (There can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @param defaultValue
     *            the value to return if this attribute-map contains no mapping for the
     *            specified key
     * @return the value to which this map maps the specified key, or the specified
     *         default value if the map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a byte (or Byte)
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    byte getByte(Attribute<Byte> key, byte defaultValue);

    /**
     * Returns the short value to which this attribute-map maps the specified key. Returns
     * <tt>'\u0000'</tt> if the attribute-map contains no mapping for this key. A return
     * value of <tt>'\u0000'</tt> does not <i>necessarily</i> indicate that the
     * attribute-map contains no mapping for the key; it's also possible that the
     * attribute-map explicitly maps the key to <tt>'\u0000'</tt>. The
     * <tt>containsKey</tt> operation may be used to distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>'\u0000'</tt>.
     * (There can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or <tt>'\u0000'</tt>
     *         if the map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a char (or haracter}
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    char getChar(Attribute<Character> key);

    /**
     * Returns the char value to which this attribute-map maps the specified key. Returns
     * the specified <tt>defaultValue</tt> if the attribute-map contains no mapping for
     * this key. A return value with the same value as defaultValue does not
     * <i>necessarily</i> indicate that the attribute-map contains no mapping for the
     * key; it's also possible that the attribute-map explicitly maps the key to
     * <tt>defaultValue</tt>. The <tt>containsKey</tt> operation may be used to
     * distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>defaultValue</tt>.
     * (There can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @param defaultValue
     *            the value to return if this attribute-map contains no mapping for the
     *            specified key
     * @return the value to which this map maps the specified key, or the specified
     *         default value if the map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a char (or Character)
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    char getChar(Attribute<Character> key, char defaultValue);

    /**
     * Returns the float value to which this attribute-map maps the specified key. Returns
     * <tt>0.0</tt> if the attribute-map contains no mapping for this key. A return
     * value of <tt>0.0</tt> does not <i>necessarily</i> indicate that the
     * attribute-map contains no mapping for the key; it's also possible that the
     * attribute-map explicitly maps the key to <tt>0.0</tt>. The <tt>containsKey</tt>
     * operation may be used to distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>0.0</tt>.
     * (There can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or <tt>0.0</tt> if
     *         the map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a double (or Double)
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    double getDouble(DoubleAttribute key);

    /**
     * Returns the double value to which this attribute-map maps the specified key.
     * Returns the specified <tt>defaultValue</tt> if the attribute-map contains no
     * mapping for this key. A return value with the same value as defaultValue does not
     * <i>necessarily</i> indicate that the attribute-map contains no mapping for the
     * key; it's also possible that the attribute-map explicitly maps the key to
     * <tt>defaultValue</tt>. The <tt>containsKey</tt> operation may be used to
     * distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>defaultValue</tt>.
     * (There can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @param defaultValue
     *            the value to return if this attribute-map contains no mapping for the
     *            specified key
     * @return the value to which this map maps the specified key, or the specified
     *         default value if the map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a double (or Double)
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    double getDouble(DoubleAttribute key, double defaultValue);

    /**
     * Returns the float value to which this attribute-map maps the specified key. Returns
     * <tt>0.0</tt> if the attribute-map contains no mapping for this key. A return
     * value of <tt>0.0</tt> does not <i>necessarily</i> indicate that the
     * attribute-map contains no mapping for the key; it's also possible that the
     * attribute-map explicitly maps the key to <tt>0.0</tt>. The <tt>containsKey</tt>
     * operation may be used to distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>0.0</tt>.
     * (There can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or <tt>0.0</tt> if
     *         the map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a float (or Float)
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    float getFloat(Attribute<Float> key);

    /**
     * Returns the float value to which this attribute-map maps the specified key. Returns
     * the specified <tt>defaultValue</tt> if the attribute-map contains no mapping for
     * this key. A return value with the same value as defaultValue does not
     * <i>necessarily</i> indicate that the attribute-map contains no mapping for the
     * key; it's also possible that the attribute-map explicitly maps the key to
     * <tt>defaultValue</tt>. The <tt>containsKey</tt> operation may be used to
     * distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>defaultValue</tt>.
     * (There can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @param defaultValue
     *            the value to return if this attribute-map contains no mapping for the
     *            specified key
     * @return the value to which this map maps the specified key, or the specified
     *         default value if the map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a float (or Float)
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    float getFloat(Attribute<Float> key, float defaultValue);

    /**
     * Returns the int value to which this attribute-map maps the specified key. Returns
     * <tt>0</tt> if the attribute-map contains no mapping for this key. A return value
     * of <tt>0</tt> does not <i>necessarily</i> indicate that the attribute-map
     * contains no mapping for the key; it's also possible that the attribute-map
     * explicitly maps the key to <tt>0</tt>. The <tt>containsKey</tt> operation may
     * be used to distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>0</tt>. (There
     * can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or <tt>0</tt> if the
     *         map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a int (or Integer)
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    int getInt(IntAttribute key);

    /**
     * Returns the int value to which this attribute-map maps the specified key. Returns
     * the specified <tt>defaultValue</tt> if the attribute-map contains no mapping for
     * this key. A return value with the same value as defaultValue does not
     * <i>necessarily</i> indicate that the attribute-map contains no mapping for the
     * key; it's also possible that the attribute-map explicitly maps the key to
     * <tt>defaultValue</tt>. The <tt>containsKey</tt> operation may be used to
     * distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>defaultValue</tt>.
     * (There can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @param defaultValue
     *            the value to return if this attribute-map contains no mapping for the
     *            specified key
     * @return the value to which this map maps the specified key, or the specified
     *         default value if the map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a int (or Integer)
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    int getInt(IntAttribute key, int defaultValue);

    /**
     * Returns the long value to which this attribute-map maps the specified key. Returns
     * <tt>0</tt> if the attribute-map contains no mapping for this key. A return value
     * of <tt>0</tt> does not <i>necessarily</i> indicate that the attribute-map
     * contains no mapping for the key; it's also possible that the attribute-map
     * explicitly maps the key to <tt>0</tt>. The <tt>containsKey</tt> operation may
     * be used to distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>0</tt>. (There
     * can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or <tt>0</tt> if the
     *         map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a long (or Long)
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    long getLong(LongAttribute key);

    /**
     * Returns the long value to which this attribute-map maps the specified key. Returns
     * the specified <tt>defaultValue</tt> if the attribute-map contains no mapping for
     * this key. A return value with the same value as defaultValue does not
     * <i>necessarily</i> indicate that the attribute-map contains no mapping for the
     * key; it's also possible that the attribute-map explicitly maps the key to
     * <tt>defaultValue</tt>. The <tt>containsKey</tt> operation may be used to
     * distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>defaultValue</tt>.
     * (There can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @param defaultValue
     *            the value to return if this attribute-map contains no mapping for the
     *            specified key
     * @return the value to which this map maps the specified key, or the specified
     *         default value if the map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a long (or Long)
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    long getLong(LongAttribute key, long defaultValue);

    /**
     * Returns the short value to which this attribute-map maps the specified key. Returns
     * <tt>0</tt> if the attribute-map contains no mapping for this key. A return value
     * of <tt>0</tt> does not <i>necessarily</i> indicate that the attribute-map
     * contains no mapping for the key; it's also possible that the attribute-map
     * explicitly maps the key to <tt>0</tt>. The <tt>containsKey</tt> operation may
     * be used to distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>0</tt>. (There
     * can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or <tt>0</tt> if the
     *         map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a short (or Short)
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    short getShort(Attribute<Short> key);

    /**
     * Returns the short value to which this attribute-map maps the specified key. Returns
     * the specified <tt>defaultValue</tt> if the attribute-map contains no mapping for
     * this key. A return value with the same value as defaultValue does not
     * <i>necessarily</i> indicate that the attribute-map contains no mapping for the
     * key; it's also possible that the attribute-map explicitly maps the key to
     * <tt>defaultValue</tt>. The <tt>containsKey</tt> operation may be used to
     * distinguish these two cases.
     * <p>
     * More formally, if this attribute-map contains a mapping from a key <tt>k</tt> to
     * a value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>,
     * then this method returns <tt>v</tt>; otherwise it returns <tt>defaultValue</tt>.
     * (There can be at most one such mapping.)
     *
     * @param key
     *            key whose associated value is to be returned.
     * @param defaultValue
     *            the value to return if this attribute-map contains no mapping for the
     *            specified key
     * @return the value to which this map maps the specified key, or the specified
     *         default value if the map contains no mapping for this key.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a short (or Short)
     * @throws NullPointerException
     *             if the key is <tt>null</tt>.
     * @see #containsKey(Object)
     */
    short getShort(Attribute<Short> key, short defaultValue);

    /**
     * Associates the specified boolean value with the specified key in this attribute-map
     * (optional operation). If the attribute-map previously contained a mapping for this
     * key, the old value is replaced by the specified value. (A attribute-map <tt>am</tt>
     * is said to contain a mapping for a key <tt>k</tt> if and only if
     * {@link #containsKey(Object) am.containsKey(k)} would return <tt>true</tt>.))
     *
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this map. For
     *             example, for a read-only attribute-map
     * @throws IllegalArgumentException
     *             if some aspect of this key or value prevents it from being stored in
     *             this map.
     * @throws NullPointerException
     *             if the key is <tt>null</tt>
     */
    boolean putBoolean(Attribute<Boolean> key, boolean value);

    /**
     * Associates the specified byte value with the specified key in this attribute-map
     * (optional operation). If the attribute-map previously contained a mapping for this
     * key, the old value is replaced by the specified value. (A attribute-map <tt>am</tt>
     * is said to contain a mapping for a key <tt>k</tt> if and only if
     * {@link #containsKey(Object) am.containsKey(k)} would return <tt>true</tt>.))
     *
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this map. For
     *             example, for a read-only attribute-map
     * @throws IllegalArgumentException
     *             if some aspect of this key or value prevents it from being stored in
     *             this map.
     * @throws NullPointerException
     *             if the key is <tt>null</tt>
     */
    byte putByte(Attribute<Byte> key, byte value);

    /**
     * Associates the specified char value with the specified key in this attribute-map
     * (optional operation). If the attribute-map previously contained a mapping for this
     * key, the old value is replaced by the specified value. (A attribute-map <tt>am</tt>
     * is said to contain a mapping for a key <tt>k</tt> if and only if
     * {@link #containsKey(Object) am.containsKey(k)} would return <tt>true</tt>.))
     *
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this map. For
     *             example, for a read-only attribute-map
     * @throws IllegalArgumentException
     *             if some aspect of this key or value prevents it from being stored in
     *             this map.
     * @throws NullPointerException
     *             if the key is <tt>null</tt>
     */
    char putChar(Attribute<Character> key, char value);

    /**
     * Associates the specified double value with the specified key in this attribute-map
     * (optional operation). If the attribute-map previously contained a mapping for this
     * key, the old value is replaced by the specified value. (A attribute-map <tt>am</tt>
     * is said to contain a mapping for a key <tt>k</tt> if and only if
     * {@link #containsKey(Object) am.containsKey(k)} would return <tt>true</tt>.))
     *
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this map. For
     *             example, for a read-only attribute-map
     * @throws IllegalArgumentException
     *             if some aspect of this key or value prevents it from being stored in
     *             this map.
     * @throws NullPointerException
     *             if the key is <tt>null</tt>
     */
    double putDouble(DoubleAttribute key, double value);

    /**
     * Associates the specified float value with the specified key in this attribute-map
     * (optional operation). If the attribute-map previously contained a mapping for this
     * key, the old value is replaced by the specified value. (A attribute-map <tt>am</tt>
     * is said to contain a mapping for a key <tt>k</tt> if and only if
     * {@link #containsKey(Object) am.containsKey(k)} would return <tt>true</tt>.))
     *
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this map. For
     *             example, for a read-only attribute-map
     * @throws IllegalArgumentException
     *             if some aspect of this key or value prevents it from being stored in
     *             this map.
     * @throws NullPointerException
     *             if the key is <tt>null</tt>
     */
    float putFloat(Attribute<Float> key, float value);

    /**
     * Associates the specified int value with the specified key in this attribute-map
     * (optional operation). If the attribute-map previously contained a mapping for this
     * key, the old value is replaced by the specified value. (A attribute-map <tt>am</tt>
     * is said to contain a mapping for a key <tt>k</tt> if and only if
     * {@link #containsKey(Object) am.containsKey(k)} would return <tt>true</tt>.))
     *
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this map. For
     *             example, for a read-only attribute-map
     * @throws IllegalArgumentException
     *             if some aspect of this key or value prevents it from being stored in
     *             this map.
     * @throws NullPointerException
     *             if the key is <tt>null</tt>
     */
    int putInt(IntAttribute attribute, int value);

    /**
     * Associates the specified long value with the specified key in this attribute-map
     * (optional operation). If the attribute-map previously contained a mapping for this
     * key, the old value is replaced by the specified value. (A attribute-map <tt>am</tt>
     * is said to contain a mapping for a key <tt>k</tt> if and only if
     * {@link #containsKey(Object) am.containsKey(k)} would return <tt>true</tt>.))
     *
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this map. For
     *             example, for a read-only attribute-map
     * @throws IllegalArgumentException
     *             if some aspect of this key or value prevents it from being stored in
     *             this map.
     * @throws NullPointerException
     *             if the key is <tt>null</tt>
     */
    long putLong(LongAttribute key, long value);

    /**
     * Associates the specified short value with the specified key in this attribute-map
     * (optional operation). If the attribute-map previously contained a mapping for this
     * key, the old value is replaced by the specified value. (A attribute-map <tt>am</tt>
     * is said to contain a mapping for a key <tt>k</tt> if and only if
     * {@link #containsKey(Object) am.containsKey(k)} would return <tt>true</tt>.))
     *
     * @param key
     *            key with which the specified value is to be associated.
     * @param value
     *            value to be associated with the specified key.
     * @throws UnsupportedOperationException
     *             if the <tt>put</tt> operation is not supported by this map. For
     *             example, for a read-only attribute-map
     * @throws IllegalArgumentException
     *             if some aspect of this key or value prevents it from being stored in
     *             this map.
     * @throws NullPointerException
     *             if the key is <tt>null</tt>
     */
    short putShort(Attribute<Short> key, short value);
}
