package org.coconut.cache.tck.service.loading;

import static org.coconut.test.CollectionUtils.M1;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.test.util.AbstractLifecycleVerifier;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.core.AttributeMap;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedObject;
import org.junit.Test;

/**
 * Tests that a Cache Loader and the Refresh filter can be managed.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LoadingCacheLoader extends AbstractLoadingTestBundle {

    @Test
    public void loadingLifecycle() {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        MyLoader2 loader = new MyLoader2();
        c = newCache(cc.loading().setLoader(loader).c());

        loader.assertNotStarted();
        loading().load(M1.getKey());// lazy start
        loader.shutdownAndAssert(c);
    }


    @Test
    public void loadingManagement() {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        cc.management().setEnabled(true);
        MyLoader loader = new MyLoader();
        c = newCache(cc.loading().setLoader(loader).c());
        loading().load(1);
        assertNotNull(loader.g);
    }
    
    static class MyLoader extends IntegerToStringLoader implements ManagedObject {
        ManagedGroup g;

        public void manage(ManagedGroup parent) {
            g = parent;
        }
    }


    static class MyLoader2 extends AbstractLifecycleVerifier implements CacheLoader {
        public Object load(Object key, AttributeMap attributes) throws Exception {
            return "1";
        }
    }
}
