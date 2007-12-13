/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import org.coconut.cache.tck.ServiceSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * A test suite consisting of all loading service test classes.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@RunWith(ServiceSuite.class)
@Suite.SuiteClasses( { ExpirationWithCacheLoader.class, ExplicitForcedLoading.class,
        ExplicitLoading.class, ImplicitLoading.class, LoadingIllegalAttributeValues.class,
        LoadingLazyStart.class, LoadingCacheLoader.class, LoadingMXBean.class,
        LoadingRefresh.class, LoadingService.class, LoadingShutdown.class, RefreshFilter.class })
public class LoadingSuite {}
