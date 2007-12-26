/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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

    public static void addComment(Document doc, ResourceBundle bundle, Class clazz, String comment,
            Node e, Object... o) {
        String c = ResourceBundleUtil.lookupKey(bundle, clazz.getSimpleName() + "." + comment, o);
        Comment eee = doc.createComment(c);
        e.appendChild(eee);
    }

    public static Element addElement(Document doc, String name, Element parent) {
        Element ee = doc.createElement(name);
        parent.appendChild(ee);
        return ee;
    }

    public static Element addElementAndSetContent(Document doc, String name, Element parent,
            String text) {
        Element ee = addElement(doc, name, parent);
        ee.setTextContent(text);
        return ee;
    }

    public static boolean addTypedElement(Document doc, Element parent, String elementName,
            ResourceBundle bundle, Class commentClazz, String commentKey, Object objectToSave) {
        if (objectToSave != null) {
            Element e = addElement(doc, elementName, parent);
            try {
                objectToSave.getClass().getConstructor((Class[]) null);
                e.setAttribute("type", objectToSave.getClass().getName());
                return true;
            } catch (NoSuchMethodException e1) {
                addComment(doc, bundle, commentClazz, commentKey, e.getParentNode(), objectToSave
                        .getClass().getName());
                e.getParentNode().removeChild(e);
            }
            return false;
        }
        return false;
    }

    public static boolean attributeBooleanGet(Element e, String name, boolean defaultValue) {
        if (!e.hasAttribute(name)) {
            return defaultValue;
        } else {
            return Boolean.parseBoolean(e.getAttribute(name));
        }
    }

    /**
     * Sets an attribute on the specified element with the specified name if the specified
     * value is different from the specified default value.
     * 
     * @param base
     *            the Element to set the attribute on
     * @param name
     *            the attribute name
     * @param value
     *            the value that attribute should be set to
     * @param defaultValue
     *            the default value of the attribute
     */
    public static void attributeBooleanSet(Element base, String name, boolean value,
            boolean defaultValue) {
        if (value != defaultValue) {
            base.setAttribute(name, Boolean.toString(value));
        }
    }

    public static double contentDoubleGet(Element e, double defaultDouble) {
        if (e == null) {
            return defaultDouble;
        } else {
            String text = e.getTextContent();
            return Double.parseDouble(text);
        }
    }

    public static int contentIntGet(Element e, int defaultInt) {
        if (e == null) {
            return defaultInt;
        } else {
            String text = e.getTextContent();
            return Integer.parseInt(text);
        }
    }

    public static void contentIntSet(Document doc, Element base, String name, int value,
            int defaultInt) {
        if (value != defaultInt) {
            addElementAndSetContent(doc, name, base, Integer.toString(value));
        }
    }

    public static long contentLongGet(Element e, long defaultLong) {
        if (e == null) {
            return defaultLong;
        } else {
            String text = e.getTextContent();
            return Long.parseLong(text);
        }
    }

    public static void contentLongSet(Document doc, Element base, String name, long value,
            long defaultLong) {
        if (value != defaultLong) {
            addElementAndSetContent(doc, name, base, Long.toString(value));
        }
    }

    public static String contentStringGet(Element e, String defaultValue) {
        if (e == null) {
            return defaultValue;
        } else {
            return e.getTextContent();
        }
    }

    public static void elementTimeUnitAdd(Document doc, Element parent, String name, long time,
            TimeUnit unit, long defaultValue) {
        if (defaultValue != time) {
            Element e = XmlUtil.addElement(doc, name, parent);
            long t = time;
            UnitOfTime b = UnitOfTime.fromTimeUnit(unit);
            for (UnitOfTime next : UnitOfTime.values()) {
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

    public static List<Element> getChildren(String name, Element e) {
        List<Element> result = new ArrayList<Element>();
        if (e != null) {
            for (int i = 0; i < e.getChildNodes().getLength(); i++) {
                if (e.getChildNodes().item(i).getNodeName().equals(name)) {
                    result.add((Element) e.getChildNodes().item(i));
                }
            }
        }
        return result;
    }

    public static <T> T loadChildObject(Element parent, String tagName, Class<T> type)
            throws Exception {
        Element e = getChild(tagName, parent);
        return loadObject(e, type);
    }

    public static <T> T loadObject(Element e, Class<T> type) throws InstantiationException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            ClassNotFoundException {
        if (e != null) {
            String c = e.getAttribute("type");
            Class<T> clazz = (Class) Class.forName(c);
            Constructor<T> con = clazz.getConstructor((Class[]) null);
            return con.newInstance();
        }
        return null;
    }

    /**
     * Converts the specified {@link Document} to a string.
     * 
     * @param doc
     *            the Document to convert
     * @return a String representing the specified Document
     * @throws TransformerException
     *             the Document could not be converted for some reason
     */
    public static String prettyprint(Document doc) throws TransformerException {
        ByteArrayOutputStream sos = new ByteArrayOutputStream();
        XmlUtil.prettyprint(doc, sos);
        return new String(sos.toByteArray());
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
}
