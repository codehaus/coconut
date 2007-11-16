/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.statistics;

import org.coconut.cache.tck.ServiceSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Tests the Statistics service.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@RunWith(ServiceSuite.class)
@Suite.SuiteClasses( { HitStat.class, StatisticsMXBean.class,
        StatisticsServiceGeneral.class, StatisticsServiceLoading.class })
public class StatisticsSuite {}
