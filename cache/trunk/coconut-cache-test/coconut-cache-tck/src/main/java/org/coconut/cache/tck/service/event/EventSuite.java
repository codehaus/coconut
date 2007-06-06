package org.coconut.cache.tck.service.event;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({EventServiceGeneral.class, EventEntryAdded.class,
    EventEntryGet.class, EventEntryRemoved.class, EventEntryUpdated.class,
    EventCacheCleared.class})
public class EventSuite {

//    public static void addEventTCKTests(CompositeRunner runner)
//            throws InitializationError {
//        runner.add(createRunner());
//        Class[] c=new Class[]{EventServiceGeneral.class };
//    }
//
//    private static CompositeRunner createRunner() throws InitializationError {
//        CompositeRunner r = new CompositeRunner("Cache Event Service");
//        // General
//        r.add(new TestClassRunner(EventServiceGeneral.class));
//
//        // Cache Entry events
//        r.add(new TestClassRunner(EventEntryAdded.class));
//        r.add(new TestClassRunner(EventEntryGet.class));
//        r.add(new TestClassRunner(EventEntryRemoved.class));
//        r.add(new TestClassRunner(EventEntryUpdated.class));
//
//        // Cache Instance Events
//        r.add(new TestClassRunner(EventCacheCleared.class));
//
//        return r;
//
//    }
}
