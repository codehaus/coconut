package org.codehaus.cake.internal.container.phases;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.codehaus.cake.container.ContainerConfiguration;
import org.codehaus.cake.container.lifecycle.Startable;
import org.codehaus.cake.container.lifecycle.StartableService;
import org.codehaus.cake.internal.container.Composer;
import org.codehaus.cake.internal.container.RunState;
import org.codehaus.cake.internal.container.phases.AbstractPhase.Entry;
import org.codehaus.cake.internal.service.DefaultServiceManager;
import org.codehaus.cake.internal.service.DefaultServiceRegistrant;
import org.codehaus.cake.internal.service.exceptionhandling.InternalExceptionService;
import org.codehaus.cake.internal.service.management.DefaultManagementService;
import org.codehaus.cake.internal.service.spi.ContainerInfo;
import org.codehaus.cake.internal.util.ArrayUtils;
import org.codehaus.cake.util.TimeFormatter;

public class StartPhase extends APhase {
    private StopPhase shutdownPhase;
    private InternalExceptionService ies;
    private ContainerConfiguration conf;
    private StartedPhase startedPhase;
    private DisposePhase disposePhase;
    Composer composer;
    DefaultServiceManager dsm;
    ContainerInfo info;

    public StartPhase(InternalExceptionService ies, ContainerConfiguration conf, StopPhase phase,
            Composer composer) {
        // super(ies, Startable.class, "start", true);
        this.ies = ies;
        this.conf = conf;
        this.shutdownPhase = phase;
        this.composer = composer;
        shutdownPhase = composer.get(StopPhase.class);
        disposePhase = composer.get(DisposePhase.class);
        startedPhase = composer.get(StartedPhase.class);
        dsm = composer.get(DefaultServiceManager.class);
        info = composer.get(ContainerInfo.class);
    }

    public void start(RunState state) {
        long startTime = System.nanoTime();
        if (ies.isDebugEnabled()) {
            ies.debug("Starting " + info.getContainerTypeName() + " [name = "
                    + info.getContainerName() + "]");
            if (ies.isTraceEnabled()) {
                StringBuilder sb = new StringBuilder();
                sb.append("  ------------" + info.getContainerTypeName()
                        + " was started by this call--------------\n");
                StackTraceElement[] trace = Thread.currentThread().getStackTrace();
                ArrayUtils.reverse(trace);
                int length = trace.length;
                for (int i = Math.max(0, trace.length - 10); i < length; i++) {
                    sb.append("    ");
                    sb.append(trace[i]);
                    if (i < length) {
                        sb.append("\n");
                    }
                }
                sb.append("  --------------------------------------------------------");
                ies.trace(sb.toString());
            }
        }
        try {
            doStart(state);
        } catch (RuntimeException e) {
            state.trySetStartupException(e);
            shutdownPhase.runPhaseSilent(state);
            throw e;
        } catch (Exception e) {
            state.trySetStartupException(e);
            shutdownPhase.runPhaseSilent(state);
            throw new IllegalStateException("Could not start", e);
        } catch (Error e) {
            state.trySetStartupException(e);
            throw e;
        } finally {
            composer = null;
        }
        ies.info(info.getContainerTypeName() + " started [name = " + info.getContainerName()
                + ", startup time = "
                + TimeFormatter.DEFAULT_TIME_FORMATTER.formatNanos(System.nanoTime() - startTime)
                + "]");
    }

    private void doStart(RunState state) throws Exception {
        List allServices = composer.prepareStart(conf);

        startedPhase.prepareAll(allServices);
        disposePhase.prepareAll(allServices);
        addArgument(conf);
        addArgument(new DefaultServiceRegistrant(dsm));
        prepareAll(Startable.class, allServices);
        for (Object o : allServices) {
            startService(state, o, dsm);
        }
        if (composer.hasService(DefaultManagementService.class)) {
            composer.get(DefaultManagementService.class).register(composer, allServices);
        }

        state.transitionToRunning();
        /* Started */
        startedPhase.run(state);
    }
    public void runPhase() throws Exception {
        try {
            for (Entry entry : entries) {
                for (Method m : entry.methods) {
                    invoke(entry.o, m);
                }
                shutdownPhase.prepare(entry.o);
            }
        } finally {
            entries.clear();
        }
    }
    public void startService(RunState state, Object service, DefaultServiceManager dsm) {
        boolean debug = ies.isDebugEnabled();
        long start = System.nanoTime();
        if (service instanceof StartableService) {
            StartableService startable = (StartableService) service;
            try {
                startable.start(conf, new DefaultServiceRegistrant(dsm));
            } catch (Exception e) {
                state.trySetStartupException(e);
                ies.fatal("Service failed to start", e);
                if (e instanceof RuntimeException) {
                    throw ((RuntimeException) e);
                } else {
                    throw new IllegalStateException("Service failed to start", e);
                }
            }
            if (debug) {
                StringBuilder sb = new StringBuilder();
                sb.append("    "); // indent
                sb.append(service);
                sb.append(": Started Succesfully [duration = ");
                sb.append(TimeFormatter.DEFAULT_TIME_FORMATTER.formatNanos(System.nanoTime()
                        - start));
                sb.append(", type = ");
                sb.append(service.getClass().getName());
                sb.append("]");
                ies.debug(sb.toString());
            }
        }
        shutdownPhase.prepare(service);
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
}
