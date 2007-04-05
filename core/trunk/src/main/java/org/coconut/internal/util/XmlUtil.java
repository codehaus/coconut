/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.lang.reflect.Constructor;

import org.coconut.cache.spi.Resources;
import org.coconut.cache.spi.XmlConfigurator;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class XmlUtil {
    public static Element addAndSetText(Document doc, String name, Element parent,
            String text) {
        Element ee = doc.createElement(name);
        parent.appendChild(ee);
        ee.setTextContent(text);
        return ee;
    }
    
    public static void addComment(Document doc, String comment, Node e, Object... o) {
        String c = Resources.lookup(XmlConfigurator.class, comment, o);
        Comment eee = doc.createComment(c);
        e.appendChild(eee);
    }

    public static long readLong(Element e, long defaultLong) {
        if (e == null) {
            return defaultLong;
        } else {
            String text = e.getTextContent();
            long result = Long.parseLong(text);
            return result;
        }
    }

    public static void writeLong(Document doc, Element base, String name, long value,
            long defaultLong) {
        if (value != defaultLong) {
            add(doc, name, base).setTextContent(Long.toString(value));
        }
    }

    public static void writeInt(Document doc, Element base, String name, int value,
            int defaultInt) {
        if (value != defaultInt) {
            add(doc, name, base).setTextContent(Integer.toString(value));
        }
    }

    public static int readInt(Element e, int defaultInt) {
        if (e == null) {
            return defaultInt;
        } else {
            String text = e.getTextContent();
            int result = Integer.parseInt(text);
            return result;
        }
    }

    public static boolean saveObject(Document doc, Element e, String comment,
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

    public static boolean saveObject(Document doc, Element e, String comment, Object o) {
        return saveObject(doc, e, comment, "type", o);
    }

    public static boolean saveObject(Document doc, Element parent, String tagName,
            String comment, Object o, Object defaultObject) {
        if (o != defaultObject && o != null && !o.equals(defaultObject)) {
            return saveObject(doc, add(doc, tagName, parent), comment, "type", o);
        }
        return false;
    }

    public static boolean saveObject(Document doc, Element e, String comment, Object o,
            Object defaultObject) {
        if (o != defaultObject && o != null && !o.equals(defaultObject)) {
            return saveObject(doc, e, comment, "type", o);
        }
        return false;
    }

    public static  <T> T loadObject(Element e, Class<T> type) throws Exception {
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

    public static Element getChild(String name, Element e) {
        for (int i = 0; i < e.getChildNodes().getLength(); i++) {
            if (e.getChildNodes().item(i).getNodeName().equals(name)) {
                return (Element) e.getChildNodes().item(i);
            }
        }
        return null;
    }

    public static Element add(Document doc, String name, Element parent) {
        Element ee = doc.createElement(name);
        parent.appendChild(ee);
        return ee;
    }

    public static Element add(Document doc, String name, Element parent, String text) {
        Element ee = doc.createElement(name);
        ee.setTextContent(text);
        parent.appendChild(ee);
        return ee;
    }

}
