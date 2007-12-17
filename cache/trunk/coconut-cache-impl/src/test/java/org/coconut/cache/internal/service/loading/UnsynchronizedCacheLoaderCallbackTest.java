/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.loading;

import org.coconut.attribute.AttributeMap;
import org.coconut.test.TestUtil;
import org.junit.Test;

public class UnsynchronizedCacheLoaderCallbackTest {

    @Test(expected = NullPointerException.class)
    public void UnsynchronizedCacheLoaderCallbackNPE() {
        new UnsynchronizedCacheLoaderCallback(null, TestUtil.dummy(AttributeMap.class));
    }

    @Test(expected = NullPointerException.class)
    public void UnsynchronizedCacheLoaderCallbackNPE1() {
        new UnsynchronizedCacheLoaderCallback("foo", null);
    }

}
