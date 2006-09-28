/* Copyright 2004 - 2006 Kasper Nielsen. Licensed under a MIT compatible 
 * license, see LICENSE.txt or http://coconut.codehaus.org/license for details. 
 */
package org.coconut.event;

import java.util.List;

import org.coconut.core.EventHandler;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public interface BatchedEventHandler<E> extends EventHandler<E> {
    void handleAll(List<? extends E> list);
}
