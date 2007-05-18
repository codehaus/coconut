/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import java.util.Map;

import org.coconut.cache.Cache;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.core.AttributeMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheExceptionHandlingStrategies {

	//log, rethrow/or not, shutdown cache, System.exit
	// logAll
	// logAndThrow
	// NoLogJustThrow
	// ShutdownNow
	//
	// ShutdownAndWriteDebugInfo
	// SystemExit

	

	public static class DefaultCacheExceptionHandler<K, V> extends
			AbstractCacheExceptionHandler<K, V> {

	

		/**
		 * @see org.coconut.cache.service.exceptionhandling.CacheExceptionHandler#warning(java.lang.String)
		 */
		@Override
		public void warning(String warning) {
			// TODO Auto-generated method stub
			
		}

	}
}
