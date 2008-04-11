package org.codehaus.cake.attribute;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface AttributeMap {
    boolean isEmpty();
    int size();
    void clear();
    boolean contains(Attribute<?> attribute);
    Set<Attribute> attributeSet();
    Set<Map.Entry<Attribute, Object>> entrySet();
    Collection<Object> values();
    <T> T get(Attribute<T> key);
    <T> T get(Attribute<T> key, T defaultValue);
    <T> T put(Attribute<T> key, T value);
    <T> T remove(Attribute<T> key);
    //missing object types
  
    long get(LongAttribute key);
    long get(LongAttribute key, long defaultValue);
    long put(LongAttribute key, long value);
    long remove(LongAttribute key);
    
    int get(IntAttribute key);
    int get(IntAttribute key, int defaultValue);
    int put(IntAttribute key, int value);
    int remove(IntAttribute key);
    
    double get(DoubleAttribute key);
    double get(DoubleAttribute key, double defaultValue);
    double put(DoubleAttribute key, double value);
    double remove(DoubleAttribute key);
    
    short get(ShortAttribute key);
    short get(ShortAttribute key, short defaultValue);
    short put(ShortAttribute key, short value);
    short remove(ShortAttribute key);
    
    byte get(ByteAttribute key);
    byte get(ByteAttribute key, byte defaultValue);
    byte put(ByteAttribute key, byte value);
    byte remove(ByteAttribute key);
    
    float get(FloatAttribute key);
    float get(FloatAttribute key, float defaultValue);
    float put(FloatAttribute key, float value);
    float remove(FloatAttribute key);
    
    char get(CharAttribute key);
    char get(CharAttribute key, char defaultValue);
    char put(CharAttribute key, char value);
    char remove(CharAttribute key);
    
    boolean get(BooleanAttribute key);
    boolean get(BooleanAttribute key, boolean defaultValue);
    boolean put(BooleanAttribute key, boolean value);
    boolean remove(BooleanAttribute key);
}
