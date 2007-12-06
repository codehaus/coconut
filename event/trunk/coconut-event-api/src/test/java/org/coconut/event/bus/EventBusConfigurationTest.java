/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.event.bus;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import org.coconut.predicate.matcher.PredicateMatcher;
import org.coconut.test.TestUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link EventBusConfiguration} class.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: CacheConfigurationTest.java 475 2007-11-20 17:22:26Z kasper $
 */
@SuppressWarnings("unchecked")
public class EventBusConfigurationTest {

    /** The default instanceof a EventBusConfiguration. */
    private EventBusConfiguration<List> conf;

    /**
     * Setup the EventBusConfiguration.
     */
    @Before
    public void setUp() {
        conf = EventBusConfiguration.create();
    }

    /**
     * Tests {@link EventBusConfiguration#setCheckReentrant(boolean)} and
     * {@link EventBusConfiguration#getCheckReentrant()}.
     */
    @Test
    public void checkReentrent() {
        assertFalse(conf.getCheckReentrant());
        assertSame(conf, conf.setCheckReentrant(true));
        assertTrue(conf.getCheckReentrant());
    }

    /**
     * Tests {@link EventBusConfiguration#setFilterMatcher(PredicateMatcher)} and
     * {@link EventBusConfiguration#getFilterMatcher()}.
     */
    @Test
    public void filterMatcher() {
        assertNull(conf.getFilterMatcher());
        PredicateMatcher pm = TestUtil.dummy(PredicateMatcher.class);
        assertSame(conf, conf.setFilterMatcher(pm));
        assertSame(pm, conf.getFilterMatcher());
    }
}
