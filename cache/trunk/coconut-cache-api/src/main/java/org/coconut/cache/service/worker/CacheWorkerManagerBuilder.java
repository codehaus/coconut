package org.coconut.cache.service.worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CacheWorkerManagerBuilder {

	/**
	 * Creates a standard thread based CacheWorkerManager.
	 *  
	 * <p>
	 * All request to retrieve an {@link ExecutorService} will return a shared
	 * {@link ExecutorService}. This {@link ExecutorService} cannot be
	 * shutdown.
	 * 
	 * Creates a thread pool that creates new threads as needed, but will reuse
	 * previously constructed threads when they are available.If no existing
	 * thread is available, a new thread will be created and added to the
	 * pool.Threads that have not been used for sixty seconds are terminated and
	 * removed from the cache. Thus, a pool that remains idle for long enough
	 * will not consume any resources.
	 * <p>
	 * All requests to execute a dedicated {@link Runnable} will result in a new
	 * thread being spawned.
	 * 
	 * 
	 * @return
	 */
	public static CacheWorkerManager create() {
		return null;
	}

	public static CacheWorkerManager create(ExecutorService service) {
		return null;
	}

	public static CacheWorkerManager create(ExecutorService service,
			ScheduledExecutorService executorService) {
		return null;
	}

	   /**
     * The default thread factory
     */
    static class DefaultThreadFactory implements ThreadFactory {
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        DefaultThreadFactory(Class<?> service) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
                    .getThreadGroup();
            namePrefix = service.getSimpleName() + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix
                    + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    static class SingleThreadedFactory implements ThreadFactory {
        final ThreadGroup group;

        final String name;

        SingleThreadedFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread()
                    .getThreadGroup();
            this.name = name;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, name, 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
