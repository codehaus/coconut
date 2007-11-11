/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.internal.service.spi;

import org.coconut.internal.util.ResourceHolder;

/**
 * This is class is used for looking up ressources. The default language is english no
 * matter what the default locale is, unless org.coconut.cache.lang is set.
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class Resources {

    private static final String BUNDLE_NAME = "org.coconut.cache.messagesimpl";//$NON-NLS-1$

    private static final ResourceHolder RESOURCE_HOLDER = new ResourceHolder(BUNDLE_NAME);

    /** Cannot instantiate. */
    // /CLOVER:OFF
    private Resources() {}
    // /CLOVER:ON

    /**
     * Looksup a message in the default bundle.
     * 
     * @param c
     *            the class looking up the value
     * @param key
     *            the message key
     * @param o
     *            additional parameters
     * @return a message from the default bundle.
     */
    public static String lookup(Class<?> c, String key, Object... o) {
        String k=key.replace(' ', '_');
        return RESOURCE_HOLDER.lookup(c.getSimpleName() + "." + k, o);
    }

}
