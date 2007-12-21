/* Written by Kasper Nielsen and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package org.coconut.cache.examples.management;

// START SNIPPET: class
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.Cache;
import org.coconut.cache.Caches;
import org.coconut.cache.service.servicemanager.AbstractCacheLifecycle;
import org.coconut.management.ManagedGroup;
import org.coconut.management.ManagedLifecycle;
import org.coconut.management.annotation.ManagedAttribute;

public class ScheduledClearManagement extends AbstractCacheLifecycle implements ManagedLifecycle {
    private long scheduleMs;

    private Runnable runnable;

    private ScheduledExecutorService ses;

    private ScheduledFuture sf;

    public synchronized long getClearScheduleMs() {
        return scheduleMs;
    }

    public synchronized void manage(ManagedGroup parent) {
        parent.addChild("ClearCache", "Controls Clearing of the cache").add(this);
    }

    @ManagedAttribute(defaultValue = "clearMS", description = "The delay between clearings of the cache in Milliseconds")
    public synchronized void setClearScheduleMs(long scheduleMs) {
        if (sf != null) { // cancel existing scheduled clearing
            sf.cancel(false);
        }
        this.scheduleMs = scheduleMs;
        sf = ses.schedule(runnable, scheduleMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public synchronized void started(Cache<?, ?> cache) {
        ses = cache.services().worker().getScheduledExecutorService(this);
        runnable = Caches.runClear(cache);
        setClearScheduleMs(60 * 60 * 1000);// default 1 hour
    }
}
// END SNIPPET: class
