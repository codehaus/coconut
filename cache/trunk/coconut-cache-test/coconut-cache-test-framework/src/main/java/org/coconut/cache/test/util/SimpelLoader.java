package org.coconut.cache.test.util;

import org.coconut.cache.service.loading.AbstractCacheLoader;
import org.coconut.core.AttributeMap;

public class SimpelLoader extends AbstractCacheLoader {
    public Object load(Object key, AttributeMap attributes) throws Exception {
        return key;
    }

}
