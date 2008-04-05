package org.codehaus.cake.internal.container.phases;

import org.codehaus.cake.container.lifecycle.Stoppable;
import org.codehaus.cake.internal.container.RunState;
import org.codehaus.cake.internal.service.exceptionhandling.InternalExceptionService;

public class StopPhase extends AbstractPhase {
    public StopPhase(InternalExceptionService ies) {
        super(ies, Stoppable.class, "stop", false);
    }
    
    public void runPhaseSilent(RunState state) {
        try {
            super.runPhase(state);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
