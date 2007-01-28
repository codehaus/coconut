/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import static org.coconut.test.CollectionUtils.M1;
import static org.coconut.test.CollectionUtils.M1_KEY_NULL;
import static org.coconut.test.CollectionUtils.M1_NULL_VALUE;
import static org.coconut.test.CollectionUtils.M2;
import static org.coconut.test.CollectionUtils.asMap;

import java.util.Arrays;

import org.junit.Test;
/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheUtilTest {

    
  
    @Test
    public void checkCollectionForNulls() {
        CacheUtil.checkCollectionForNulls(Arrays.asList(3, 4, 5));
    }

    @Test(expected = NullPointerException.class)
    public void checkCollectionForNulls2() {
        CacheUtil.checkCollectionForNulls(Arrays.asList(3, 4, 5, null));
    }

    @Test
    public void checkMapForNulls() {
        CacheUtil.checkMapForNulls(asMap(M1,M2));
    }
    @Test(expected = NullPointerException.class)

    public void checkMapForNulls1() {
        CacheUtil.checkMapForNulls(asMap(M2,M1_NULL_VALUE));
    }
    @Test(expected = NullPointerException.class)
    public void checkMapForNulls2() {
        CacheUtil.checkMapForNulls(asMap(M2,M1_KEY_NULL));
    }
}
