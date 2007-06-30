package org.coconut.cache.internal.service.attribute;


/**
 * but but but but whyyy?
 * 
 * @param <K>
 * @param <V>
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public interface DefaultAttributes {

    void setExpirationTimeNanos(long nanos);

    long getExpirationTimeNanos();

    void setTimeToFreshNanos(long nanos);

    long getTimeToRefreshNanos();
}
