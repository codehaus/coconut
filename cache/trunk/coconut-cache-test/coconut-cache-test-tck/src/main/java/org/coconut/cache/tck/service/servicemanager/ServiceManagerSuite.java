/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.servicemanager;

import org.coconut.cache.tck.ServiceSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(ServiceSuite.class)
@Suite.SuiteClasses( { Lifecycle.class, LifecycleErroneousInitialize.class,
        LifecycleErroneousShutdown.class, LifecycleErroneousStart.class,
        LifecycleErroneousTermination.class, LifecycleManaged.class, ServiceManagerObjects.class,
        ServiceManagerService.class, StartupFailed.class })
public class ServiceManagerSuite {

}
