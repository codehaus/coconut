package org.coconut.cache.tck.service.loading;

import static org.coconut.test.CollectionUtils.M1;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.tck.AbstractCacheTCKTest;
import org.coconut.cache.test.util.AbstractLifecycleVerifier;
import org.coconut.cache.test.util.IntegerToStringLoader;
import org.coconut.core.AttributeMap;
import org.coconut.filter.Filter;
import org.junit.Before;
import org.junit.Test;

public class LoadingLifecycleIntegration extends AbstractCacheTCKTest {

    @Before
    public void setupLoading() {}

    // testrefreshfilter

    @Test
    public void load() {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        MyLoader loader = new MyLoader();
        c = newCache(cc.loading().setLoader(loader).c());

        loader.assertNotStarted();
        loading().load(M1.getKey());// lazy start
        loader.assertInStartedPhase();
        c.shutdown();
        loader.assertShutdownOrTerminatedPhase();
        c.shutdown();
    }

    @Test
    public void filter() {
        CacheConfiguration<Integer, String> cc = CacheConfiguration.create();
        MyFilter filter = new MyFilter();
        c = newCache(cc.loading().setRefreshFilter(filter).setLoader(
                new IntegerToStringLoader()).c());

        filter.assertNotStarted();
        loading().load(M1.getKey());// lazy start
        filter.assertInStartedPhase();
        c.shutdown();
        filter.assertShutdownOrTerminatedPhase();
        c.shutdown();
    }

    static class MyLoader extends AbstractLifecycleVerifier implements CacheLoader {
        public Object load(Object key, AttributeMap attributes) throws Exception {
            return "1";
        }
    }

    static class MyFilter extends AbstractLifecycleVerifier implements Filter {
        public boolean accept(Object element) {
            return false;
        }
    }
}
