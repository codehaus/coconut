package org.codehaus.cake.internal.container.phases;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.codehaus.cake.container.ContainerConfiguration;
import org.codehaus.cake.internal.container.Composer;
import org.codehaus.cake.internal.picocontainer.MutablePicoContainer;
import org.codehaus.cake.internal.picocontainer.defaults.DefaultPicoContainer;
import org.codehaus.cake.internal.service.DefaultServiceManager;
import org.codehaus.cake.internal.service.exceptionhandling.InternalExceptionService;
import org.codehaus.cake.internal.service.spi.ContainerInfo;

public class Start2Phase {

    private StopPhase shutdownPhase;
    private InternalExceptionService ies;
    private ContainerConfiguration conf;
    private StartedPhase startedPhase;
    private DisposePhase disposePhase;
    Composer composer;
    DefaultServiceManager dsm;
    ContainerInfo info;

    public Start2Phase(InternalExceptionService ies, ContainerConfiguration conf, StopPhase phase,
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
    MutablePicoContainer mpc = new DefaultPicoContainer();
    
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
