/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.worker;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.coconut.attribute.AttributeMap;
import org.coconut.cache.internal.service.servicemanager.CompositeService;
import org.coconut.cache.internal.service.servicemanager.InternalCacheServiceManager;
import org.coconut.cache.service.servicemanager.CacheLifecycle;
import org.coconut.cache.service.worker.CacheWorkerConfiguration;
import org.coconut.cache.service.worker.CacheWorkerManager;
import org.coconut.cache.service.worker.CacheWorkerService;

/**
 *
 *
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class SynchronizedCacheWorkerService extends AbstractCacheWorkerService implements
        CompositeService {

    private final CacheWorkerManager worker;

    public SynchronizedCacheWorkerService(String cacheName, CacheWorkerConfiguration conf) {
        CacheWorkerManager worker = conf.getWorkerManager();
        if (worker == null) {
            worker = new SharedCacheWorkerManager(cacheName);
        }
        this.worker = worker;
    }

    /** {@inheritDoc} */
    public Collection<?> getChildServices() {
        return Arrays.asList(worker);
    }

    /** {@inheritDoc} */
    public ExecutorService getExecutorService(Object service, AttributeMap attributes) {
        return worker.getExecutorService(service, attributes);
    }

    /** {@inheritDoc} */
    public ScheduledExecutorService getScheduledExecutorService(Object service,
            AttributeMap attributes) {
        return worker.getScheduledExecutorService(service, attributes);
    }

    /** {@inheritDoc} */
    @Override
    public void initialize(CacheLifecycle.Initializer cli) {
        cli.registerService(CacheWorkerService.class, this);
    }

}
