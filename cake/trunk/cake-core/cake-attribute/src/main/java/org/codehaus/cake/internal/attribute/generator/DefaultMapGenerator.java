package org.codehaus.cake.internal.attribute.generator;

import static org.objectweb.asm.Type.getMethodDescriptor;
import static org.objectweb.asm.Type.getType;

import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import org.codehaus.cake.attribute.Attribute;
import org.codehaus.cake.attribute.AttributeMap;
import org.codehaus.cake.attribute.ObjectAttribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class DefaultMapGenerator implements Opcodes {
    private final static String ATTRIBUTE_MAP_INTERFACE_DESCRIPTOR = "org/codehaus/cake/attribute/AttributeMap";
    final static WeakHashMap<Class<?>, Attribute<?>[]> initializers = new WeakHashMap<Class<?>, Attribute<?>[]>();
    private final static String KEYSET_NAME = "ATTRIBUTES";
    private final static Object lock = new Object();
    static final Type T_ATTRIBUTE = Type.getType(Attribute.class);
    private final String classDescriptor;
    ClassVisitor cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

    private final Info[] info;
    private final int sizeNonHidden;

    DefaultMapGenerator(String classDescriptor, List<AttributeInfo> infos) {
        this.classDescriptor = classDescriptor;
        info = new Info[infos.size()];
        for (int i = 0; i < info.length; i++) {
            info[i] = new Info(i, infos.get(i));
        }
        int count = 0;
        for (Info i : info) {
            if (!i.isHidden) {
                count++;
            }
        }
        sizeNonHidden = count;
    }

    void _fields() {
        // add all fields with default values
        for (Info i : info) {
            int mod = ACC_PRIVATE + (i.isMutable ? 0 : ACC_FINAL);
            cw.visitField(mod, i.getFieldName(), i.descriptor, null, null).visitEnd();
        }
    }

    void _init() {
        Type[] types = new Type[info.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = Type.getType(info[i].attribute.getType());
        }
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", Type.getMethodDescriptor(
                Type.VOID_TYPE, types), null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        int index = 1;
        for (Info i : info) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(i.vType.loadCode(), index);
            index += i.vType.indexInc();
            mv.visitFieldInsn(PUTFIELD, classDescriptor, i.getFieldName(), i.getAtrDescriptor());
        }

        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    void _static_fields() {
        // add static fields, each containing an attribute
        for (Info i : info) {
            cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC, i.getFieldStaticName(),
                    i.attributeDescriptor, null, null).visitEnd();
        }
        // add keyset
        cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC, KEYSET_NAME, "Ljava/util/Set;", null,
                null).visitEnd();
    }

    void _static_init() {
        MethodVisitor mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        mv.visitCode();
        mv.visitLdcInsn(Type.getType("L" + classDescriptor + ";"));
        mv.visitMethodInsn(INVOKESTATIC, getType(DefaultMapGenerator.class).getInternalName(),
                "init", getMethodDescriptor(getType(Attribute[].class),
                        new Type[] { getType(Class.class) }));
        mv.visitVarInsn(ASTORE, 0);
        for (Info i : info) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitIntInsn(BIPUSH, i.index);
            mv.visitInsn(AALOAD);
            mv.visitTypeInsn(CHECKCAST, i.vType.getType().getInternalName());
            mv.visitFieldInsn(PUTSTATIC, classDescriptor, i.getFieldStaticName(),
                    i.attributeDescriptor);
        }
        // create keyset
        mv.visitTypeInsn(NEW, "java/util/HashSet");
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, sizeNonHidden);
        mv.visitTypeInsn(ANEWARRAY, T_ATTRIBUTE.getInternalName());
        int count = 0;
        for (Info i : info) {
            if (!i.isHidden) {
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, count++);
                i.visitStaticGet(mv);
                mv.visitInsn(AASTORE);
            }
        }
        mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "asList",
                "([Ljava/lang/Object;)Ljava/util/List;");
        mv.visitTypeInsn(CHECKCAST, "java/util/Collection");
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashSet", "<init>",
                "(Ljava/util/Collection;)V");
        mv.visitMethodInsn(INVOKESTATIC, "java/util/Collections", "unmodifiableSet",
                "(Ljava/util/Set;)Ljava/util/Set;");
        mv.visitFieldInsn(PUTSTATIC, classDescriptor, KEYSET_NAME, "Ljava/util/Set;");

        // /
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    private void addClear() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "clear", "()V", null, null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("clear not supported");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>",
                "(Ljava/lang/String;)V");
        mv.visitInsn(ATHROW);
        mv.visitMaxs(3, 1);
        mv.visitEnd();
    }

    private void addContainsKey() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "contains", getMethodDescriptor(
                Type.BOOLEAN_TYPE, new Type[] { T_ATTRIBUTE }), null, null);
        mv.visitCode();
        Label l = new Label();
        for (Info i : info) {
            if (!i.isHidden) {
                mv.visitVarInsn(ALOAD, 1);
                i.visitStaticGet(mv);
                mv.visitJumpInsn(IF_ACMPEQ, l);
            }
        }
        mv.visitInsn(ICONST_0);
        mv.visitInsn(IRETURN);
        mv.visitLabel(l);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    private void addEquals() {
        MethodVisitor mv = cw
                .visitMethod(ACC_PUBLIC, "equals", "(Ljava/lang/Object;)Z", null, null);
        mv.visitCode();
        Label f = new Label();
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 0);
        Label l = new Label();
        mv.visitJumpInsn(IF_ACMPNE, l);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IRETURN);

        // if instanceof same class
        mv.visitLabel(l);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(INSTANCEOF, classDescriptor);
        l = new Label();
        mv.visitJumpInsn(IFEQ, l); // jump if false
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, classDescriptor);
        mv.visitVarInsn(ASTORE, 2);
        for (Info i : info) {
            if (!i.isHidden) {
                mv.visitVarInsn(ALOAD, 0);
                i.visitGet(mv);
                mv.visitVarInsn(ALOAD, 2);
                i.visitGet(mv);
                if (i.vType == PrimType.OBJECT) {
                    mv.visitMethodInsn(INVOKESTATIC, classDescriptor, "eq",
                            "(Ljava/lang/Object;Ljava/lang/Object;)Z");
                    mv.visitJumpInsn(IFEQ, f);
                } else {
                    jumpIfNotEqual(mv, i.vType, f);
                }
            }
        }
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IRETURN);

        // insert check of other attributemap instances here
        mv.visitLabel(l);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(INSTANCEOF, "org/codehaus/cake/attribute/AttributeMap");
        mv.visitJumpInsn(IFEQ, f);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, "org/codehaus/cake/attribute/AttributeMap");
        mv.visitVarInsn(ASTORE, 2);

        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEINTERFACE, "org/codehaus/cake/attribute/AttributeMap", "size",
                "()I");
        mv.visitLdcInsn(new Integer(sizeNonHidden));
        mv.visitJumpInsn(IF_ICMPNE, f);

        // compare entries
        l = new Label();
        // check for v0 == m.get(A2) && (v0 != A2.getDefaultValue() || m.isSet(A2))
        for (Info i : info) {
            if (!i.isHidden) {
                mv.visitVarInsn(ALOAD, 0);
                i.visitGet(mv);
                mv.visitVarInsn(ALOAD, 2);
                i.visitStaticGet(mv);
                if (i.vType == PrimType.OBJECT) {
                    mv.visitMethodInsn(INVOKEINTERFACE, "org/codehaus/cake/attribute/AttributeMap",
                            "get", "(Lorg/codehaus/cake/attribute/Attribute;)Ljava/lang/Object;");
                    mv.visitMethodInsn(INVOKESTATIC, classDescriptor, "eq",
                            "(Ljava/lang/Object;Ljava/lang/Object;)Z");
                    mv.visitJumpInsn(IFEQ, f);
                } else {
                    mv.visitMethodInsn(INVOKEINTERFACE, "org/codehaus/cake/attribute/AttributeMap",
                            "get", getMethodDescriptor(i.vType.getPrimType(), new Type[] { i.vType
                                    .getType() }));
                    jumpIfNotEqual(mv, i.vType, f);
                    l = new Label();
                    // check v0 != A2.getDefaultValue()
                    mv.visitVarInsn(ALOAD, 0);
                    i.visitGet(mv);
                    i.visitStaticGet(mv);
                    mv.visitMethodInsn(INVOKEVIRTUAL, i.vType.getType().getInternalName(),
                            "getDefaultValue", getMethodDescriptor(i.vType.getPrimType(),
                                    new Type[] {}));
                    jumpIfNotEqual(mv, i.vType, l);
                }
                // check m.isSet(A2)
                mv.visitVarInsn(ALOAD, 2);
                i.visitStaticGet(mv);
                mv.visitMethodInsn(INVOKEINTERFACE, "org/codehaus/cake/attribute/AttributeMap",
                        "contains", "(Lorg/codehaus/cake/attribute/Attribute;)Z");
                mv.visitJumpInsn(IFEQ, f);
                mv.visitLabel(l);
            }
        }
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IRETURN);
        // return false;
        mv.visitLabel(f);
        mv.visitInsn(ICONST_0);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    private void addEq() {
        // private static boolean eq(Object o1, Object o2) {
        // return o1 == null ? o2 == null : o1 == o2 || o1.equals(o2);
        // }
        MethodVisitor mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, "eq",
                "(Ljava/lang/Object;Ljava/lang/Object;)Z", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        Label l0 = new Label();
        mv.visitJumpInsn(IFNONNULL, l0);
        mv.visitVarInsn(ALOAD, 1);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNONNULL, l1);
        mv.visitInsn(ICONST_1);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        mv.visitInsn(ICONST_0);
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        Label l3 = new Label();
        mv.visitJumpInsn(IF_ACMPEQ, l3);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z");
        mv.visitJumpInsn(IFNE, l3);
        mv.visitInsn(ICONST_0);
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l3);
        mv.visitInsn(ICONST_1);
        mv.visitLabel(l2);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    private void addGet() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "get",
                "(Lorg/codehaus/cake/attribute/Attribute;)Ljava/lang/Object;",
                "<T:Ljava/lang/Object;>(Lorg/codehaus/cake/attribute/Attribute<TT;>;)TT;", null);
        mv.visitCode();
        Label l = null;
        for (Info i : info) {
            if (i.vType == PrimType.OBJECT) {
                mv.visitVarInsn(ALOAD, 1);
                i.visitStaticGet(mv);
                l = new Label();
                mv.visitJumpInsn(IF_ACMPNE, l);
                mv.visitVarInsn(ALOAD, 0);
                i.visitGet(mv);
                mv.visitInsn(ARETURN);
                mv.visitLabel(l);
            }
        }
        for (Info i : info) {
            if (i.vType != PrimType.OBJECT) {
                mv.visitVarInsn(ALOAD, 1);
                i.visitStaticGet(mv);
                l = new Label();
                mv.visitJumpInsn(IF_ACMPNE, l);
                mv.visitVarInsn(ALOAD, 0);
                i.visitGet(mv);
                mv.visitMethodInsn(INVOKESTATIC, i.vType.getObjectType().getInternalName(),
                        "valueOf", Type.getMethodDescriptor(i.vType.getObjectType(),
                                new Type[] { i.vType.getPrimType() }));
                mv.visitInsn(ARETURN);
                mv.visitLabel(l);
            }
        }
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/cake/attribute/Attribute", "getDefault",
                "()Ljava/lang/Object;");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
    }

    private void addGetDefault() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "get",
                "(Lorg/codehaus/cake/attribute/Attribute;Ljava/lang/Object;)Ljava/lang/Object;",
                "<T:Ljava/lang/Object;>(Lorg/codehaus/cake/attribute/Attribute<TT;>;TT;)TT;", null);
        mv.visitCode();
        Label l = null;
        for (Info i : info) {
            if (i.vType == PrimType.OBJECT) {
                mv.visitVarInsn(ALOAD, 1);
                i.visitStaticGet(mv);
                l = new Label();
                mv.visitJumpInsn(IF_ACMPNE, l);
                mv.visitVarInsn(ALOAD, 0);
                i.visitGet(mv);
                mv.visitInsn(ARETURN);
                mv.visitLabel(l);
            }
        }
        for (Info i : info) {
            if (i.vType != PrimType.OBJECT) {
                mv.visitVarInsn(ALOAD, 1);
                i.visitStaticGet(mv);
                l = new Label();
                mv.visitJumpInsn(IF_ACMPNE, l);
                mv.visitVarInsn(ALOAD, 0);
                i.visitGet(mv);
                mv.visitMethodInsn(INVOKESTATIC, i.vType.getObjectType().getInternalName(),
                        "valueOf", Type.getMethodDescriptor(i.vType.getObjectType(),
                                new Type[] { i.vType.getPrimType() }));
                mv.visitInsn(ARETURN);
                mv.visitLabel(l);
            }
        }
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(2, 2);
    }

    private void addGet(PrimType type) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "get", getMethodDescriptor(
                type.getPrimType(), new Type[] { type.getType() }), null, null);
        mv.visitCode();
        Label l = null;
        for (Info i : info) {
            if (i.vType == type) {
                mv.visitVarInsn(ALOAD, 1);
                i.visitStaticGet(mv);
                l = new Label();
                mv.visitJumpInsn(IF_ACMPNE, l);
                mv.visitVarInsn(ALOAD, 0);
                i.visitGet(mv);
                mv.visitInsn(type.returnCode());
                mv.visitLabel(l);
            }
        }
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, type.getType().getInternalName(), "getDefaultValue",
                getMethodDescriptor(type.getPrimType(), new Type[] {}));
        mv.visitInsn(type.returnCode());
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    private void addGetDefault(PrimType type) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "get", getMethodDescriptor(
                type.getPrimType(), new Type[] { type.getType(), type.getPrimType() }), null, null);
        mv.visitCode();
        Label l = null;
        for (Info i : info) {
            // System.out.println("makeit " +i.type + ", " + type.getPrimType());
            // System.out.println(i.vType + ", " + type);
            if (i.vType == type) {
                mv.visitVarInsn(ALOAD, 1);
                i.visitStaticGet(mv);
                l = new Label();
                mv.visitJumpInsn(IF_ACMPNE, l);
                mv.visitVarInsn(ALOAD, 0);
                i.visitGet(mv);
                mv.visitInsn(type.returnCode());
                mv.visitLabel(l);
            }
        }
        mv.visitVarInsn(type.loadCode(), 2);
        mv.visitInsn(type.returnCode());
        mv.visitMaxs(2, 4);
        mv.visitEnd();
    }

    private void addHashCode() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "hashCode", "()I", null, null);
        mv.visitCode();
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 1);

        for (Info i : info) {
            if (!i.isHidden) {
                if (i.vType == PrimType.DOUBLE) {
                    mv.visitVarInsn(ALOAD, 0);
                    i.visitGet(mv);
                    mv
                            .visitMethodInsn(INVOKESTATIC, "java/lang/Double", "doubleToLongBits",
                                    "(D)J");
                    mv.visitVarInsn(LSTORE, 2);
                    mv.visitVarInsn(ILOAD, 1);
                    mv.visitLdcInsn(new Integer(i.attribute.hashCode()));
                    mv.visitVarInsn(LLOAD, 2);
                    mv.visitVarInsn(LLOAD, 2);
                    mv.visitIntInsn(BIPUSH, 32);
                    mv.visitInsn(LUSHR);
                    mv.visitInsn(LXOR);
                    mv.visitInsn(L2I);
                } else {
                    mv.visitVarInsn(ILOAD, 1);
                    mv.visitLdcInsn(new Integer(i.attribute.hashCode()));
                    mv.visitVarInsn(ALOAD, 0);
                    i.visitGet(mv);
                    if (i.vType == PrimType.BOOLEAN) {
                        Label l0 = new Label();
                        mv.visitJumpInsn(IFEQ, l0);
                        mv.visitIntInsn(SIPUSH, 1231);
                        Label l1 = new Label();
                        mv.visitJumpInsn(GOTO, l1);
                        mv.visitLabel(l0);
                        mv.visitIntInsn(SIPUSH, 1237);
                        mv.visitLabel(l1);
                    } else if (i.vType == PrimType.LONG) {
                        mv.visitVarInsn(ALOAD, 0);
                        i.visitGet(mv);
                        mv.visitIntInsn(BIPUSH, 32);
                        mv.visitInsn(LUSHR);
                        mv.visitInsn(LXOR);
                        mv.visitInsn(L2I);
                    } else if (i.vType == PrimType.FLOAT) {
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "floatToIntBits",
                                "(F)I");
                    } else if (i.vType == PrimType.OBJECT) {
                        Label l0 = new Label();
                        mv.visitJumpInsn(IFNONNULL, l0);
                        mv.visitInsn(ICONST_0);
                        Label l1 = new Label();
                        mv.visitJumpInsn(GOTO, l1);
                        mv.visitLabel(l0);
                        mv.visitVarInsn(ALOAD, 0);
                        i.visitGet(mv);
                        String internalName = Type.getType(i.attribute.getType()).getInternalName();
                        if (i.attribute.getType().isInterface()) {
                            mv.visitMethodInsn(INVOKEINTERFACE, internalName, "hashCode", "()I");
                        } else {
                            mv.visitMethodInsn(INVOKEVIRTUAL, internalName, "hashCode", "()I");
                        }
                        mv.visitLabel(l1);
                    }
                }
                mv.visitInsn(IXOR);
                mv.visitInsn(IADD);
                mv.visitVarInsn(ISTORE, 1);
            }
        }

        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }

    private void addIsEmpty() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "isEmpty", "()Z", null, null);
        mv.visitCode();
        if (sizeNonHidden > 0) {
            mv.visitInsn(ICONST_0);// false
        } else {
            mv.visitInsn(ICONST_1);// false
        }
        mv.visitInsn(IRETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private void addKeySet() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "attributeSet", "()Ljava/util/Set;",
                getMethodDescriptor(Type.getType(Set.class), new Type[] {}), null);

        // MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "keySet", "()Ljava/util/Set;",
        // "()Ljava/util/Set<Lorg/codehaus/cake/util/attribute/Attribute<*>;>;", null);

        mv.visitCode();
        mv.visitFieldInsn(GETSTATIC, classDescriptor, KEYSET_NAME, "Ljava/util/Set;");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private void addPut() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "put",
                "(Lorg/codehaus/cake/attribute/Attribute;Ljava/lang/Object;)Ljava/lang/Object;",
                "<T:Ljava/lang/Object;>(Lorg/codehaus/cake/attribute/Attribute<TT;>;TT;)TT;", null);
        mv.visitCode();
        Label l = null;
        for (Info i : info) {
            if (i.vType == PrimType.OBJECT && i.isMutable) {
                mv.visitVarInsn(ALOAD, 1);
                i.visitStaticGet(mv);
                l = new Label();
                mv.visitJumpInsn(IF_ACMPNE, l);
                mv.visitVarInsn(ALOAD, 0);
                i.visitGet(mv);
                mv.visitVarInsn(ASTORE, 3);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitTypeInsn(CHECKCAST, i.type.getInternalName());
                mv.visitFieldInsn(PUTFIELD, classDescriptor, i.getFieldName(), i
                        .getPrimTypeDescriptor());
                mv.visitVarInsn(ALOAD, 3);
                mv.visitInsn(ARETURN);
                mv.visitLabel(l);
            }
        }
        for (Info i : info) {
            if (i.vType != PrimType.OBJECT && i.isMutable) {
                mv.visitVarInsn(ALOAD, 1);
                i.visitStaticGet(mv);
                l = new Label();
                mv.visitJumpInsn(IF_ACMPNE, l);
                mv.visitVarInsn(ALOAD, 0);
                i.visitGet(mv);
                mv.visitMethodInsn(INVOKESTATIC, i.vType.getObjectType().getInternalName(),
                        "valueOf", Type.getMethodDescriptor(i.vType.getObjectType(),
                                new Type[] { i.vType.getPrimType() }));
                mv.visitVarInsn(ASTORE, 3);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 2);
                // mv.visitVarInsn(i.vType.storeCode(), 2 + i.vType.indexInc());
                // mv.visitVarInsn(ALOAD, 0);
                // mv.visitVarInsn(i.vType.loadCode(), 2);

                mv.visitTypeInsn(CHECKCAST, i.vType.getObjectType().getInternalName());
                // System.out.println(i.descriptor);
                mv.visitMethodInsn(INVOKEVIRTUAL, i.vType.getObjectType().getInternalName(),
                        i.vType.name().toLowerCase() + "Value", "()" + i.getPrimTypeDescriptor());
                mv.visitFieldInsn(PUTFIELD, classDescriptor, i.getFieldName(), i
                        .getPrimTypeDescriptor());
                // mv.visitVarInsn(i.vType.loadCode(), 2 + i.vType.indexInc());
                // mv.visitInsn(i.vType.returnCode());
                mv.visitVarInsn(ALOAD, 3);
                mv.visitInsn(ARETURN);
                mv.visitLabel(l);
            }
        }
        mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
        mv.visitInsn(DUP);
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("put not supported for ");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>",
                "(Ljava/lang/String;)V");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                "(Ljava/lang/Object;)Ljava/lang/StringBuilder;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
                "()Ljava/lang/String;");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>",
                "(Ljava/lang/String;)V");
        mv.visitInsn(ATHROW);
        mv.visitMaxs(5, 6);
        mv.visitEnd();
    }

    private void addPut(PrimType type) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "put", getMethodDescriptor(
                type.getPrimType(), new Type[] { type.getType(), type.getPrimType() }), null, null);
        mv.visitCode();
        Label l = null;
        for (Info i : info) {
            if (i.vType == type && i.isMutable) {
                mv.visitVarInsn(ALOAD, 1);
                i.visitStaticGet(mv);
                l = new Label();
                mv.visitJumpInsn(IF_ACMPNE, l);
                mv.visitVarInsn(ALOAD, 0);
                i.visitGet(mv);
                mv.visitVarInsn(type.storeCode(), 2 + type.indexInc());
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(type.loadCode(), 2);
//                if (i.vType == PrimType.OBJECT) {
//                    mv.visitTypeInsn(CHECKCAST, i.type.getInternalName());
//                }
                mv.visitFieldInsn(PUTFIELD, classDescriptor, i.getFieldName(), i
                        .getPrimTypeDescriptor());

                mv.visitVarInsn(type.loadCode(), 2 + type.indexInc());
                mv.visitInsn(type.returnCode());
                mv.visitLabel(l);
            }
        }

        mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
        mv.visitInsn(DUP);
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("put not supported for ");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>",
                "(Ljava/lang/String;)V");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                "(Ljava/lang/Object;)Ljava/lang/StringBuilder;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
                "()Ljava/lang/String;");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>",
                "(Ljava/lang/String;)V");
        mv.visitInsn(ATHROW);
        mv.visitMaxs(5, 6);
        mv.visitEnd();
    }

    private void addRemove(PrimType type) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "remove", getMethodDescriptor(type
                .getPrimType(), new Type[] { type.getType() }), null, null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("remove not supported");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>",
                "(Ljava/lang/String;)V");
        mv.visitInsn(ATHROW);
        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }

    private void addRemove() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "remove",
                "(Lorg/codehaus/cake/attribute/Attribute;)Ljava/lang/Object;",
                "<T:Ljava/lang/Object;>(Lorg/codehaus/cake/attribute/Attribute<TT;>;)TT;", null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
        mv.visitInsn(DUP);
        mv.visitLdcInsn("remove not supported");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>",
                "(Ljava/lang/String;)V");
        mv.visitInsn(ATHROW);
        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }

    private void addSize() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "size", "()I", null, null);
        mv.visitCode();
        mv.visitIntInsn(BIPUSH, sizeNonHidden);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private void addEntrySet() {
        MethodVisitor mv = cw
                .visitMethod(
                        ACC_PUBLIC,
                        "entrySet",
                        "()Ljava/util/Set;",
                        "()Ljava/util/Set<Ljava/util/Map$Entry<Lorg/codehaus/cake/attribute/Attribute;Ljava/lang/Object;>;>;",
                        null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, "java/util/HashSet");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashSet", "<init>", "()V");
        mv.visitVarInsn(ASTORE, 1);

        for (Info i : info) {
            if (!i.isHidden) {
                mv.visitVarInsn(ALOAD, 1);
                mv.visitTypeInsn(NEW,
                        "org/codehaus/cake/internal/attribute/AttributeHelper$SimpleImmutableEntry");
                mv.visitInsn(DUP);
                i.visitStaticGet(mv);
                mv.visitVarInsn(ALOAD, 0);
                i.visitGet(mv);
                if (i.vType != PrimType.OBJECT) {
                    mv.visitMethodInsn(INVOKESTATIC, i.vType.getObjectType().getInternalName(),
                            "valueOf", Type.getMethodDescriptor(i.vType.getObjectType(),
                                    new Type[] { i.vType.getPrimType() }));
                }
                mv.visitMethodInsn(INVOKESPECIAL,
                        "org/codehaus/cake/internal/attribute/AttributeHelper$SimpleImmutableEntry",
                        "<init>", "(Ljava/lang/Object;Ljava/lang/Object;)V");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashSet", "add", "(Ljava/lang/Object;)Z");
                mv.visitInsn(POP);

            }
        }

        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "java/util/Collections", "unmodifiableSet",
                "(Ljava/util/Set;)Ljava/util/Set;");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(6, 2);
        mv.visitEnd();
    }

    private void addValues() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "values", "()Ljava/util/Collection;",
                "()Ljava/util/Collection<Ljava/lang/Object;>;", null);
        mv.visitCode();
        mv.visitIntInsn(BIPUSH, sizeNonHidden);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        int count = 0;
        for (Info i : info) {
            if (!i.isHidden) {
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, count++);
                mv.visitVarInsn(ALOAD, 0);
                i.visitGet(mv);
                if (i.vType != PrimType.OBJECT) {
                    mv.visitMethodInsn(INVOKESTATIC, i.vType.getObjectType().getInternalName(),
                            "valueOf", Type.getMethodDescriptor(i.vType.getObjectType(),
                                    new Type[] { i.vType.getPrimType() }));
                }
                mv.visitInsn(AASTORE);
            }
        }
        mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "asList",
                "([Ljava/lang/Object;)Ljava/util/List;");
        mv.visitTypeInsn(CHECKCAST, "java/util/Collection");
        mv.visitMethodInsn(INVOKESTATIC, "java/util/Collections", "unmodifiableCollection",
                "(Ljava/util/Collection;)Ljava/util/Collection;");
        mv.visitInsn(ARETURN);
        mv.visitMaxs(5, 1);
        mv.visitEnd();
    }

    private void addToString() {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null,
                null);
        mv.visitCode();
        if (sizeNonHidden == 0) {
            mv.visitLdcInsn("{}");
        } else {
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V");
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(BIPUSH, 123);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                    "(C)Ljava/lang/StringBuilder;");
            boolean isFirst = true;
            for (Info i : info) {
                if (!i.isHidden) {
                    mv.visitInsn(POP);
                    mv.visitVarInsn(ALOAD, 1);
                    if (isFirst) {
                        mv.visitLdcInsn(i.attribute.getName() + "=");
                        isFirst = false;
                    } else {
                        mv.visitLdcInsn(", " + i.attribute.getName() + "=");
                    }
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                            "(Ljava/lang/String;)Ljava/lang/StringBuilder;");

                    mv.visitInsn(POP);
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitVarInsn(ALOAD, 0);
                    i.visitGet(mv);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                            getMethodDescriptor(Type.getType(StringBuilder.class),
                                    new Type[] { i.vType.getPrimType() }));

                }
            }
            mv.visitInsn(POP);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(BIPUSH, 125);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                    "(C)Ljava/lang/StringBuilder;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
                    "()Ljava/lang/String;");
        }
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    void createClass() {
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, classDescriptor, null, "java/lang/Object",
                new String[] { ATTRIBUTE_MAP_INTERFACE_DESCRIPTOR });
        _static_fields();
        _fields();
        _static_init();
        _init();

        // methods
        addClear();
        addContainsKey();
        addKeySet();
        addIsEmpty();
        addSize();
        addGet();
        addGetDefault();
        addPut();
        addRemove();
        for (PrimType pt : PrimType.values()) {
            if (pt != PrimType.OBJECT) {
                addGet(pt);
                addGetDefault(pt);
                addPut(pt);
                addRemove(pt);
            }
        }
        // addPutLong();
        addEquals();
        addHashCode();
        addToString();
        addValues();
        addEntrySet();
        addEq();
        cw.visitEnd();
    }

    public static Class<AttributeMap> generate(String className, List<AttributeInfo> infos)
            throws Exception {
        String descriptor = className.replace('.', '/');

        DefaultMapGenerator g = new DefaultMapGenerator(descriptor, infos);
        // g.cw = new ASMifierClassVisitor(new PrintWriter(System.out));

        g.createClass();
        MyLoader ml = new MyLoader();
        Class c = ml.defineClass(className, ((ClassWriter) g.cw).toByteArray());
        Attribute[] attributes = new Attribute[g.info.length];
        for (int i = 0; i < attributes.length; i++) {
            attributes[i] = g.info[i].attribute;
        }
        synchronized (lock) {
            initializers.put(c, attributes);
        }
        return c;
    }

    public static Attribute[] init(Class c) {
        synchronized (lock) {
            return initializers.remove(c);
        }
    }

    static void jumpIfNotEqual(MethodVisitor mv, PrimType type, Label l) {
        if (type == PrimType.LONG) {
            mv.visitInsn(LCMP);
            mv.visitJumpInsn(IFNE, l);
        } else if (type == PrimType.DOUBLE) {
            mv.visitInsn(DCMPL);
            mv.visitJumpInsn(IFNE, l);
        } else if (type == PrimType.FLOAT) {
            mv.visitInsn(FCMPL);
            mv.visitJumpInsn(IFNE, l);
        }
//        else if (type == PrimType.OBJECT) {
//            mv.visitJumpInsn(IF_ACMPNE, l);
//        } 
        else {
            mv.visitJumpInsn(IF_ICMPNE, l);
        }
    }

    public class Info {
        public final Attribute<?> attribute;
        /** For example <code>Lorg/codehaus/cake/attribute/DoubleAttribute; </code> */
        final String attributeDescriptor;
        final String descriptor;
        private final int index;
        final boolean isHidden;
        final boolean isMutable;
        final Type type;

        final PrimType vType;

        Info(int index, AttributeInfo info) {
            this.index = index;
            this.isHidden = info.isHidden();
            this.isMutable = info.isMutable();
            this.attribute = info.getAttribute();
            type = Type.getType(attribute.getType());
            this.descriptor = type.getDescriptor();
            vType = PrimType.from(attribute);
            attributeDescriptor = vType.getDescriptor();
        }

        public String getAtrDescriptor() {
            return descriptor;
        }

        public String getFieldName() {
            return "v" + index;
        }

        String getFieldStaticName() {
            return "A" + index;
        }

        String getPrimTypeDescriptor() {
            if (attribute instanceof ObjectAttribute) {
                return Type.getType(attribute.getType()).getDescriptor();
            }
            return vType.getPrimDescriptor();
        }

        void visitGet(MethodVisitor mv) {
            mv.visitFieldInsn(GETFIELD, classDescriptor, getFieldName(), getPrimTypeDescriptor());
        }

        void visitStaticGet(MethodVisitor mv) {
            mv.visitFieldInsn(GETSTATIC, classDescriptor, getFieldStaticName(), vType
                    .getDescriptor());
        }
    }

    static class MyLoader extends ClassLoader {
        public Class defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}
