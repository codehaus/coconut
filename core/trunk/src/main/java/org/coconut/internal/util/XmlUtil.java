/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.coconut.core.Logger;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public final class XmlUtil {
    // private final static ResourceHolder RH = ResourceHolder.fromPackage(XmlUtil.class);
    // /CLOVER:OFF
    /** Cannot instantiate. */
    private XmlUtil() {}

    // /CLOVER:ON

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

    public static boolean addAndsaveObject(Document doc, Element parent, String elementName,
            ResourceBundle bundle, Class clazz, String key, Object o) {
        if (o != null) {
            Element e = add(doc, elementName, parent);
            return saveObject(doc, e, bundle, clazz.getSimpleName() + "." + key, o);
        }
        return false;
    }

    public static boolean addAndsaveObject(Document doc, Element parent, String elementName,
            ResourceBundle bundle, String comment, Object o) {
        if (o != null) {
            Element e = add(doc, elementName, parent);
            return saveObject(doc, e, bundle, comment, o);
        }
        return false;
    }

    public static Element addAndSetText(Document doc, String name, Element parent, String text) {
        Element ee = doc.createElement(name);
        parent.appendChild(ee);
        ee.setTextContent(text);
        return ee;
    }

    public static void addComment(Document doc, ResourceBundle bundle, Class clazz, String comment,
            Node e, Object... o) {
        String c = ResourceHolder.lookupKey(bundle, clazz.getSimpleName() + "." + comment, o);
        Comment eee = doc.createComment(c);
        e.appendChild(eee);
    }

    public static void addComment(Document doc, ResourceBundle bundle, String comment, Node e,
            Object... o) {
        String c = ResourceHolder.lookupKey(bundle, comment, o);
        Comment eee = doc.createComment(c);
        e.appendChild(eee);
    }

    public static boolean attributeBooleanGet(Element e, String name, boolean defaultValue) {
        if (!e.hasAttribute(name)) {
            return defaultValue;
        } else {
            return Boolean.parseBoolean(e.getAttribute(name));
        }
    }

    /**
     * Sets an attribute on the specified element with the specified name.
     */
    public static void attributeBooleanSet(Element base, String name, boolean value,
            boolean defaultValue) {
        if (value != defaultValue) {
            base.setAttribute(name, Boolean.toString(value));
        }
    }

    public static int contentIntGet(Element e, int defaultInt) {
        if (e == null) {
            return defaultInt;
        } else {
            String text = e.getTextContent();
            int result = Integer.parseInt(text);
            return result;
        }
    }

    public static void contentIntSet(Document doc, Element base, String name, int value,
            int defaultInt) {
        if (value != defaultInt) {
            add(doc, name, base).setTextContent(Integer.toString(value));
        }
    }

    public static long contentLongGet(Element e, long defaultLong) {
        if (e == null) {
            return defaultLong;
        } else {
            String text = e.getTextContent();
            long result = Long.parseLong(text);
            return result;
        }
    }

    public static void contentLongSet(Document doc, Element base, String name, long value,
            long defaultLong) {
        if (value != defaultLong) {
            add(doc, name, base).setTextContent(Long.toString(value));
        }
    }

    public static void elementLoggerAdd(Document doc, Element base, String element, Logger logger) {
        LogHelper.saveLogger(doc, base, element, logger, "could not save logger");
    }

    public static Logger elementLoggerRead(Element base, String element) {
        Element e = getChild(element, base);
        return e == null ? null : LogHelper.readLog(e);
    }

    public static void elementTimeUnitAdd(Document doc, Element parent, String name, Long time,
            TimeUnit unit, long defaultValue) {
        if (defaultValue != time.longValue()) {
            Element e = XmlUtil.add(doc, name, parent);
            long t = time;
            UnitOfTime b = UnitOfTime.fromTimeUnit(unit);
            while (b.ordinal() != UnitOfTime.values().length) {
                UnitOfTime next = UnitOfTime.values()[b.ordinal() + 1];
                long from = next.convert(t, b);
                if (t == b.convert(from, next)) {
                    b = next;
                    t = from;
                } else {
                    break;
                }
            }
            e.setAttribute("time-unit", b.getSymbol());
            e.setTextContent(Long.toString(t));
        }
    }

    public static long elementTimeUnitRead(Element e, TimeUnit unit, long defaultTime) {
        if (e != null) {
            long val = Long.parseLong(e.getTextContent());
            UnitOfTime from = UnitOfTime.fromSiSymbol(e.getAttribute("time-unit"));
            return UnitOfTime.fromTimeUnit(unit).convert(val, from);
        } else {
            return defaultTime;
        }
    }

    public static Element getChild(String name, Element e) {
        for (int i = 0; i < e.getChildNodes().getLength(); i++) {
            if (e.getChildNodes().item(i).getNodeName().equals(name)) {
                return (Element) e.getChildNodes().item(i);
            }
        }
        return null;
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
            Constructor<T> con = clazz.getConstructor(null);
            return con.newInstance();
        }
        return null;
    }

    public static <T> T loadOptional(Element parent, String tagName, Class<T> type)
            throws Exception {
        Element e = getChild(tagName, parent);
        return loadObject(e, type);
    }

    /**
     * Pretty prints the specified XML document to the specified OutputStream.
     * 
     * @param doc
     *            the xml document to pretty print
     * @param stream
     *            the to print the document to
     * @throws TransformerException
     *             the document could not be pretty printed
     */
    public static void prettyprint(Document doc, OutputStream stream) throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StreamResult result = new StreamResult(stream);
        Transformer f = TransformerFactory.newInstance().newTransformer();
        f.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        f.setOutputProperty(OutputKeys.INDENT, "yes");
        f.transform(domSource, result);
    }

    public static String readValue(Element e, String defaultValue) {
        if (e != null) {
            return e.getTextContent();
        } else {
            return defaultValue;
        }
    }

    public static boolean saveObject(Document doc, Element e, ResourceBundle bundle,
            String comment, Object o) {
        return saveObject(doc, e, bundle, comment, "type", o);
    }

    public static boolean saveObject(Document doc, Element e, ResourceBundle bundle,
            String comment, String atrbName, Object o) {
        Constructor c = null;
        try {
            c = o.getClass().getConstructor(null);
            e.setAttribute(atrbName, o.getClass().getName());
            return true;
        } catch (NoSuchMethodException e1) {
            addComment(doc, bundle, comment, e.getParentNode(), o.getClass());
            e.getParentNode().removeChild(e);
        }
        return false;
    }
}
