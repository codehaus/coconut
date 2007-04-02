/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.attribute;

import org.coconut.cache.internal.DefaultAttributeMap;
import org.coconut.core.AttributeMap;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheAttributeService implements InternalCacheAttributeService {

    public AttributeMap createMap() {
        return new DefaultAttributeMap();
    }

    public AttributeMap createMap(AttributeMap fromUser) {
        return new DefaultAttributeMap(fromUser);
    }

}
