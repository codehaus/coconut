/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.coconut.internal.picocontainer.defaults;

import org.coconut.internal.picocontainer.ComponentAdapter;
import org.coconut.internal.picocontainer.Parameter;
import org.coconut.internal.picocontainer.PicoIntrospectionException;

public class DecoratingComponentAdapterFactory extends MonitoringComponentAdapterFactory {
    private ComponentAdapterFactory delegate;

    public DecoratingComponentAdapterFactory(ComponentAdapterFactory delegate) {
        this.delegate = delegate;
    }

    public ComponentAdapter createComponentAdapter(Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter[] parameters) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return delegate.createComponentAdapter(componentKey, componentImplementation, parameters);
    }
}
