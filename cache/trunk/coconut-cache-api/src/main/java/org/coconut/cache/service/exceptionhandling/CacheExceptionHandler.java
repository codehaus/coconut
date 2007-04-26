/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.service.exceptionhandling;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.coconut.cache.Cache;
import org.coconut.cache.service.event.CacheEvent;
import org.coconut.cache.service.loading.CacheLoader;
import org.coconut.cache.spi.Resources;
import org.coconut.core.AttributeMap;
import org.coconut.core.Logger;
import org.coconut.core.Logs;
import org.coconut.event.EventSubscription;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class CacheExceptionHandler<K, V> {
	private String name;

	private volatile Logger logger;

	public final synchronized void setCacheName(String name) {
		this.name = name;
	}

	public final synchronized String getCacheName() {
		return name;
	}

	public final synchronized void setLogger(Logger logger) {
		if (logger == null) {
			throw new NullPointerException("logger is null");
		}
		this.logger = logger;
	}

	public final boolean hasLogger() {
		return logger != null;
	}

	public final Logger getLogger() {
		Logger l = logger;
		if (l != null) {
			return l;
		}
		return initializeLogger();
	}

	public Map<K, V> loadAllFailed(final CacheLoader<? super K, ?> loader,
			Map<? extends K, AttributeMap> keysWithAttributes, boolean isAsynchronous,
			Throwable cause) {
		return null;
	}

	public Map<K, V> loadAllFailed(Cache<K, V> cache, CacheLoader<? super K, ?> loader,
			Map<K, AttributeMap> keysWithAttributes, boolean isAsync, Throwable cause) {
		HashMap<K, V> map = new HashMap<K, V>();
		for (Map.Entry<K, AttributeMap> e : keysWithAttributes.entrySet()) {
			K key = e.getKey();
			AttributeMap aMap = e.getValue();
			map.put(key, loadFailed(cache, loader, key, aMap, isAsync, cause));
		}
		return map;
	}

	public V loadFailed(Cache<K, V> cache, CacheLoader<? super K, ?> loader, K key,
			AttributeMap map, boolean isAsync, Throwable cause) {
		return null;
	}

	public void eventDeliveryFailed(Cache<K, V> cache, CacheEvent<K, V> event,
			EventSubscription<CacheEvent<K, V>> destination, Throwable cause) {}

	public final void unhandledRuntimeException(RuntimeException t) {}

	public void configurationChanged(Cache<?, ?> cache, String title) {}

	public void warning(String warning) {}

	private synchronized Logger initializeLogger() {
		if (logger == null) {
			logger = createLogger();
		}
		return logger;
	}

	private Logger createLogger() {
		String loggerName = Cache.class.getPackage().getName() + "." + name;
		java.util.logging.Logger l = java.util.logging.Logger.getLogger(loggerName);
		String infoMsg = Resources.lookup(CacheExceptionHandler.class, "noLogger");
		Logger logger = Logs.JDK.from(l);
		l.setLevel(Level.ALL);
		logger.info(MessageFormat.format(infoMsg, name, loggerName));
		l.setLevel(Level.SEVERE);
		return logger;
	}
}
