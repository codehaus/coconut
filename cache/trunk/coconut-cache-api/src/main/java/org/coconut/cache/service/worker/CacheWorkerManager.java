package org.coconut.cache.service.worker;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public abstract class CacheWorkerManager {

	public static final String DEDICATED_THREAD_NAME_KEY = "dedicated_thread_name";

	public static final String CORE_POOL_THREADS_KEY = "core_pool_threads_key";

	public ExecutorService createExecutorService(Class<?> service) {
		return createExecutorService(service, new Properties());
	}

	/**
	 * @param service
	 *            the service for which an ExecutorService should be returned
	 * @param properties
	 *            a list of properties
	 * @return
	 */
	public abstract ExecutorService createExecutorService(Class<?> service,
			Properties properties);

	public ScheduledExecutorService createScheduledExecutorService(
			Class<?> service) {
		return createScheduledExecutorService(service, new Properties());
	}

	public abstract ScheduledExecutorService createScheduledExecutorService(
			Class<?> service, Properties properties);

	public void executeDedicated(Class<?> service, Runnable r) {
		executeDedicated(service, r, new Properties());
	}

	/**
	 * Executes the {@link Runnable} in a dedicated thread.
	 */
	public abstract void executeDedicated(Class<?> service, Runnable r,
			Properties properties);
}
