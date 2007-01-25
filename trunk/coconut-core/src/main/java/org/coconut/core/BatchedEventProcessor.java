/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.core;

import java.util.List;

import org.coconut.core.EventProcessor;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: BatchedEventHandler.java 176 2007-01-11 15:49:18Z kasper $
 */
public interface BatchedEventProcessor<E> extends EventProcessor<E> {
    void handleAll(List<? extends E> list);
}
