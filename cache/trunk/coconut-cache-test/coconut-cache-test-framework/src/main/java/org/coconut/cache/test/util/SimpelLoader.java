/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.test.util;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.service.loading.AbstractCacheLoader;

public class SimpelLoader extends AbstractCacheLoader {
    public Object load(Object key, AttributeMap attributes) throws Exception {
        return key;
    }

}
