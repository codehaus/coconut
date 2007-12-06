/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import static org.junit.Assert.assertEquals;

import java.util.ResourceBundle;

import org.junit.Test;

public class ResourceBundleUtilTest {
    static final ResourceBundle bundle = ResourceBundle
            .getBundle("org.coconut.internal.util.xmlutil");

    @Test
    public void test() {
        assertEquals(ResourceBundleUtil.lookupKey(bundle, "ResourceBundleUtilTest.test"), "foo");
    }
}
