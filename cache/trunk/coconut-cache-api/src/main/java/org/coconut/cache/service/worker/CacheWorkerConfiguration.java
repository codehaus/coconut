package org.coconut.cache.service.worker;

import java.util.concurrent.ThreadPoolExecutor;

public class CacheWorkerConfiguration {
	private CacheWorkerManager workerFactory;

	private Thread.UncaughtExceptionHandler handler;

	public CacheWorkerManager getWorkerManager() {
		return workerFactory;
	}

	public void setWorkerManager(CacheWorkerManager workerFactory) {
		this.workerFactory = workerFactory;
	}

	/**
	 * Sets a default Thread.UncaughtExceptionHandler. If no exception handler
	 * is set. The cache will call CacheExceptionHandler#handleUn(CacheContext,
	 * Thread t, Throwable e); Which logs the exception and lets the thread exit
	 * <p>
	 * Any uncaught exception handler set by this method will only applies to
	 * executor services that are initialized by the cache itself. Any executor
	 * services that the user creates them self must manually.
	 * 
	 * @param handler
	 */
	public void setUncaughtExceptionHandler(
			Thread.UncaughtExceptionHandler handler) {
		ThreadPoolExecutor tpe=null;
		tpe.set
	}
}
