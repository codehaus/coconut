package org.codehaus.cake.internal.container.phases;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.cake.internal.container.RunState;
import org.codehaus.cake.internal.container.phases.AbstractPhase.Entry;
import org.codehaus.cake.internal.picocontainer.MutablePicoContainer;
import org.codehaus.cake.internal.picocontainer.defaults.DefaultPicoContainer;

public abstract class APhase {

    final List<Entry> entries = new ArrayList<Entry>();
    final MutablePicoContainer mpc = new DefaultPicoContainer();

    public void addArgument(Object... arguments) {
        for (Object o : arguments) {
            mpc.registerComponentInstance(o);
        }
    }

    public void addArguments(Iterable arguments) {
        for (Object o : arguments) {
            addArgument(o);
        }
    }

    public abstract void start(RunState state);

    public void prepareAll(Class<? extends Annotation> annotation, Iterable i) {
        for (Object o : i) {
            prepare(annotation, o);
        }
    }

    public void prepare(Class<? extends Annotation> annotation, Object o) {
        if (o != null) {
            for (Method m : o.getClass().getMethods()) {
                Annotation a = m.getAnnotation(annotation);
                if (a != null) {
                    if (m.getParameterTypes().length > 0) {
                        if (mpc.getComponentInstances().size() == 0) {
                            entries.clear();// startup phase
                            throw new IllegalArgumentException("Cannot use the " + annotation
                                    + " on a method that takes argumenets");

                        }
                        for (int i = 0; i < m.getParameterTypes().length; i++) {
                            Object oo = mpc.getComponentInstanceOfType(m.getParameterTypes()[i]);
                            if (oo == null) {
                                throw new IllegalStateException("No service registered for type "
                                        + m.getParameterTypes()[i]);
                            }
                        }
                    }
                    Entry e = new Entry(o);
                    e.methods.add(m);
                    entries.add(e);
                }
            }
        }
    }

    public void runPhase() throws Exception {
        try {
            for (Entry entry : entries) {
                for (Method m : entry.methods) {
                    invoke(entry.o, m);
                }
            }
        } finally {
            entries.clear();
        }
    }

    void invoke(Object o, Method m) throws Exception {
        Object[] obs = new Object[m.getParameterTypes().length];
        for (int i = 0; i < m.getParameterTypes().length; i++) {
            Object oo = mpc.getComponentInstanceOfType(m.getParameterTypes()[i]);
            if (oo == null) {
                throw new IllegalStateException("No service registered for type "
                        + m.getParameterTypes()[i]);
            }
            obs[i] = oo;
        }
        try {
            m.invoke(o, obs);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            } else if (e.getCause() instanceof Error) {
                throw (Error) e.getCause();
            }
            throw e;
        }
    }
}
