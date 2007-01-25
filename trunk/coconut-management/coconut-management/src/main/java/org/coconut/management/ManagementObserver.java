/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.management;

import java.util.List;

import org.coconut.core.EventProcessor;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface ManagementObserver<T> {
    EventProcessor<? super T> addEventProcessor(EventProcessor<? super T> e);

    List<EventProcessor<? super T>> getEventProcessors();

    boolean removeEventProcessor(EventProcessor<?> e);
}
