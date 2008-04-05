package org.codehaus.cake.internal.container.phases;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.cake.internal.container.RunState;
import org.codehaus.cake.internal.picocontainer.MutablePicoContainer;
import org.codehaus.cake.internal.picocontainer.defaults.DefaultPicoContainer;
import org.codehaus.cake.internal.service.exceptionhandling.InternalExceptionService;

public class AbstractPhase {

    private final Class<? extends Annotation> annotation;
    final List<Entry> entries = new ArrayList<Entry>();
    private final String phase;
    private final InternalExceptionService ies;
    private final boolean isStarting;
    MutablePicoContainer mpc = new DefaultPicoContainer();
    RunState state;

    AbstractPhase(InternalExceptionService<?> ies, Class<? extends Annotation> annotation,
            String phaseName, boolean isStarting) {
        this.annotation = annotation;
        this.phase = phaseName;
        this.ies = ies;
        this.isStarting = isStarting;
    }

    public AbstractPhase prepareAll(Iterable list) {
        for (Object o : list) {
            prepare(o);
        }
        return this;
    }

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

    public void prepare(Object o) {
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

    static class Entry {
        final Object o;
        final List<Method> methods = new ArrayList<Method>(1);

        Entry(Object o) {
            this.o = o;
        }
    }


    public void runPhase(RunState state) throws Exception {
        try {
            for (Entry entry : entries) {
                for (Method m : entry.methods) {
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
                        invoke(state, entry.o, m, obs);
                    } catch (RuntimeException e) {
                        if (isStarting) {
                            throw e;
                        }
                    } catch (Exception e) {
                        if (isStarting) {
                            throw new IllegalStateException("Could not start", e);
                        }
                    }
                }
            }
        } finally {
            entries.clear();
        }
    }

    public void handleException(Exception e) throws Exception {
        if (isStarting) {
            throw e;
        }
        ies.error("Failed to run" + phase, e);
    }

    void invoke(Object o, Method m, Object... args) throws Exception {
        try {
            m.invoke(o, args);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            } else if (e.getCause() instanceof Error) {
                throw (Error) e.getCause();
            }
            throw e;
        }
    }

    void invoke(RunState state, Object o, Method m, Object... args) throws Exception {
        try {
            m.invoke(o, args);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception) {
                fail(state, "Exception while running " + phase, e.getCause());
                throw (Exception) e.getCause();
            } else if (e.getCause() instanceof Error) {
                fail(state, "Error while running " + phase, e.getCause());
                throw (Error) e.getCause();
            }
            fail(state, "Unknown Throwable", e.getCause());
            throw new RuntimeException(e); // some strang throwable
        } catch (IllegalAccessException e) {
            fail(state, "Could not invoke method while running " + phase, e.getCause());
        }
    }

    void invokeStarting(Object o, Method m, Object args) throws Exception {

    }

    void fail(RunState state, String msg, Throwable cause) {
        if (isStarting) {
            state.trySetStartupException(cause);
            ies.fatal(msg, cause);
        } else {
            ies.error(msg, cause);
        }
    }
}
