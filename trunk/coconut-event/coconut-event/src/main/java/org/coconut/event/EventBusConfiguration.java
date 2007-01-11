/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.event;

import org.coconut.core.Log;
import org.coconut.filter.matcher.FilterMatcher;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class EventBusConfiguration<E> {

    public static final EventBusConfiguration DEFAULT_CONFIGURATION = null;

    private FilterMatcher<?, E> fm;

    private Log log;
}
