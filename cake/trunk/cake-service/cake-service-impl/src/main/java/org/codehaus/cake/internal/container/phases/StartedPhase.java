package org.codehaus.cake.internal.container.phases;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.cake.container.Container;
import org.codehaus.cake.container.lifecycle.Started;
import org.codehaus.cake.internal.container.RunState;
import org.codehaus.cake.internal.service.exceptionhandling.InternalExceptionService;
import org.codehaus.cake.service.ServiceManager;

public class StartedPhase extends AbstractPhase {

    ServiceManager services;
    Container container;

    public StartedPhase(InternalExceptionService<?> ies, Container container,
            ServiceManager serviceManager) {
        super(ies, Started.class, "Started", true);
        this.services = serviceManager;
        this.container = container;
    }

    List list = new ArrayList();

    public void run(RunState state) throws Exception {
        addArgument(container);
        addArguments(services.getAllServices().values());
        super.prepareAll(list);
        runPhase(state);
    }

    @Override
    public AbstractPhase prepareAll(Iterable list) {
        for (Object o : list) {
            this.list.add(o);
        }
        return this;
    }

    //
    // void invoke(Object o, Method m, Object... args) throws Exception {
    // try {
    // m.invoke(o, args);
    // } catch (InvocationTargetException e) {
    // if (e.getCause() instanceof Exception) {
    // throw (Exception) e.getCause();
    // } else if (e.getCause() instanceof Error) {
    // throw (Error) e.getCause();
    // }
    // throw e;
    // }
    // }
}
