/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static org.coconut.test.CollectionTestUtil.M1;
import static org.coconut.test.CollectionTestUtil.M1_KEY_NULL;
import static org.coconut.test.CollectionTestUtil.M1_NULL_VALUE;
import static org.coconut.test.CollectionTestUtil.M2;
import static org.coconut.test.CollectionTestUtil.asMap;

import java.util.Arrays;

import org.coconut.internal.util.CollectionUtils;
import org.junit.Test;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class CacheUtilTest {

    @Test
    public void checkCollectionForNulls() {
        CollectionUtils.checkCollectionForNulls(Arrays.asList(3, 4, 5));
    }

    @Test(expected = NullPointerException.class)
    public void checkCollectionForNulls2() {
        CollectionUtils.checkCollectionForNulls(Arrays.asList(3, 4, 5, null));
    }

    @Test
    public void checkMapForNulls() {
        CollectionUtils.checkMapForNulls(asMap(M1, M2));
    }

    @Test(expected = NullPointerException.class)
    public void checkMapForNulls1() {
        CollectionUtils.checkMapForNulls(asMap(M2, M1_NULL_VALUE));
    }

    @Test(expected = NullPointerException.class)
    public void checkMapForNulls2() {
        CollectionUtils.checkMapForNulls(asMap(M2, M1_KEY_NULL));
    }
}
