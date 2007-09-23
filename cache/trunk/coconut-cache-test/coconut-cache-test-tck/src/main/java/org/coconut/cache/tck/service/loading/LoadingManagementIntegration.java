package org.coconut.cache.tck.service.loading;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.filter.Filter;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedObject;
import org.junit.Test;

/**
 * Tests that a Cache Loader and the Refresh filter can be managed.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class LoadingManagementIntegration extends AbstractLoadingTestBundle {

    @Test
    public void testLoader() {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        cc.management().setEnabled(true);
        MyLoader loader = new MyLoader();
        c = newCache(cc.loading().setLoader(loader).c());
        loading().load(1);
        assertNotNull(loader.g);
    }

    @Test
    public void testFilter() {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        cc.management().setEnabled(true);
        MyFilter filter = new MyFilter();
        c = newCache(cc.loading().setRefreshFilter(filter).setLoader(loader).c());
        loading().load(1);
        assertNotNull(filter.g);
    }

    static class MyFilter implements ManagedObject, Filter {
        ManagedGroup g;

        public void manage(ManagedGroup parent) {
            g = parent;
        }

        public boolean accept(Object element) {
            return false;
        }
    }

    static class MyLoader extends IntegerToStringLoader implements ManagedObject {
        ManagedGroup g;

        public void manage(ManagedGroup parent) {
            g = parent;
        }
    }
}
