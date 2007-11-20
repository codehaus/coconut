/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Various String utils. 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CollectionUtilsTest {

    @Test
    public void checkCollectionForNulls() {
        CollectionUtils.checkCollectionForNulls(Arrays.asList("foo", "ayy", "Foo"));
    }

    @Test(expected = NullPointerException.class)
    public void checkCollectionForNullsNPE() {
        CollectionUtils.checkCollectionForNulls(Arrays.asList("foo", null, "Foo"));
    }

    @Test
    public void checkMapForNulls() {
        Map m = new HashMap<String, String>();
        m.put("1", "3");
        m.put("foo", "boo");
        CollectionUtils.checkMapForNulls(m);
    }

    @Test(expected = NullPointerException.class)
    public void checkMapForNullsKeyNPE() {
        Map m = new HashMap<String, String>();
        m.put("1", "3");
        m.put(null, "boo");
        CollectionUtils.checkMapForNulls(m);
    }

    @Test(expected = NullPointerException.class)
    public void checkMapForNullsValueNPE() {
        Map m = new HashMap<String, String>();
        m.put("1", "3");
        m.put("ggg", null);
        CollectionUtils.checkMapForNulls(m);
    }
}
