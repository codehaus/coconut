/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
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
