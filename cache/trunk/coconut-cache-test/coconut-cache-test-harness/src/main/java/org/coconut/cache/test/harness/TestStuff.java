package org.coconut.cache.test.harness;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.test.CacheTestRunner;
import org.coconut.cache.test.operations.CacheOperations;
import org.coconut.cache.test.operations.LoadingServiceOperations;
import org.coconut.cache.test.util.SimpelLoader;

public class TestStuff {

    public static void main(String[] args) throws Exception {
        CacheConfiguration conf = CacheConfiguration.create();
        conf.loading().setLoader(new SimpelLoader());
        conf.eviction().setMaximumSize(5000);
        conf.setProperty(LoadingServiceOperations.Load.NAME, new Float(1).toString());
        conf.setProperty(CacheOperations.Get.NAME, new Float(50).toString());
        conf.setProperty(CacheOperations.Clear.NAME, new Float(0.002).toString());
        conf.setProperty(LoadingServiceOperations.ForceLoad.NAME, new Float(0.3).toString());

        CacheTestRunner htr = new CacheTestRunner(conf);
        htr.start();
    }
}
