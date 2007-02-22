/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import org.coconut.cache.service.event.CacheEventService;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheServiceConfiguration {

    final String tag;

    final Class serviceClass;
    public AbstractCacheServiceConfiguration(String tag, Class c) {
        this.tag = tag;
        this.serviceClass=c;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#getServiceInterface()
     */
    public final Class getServiceInterface() {
        return serviceClass;
    }
    
    protected abstract void fromXML(Document doc, Element parent);

    protected abstract void toXML(Document doc, Element parent);

    protected static void addComment(Document doc, String comment, Node e, Object... o) {
        String c = Resources.lookup(XmlConfigurator.class, comment, o);
        Comment eee = doc.createComment(c);
        e.appendChild(eee);
    }

    protected Element getChild(String name, Element e) {
        for (int i = 0; i < e.getChildNodes().getLength(); i++) {
            if (e.getChildNodes().item(i).getNodeName().equals(name)) {
                return (Element) e.getChildNodes().item(i);
            }
        }
        return null;
    }

    protected Element add(Document doc, String name, Element parent) {
        Element ee = doc.createElement(name);
        parent.appendChild(ee);
        return ee;
    }

}
