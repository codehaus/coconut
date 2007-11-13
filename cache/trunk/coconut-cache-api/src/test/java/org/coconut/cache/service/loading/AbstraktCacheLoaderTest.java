/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.loading;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@SuppressWarnings("unchecked")
@RunWith(JMock.class)
public class AbstraktCacheLoaderTest {

    private final static Error ERR = new Error();

    Mockery context = new JUnit4Mockery();

    @Test
    public void loadAll() {
        final CacheLoaderCallback callback = context.mock(CacheLoaderCallback.class);
        context.checking(new Expectations() {
            {
                one(callback).getKey();
                will(returnValue(1));
                one(callback).getAttributes();
                will(returnValue(AttributeMaps.EMPTY_MAP));
                one(callback).completed("foo");
            }
        });
        new MyLoader().loadAll((Collection) Arrays.asList(callback));

    }
    @Test
    public void loadAllErr() {
        final CacheLoaderCallback callback = context.mock(CacheLoaderCallback.class);
        context.checking(new Expectations() {
            {
                one(callback).getKey();
                will(returnValue(0));
                one(callback).getAttributes();
                will(returnValue(AttributeMaps.EMPTY_MAP));
                one(callback).failed(ERR);
            }
        });
        new MyLoader().loadAll((Collection) Arrays.asList(callback));
    }
    /**
     * Tests that the signature of CacheLoader Is okay.
     */
    public void genericSignature() {
        AbstractCacheLoader<Number, List> cl = null;
        List<CacheLoaderCallback<Number, List>> l1 = null;
        List<AbstractCacheLoaderCallback<Number, List>> l2 = null;
        List<AbstractCacheLoaderCallback<Number, Collection>> l3 = null;
        List<AbstractCacheLoaderCallback<Integer, List>> l4 = null;
        List<AbstractCacheLoaderCallback<Integer, Collection>> l5 = null;
        cl.loadAll(l1);
        cl.loadAll(l2);
        cl.loadAll(l3);
        cl.loadAll(l4);
        cl.loadAll(l5);
    }

    abstract static class AbstractCacheLoaderCallback<K, V> implements CacheLoaderCallback<K, V> {}

    static class MyLoader extends AbstractCacheLoader<Integer, String> {

        /** {@inheritDoc} */
        public String load(Integer key, AttributeMap attributes) throws Exception {
            if (key.equals(0)) {
                throw ERR;
            } else {
                assertEquals(1, key.intValue());
                assertSame(AttributeMaps.EMPTY_MAP, attributes);
                return "foo";
            }
        }
    }
}
