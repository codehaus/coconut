package org.codehaus.cake.attribute;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A map specifically for the storage of Attribute->Object values.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: AttributeMap.java 415 2007-11-09 08:25:23Z kasper $
 */
public interface AttributeMap {
    Set<Attribute> attributeSet();

    /**
     * Removes all of the mappings from this map (optional operation). The map will be empty after
     * this call returns.
     * 
     * @throws UnsupportedOperationException
     *             if the <tt>clear</tt> operation is not supported by this map
     */
    void clear();

    /**
     * Returns <tt>true</tt> if this attributemap contains a mapping for the specified attribute.
     * More formally, returns <tt>true</tt> if and only if this map contains a mapping for a
     * attribute <tt>a</tt> such that <tt>(attribute==a)</tt>. (There can be at most one such
     * mapping.)
     * 
     * @param attribute
     *            attribute whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified attribute
     * @throws NullPointerException
     *             if the specified attribute is null
     */
    boolean contains(Attribute<?> attribute);

    Set<Map.Entry<Attribute, Object>> entrySet();

    <T> T get(Attribute<T> key);

    <T> T get(Attribute<T> key, T defaultValue);

    /**
     * Returns the byte value to which this attribute-map maps the specified key. Returns the value
     * returned by the specified attributes {@link BooleanAttribute#getDefaultValue()} method if the
     * attribute-map contains no mapping for the specified attribute. A return value of
     * <tt>{@link BooleanAttribute#getDefaultValue()}</tt> does not <i>necessarily</i> indicate
     * that the attribute-map contains no mapping for the key; it's also possible that the
     * attribute-map explicitly maps the key to <tt>{@link BooleanAttribute#getDefaultValue()}</tt>.
     * The <tt>{@link #contains(Attribute)}</tt> operation may be used to distinguish these two
     * cases.
     * <p>
     * More formally, if this attributemap contains a mapping from a attribute <tt>a</tt> to a
     * value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>, then this
     * method returns <tt>v</tt>; otherwise it returns <tt>0</tt>. (There can be at most one
     * such mapping.)
     * 
     * @param attribute
     *            attribute whose associated value is to be returned.
     * @return the value to which this map maps the specified attribute, or
     *         <tt>{@link BooleanAttribute#getDefaultValue()}</tt> if the map contains no mapping
     *         for this attribute.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a boolean (or <Boolean>)
     * @throws NullPointerException
     *             if the specified attribute is <tt>null</tt>.
     * @see #contains(Attribute)
     */
    boolean get(BooleanAttribute attribute);

    boolean get(BooleanAttribute attribute, boolean defaultValue);

    /**
     * Returns the byte value to which this attribute-map maps the specified key. Returns the value
     * returned by the specified attributes {@link ByteAttribute#getDefaultValue()} method if the
     * attribute-map contains no mapping for the specified attribute. A return value of
     * <tt>{@link ByteAttribute#getDefaultValue()}</tt> does not <i>necessarily</i> indicate that
     * the attribute-map contains no mapping for the key; it's also possible that the attribute-map
     * explicitly maps the key to <tt>{@link ByteAttribute#getDefaultValue()}</tt>. The
     * <tt>{@link #contains(Attribute)}</tt> operation may be used to distinguish these two cases.
     * <p>
     * More formally, if this attributemap contains a mapping from a attribute <tt>a</tt> to a
     * value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>, then this
     * method returns <tt>v</tt>; otherwise it returns <tt>0</tt>. (There can be at most one
     * such mapping.)
     * 
     * @param attribute
     *            attribute whose associated value is to be returned.
     * @return the value to which this map maps the specified attribute, or
     *         <tt>{@link ByteAttribute#getDefaultValue()}</tt> if the map contains no mapping for
     *         this attribute.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a byte (or <Byte>)
     * @throws NullPointerException
     *             if the specified attribute is <tt>null</tt>.
     * @see #contains(Attribute)
     */
    byte get(ByteAttribute attribute);

    byte get(ByteAttribute attribute, byte defaultValue);

    /**
     * Returns the byte value to which this attribute-map maps the specified key. Returns the value
     * returned by the specified attributes {@link CharAttribute#getDefaultValue()} method if the
     * attribute-map contains no mapping for the specified attribute. A return value of
     * <tt>{@link CharAttribute#getDefaultValue()}</tt> does not <i>necessarily</i> indicate that
     * the attribute-map contains no mapping for the key; it's also possible that the attribute-map
     * explicitly maps the key to <tt>{@link CharAttribute#getDefaultValue()}</tt>. The
     * <tt>{@link #contains(Attribute)}</tt> operation may be used to distinguish these two cases.
     * <p>
     * More formally, if this attributemap contains a mapping from a attribute <tt>a</tt> to a
     * value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>, then this
     * method returns <tt>v</tt>; otherwise it returns <tt>0</tt>. (There can be at most one
     * such mapping.)
     * 
     * @param attribute
     *            attribute whose associated value is to be returned.
     * @return the value to which this map maps the specified attribute, or
     *         <tt>{@link CharAttribute#getDefaultValue()}</tt> if the map contains no mapping for
     *         this attribute.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a char (or <Character>)
     * @throws NullPointerException
     *             if the specified attribute is <tt>null</tt>.
     * @see #contains(Attribute)
     */
    char get(CharAttribute attribute);

    char get(CharAttribute attribute, char defaultValue);

    /**
     * Returns the byte value to which this attribute-map maps the specified key. Returns the value
     * returned by the specified attributes {@link DoubleAttribute#getDefaultValue()} method if the
     * attribute-map contains no mapping for the specified attribute. A return value of
     * <tt>{@link DoubleAttribute#getDefaultValue()}</tt> does not <i>necessarily</i> indicate
     * that the attribute-map contains no mapping for the key; it's also possible that the
     * attribute-map explicitly maps the key to <tt>{@link DoubleAttribute#getDefaultValue()}</tt>.
     * The <tt>{@link #contains(Attribute)}</tt> operation may be used to distinguish these two
     * cases.
     * <p>
     * More formally, if this attributemap contains a mapping from a attribute <tt>a</tt> to a
     * value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>, then this
     * method returns <tt>v</tt>; otherwise it returns <tt>0</tt>. (There can be at most one
     * such mapping.)
     * 
     * @param attribute
     *            attribute whose associated value is to be returned.
     * @return the value to which this map maps the specified attribute, or
     *         <tt>{@link DoubleAttribute#getDefaultValue()}</tt> if the map contains no mapping
     *         for this attribute.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a double (or <Double>)
     * @throws NullPointerException
     *             if the specified attribute is <tt>null</tt>.
     * @see #contains(Attribute)
     */
    double get(DoubleAttribute attribute);

    double get(DoubleAttribute attribute, double defaultValue);

    /**
     * Returns the byte value to which this attribute-map maps the specified key. Returns the value
     * returned by the specified attributes {@link FloatAttribute#getDefaultValue()} method if the
     * attribute-map contains no mapping for the specified attribute. A return value of
     * <tt>{@link FloatAttribute#getDefaultValue()}</tt> does not <i>necessarily</i> indicate
     * that the attribute-map contains no mapping for the key; it's also possible that the
     * attribute-map explicitly maps the key to <tt>{@link FloatAttribute#getDefaultValue()}</tt>.
     * The <tt>{@link #contains(Attribute)}</tt> operation may be used to distinguish these two
     * cases.
     * <p>
     * More formally, if this attributemap contains a mapping from a attribute <tt>a</tt> to a
     * value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>, then this
     * method returns <tt>v</tt>; otherwise it returns <tt>0</tt>. (There can be at most one
     * such mapping.)
     * 
     * @param attribute
     *            attribute whose associated value is to be returned.
     * @return the value to which this map maps the specified attribute, or
     *         <tt>{@link FloatAttribute#getDefaultValue()}</tt> if the map contains no mapping
     *         for this attribute.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a float (or <Float>)
     * @throws NullPointerException
     *             if the specified attribute is <tt>null</tt>.
     * @see #contains(Attribute)
     */
    float get(FloatAttribute attribute);

    float get(FloatAttribute attribute, float defaultValue);

    /**
     * Returns the byte value to which this attribute-map maps the specified key. Returns the value
     * returned by the specified attributes {@link IntAttribute#getDefaultValue()} method if the
     * attribute-map contains no mapping for the specified attribute. A return value of
     * <tt>{@link IntAttribute#getDefaultValue()}</tt> does not <i>necessarily</i> indicate that
     * the attribute-map contains no mapping for the key; it's also possible that the attribute-map
     * explicitly maps the key to <tt>{@link IntAttribute#getDefaultValue()}</tt>. The
     * <tt>{@link #contains(Attribute)}</tt> operation may be used to distinguish these two cases.
     * <p>
     * More formally, if this attributemap contains a mapping from a attribute <tt>a</tt> to a
     * value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>, then this
     * method returns <tt>v</tt>; otherwise it returns <tt>0</tt>. (There can be at most one
     * such mapping.)
     * 
     * @param attribute
     *            attribute whose associated value is to be returned.
     * @return the value to which this map maps the specified attribute, or
     *         <tt>{@link IntAttribute#getDefaultValue()}</tt> if the map contains no mapping for
     *         this attribute.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a int (or <Integer>)
     * @throws NullPointerException
     *             if the specified attribute is <tt>null</tt>.
     * @see #contains(Attribute)
     */
    int get(IntAttribute attribute);

    int get(IntAttribute attribute, int defaultValue);

    /**
     * Returns the byte value to which this attribute-map maps the specified key. Returns the value
     * returned by the specified attributes {@link LongAttribute#getDefaultValue()} method if the
     * attribute-map contains no mapping for the specified attribute. A return value of
     * <tt>{@link LongAttribute#getDefaultValue()}</tt> does not <i>necessarily</i> indicate that
     * the attribute-map contains no mapping for the key; it's also possible that the attribute-map
     * explicitly maps the key to <tt>{@link LongAttribute#getDefaultValue()}</tt>. The
     * <tt>{@link #contains(Attribute)}</tt> operation may be used to distinguish these two cases.
     * <p>
     * More formally, if this attributemap contains a mapping from a attribute <tt>a</tt> to a
     * value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>, then this
     * method returns <tt>v</tt>; otherwise it returns <tt>0</tt>. (There can be at most one
     * such mapping.)
     * 
     * @param attribute
     *            attribute whose associated value is to be returned.
     * @return the value to which this map maps the specified attribute, or
     *         <tt>{@link LongAttribute#getDefaultValue()}</tt> if the map contains no mapping for
     *         this attribute.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a long (or <Long>)
     * @throws NullPointerException
     *             if the specified attribute is <tt>null</tt>.
     * @see #contains(Attribute)
     */
    long get(LongAttribute attribute);

    long get(LongAttribute attribute, long defaultValue);

    /**
     * Returns the byte value to which this attribute-map maps the specified key. Returns the value
     * returned by the specified attributes {@link ShortAttribute#getDefaultValue()} method if the
     * attribute-map contains no mapping for the specified attribute. A return value of
     * <tt>{@link ShortAttribute#getDefaultValue()}</tt> does not <i>necessarily</i> indicate
     * that the attribute-map contains no mapping for the key; it's also possible that the
     * attribute-map explicitly maps the key to <tt>{@link ShortAttribute#getDefaultValue()}</tt>.
     * The <tt>{@link #contains(Attribute)}</tt> operation may be used to distinguish these two
     * cases.
     * <p>
     * More formally, if this attributemap contains a mapping from a attribute <tt>a</tt> to a
     * value <tt>v</tt> such that <tt>(key==null ? k==null :
     * key.equals(k))</tt>, then this
     * method returns <tt>v</tt>; otherwise it returns <tt>0</tt>. (There can be at most one
     * such mapping.)
     * 
     * @param attribute
     *            attribute whose associated value is to be returned.
     * @return the value to which this map maps the specified attribute, or
     *         <tt>{@link ShortAttribute#getDefaultValue()}</tt> if the map contains no mapping
     *         for this attribute.
     * @throws ClassCastException
     *             if the asssociated value is of another type then a short (or <Short>)
     * @throws NullPointerException
     *             if the specified attribute is <tt>null</tt>.
     * @see #contains(Attribute)
     */
    short get(ShortAttribute attribute);

    short get(ShortAttribute attribute, short defaultValue);

    /**
     * Returns <tt>true</tt> if this map contains no attribute-value mappings.
     * 
     * @return <tt>true</tt> if this map contains no attribute-value mappings
     */
    boolean isEmpty();

    <T> T put(Attribute<T> key, T value);

    boolean put(BooleanAttribute attribute, boolean value);

    byte put(ByteAttribute attribute, byte value);

    char put(CharAttribute attribute, char value);

    double put(DoubleAttribute attribute, double value);

    float put(FloatAttribute attribute, float value);

    int put(IntAttribute attribute, int value);

    long put(LongAttribute attribute, long value);

    short put(ShortAttribute attribute, short value);

    <T> T remove(Attribute<T> key);

    boolean remove(BooleanAttribute key);

    byte remove(ByteAttribute key);

    char remove(CharAttribute key);

    double remove(DoubleAttribute key);

    float remove(FloatAttribute key);

    int remove(IntAttribute key);

    long remove(LongAttribute key);

    short remove(ShortAttribute key);

    /**
     * Returns the number of attribite-value mappings in this attributemap. If the map contains more
     * than <tt>Integer.MAX_VALUE</tt> elements, returns <tt>Integer.MAX_VALUE</tt>.
     * 
     * @return the number of attribute-value mappings in this map
     */
    int size();

    Collection<Object> values();

}
