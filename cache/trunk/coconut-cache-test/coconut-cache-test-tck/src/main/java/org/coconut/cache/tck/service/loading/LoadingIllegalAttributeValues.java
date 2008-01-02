/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.tck.service.loading;

import org.coconut.attribute.AttributeMap;
import org.coconut.attribute.DefaultAttributeMap;
import org.coconut.attribute.common.CostAttribute;
import org.coconut.attribute.common.DateCreatedAttribute;
import org.coconut.attribute.common.DateModifiedAttribute;
import org.coconut.attribute.common.HitsAttribute;
import org.coconut.attribute.common.SizeAttribute;
import org.coconut.attribute.common.TimeToLiveAttribute;
import org.coconut.attribute.common.TimeToRefreshAttribute;
import org.coconut.cache.service.exceptionhandling.CacheExceptionContext;
import org.coconut.cache.service.exceptionhandling.CacheExceptionHandler;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests illegal values of various cache attributes, cache shouldn't fail.
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@RunWith(JMock.class)
public class LoadingIllegalAttributeValues extends AbstractCacheTCKTest {
    Mockery context = new JUnit4Mockery();
    private CacheExceptionContext ceh;

    AttributeMap map;

    @After
    public void after() {
        loading().load(1, map);
        awaitAllLoads();
        assertNotNull(ceh);
        assertEquals(1, c.getEntry(1).getHits());
        assertEquals(1.0, c.getEntry(1).getCost());
        assertEquals(1, c.getEntry(1).getSize());
        // TODO add the rest of the attributes
    }

    @Test
    public void cost() {
        map.put(CostAttribute.INSTANCE, Double.NaN);
    }

    @Test
    public void creationTime() {
        map.put(DateCreatedAttribute.INSTANCE, -1L);
    }

    @Test
    public void hits() {
        map.put(HitsAttribute.INSTANCE, -1L);
    }

    @Test
    public void modifiedDate() {
        map.put(DateModifiedAttribute.INSTANCE, -1L);
    }

    @Before
    public void setup() {
        map = new DefaultAttributeMap();
        conf.loading().setLoader(new IntegerToStringLoader());
        conf.exceptionHandling().setExceptionHandler(new CacheExceptionHandler() {
            @Override
            public void apply(CacheExceptionContext context) {
                ceh = context;
            }
        });
        init();
    }

    @Test
    public void size() {
        map.put(SizeAttribute.INSTANCE, -1L);
    }

    @Test
    public void timeToLive() {
        map.put(TimeToLiveAttribute.INSTANCE, -1L);
    }

    @Test
    public void timeToRefresh() {
        map.put(TimeToRefreshAttribute.INSTANCE, -1L);
    }

}
