/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.servicemanager;

import org.coconut.cache.tck.ServiceSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(ServiceSuite.class)
@Suite.SuiteClasses( { LifecycleInitialize.class, LifecycleStart.class,
        ServiceManagerManagement.class, ServiceManagerObjects.class, ServiceManagerOnCache.class,
        ServiceManagerService.class, StartupFailed.class })
public class ServiceManagerSuite {

}
