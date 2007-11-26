/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.policy.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

import java.util.Collections;

import org.coconut.cache.policy.paging.LRUPolicy;
import org.coconut.cache.spi.ReplacementPolicy;
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
 * @version $Id$
 */
@SuppressWarnings("unchecked")
@RunWith(JMock.class)
public class ReplacementPolicyDecoratorTest {

    Mockery context = new JUnit4Mockery();

    @Test(expected = NullPointerException.class)
    public void PolicyDecoratorNPE() {
        new ReplacementPolicyDecorator(null);
    }

    @Test
    public void toStringTest() {
        // JMock does not test toStrings
        ReplacementPolicyDecorator pd = new ReplacementPolicyDecorator(new LRUPolicy());
        assertEquals(new LRUPolicy().toString(), pd.toString());
    }

    @Test
    public void test() {
        final AttributeMap am = AttributeMaps.EMPTY_MAP;
        final ReplacementPolicy policy = context.mock(ReplacementPolicy.class);
        context.checking(new Expectations() {
            {
                one(policy).add("add", am);
                will(returnValue(1));
                one(policy).clear();
                one(policy).evictNext();
                will(returnValue("evictNext"));
                one(policy).getSize();
                will(returnValue(2));
                one(policy).peek();
                will(returnValue("peek"));
                one(policy).peekAll();
                will(returnValue(Collections.EMPTY_LIST));
                one(policy).remove(3);
                will(returnValue("remove"));
                one(policy).touch(4);
                one(policy).update(5, "updateNew", am);
                will(returnValue(true));
            }
        });
        ReplacementPolicyDecorator pd = new ReplacementPolicyDecorator(policy) {};
        assertSame(policy, pd.getPolicy());
        assertEquals(1, pd.add("add", am));
        pd.clear();
        assertEquals("evictNext", pd.evictNext());
        assertEquals(2, pd.getSize());
        assertEquals("peek", pd.peek());
        assertEquals(Collections.EMPTY_LIST, pd.peekAll());
        assertEquals("remove", pd.remove(3));
        pd.touch(4);
        assertTrue(pd.update(5, "updateNew", am));
    }

}
