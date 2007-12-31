/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.eviction;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.coconut.cache.spi.XmlConfiguratorTest.reloadService;

import org.coconut.cache.policy.ReplacementPolicy;
import org.coconut.cache.policy.paging.ClockPolicy;
import org.coconut.cache.policy.paging.FIFOPolicy;
import org.coconut.cache.policy.paging.LFUPolicy;
import org.coconut.cache.policy.paging.LIFOPolicy;
import org.coconut.cache.policy.paging.LRUPolicy;
import org.coconut.cache.policy.paging.MRUPolicy;
import org.coconut.cache.policy.paging.RandomPolicy;
import org.coconut.operations.Predicates;
import org.coconut.operations.Ops.Predicate;
import org.coconut.test.TestUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link CacheEvictionConfiguration}.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheEvictionConfigurationTest {

    static CacheEvictionConfiguration<Integer, String> DEFAULT = new CacheEvictionConfiguration<Integer, String>();

    private CacheEvictionConfiguration<Integer, String> conf;

    @Before
    public void setUp() {
        conf = new CacheEvictionConfiguration<Integer, String>();
    }

    @Test
    public void testMaximumCapacity() {
        assertEquals(0, conf.getMaximumVolume());
        assertSame(conf, conf.setMaximumVolume(4));
        assertEquals(4, conf.getMaximumVolume());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaximumCapacityIAE() {
        conf.setMaximumVolume(-1);
    }

    @Test
    public void testMaximumCapacityXML() throws Exception {
        conf = reloadService(conf);
        assertEquals(0, conf.getMaximumVolume());
        assertSame(conf, conf.setMaximumVolume(Long.MAX_VALUE));

        conf = reloadService(conf);
        assertEquals(Long.MAX_VALUE, conf.getMaximumVolume());
    }

    @Test
    public void isDisabled() {
        assertFalse(conf.isDisabled());
        assertSame(conf, conf.setDisabled(true));
        assertTrue(conf.isDisabled());
    }

    @Test
    public void isDisabledXML() throws Exception {
        conf = reloadService(conf);
        assertFalse(conf.isDisabled());
        assertSame(conf, conf.setDisabled(true));

        conf = reloadService(conf);
        assertTrue(conf.isDisabled());
    }

    @Test
    public void testMaximumSize() {
        assertEquals(0, conf.getMaximumSize());
        assertSame(conf, conf.setMaximumSize(4));
        assertEquals(4, conf.getMaximumSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMaximumSizeIAE() {
        conf.setMaximumSize(-1);
    }

    @Test
    public void testMaximumSizeXML() throws Exception {
        conf = reloadService(conf);
        assertEquals(0, conf.getMaximumSize());
        assertSame(conf, conf.setMaximumSize(Integer.MAX_VALUE));

        conf = reloadService(conf);
        assertEquals(Integer.MAX_VALUE, conf.getMaximumSize());
    }

    @Test
    public void isCacheable() {
        Predicate i = TestUtil.dummy(Predicate.class);
        assertNull(conf.getIsCacheableFilter());
        assertSame(conf, conf.setIsCacheableFilter(i));
        assertEquals(i, conf.getIsCacheableFilter());
    }

    @Test
    public void isCacheableXML() throws Exception {
        conf = reloadService(conf);
        assertNull(conf.getIsCacheableFilter());
        assertSame(conf, conf.setIsCacheableFilter(new MyPredicate()));

        conf = reloadService(conf);
        assertTrue(conf.getIsCacheableFilter() instanceof MyPredicate);

        assertSame(conf, conf.setIsCacheableFilter(Predicates.TRUE));
        conf = reloadService(conf);
        assertNull(conf.getIsCacheableFilter());
    }

    @Test
    public void testPolicy() {
        ReplacementPolicy<?> p = TestUtil.dummy(ReplacementPolicy.class);
        assertNull(conf.getPolicy());
        assertEquals(conf, conf.setPolicy(p));
        assertEquals(p, conf.getPolicy());
    }

    @Test
    public void testPolicyXML() throws Exception {
        conf = reloadService(conf);
        assertNull(conf.getPolicy());

        conf.setPolicy(new ClockPolicy());
        conf = reloadService(conf);
        assertTrue(conf.getPolicy() instanceof ClockPolicy);

        conf.setPolicy(new FIFOPolicy());
        Object prev = conf;
        conf = reloadService(conf);
        assertTrue(conf.getPolicy() instanceof FIFOPolicy);
        conf.setPolicy(new LIFOPolicy());
        conf = reloadService(conf);
        assertTrue(conf.getPolicy() instanceof LIFOPolicy);

        conf.setPolicy(new LFUPolicy());
        conf = reloadService(conf);
        assertTrue(conf.getPolicy() instanceof LFUPolicy);

        conf.setPolicy(new LRUPolicy());
        conf = reloadService(conf);
        assertTrue(conf.getPolicy() instanceof LRUPolicy);

        conf.setPolicy(new MRUPolicy());
        conf = reloadService(conf);
        assertTrue(conf.getPolicy() instanceof MRUPolicy);

        conf.setPolicy(new RandomPolicy());
        conf = reloadService(conf);
        assertTrue(conf.getPolicy() instanceof RandomPolicy);

        conf.setPolicy(new RP());
        conf = reloadService(conf);
        assertTrue(conf.getPolicy() instanceof RP);
        conf.setPolicy(new RPNone());
        conf = reloadService(conf);
        assertNull(conf.getPolicy());
    }

    public static class MyPredicate implements Predicate {
        public boolean evaluate(Object t) {
            return false;
        }
    }

    public static class RP extends LIFOPolicy {

    }

    public class RPNone extends LIFOPolicy {

    }
}
