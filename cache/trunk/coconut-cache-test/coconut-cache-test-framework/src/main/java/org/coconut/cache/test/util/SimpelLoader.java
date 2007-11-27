package org.coconut.cache.test.util;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.service.loading.AbstractCacheLoader;

public class SimpelLoader extends AbstractCacheLoader {
    public Object load(Object key, AttributeMap attributes) throws Exception {
        return key;
    }

}
