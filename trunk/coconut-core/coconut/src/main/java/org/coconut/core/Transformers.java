package org.coconut.core;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
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

    public static <K, V> Transformer<Map.Entry<K, V>, V> mapEntryToValue() {
        return new ValueFromMapEntry<K, V>();
    }

    public static <K, V> Transformer<Map.Entry<K, V>, K> mapEntryToKey() {
        return new KeyFromMapEntry<K, V>();
    }

    public static <K, V> Map.Entry<K, V> immutableMapEntry(K key, V value) {
        return EventHandlers.newMapEntry(key, value);
    }

    public static <K, F, T> Map<K, T> wrapMap(final Map<K, F> map,
            final Transformer<F, T> tIn, final Transformer<T, F> tOut) {
        return new Map<K, T>() {
            public int size() {
                return map.size();
            }

            public boolean isEmpty() {
                return map.isEmpty();
            }

            public boolean containsKey(Object key) {
                return map.containsKey(key);
            }

            @SuppressWarnings("unchecked")
            public boolean containsValue(Object value) {
                return map.containsValue(tIn.transform((F) value));
            }

            public T get(Object key) {
                return tIn.transform(map.get(key));
            }

            public T put(K key, T value) {
                return tIn.transform(map.put(key, tOut.transform(value)));
            }

            public T remove(Object key) {
                return tIn.transform(map.remove(key));
            }

            public void putAll(Map<? extends K, ? extends T> t) {
                Map<K, F> m = new HashMap<K, F>();
                for (Map.Entry<? extends K, ? extends T> entry : t.entrySet()) {
                    m.put(entry.getKey(), tOut.transform(entry.getValue()));
                }
                map.putAll(m);
            }

            public void clear() {
                map.clear();
            }

            public Set<K> keySet() {
                return map.keySet();
            }

            public Collection<T> values() {
                return wrapCollection(map.values(), tIn, tOut);
            }

            public Set<Entry<K, T>> entrySet() {
                @SuppressWarnings("unused") Transformer<Entry<K, F>, Entry<K, T>> t1 = new Transformer<Entry<K, F>, Entry<K, T>>() {
                    public Entry<K, T> transform(Entry<K, F> from) {
                        return null;
                    }

                };
                return null;
                // new ImmutableMapEntry
                // return set(map.entrySet(),tIn,tOut);
            }

        };

    }

    @SuppressWarnings("unchecked")
    public static <F, T> Collection<T> wrapCollection(final Collection<F> col,
            final Transformer<F, T> tIn, final Transformer<T, F> tOut) {
        return (Collection<T>) col;
    }

    @SuppressWarnings("unchecked")
    public static <F, T> List<T> wrapList(final List<F> col,
            final Transformer<F, T> tIn, final Transformer<T, F> tOut) {
        return (List<T>) col;
    }

    @SuppressWarnings("unchecked")
    public static <F, T> Queue<T> wrapQueue(final Queue<F> col,
            final Transformer<F, T> tIn, final Transformer<T, F> tOut) {
        return (Queue<T>) col;
    }

    @SuppressWarnings("unchecked")
    public static <F, T> Set<T> wrapSet(final Set<F> col,
            final Transformer<F, T> tIn, final Transformer<T, F> tOut) {
        return (Set<T>) col;
    }

    public static <F, T> Future<T> wrapFuture(final Future<F> from,
            final Transformer<F, T> t) {
        return new Future<T>() {

            public boolean cancel(boolean mayInterruptIfRunning) {
                return from.cancel(mayInterruptIfRunning);
            }

            public boolean isCancelled() {
                return from.isCancelled();
            }

            public boolean isDone() {
                return from.isDone();
            }

            public T get() throws InterruptedException, ExecutionException {
                return t.transform(from.get());
            }

            public T get(long timeout, TimeUnit unit)
                    throws InterruptedException, ExecutionException,
                    TimeoutException {
                return t.transform(from.get(timeout, unit));
            }
        };
    }

    static class TransformableCallable<F, T> implements Callable<T>,
            Serializable {
        /** serialVersionUID. */
        private static final long serialVersionUID = -7911352798294529252L;

        private final Callable<F> from;

        private final Transformer<F, T> t;

        public TransformableCallable(Callable<F> from, Transformer<F, T> t) {
            if (from == null) {
                throw new NullPointerException("from is null");
            } else if (t == null) {
                throw new NullPointerException("t is null");
            }
            this.from = from;
            this.t = t;
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

        public TransformableFuture(Future<F> from, Transformer<F, T> t) {
            if (from == null) {
                throw new NullPointerException("from is null");
            } else if (t == null) {
                throw new NullPointerException("t is null");
            }
            this.from = from;
            this.t = t;
        }

        /**
         * @see java.util.concurrent.Future#cancel(boolean)
         */
        public boolean cancel(boolean mayInterruptIfRunning) {
            return from.cancel(mayInterruptIfRunning);
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
    }

    public static <F, T> Callable<T> wrapCallable(final Callable<F> from,
            final Transformer<F, T> t) {
        return new TransformableCallable<F, T>(from, t);
    }

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
            System.err.println("unknown type " + c);
            throw new IllegalAccessError();
        }
    }

    @SuppressWarnings("unchecked")
    public static <F, T> DynamicTransformer<F, T> transform(Class<F> fromClass,
            String method, Object... parameters) {
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
                return new GeneratedTransformer<F, T>(fromClass.getMethod(
                        method, (Class[]) null), parameters);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("public method " + method
                        + "() not found for class " + fromClass);
            }
        }

        // find all potential matches
        // because we need to handle nulls, this approach is easier,
        // then doing a single scan looking for the method

        int pLength = parameters == null ? 1 : parameters.length;
//        Object[] params = parameters == null ? new Object[] { null }
//                : parameters;
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
            throw new IllegalArgumentException("no public method called "
                    + method + " found for class " + fromClass);
        } else if (list.size() == 0) {
            if (foundNullPrimitiveMethod) {
                throw new IllegalArgumentException(
                        "there was only public method called " + method
                                + " found for class " + fromClass);
            } else {
                throw new IllegalArgumentException("no public method called "
                        + method + " with " + pLength
                        + " parameters found for class " + fromClass);
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
                            && fromPrimitive(arg).equals(
                                    parameters[i].getClass()))) {
                        iter.remove();
                    }
                }
            }
        }

        if (list.size() == 0) {
            throw new IllegalArgumentException("no public method called "
                    + method + " with " + pLength
                    + " parameters found for class " + fromClass);
        } else if (list.size() == 1) {
            // only one matching method, we will take it
            return new GeneratedTransformer<F, T>(list.get(0), parameters);
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
                        if (!(arg.equals(parameters[i].getClass()) || (arg
                                .isPrimitive())
                                && fromPrimitive(arg).equals(
                                        parameters[i].getClass()))) {
                            iter.remove();
                        }
                    }
                }
            }
            if (list.size() == 0) {
                throw new IllegalArgumentException(
                        "There was no public method called "
                                + method
                                + " that matched the exact signature of "
                                + pLength
                                + " However there was dublicate matches, ...list others pr "
                                + fromClass);
            } else if (list.size() == 1) {
                // only one matching method, we will take it
                return new GeneratedTransformer<F, T>(list.get(0), parameters);
            } else {
                // there was multiple matching signature, because of autoboxing
                throw new IllegalArgumentException(
                        "There was no public method called "
                                + method
                                + " that matched the exact signature of "
                                + pLength
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
        return new GeneratedTransformer<F, T>(method, parameters);
    }

    final static class NoTransformer<K> implements Transformer<K, K> {
        public K transform(K element) {
            return element;
        }
    }

    public static <K> Transformer<K,K> noTransformer() {
        return new NoTransformer<K>();
    }
    final static class ValueFromMapEntry<K, V> implements
            Transformer<Map.Entry<K, V>, V> {
        public V transform(Map.Entry<K, V> element) {
            return element.getValue();
        }
    }

    final static class KeyFromMapEntry<K, V> implements
            Transformer<Map.Entry<K, V>, K> {
        public K transform(Map.Entry<K, V> element) {
            return element.getKey();
        }
    }

    public static <F, T> Transformer<F, T> reflect(Class<F> type, String method)
            throws SecurityException, NoSuchMethodException {
        return reflect(type, method, null);
    }

    @SuppressWarnings("unchecked")
    public static <F, T> Transformer<F, T> t(Transformer... transformers) {
        if (transformers == null) {
            throw new NullPointerException("transformers is null");
        } else if (transformers.length == 1) {
            return transformers[0];
        } else {
            return new DTransformer(transformers);
        }
    }

    static class DTransformer implements Transformer {

        private final Transformer[] t;

        public DTransformer(Transformer[] t) {
            this.t = t;
        }

        /**
         * @see org.coconut.core.Transformer#transform(F)
         */
        @SuppressWarnings("unchecked")
        public Object transform(Object from) {
            Object o = from;
            for (int i = t.length - 1; i >= 0; i++) {
                o = t[i].transform(o);
            }
            return o;
        }

    }

    @SuppressWarnings("unchecked")
    public static <F, T> Transformer<F, T> reflect(Class<F> type,
            String method, Class<T> to) throws SecurityException,
            NoSuchMethodException {
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

    /**
     * This class is serializable is all provided parameters are serializable
     */
    static final class GeneratedTransformer<F, T> implements
            DynamicTransformer<F, T>, Serializable, Cloneable {

        // TODO make serializable
        // TODO make primitive arguments
        // TODO better exception messages
        // TODO toString method
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

        private final static String transformerTypeName = Type
                .getInternalName(DynamicTransformer.class);

        private final static Object[] EMPTY_ARRAY = new Object[0];

        private final static AtomicLong counter = new AtomicLong();

        private final static SimpleLoader loader = new SimpleLoader(
                GeneratedTransformer.class.getClassLoader());

        private final static Field f;
        static {
            Field field = null;
            try {
                field = GeneratedTransformer.class.getField("t");
            } catch (NoSuchFieldException e) { /* not happening */
            }
            f = field;
        }

        private static String generateClassName(Method m) {
            return m.getName() + "From" + getFullName(m.getDeclaringClass())
                    + counter.incrementAndGet();
        }

        @SuppressWarnings("unchecked")
        private static <F, T> DynamicTransformer<F, T> generateTransformer(
                Method method, Object... args) {
            // TODO rework this classloading sh#t
            ClassLoader cl = method.getDeclaringClass().getClassLoader();

            final SimpleLoader sl = cl == null
                    || cl.equals(GeneratedTransformer.class.getClassLoader()) ? loader
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

            MethodVisitor mv;
            FieldVisitor fv;

            // Generate Header
            cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                    className, "Ljava/lang/Object;L" + transformerTypeName
                            + "<" + from + to + ">;", "java/lang/Object",
                    new String[] { transformerTypeName });

            cw.visitSource(shortClassName + ".java", null);
            String construtorDesc = "";
            for (int i = 0; i < parameters.length; i++) {
                fv = cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL,
                        "param" + (i + 1), Type.getDescriptor(parameters[i]),
                        null, null);
                fv.visitEnd();
                construtorDesc += Type.getDescriptor(parameters[i]);
            }
            // Generate Constructor
            {
                mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "("
                        + construtorDesc + ")V", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object",
                        "<init>", "()V");

                for (int i = 0; i < parameters.length; i++) {
                    Label l = new Label();
                    mv.visitLabel(l);
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.ALOAD, i + 1);
                    mv.visitFieldInsn(Opcodes.PUTFIELD, className, "param"
                            + (i + 1), Type.getDescriptor(parameters[i]));
                }

                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitInsn(Opcodes.RETURN);

                Label l2 = new Label();
                mv.visitLabel(l2);
                mv.visitLocalVariable("this", "L" + className + ";", null, l0,
                        l2, 0);
                for (int i = 0; i < parameters.length; i++) {
                    mv.visitLocalVariable("p" + (1 + i), Type
                            .getDescriptor(parameters[i]), null, l0, l2, 1 + i);
                }
                mv.visitMaxs(1, 1 + parameters.length);
                mv.visitEnd();
            }
            // generate getParameters method
            {
                mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getParameters",
                        "()[Ljava/lang/Object;", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitLineNumber(16, l0);
                mv.visitInsn(Opcodes.ACONST_NULL);
                mv.visitInsn(Opcodes.ARETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "L" + className + ";", null, l0,
                        l1, 0);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
            }
            // Generate dummy getMethod() just returns null
            {
                mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getMethod",
                        "()Ljava/lang/reflect/Method;", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitLineNumber(22, l0);
                mv.visitInsn(Opcodes.ACONST_NULL);
                mv.visitInsn(Opcodes.ARETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "L" + className + ";", null, l0,
                        l1, 0);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
            }
            // generate the actual transform method
            {
                mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "transform", "(" + from
                        + ")" + to, null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitLineNumber(7, l0);
                mv.visitVarInsn(Opcodes.ALOAD, 1);
                for (int i = 0; i < parameters.length; i++) {
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitFieldInsn(Opcodes.GETFIELD, className, "param"
                            + (i + 1), Type.getDescriptor(parameters[i]));

                }

                int opcode = m.getDeclaringClass().isInterface() ? Opcodes.INVOKEINTERFACE
                        : Opcodes.INVOKEVIRTUAL;
                mv.visitMethodInsn(opcode, Type.getInternalName(m
                        .getDeclaringClass()), m.getName(), "("
                        + construtorDesc + ")"
                        + Type.getDescriptor(m.getReturnType()));

                if (m.getReturnType().isPrimitive()) {
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type
                            .getInternalName(returnType), "valueOf", "("
                            + Type.getDescriptor(m.getReturnType()) + ")" + to);
                }
                mv.visitInsn(Opcodes.ARETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "L" + className + ";", null, l0,
                        l1, 0);
                mv.visitLocalVariable("from", from, null, l0, l1, 1);
                mv.visitMaxs(1 + parameters.length, 2);
                mv.visitEnd();
            }
            // generate type less transform method
            {
                mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_BRIDGE
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
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className,
                        "transform", "(" + from + ")" + to);
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
                mv.visitInsn(Opcodes.ARETURN);
                mv.visitMaxs(2, 2);
                mv.visitEnd();
            }
            cw.visitEnd();

            return cw.toByteArray();
        }

        private final Method m;

        private final transient DynamicTransformer<F, T> t;

        private GeneratedTransformer(Method m, Object... args) {
            t = generateTransformer(m, args);
            this.m = m;
        }

        /**
         * Constructs a new transformer by copying an existing
         * GeneratedTransformer.
         * 
         * @param transformer
         *            the GeneratedTransformer to copy
         */
        public GeneratedTransformer(GeneratedTransformer<F, T> transformer) {
            this.m = transformer.m;
            this.t = transformer.t;
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
         * @see java.lang.Object#clone()
         */
        @Override
        protected Object clone() {
            return new GeneratedTransformer<F, T>(this);
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof GeneratedTransformer)
                    && ((GeneratedTransformer) obj).m.equals(m);
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
            return "todo transform";
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

        private static String getFullName(Class c) {
            String name = c.getSimpleName();
            Class clazz = c;
            while (clazz.isMemberClass()) {
                clazz = clazz.getDeclaringClass();
                name = clazz.getSimpleName() + "$" + name;
            }
            return name;
        }

        /**
         * Reconstitute the <tt>ConcurrentHashMap</tt> instance from a stream
         * (i.e., deserialize it).
         * 
         * @param s
         *            the stream
         */
        private void readObject(java.io.ObjectInputStream s)
                throws IOException, ClassNotFoundException {
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

        private void writeObject(java.io.ObjectOutputStream s)
                throws IOException {
            s.defaultWriteObject();
            s.writeObject(t.getParameters());
        }
    }

    public interface DynamicTransformer<F, T> extends Transformer<F, T> {
        /**
         * Returns additional parameters that
         * 
         * @return
         */
        public Object[] getParameters();

        /**
         * Returns the method that is being called from the dynamic transformer.
         * 
         * @return the method that is being called from the dynamic transformer.
         */
        public Method getMethod();
    }

    private Transformers() {

    }

}
