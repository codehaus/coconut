/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.core;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import org.coconut.internal.asm.ClassWriter;
import org.coconut.internal.asm.FieldVisitor;
import org.coconut.internal.asm.Label;
import org.coconut.internal.asm.MethodVisitor;
import org.coconut.internal.asm.Opcodes;
import org.coconut.internal.asm.Type;

/**
 * Not quite done yet.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class Transformers {

    public interface DynamicTransformer<F, T> extends Transformer<F, T> {
        /**
         * Returns the method that is being called from the dynamic transformer.
         * 
         * @return the method that is being called from the dynamic transformer.
         */
        Method getMethod();

        /**
         * Returns additional parameters that
         * 
         * @return
         */
        Object[] getParameters();
    }

    /**
     * This class is serializable is all provided parameters are serializable
     */
    static final class ASMBasedTransformer<F, T> implements DynamicTransformer<F, T>,
            Serializable, Cloneable {

        // DONE toString method

        // TODO make serializable
        // TODO make primitive arguments
        // TODO better exception messages
        // TODO generated class should return getParameters()
        // TODO handle void
        static class SimpleLoader extends SecureClassLoader {
            SimpleLoader(ClassLoader loader) {
                super(loader);
            }

            Class defineClass(String name, byte[] code) {
                return defineClass(name, code, 0, code.length);
            }
        }

        private final static AtomicLong counter = new AtomicLong();

        private final static Object[] EMPTY_ARRAY = new Object[0];

        private final static Field f;

        private final static SimpleLoader loader = new SimpleLoader(
                ASMBasedTransformer.class.getClassLoader());

        private final static String transformerTypeName = Type
                .getInternalName(DynamicTransformer.class);
        static {
            Field field = null;
            try {
                field = ASMBasedTransformer.class.getField("t");
            } catch (NoSuchFieldException e) { /* not happening */
            }
            f = field;
        }

        private static String generateClassName(Method m) {
            return m.getName() + "From" + getFullName(m.getDeclaringClass())
                    + counter.incrementAndGet();
        }

        @SuppressWarnings("unchecked")
        private static <F, T> DynamicTransformer<F, T> generateTransformer(Method method,
                Object... args) {
            // TODO rework this classloading sh#t
            ClassLoader cl = method.getDeclaringClass().getClassLoader();

            final SimpleLoader sl = cl == null
                    || cl.equals(ASMBasedTransformer.class.getClassLoader()) ? loader
                    : new SimpleLoader(cl);

            final String name = generateClassName(method);
            Class c = sl.defineClass(name, generateTransformer(name, method));
            Constructor cons;
            try {
                cons = c.getConstructor((Class[]) method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        "No Constructor Found. This is a serious bug in the underlying framework. This should never happen",
                        e);
            }
            DynamicTransformer<F, T> p = null;
            try {
                p = (DynamicTransformer) cons.newInstance(args);
            } catch (Exception e) {
                throw new IllegalStateException(
                        "This is a serious bug in the underlying framework. This should never happen",
                        e);
            }
            return p;
        }

        private static byte[] generateTransformer(String className, Method m) {
            Class[] parameters = m.getParameterTypes();
            String from = Type.getDescriptor(m.getDeclaringClass());
            String[] spilt = className.split("/");
            String shortClassName = spilt[spilt.length - 1];
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            final Class returnType;
            if (m.getReturnType().isPrimitive()) {
                returnType = fromPrimitive(m.getReturnType());
            } else {
                returnType = m.getReturnType();
            }
            String to = Type.getDescriptor(returnType);

            // Generate Header
            cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, className,
                    "Ljava/lang/Object;L" + transformerTypeName + "<" + from + to + ">;",
                    "java/lang/Object", new String[] { transformerTypeName });

            cw.visitSource(shortClassName + ".java", null);
            String construtorDesc = "";
            for (int i = 0; i < parameters.length; i++) {
                FieldVisitor fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL,
                        "param" + (i + 1), Type.getDescriptor(parameters[i]), null, null);
                fv.visitEnd();
                construtorDesc += Type.getDescriptor(parameters[i]);
            }
            // Generate Constructor
            {
                MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "("
                        + construtorDesc + ")V", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>",
                        "()V");

                for (int i = 0; i < parameters.length; i++) {
                    Label l = new Label();
                    mv.visitLabel(l);
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.ALOAD, i + 1);
                    mv.visitFieldInsn(Opcodes.PUTFIELD, className, "param" + (i + 1),
                            Type.getDescriptor(parameters[i]));
                }

                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitInsn(Opcodes.RETURN);

                Label l2 = new Label();
                mv.visitLabel(l2);
                mv.visitLocalVariable("this", "L" + className + ";", null, l0, l2, 0);
                for (int i = 0; i < parameters.length; i++) {
                    mv.visitLocalVariable("p" + (1 + i), Type
                            .getDescriptor(parameters[i]), null, l0, l2, 1 + i);
                }
                mv.visitMaxs(1, 1 + parameters.length);
                mv.visitEnd();
            }
            // generate getParameters method
            {
                MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getParameters",
                        "()[Ljava/lang/Object;", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitLineNumber(16, l0);
                mv.visitInsn(Opcodes.ACONST_NULL);
                mv.visitInsn(Opcodes.ARETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "L" + className + ";", null, l0, l1, 0);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
            }
            // Generate dummy getMethod() just returns null
            {
                MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getMethod",
                        "()Ljava/lang/reflect/Method;", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitLineNumber(22, l0);
                mv.visitInsn(Opcodes.ACONST_NULL);
                mv.visitInsn(Opcodes.ARETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "L" + className + ";", null, l0, l1, 0);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
            }
            // generate the actual transform method
            {
                MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "transform", "("
                        + from + ")" + to, null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitLineNumber(7, l0);
                mv.visitVarInsn(Opcodes.ALOAD, 1);
                for (int i = 0; i < parameters.length; i++) {
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitFieldInsn(Opcodes.GETFIELD, className, "param" + (i + 1),
                            Type.getDescriptor(parameters[i]));

                }

                int opcode = m.getDeclaringClass().isInterface() ? Opcodes.INVOKEINTERFACE
                        : Opcodes.INVOKEVIRTUAL;
                mv.visitMethodInsn(opcode, Type.getInternalName(m.getDeclaringClass()), m
                        .getName(), "(" + construtorDesc + ")"
                        + Type.getDescriptor(m.getReturnType()));

                if (m.getReturnType().isPrimitive()) {
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type
                            .getInternalName(returnType), "valueOf", "("
                            + Type.getDescriptor(m.getReturnType()) + ")" + to);
                }
                mv.visitInsn(Opcodes.ARETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "L" + className + ";", null, l0, l1, 0);
                mv.visitLocalVariable("from", from, null, l0, l1, 1);
                mv.visitMaxs(1 + parameters.length, 2);
                mv.visitEnd();
            }
            // generate type less transform method
            {
                MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_BRIDGE
                        + Opcodes.ACC_SYNTHETIC, "transform",
                        "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitLineNumber(1, l0);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitVarInsn(Opcodes.ALOAD, 1);
                mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(m
                        .getDeclaringClass()));
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, "transform", "("
                        + from + ")" + to);
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
                mv.visitInsn(Opcodes.ARETURN);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
            }
            cw.visitEnd();

            return cw.toByteArray();
        }

        private static String getFullName(Class c) {
            String name = c.getSimpleName();
            Class clazz = c;
            while (clazz.isMemberClass()) {
                clazz = clazz.getDeclaringClass();
                name = clazz.getSimpleName() + "$" + name;
            }
            return name;
        }

        private final Method m;

        private final transient DynamicTransformer<F, T> t;

        /**
         * Constructs a new transformer by copying an existing
         * GeneratedTransformer.
         * 
         * @param transformer
         *            the GeneratedTransformer to copy
         */
        public ASMBasedTransformer(ASMBasedTransformer<F, T> transformer) {
            this.m = transformer.m;
            this.t = transformer.t;
        }

        private ASMBasedTransformer(Method m, Object... args) {
            t = generateTransformer(m, args);
            this.m = m;
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof ASMBasedTransformer)
                    && ((ASMBasedTransformer) obj).m.equals(m);
        }

        /**
         * @see org.coconut.core.Transformers.DynamicTransformer#getMethod()
         */
        public Method getMethod() {
            return m;
        }

        /**
         * @see org.coconut.core.Transformers.DynamicTransformer#getParameters()
         */
        public Object[] getParameters() {
            Object[] parameters = t.getParameters();
            return parameters == null ? EMPTY_ARRAY : parameters;
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return m.hashCode();
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Dynamic transformer (" + m.getDeclaringClass().getCanonicalName()
                    + " -> " + m.getReturnType().getCanonicalName() + ")";
        }

        /**
         * @see org.coconut.core.Transformer#transform(null)
         */
        public T transform(F from) {
            if (from == null) {
                throw new NullPointerException("from is null");
            }
            return t.transform(from);
        }

        /**
         * Reconstitute the <tt>ConcurrentHashMap</tt> instance from a stream
         * (i.e., deserialize it).
         * 
         * @param s
         *            the stream
         */
        private void readObject(java.io.ObjectInputStream s) throws IOException,
                ClassNotFoundException {
            s.defaultReadObject();
            Object[] args = (Object[]) s.readObject();
            DynamicTransformer<F, T> dt = generateTransformer(m, args);
            boolean prev = f.isAccessible();
            f.setAccessible(true);
            try {
                f.set(this, dt);
            } catch (IllegalAccessException e) {
                throw new IOException(
                        "could not deserialize the object, this is highly irregular");
            }
            f.setAccessible(prev);
        }

        private void writeObject(java.io.ObjectOutputStream s) throws IOException {
            s.defaultWriteObject();
            s.writeObject(t.getParameters());
        }

        /**
         * @see java.lang.Object#clone()
         */
        @Override
        protected Object clone() {
            return new ASMBasedTransformer<F, T>(this);
        }
    }

    /**
     * TODO describe
     */
    final static class ArrayTransformer implements Transformer, Serializable, Cloneable {
        /** serialVersionUID */
        private static final long serialVersionUID = 4920880113547573214L;

        private final Transformer<Object, Object>[] t;

        ArrayTransformer(Transformer<Object, Object>[] t) {
            this.t = t;
        }

        @Override
        public Object clone() {
            return new ArrayTransformer(t);
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof ArrayTransformer)
                    && Arrays.equals(t, ((ArrayTransformer) obj).t);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(t);
        }

        @Override
        public String toString() {
            return Arrays.toString(t);
        }

        /**
         * @see org.coconut.core.Transformer#transform(F)
         */
        @SuppressWarnings("unchecked")
        public Object transform(Object from) {
            Object o = from;
            for (int i = 0; i < t.length; i++) {
                o = t[i].transform(o);
            }
            return o;
        }
    }

    final static class PassThroughTransformer<K> implements Transformer<K, K>,
            Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -8159540593935721003L;

        /**
         * @see org.coconut.core.Transformer#transform(java.lang.Object)
         */
        public K transform(K element) {
            return element;
        }
    }

    final static class TransformableCallable<F, T> implements Callable<T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -7911352798294529252L;

        private final Callable<F> from;

        private final Transformer<F, T> t;

        public TransformableCallable(Callable<F> callable, Transformer<F, T> transformer) {
            if (callable == null) {
                throw new NullPointerException("from is null");
            } else if (transformer == null) {
                throw new NullPointerException("t is null");
            }
            this.from = callable;
            this.t = transformer;
        }

        public T call() throws Exception {
            return t.transform(from.call());
        }
    }

    static class TransformableFuture<F, T> implements Future<T>, Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -7911352798294529252L;

        private final Future<F> from;

        private final Transformer<F, T> t;

        public TransformableFuture(Future<F> future, Transformer<F, T> transformer) {
            if (future == null) {
                throw new NullPointerException("from is null");
            } else if (transformer == null) {
                throw new NullPointerException("t is null");
            }
            this.from = future;
            this.t = transformer;
        }

        /**
         * @see java.util.concurrent.Future#cancel(boolean)
         */
        public boolean cancel(boolean mayInterruptIfRunning) {
            return from.cancel(mayInterruptIfRunning);
        }

        /**
         * @see java.util.concurrent.Future#get()
         */
        public T get() throws InterruptedException, ExecutionException {
            return t.transform(from.get());
        }

        /**
         * @see java.util.concurrent.Future#get(long,
         *      java.util.concurrent.TimeUnit)
         */
        public T get(long timeout, TimeUnit unit) throws InterruptedException,
                ExecutionException, TimeoutException {
            return t.transform(from.get(timeout, unit));
        }

        /**
         * @see java.util.concurrent.Future#isCancelled()
         */
        public boolean isCancelled() {
            return from.isCancelled();
        }

        /**
         * @see java.util.concurrent.Future#isDone()
         */
        public boolean isDone() {
            return from.isDone();
        }
    }

    public static <K, V> Transformer<Map.Entry<K, V>, K> mapEntryToKey() {
        return (Transformer) transform(Map.Entry.class, "getKey");
    }

    public static <K, V> Transformer<Map.Entry<K, V>, V> mapEntryToValue() {
        return (Transformer) transform(Map.Entry.class, "getValue");
    }

    public static <K> Transformer<K, K> passThroughTransformer() {
        return new PassThroughTransformer<K>();
    }

    public static <F, T> Transformer<F, T> reflect(Class<F> type, String method)
            throws SecurityException, NoSuchMethodException {
        return reflect(type, method, null);
    }

    @SuppressWarnings("unchecked")
    public static <F, T> Transformer<F, T> reflect(Class<F> type, String method,
            Class<T> to) throws SecurityException, NoSuchMethodException {
        final Method m = type.getMethod(method, new Class[] {});
        return new Transformer<F, T>() {
            public T transform(F from) {
                try {
                    return (T) m.invoke(from, (Object[]) null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <F, T> Transformer<F, T> t(Transformer... transformers) {
        if (transformers == null) {
            throw new NullPointerException("transformers is null");
        } else if (transformers.length == 1) {
            return transformers[0];
        } else {
            return new ArrayTransformer(transformers);
        }
    }

    @SuppressWarnings("unchecked")
    public static <F, T> DynamicTransformer<F, T> transform(Class<F> fromClass,
            String method, Object... parameters) {

        // TODO write warn if they specify parameters=null
        // and does not find a method (just 30 min on that error)
        if (fromClass == null) {
            throw new NullPointerException("fromClass is null");
        } else if (method == null) {
            throw new NullPointerException("method is null");
        } else if (method.length() == 0) {
            throw new IllegalArgumentException("method name cannot be \"\"");
        }
        // Simpel version, looking for a method with no arguments
        if (parameters != null && parameters.length == 0) {
            try {
                return new ASMBasedTransformer<F, T>(fromClass.getMethod(method,
                        (Class[]) null), parameters);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("public method " + method
                        + "() not found for class " + fromClass);
            }
        }

        // find all potential matches
        // because we need to handle nulls, this approach is easier,
        // then doing a single scan looking for the method

        int pLength = parameters == null ? 1 : parameters.length;
        // Object[] params = parameters == null ? new Object[] { null }
        // : parameters;
        ArrayList<Method> list = new ArrayList<Method>();
        boolean foundName = false; // did we find any method with right name
        boolean foundNullPrimitiveMethod = false;
        for (Method m : fromClass.getMethods()) {
            if (m.getName().equals(method)) {
                foundName = true; // there is a name that matches all right
                if (parameters == null) {
                    if (m.getParameterTypes().length == 1) {
                        if (m.getParameterTypes()[0].isPrimitive()) {
                            // we found a match, however it requires a
                            // primitive as argument
                            foundNullPrimitiveMethod = true;
                        } else {
                            // we found a method taking a single argument
                            // that is not primitive
                            list.add(m);
                        }
                    }
                } else if (m.getParameterTypes().length == parameters.length) {
                    list.add(m);
                }
            }
        }
        if (!foundName) {
            throw new IllegalArgumentException("no public method called " + method
                    + " found for class " + fromClass);
        } else if (list.size() == 0) {
            if (foundNullPrimitiveMethod) {
                throw new IllegalArgumentException("there was only public method called "
                        + method + " found for class " + fromClass);
            } else {
                throw new IllegalArgumentException("no public method called " + method
                        + " with " + pLength + " parameters found for class " + fromClass);
            }
        }

        // Now we have a list of potential matches
        // remove all methods where the type of the argumentes does not match
        for (Iterator<Method> iter = list.iterator(); iter.hasNext();) {
            Method m = iter.next();
            for (int i = 0; i < m.getParameterTypes().length; i++) {
                Class arg = m.getParameterTypes()[i];
                if (parameters == null || parameters[i] == null) {
                    // we have a null argument, match all except primitives
                    if (arg.isPrimitive()) {
                        iter.remove();
                    }
                } else {
                    // make sure they are assignable
                    if (!(arg.isAssignableFrom(parameters[i].getClass()) || (arg
                            .isPrimitive())
                            && fromPrimitive(arg).equals(parameters[i].getClass()))) {
                        iter.remove();
                    }
                }
            }
        }

        if (list.size() == 0) {
            throw new IllegalArgumentException("no public method called " + method
                    + " with " + pLength + " parameters found for class " + fromClass);
        } else if (list.size() == 1) {
            // only one matching method, we will take it
            return new ASMBasedTransformer<F, T>(list.get(0), parameters);
        } else {
            // multiple methods with right number of arguments
            // first try and find one where the types of the argument are an
            // exact match, however foo(int) and foo(Integer) will both match
            // Integer.class
            for (Iterator<Method> iter = list.iterator(); iter.hasNext();) {
                Method m = iter.next();
                for (int i = 0; i < m.getParameterTypes().length; i++) {
                    Class arg = m.getParameterTypes()[i];
                    if (parameters[i] != null) {
                        if (!(arg.equals(parameters[i].getClass()) || (arg.isPrimitive())
                                && fromPrimitive(arg).equals(parameters[i].getClass()))) {
                            iter.remove();
                        }
                    }
                }
            }
            if (list.size() == 0) {
                throw new IllegalArgumentException("There was no public method called "
                        + method + " that matched the exact signature of " + pLength
                        + " However there was dublicate matches, ...list others pr "
                        + fromClass);
            } else if (list.size() == 1) {
                // only one matching method, we will take it
                return new ASMBasedTransformer<F, T>(list.get(0), parameters);
            } else {
                // there was multiple matching signature, because of autoboxing
                throw new IllegalArgumentException("There was no public method called "
                        + method + " that matched the exact signature of " + pLength
                        + " However there was dublicate matches, ...list others pr "
                        + fromClass);

            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <F, T> DynamicTransformer<F, T> transform(Method method,
            Object... parameters) {
        if (method == null) {
            throw new NullPointerException("method is null");
        }
        if (parameters == null) {
            if (method.getParameterTypes().length != 1
                    || method.getParameterTypes()[0].isPrimitive()) {
                throw new IllegalArgumentException("TODO");
            }
        } else {
            if (method.getParameterTypes().length == parameters.length) {
                boolean ok = true;
                for (int i = 0; i < parameters.length; i++) {
                    Class arg = method.getParameterTypes()[i];
                    if (parameters[i] == null) {
                        ok &= !arg.isPrimitive();
                    } else {
                        ok &= arg.isAssignableFrom(parameters[i].getClass());
                    }
                }
                if (!ok) {
                    throw new IllegalArgumentException("TODO");
                }
            } else {
                throw new IllegalArgumentException("TODO");
            }
        }
        // check assignability for parameters
        return new ASMBasedTransformer<F, T>(method, parameters);
    }

    public static <F, T> Callable<T> wrapCallable(final Callable<F> from,
            final Transformer<F, T> t) {
        return new TransformableCallable<F, T>(from, t);
    }

    @SuppressWarnings("unchecked")
    public static <F, T> Collection<T> wrapCollection(final Collection<F> col,
            final Transformer<F, T> tIn, final Transformer<T, F> tOut) {
        return (Collection<T>) col;
    }

    /**
     * Returns a future where any value retrieved using either
     * {@link Future#get()} or {@link Future#get(long, TimeUnit)} will returned
     * through the specified transformer.
     * <p>
     * 
     * @param from
     * @param t
     *            the transformer used for transforming the value returned from
     *            the specified future
     * @return
     */
    public static <F, T> Future<T> wrapFuture(final Future<F> future,
            final Transformer<F, T> transformer) {
        return new TransformableFuture<F, T>(future, transformer);
    }

    public static <F, T> Collection<T> transformCollection(Collection<? extends F> col,
            Transformer<F, T> t) {
        ArrayList<T> list = new ArrayList<T>(col.size());
        for (F f : col) {
            list.add(t.transform(f));
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <F, T> List<T> wrapList(final List<F> list,
            final Transformer<F, T> tIn, final Transformer<T, F> tOut) {
        return new WrappedList<F, T>(list, tIn, tOut);
    }

    static class WrappedCollection<V, VV> implements Collection<VV> {
        final Transformer<V, VV> from;

        final Transformer<VV, V> to;

        private final Collection<V> col;

        /**
         * @param list
         */
        WrappedCollection(Collection<V> col, Transformer<V, VV> from,
                Transformer<VV, V> to) {
            if (col == null) {
                throw new NullPointerException("col is null");
            } else if (from == null) {
                throw new NullPointerException("from is null");
            } else if (to == null) {
                throw new NullPointerException("to is null");
            } else
                this.col = col;
            this.to = to;
            this.from = from;
        }

        public boolean add(VV o) {
            return col.add(to.transform(o));
        }

        public boolean addAll(Collection<? extends VV> c) {
            return col.addAll(transformCollection(c, to));
        }

        public void clear() {
            col.clear();
        }

        public boolean isEmpty() {
            return col.isEmpty();
        }

        public int size() {
            return col.size();
        }

        public boolean contains(Object o) {
            return col.contains(to.transform((VV) o));
        }

        public boolean containsAll(Collection<?> c) {
            return col.containsAll(transformCollection((Collection) c, to));
        }

        public Iterator<VV> iterator() {
            final Iterator<V> iter = col.iterator();
            return new Iterator<VV>() {

                public boolean hasNext() {
                    return iter.hasNext();
                }

                public VV next() {
                    return from.transform(iter.next());
                }

                public void remove() {
                    iter.remove();
                }
            };
        }

        public boolean remove(Object o) {
            return col.remove(to.transform((VV) o));
        }

        public boolean removeAll(Collection<?> c) {
            return col.removeAll(transformCollection((Collection) c, to));
        }

        public boolean retainAll(Collection<?> c) {
            return col.retainAll(transformCollection((Collection) c, to));
        }

        // public int hashCode() {
        // int h = 0;
        // Iterator<VV> i = iterator();
        // while (i.hasNext()) {
        // VV obj = i.next();
        // if (obj != null)
        // h += obj.hashCode();
        // }
        // return h;
        // }

        public Object[] toArray() {
            return transformCollection((Collection) Arrays.asList(col.toArray()), from)
                    .toArray();
        }

        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException(
                    "Generified version of toArray not supported");
        }

    }

    static class WrappedList<V, VV> extends WrappedCollection<V, VV> implements List<VV> {

        private final List<V> list;

        WrappedList(List<V> list, Transformer<V, VV> from, Transformer<VV, V> to) {
            super(list, from, to);
            this.list=list;
        }

        /**
         * @see java.util.List#add(int, java.lang.Object)
         */
        public void add(int index, VV element) {
            list.add(index, to.transform(element));
        }

        /**
         * @see java.util.List#addAll(int, java.util.Collection)
         */
        public boolean addAll(int index, Collection<? extends VV> c) {
            return list.addAll(index, transformCollection(c, to));
        }

        /**
         * @see java.util.List#get(int)
         */
        public VV get(int index) {
            return from.transform(list.get(index));
        }

        /**
         * @see java.util.List#indexOf(java.lang.Object)
         */
        public int indexOf(Object o) {
            return list.indexOf(to.transform((VV) o));
        }

        /**
         * @see java.util.List#lastIndexOf(java.lang.Object)
         */
        public int lastIndexOf(Object o) {
            return list.lastIndexOf(to.transform((VV) o));
        }

        /**
         * @see java.util.List#listIterator()
         */
        public ListIterator<VV> listIterator() {
            return createListIterator(list.listIterator());
        }

        /**
         * @see java.util.List#listIterator(int)
         */
        public ListIterator<VV> listIterator(int index) {
            return createListIterator(list.listIterator(index));
        }

        private ListIterator<VV> createListIterator(final ListIterator<V> iter) {
            return new ListIterator<VV>() {

                public boolean hasNext() {
                    return iter.hasNext();
                }

                public VV next() {
                    return from.transform(iter.next());
                }

                public void remove() {
                    iter.remove();
                }

                public void add(VV o) {
                    iter.add(to.transform(o));
                }

                public boolean hasPrevious() {
                    return iter.hasPrevious();
                }

                public int nextIndex() {
                    return iter.nextIndex();
                }

                public VV previous() {
                    return from.transform(iter.previous());
                }

                public int previousIndex() {
                    return iter.previousIndex();
                }

                public void set(VV o) {
                    iter.set(to.transform(o));
                }
            };
        }

        /**
         * @see java.util.List#remove(int)
         */
        public VV remove(int index) {
            return from.transform(list.remove(index));
        }

        /**
         * @see java.util.List#set(int, java.lang.Object)
         */
        public VV set(int index, VV element) {
            return from.transform(list.set(index, to.transform(element)));
        }

        /**
         * @see java.util.List#subList(int, int)
         */
        public List<VV> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException("sublist not supported");
        }

    }

    //
    // static class WrappedMap<K, V, KK, VV> /* implements Map<K,V> */{
    // private final Map<KK, VV> map = null;
    //
    // private final Transformer<KK, K> fromKey = null;
    //
    // private final Transformer<KK, K> toKey = null;
    //
    // public void clear() {
    // map.clear();
    // }
    //
    // public boolean isEmpty() {
    // return map.isEmpty();
    // }
    //
    // public int size() {
    // return map.size();
    // }
    // }

    // @SuppressWarnings("unchecked")
    // public static <F, T> Queue<T> wrapQueue(final Queue<F> col,
    // final Transformer<F, T> tIn, final Transformer<T, F> tOut) {
    // throw new UnsupportedOperationException();
    // }
    //
    // @SuppressWarnings("unchecked")
    // public static <F, T> Set<T> wrapSet(final Set<F> col, final
    // Transformer<F, T> tIn,
    // final Transformer<T, F> tOut) {
    // throw new UnsupportedOperationException();
    // }

    private static Class fromPrimitive(Class c) {
        if (c.equals(Integer.TYPE)) {
            return Integer.class;
        } else if (c.equals(Double.TYPE)) {
            return Double.class;
        } else if (c.equals(Byte.TYPE)) {
            return Byte.class;
        } else if (c.equals(Float.TYPE)) {
            return Float.class;
        } else if (c.equals(Long.TYPE)) {
            return Long.class;
        } else if (c.equals(Short.TYPE)) {
            return Short.class;
        } else if (c.equals(Boolean.TYPE)) {
            return Boolean.class;
        } else if (c.equals(Character.TYPE)) {
            return Character.class;
        } else {
            throw new Error("unknown type " + c);
        }
    }

    private Transformers() {

    }

}
