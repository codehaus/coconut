/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.parallel;

import org.coconut.cache.tck.ServiceSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Tests the Parallel service.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: StatisticsSuite.java 466 2007-11-16 14:08:17Z kasper $
 */
@RunWith(ServiceSuite.class)
@Suite.SuiteClasses( { ParallelApply.class, WithFilter.class,
        WithKeys.class, WithMapping.class, WithValues.class })
public class ParallelSuite {}