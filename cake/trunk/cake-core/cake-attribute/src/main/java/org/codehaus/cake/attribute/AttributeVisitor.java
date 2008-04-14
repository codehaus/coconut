package org.codehaus.cake.attribute;

public interface AttributeVisitor {
    void visit(BooleanAttribute attribute, boolean value);

    void visit(ByteAttribute attribute, byte value);

    void visit(CharAttribute attribute, long value);

    void visit(DoubleAttribute attribute, double value);

    void visit(FloatAttribute attribute, float value);

    void visit(IntAttribute attribute, int value);

    void visit(LongAttribute attribute, long value);

    void visit(ObjectAttribute attribute, Object value);

    void visit(ShortAttribute attribute, short value);
}
