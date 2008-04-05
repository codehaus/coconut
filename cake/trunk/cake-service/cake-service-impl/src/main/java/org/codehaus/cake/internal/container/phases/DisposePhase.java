package org.codehaus.cake.internal.container.phases;

import org.codehaus.cake.container.lifecycle.Disposable;
import org.codehaus.cake.internal.service.exceptionhandling.InternalExceptionService;

public class DisposePhase extends AbstractPhase {
    public DisposePhase(InternalExceptionService ies) {
        super(ies, Disposable.class, "disphose", false);
    }
}
