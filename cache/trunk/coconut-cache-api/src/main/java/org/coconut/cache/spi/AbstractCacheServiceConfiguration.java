/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.coconut.cache.CacheConfiguration;
import org.coconut.cache.CacheException;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public abstract class AbstractCacheServiceConfiguration<K, V> {

    final String tag;

    final Class serviceClass;

    public AbstractCacheServiceConfiguration(String tag, Class c) {
        this.tag = tag;
        this.serviceClass = c;
    }

    /**
     * @see org.coconut.cache.spi.AbstractCacheServiceConfiguration#getServiceInterface()
     */
    public final Class getServiceInterface() {
        return serviceClass;
    }

    /**
     * The parent configuration, or <tt>null</tt> if this configuration has
     * not been registered yet.
     */
    public CacheConfiguration<K, V> c() {
        return conf;
    }

    private CacheConfiguration<K, V> conf;

    void setConfiguration(CacheConfiguration<K, V> conf) {
        this.conf = conf;
    }

    protected abstract void fromXML(Document doc, Element parent) throws Exception;

    protected abstract void toXML(Document doc, Element parent) throws Exception;

    protected static void addComment(Document doc, String comment, Node e, Object... o) {
        String c = Resources.lookup(XmlConfigurator.class, comment, o);
        Comment eee = doc.createComment(c);
        e.appendChild(eee);
    }

    protected static long readLong(Element e, long defaultLong) {
        if (e == null) {
            return defaultLong;
        } else {
            String text = e.getTextContent();
            long result = Long.parseLong(text);
            return result;
        }
    }

    protected static void writeLong(Document doc, Element base, String name, long value,
            long defaultLong) {
        if (value != defaultLong) {
            add(doc, name, base).setTextContent(Long.toString(value));
        }
    }

    protected static void writeInt(Document doc, Element base, String name, int value,
            int defaultInt) {
        if (value != defaultInt) {
            add(doc, name, base).setTextContent(Integer.toString(value));
        }
    }

    protected static int readInt(Element e, int defaultInt) {
        if (e == null) {
            return defaultInt;
        } else {
            String text = e.getTextContent();
            int result = Integer.parseInt(text);
            return result;
        }
    }

    protected static boolean saveObject(Document doc, Element e, String comment,
            String atrbName, Object o) {
        Constructor c = null;
        try {
            c = o.getClass().getConstructor(null);
            e.setAttribute(atrbName, o.getClass().getName());
            return true;
        } catch (NoSuchMethodException e1) {
            addComment(doc, comment, e.getParentNode(), o.getClass());
            e.getParentNode().removeChild(e);
        }
        return false;
    }

    protected static boolean saveObject(Document doc, Element e, String comment, Object o) {
        return saveObject(doc, e, comment, "type", o);
    }

    protected static boolean saveObject(Document doc, Element parent, String tagName,
            String comment, Object o, Object defaultObject) {
        if (o != defaultObject && o != null && !o.equals(defaultObject)) {
            return saveObject(doc, add(doc, tagName, parent), comment, "type", o);
        }
        return false;
    }

    protected static boolean saveObject(Document doc, Element e, String comment, Object o,
            Object defaultObject) {
        if (o != defaultObject && o != null && !o.equals(defaultObject)) {
            return saveObject(doc, e, comment, "type", o);
        }
        return false;
    }

    protected static  <T> T loadObject(Element e, Class<T> type) throws Exception {
        if (e != null) {
            String c = e.getAttribute("type");
            Class<T> clazz = null;
            try {
                clazz = (Class) Class.forName(c);
            } catch (ClassNotFoundException e1) {
                System.out.println("Class name='" + c + "'");
                throw e1;
            }
            Constructor<T> con = clazz.getConstructor(null);
            return con.newInstance();
        }
        return null;
    }

    protected static Element getChild(String name, Element e) {
        for (int i = 0; i < e.getChildNodes().getLength(); i++) {
            if (e.getChildNodes().item(i).getNodeName().equals(name)) {
                return (Element) e.getChildNodes().item(i);
            }
        }
        return null;
    }

    protected static Element add(Document doc, String name, Element parent) {
        Element ee = doc.createElement(name);
        parent.appendChild(ee);
        return ee;
    }

    protected static Element add(Document doc, String name, Element parent, String text) {
        Element ee = doc.createElement(name);
        ee.setTextContent(text);
        parent.appendChild(ee);
        return ee;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        ByteArrayOutputStream sos = new ByteArrayOutputStream();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement(tag);
            doc.appendChild(root);
            toXML(doc, root);
            XmlConfigurator.transform(doc, sos);
            return new String(sos.toByteArray());
        } catch (Exception e) {
            throw new CacheException("This is highly irregular, please report", e);
        }
    }
}
