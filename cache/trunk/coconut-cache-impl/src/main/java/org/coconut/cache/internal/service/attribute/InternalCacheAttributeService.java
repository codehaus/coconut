/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.attribute;

import org.coconut.core.AttributeMap;

/**
 * This service is responsible for creating attribute maps.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface InternalCacheAttributeService {
	/**
     * Creates a new empty AttributeMap.
     * 
     * @return a new empty AttributeMap
     */
	AttributeMap createMap();

	/**
     * Creates a new AttributeMap populated containing the entries specified in
     * the provided attribute map.
     * 
     * @param copyFrom
     *            the map to copy entries from
     * @return a new AttributeMap populated containing the entries specified in
     * the provided attribute map
     */
	AttributeMap createMap(AttributeMap copyFrom);
}
