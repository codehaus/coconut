/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.lang.reflect.Constructor;
import java.util.ResourceBundle;

import org.coconut.core.Logger;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class XmlUtil {
    // private final static ResourceHolder RH = ResourceHolder.fromPackage(XmlUtil.class);

    public static <T> T getAttribute(Element e, String name, T defaultValue) {
        if (!e.hasAttribute(name)) {
            return defaultValue;
        } else {
            return (T) e.getAttribute(name);
        }
    }

    public static boolean getAttributeBoolean(Element e, String name, boolean defaultValue) {
        if (!e.hasAttribute(name)) {
            return defaultValue;
        } else {
            return Boolean.parseBoolean(e.getAttribute(name));
        }
    }

    public static String readValue(Element e, String defaultValue) {
        if (e != null) {
            return e.getTextContent();
        } else {
            return defaultValue;
        }
    }

    public static Element addAndSetText(Document doc, String name, Element parent,
            String text) {
        Element ee = doc.createElement(name);
        parent.appendChild(ee);
        ee.setTextContent(text);
        return ee;
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

    public static void writeBooleanAttribute(Element base, String name, boolean value,
            boolean defaultInt) {
        if (value != defaultInt) {
            base.setAttribute(name, Boolean.toString(value));
        }
    }

    public static void writeLogger(Document doc, Element base, String element,
            Logger logger) {
        if (logger != null) {
            LogHelper.saveLogger(doc, base, element, logger, "could not save logger");
        }
    }

    public static Logger readLogger(Element base, String element) {
        Element e = getChild(element, base);
        return e == null ? null : LogHelper.readLog(e);
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

    public static boolean addAndsaveObject(Document doc, Element parent,
            String elementName, ResourceBundle bundle, String comment, Object o) {
        if (o != null) {
            Element e = add(doc, elementName, parent);
            return saveObject(doc, e, bundle, comment, o);
        }
        return false;
    }

    public static boolean saveObject(Document doc, Element e, ResourceBundle bundle,
            String comment, Object o) {
        return saveObject(doc, e, bundle, comment, "type", o);
    }

    public static boolean saveObject(Document doc, Element parent, String tagName,
            ResourceBundle bundle, String comment, Object o, Object defaultObject) {
        if (o != defaultObject && o != null && !o.equals(defaultObject)) {
            return saveObject(doc, add(doc, tagName, parent), bundle, comment, "type", o);
        }
        return false;
    }

    public static boolean saveObject(Document doc, Element e, ResourceBundle bundle,
            String comment, Object o, Object defaultObject) {
        if (o != defaultObject && o != null && !o.equals(defaultObject)) {
            return saveObject(doc, e, bundle, comment, "type", o);
        }
        return false;
    }

    public static boolean saveObject(Document doc, Element e, ResourceBundle bundle,
            String comment, String atrbName, Object o) {
        Constructor c = null;
        try {
            c = o.getClass().getConstructor( null);
            e.setAttribute(atrbName, o.getClass().getName());
            return true;
        } catch (NoSuchMethodException e1) {
            addComment(doc, bundle, comment, e.getParentNode(), o.getClass());
            e.getParentNode().removeChild(e);
        }
        return false;
    }

    public static void addComment(Document doc, ResourceBundle bundle, String comment,
            Node e, Object... o) {
        String c = ResourceHolder.lookupKey(bundle, comment, o);
        Comment eee = doc.createComment(c);
        e.appendChild(eee);
    }

    public static void addComment(ResourceHolder rh, Document doc, String comment,
            Node e, Object... o) {
        String c = rh.lookup(XmlUtil.class, comment, o);
        Comment eee = doc.createComment(c);
        e.appendChild(eee);
    }

    public static <T> T loadOptional(Element parent, String tagName, Class<T> type)
            throws Exception {
        Element e = getChild(tagName, parent);
        return loadObject(e, type);
    }

    public static <T> T loadObject(Element e, Class<T> type) throws Exception {
        if (e != null) {
            String c = e.getAttribute("type");
            Class<T> clazz = null;
            try {
                clazz = (Class) Class.forName(c);
            } catch (ClassNotFoundException e1) {
                System.out.println("Class name='" + c + "'");
                throw e1;
            }
            Constructor<T> con = clazz.getConstructor( null);
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
