/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.attribute;

import org.coconut.core.AttributeMap;
import org.coconut.core.AttributeMaps;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class DefaultCacheAttributeService implements InternalCacheAttributeService {

    private Dummy dummy = new Dummy();

    /** {@inheritDoc} */
    public AttributeMap createMap() {
        return new AttributeMaps.DefaultAttributeMap();
    }

    /** {@inheritDoc} */
    public AttributeMap createMap(AttributeMap copyFrom) {
        return new AttributeMaps.DefaultAttributeMap(copyFrom);
    }

    /** {@inheritDoc} */
    public DefaultAttributes update() {
        return dummy;
    }

    class Dummy implements DefaultAttributes {

        private long goo;

        private long refresh;

        private int maximumSize;

        /** {@inheritDoc} */
        public long getExpirationTimeNanos() {
            return goo;
        }

        /** {@inheritDoc} */
        public void setExpirationTimeNanos(long nanos) {
            this.goo = nanos;
        }

        /** {@inheritDoc} */
        public long getTimeToRefreshNanos() {
            return refresh;
        }

        /** {@inheritDoc} */
        public void setTimeToFreshNanos(long nanos) {
            this.refresh = nanos;
        }

        /** {@inheritDoc} */
        public int getMaximumSize() {
            return maximumSize;
        }

        /** {@inheritDoc} */
        public void setMaximumSize(int maximumSize) {
            this.maximumSize = maximumSize;
        }
    }
}
