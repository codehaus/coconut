/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.servicemanager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheServices;
import org.junit.Test;

public class CacheServiceManagerUtilTest {
    
    @Test
    public void noTest() {
        
    }
    
    public static void main(String[] args) {
        CacheConfiguration<?, ?> conf = CacheConfiguration.create();

        final Cache c = null;

        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        CacheServices.servicemanager(c).registerService(
                CacheServiceManagerUtil.wrapExecutorService(ses, "Daily Cache Clearing"));
        ses.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                c.clear();
            }
        }, 0, 60 * 60 * 24, TimeUnit.SECONDS);

        CacheServiceManagerUtil.registerSingleThreadSchedulingService(c,
                "Daily Cache Clearing", new Runnable() {
                    public void run() {
                        c.clear();
                    }
                }, 0, 60 * 60 * 24, TimeUnit.SECONDS);
    }
}
