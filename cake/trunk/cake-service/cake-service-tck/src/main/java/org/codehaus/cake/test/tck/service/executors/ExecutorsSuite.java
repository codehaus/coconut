package org.codehaus.cake.test.tck.service.executors;

import org.codehaus.cake.test.tck.ServiceSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Tests the Statistics service.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: StatisticsSuite.java 466 2007-11-16 14:08:17Z kasper $
 */
@RunWith(ServiceSuite.class)
@Suite.SuiteClasses( { WorkerExecutorService.class, WorkerScheduling.class, WorkerService.class,
        WorkerServiceShutdown.class })
public class ExecutorsSuite {}
