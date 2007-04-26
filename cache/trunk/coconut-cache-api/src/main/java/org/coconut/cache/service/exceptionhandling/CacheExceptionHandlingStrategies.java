/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import org.coconut.cache.Cache;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.core.AttributeMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheExceptionHandlingStrategies {

	// logAll
	// logAndThrow
	// NoLogJustThrow
	// ShutdownNow
	//
	// ShutdownAndWriteDebugInfo
	// SystemExit

	
	// Interceptor
	static class ExceptionHandlerInterceptor extends CacheExceptionHandler {		CacheExceptionHandler delegate;

		/**
         * @see org.coconut.cache.service.exceptionhandling.CacheExceptionHandler#loadFailed(org.coconut.cache.Cache,
         *      org.coconut.cache.service.loading.CacheLoader, java.lang.Object,
         *      org.coconut.core.AttributeMap, boolean, java.lang.Throwable)
         */
		@Override
		public Object loadFailed(Cache cache, CacheLoader loader, Object key,
				AttributeMap map, boolean isAsync, Throwable cause) {
			beforeLoadFailed(cache, loader, key, map, isAsync, cause);
			Object o = delegate.loadFailed(cache, loader, key, map, isAsync, cause);
			afterLoadFailed(cache, loader, key, map, isAsync, cause);
			return o;
		}

		protected void beforeLoadFailed(Cache cache, CacheLoader loader, Object key,
				AttributeMap map, boolean isAsync, Throwable cause) {
		// ignore
		}

		protected void afterLoadFailed(Cache cache, CacheLoader loader, Object key,
				AttributeMap map, boolean isAsync, Throwable cause) {
		// ignore
		}
	}


	public static class DefaultCacheExceptionHandler<K, V> extends
			CacheExceptionHandler<K, V> {

	}
}
