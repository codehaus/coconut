/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.event;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { EventBusShutdownLazyStart.class, EventCacheCleared.class, EventGet.class,
        EventPut.class, EventRemove.class, EventReplace.class, EventServiceBus.class,
        EventServiceEviction.class, EventServiceExpiration.class, EventServiceGeneral.class,
        EventServiceLoading.class })
public class EventSuite {}
