/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//@RunWith(JMock.class)
public class XmlUtilTest {

    static final ResourceBundle bundle = ResourceBundle
            .getBundle("org.coconut.internal.util.xmlutil");

    Document doc;

    Element e;

    @Test
    public void addElement() {
        assertEquals(0, e.getChildNodes().getLength());
        XmlUtil.addElement(doc, "foo", e).setTextContent("asdf");
        assertEquals(1, e.getChildNodes().getLength());
        assertEquals("asdf", ((Element) e.getChildNodes().item(0)).getTextContent());
    }

    @Test
    public void addElementAndSetContent() {
        assertEquals(0, e.getChildNodes().getLength());
        XmlUtil.addElementAndSetContent(doc, "foo", e, "asdf");
        assertEquals(1, e.getChildNodes().getLength());
        assertEquals("asdf", ((Element) e.getChildNodes().item(0)).getTextContent());
    }

    @Test
    public void addTypedElement() {
        XmlUtil.addTypedElement(doc, e, "noting", bundle, XmlUtilTest.class, "nomsg", null);
        assertEquals(0, e.getChildNodes().getLength());

        XmlUtil.addTypedElement(doc, e, "objecttest", bundle, XmlUtilTest.class, "nomsg",
                new Object());
        assertEquals(1, e.getChildNodes().getLength());
        Element elem = (Element) e.getChildNodes().item(0);
        assertEquals("objecttest", elem.getNodeName());
        assertEquals(Object.class.getName(), elem.getAttribute("type"));

        XmlUtil.addTypedElement(doc, e, "objecttest2", bundle, XmlUtilTest.class, "nomsg",
                new HashMap());
        assertEquals(2, e.getChildNodes().getLength());
        elem = (Element) e.getChildNodes().item(1);
        assertEquals("objecttest2", elem.getNodeName());
        assertEquals(HashMap.class.getName(), elem.getAttribute("type"));

        XmlUtil.addTypedElement(doc, e, "objecttest2", bundle, XmlUtilTest.class, "msg1",
                new Integer(1));
        assertEquals(3, e.getChildNodes().getLength());
        Comment com = (Comment) e.getChildNodes().item(2);
        assertEquals("msg1", com.getTextContent());
    }

    @Test
    public void attributeBooleanGet() {
        e.setAttribute("foo", "true");
        assertTrue(XmlUtil.attributeBooleanGet(e, "foo", false));
        assertFalse(XmlUtil.attributeBooleanGet(e, "foo1", false));
    }

    @Test
    public void attributeBooleanSet() {
        XmlUtil.attributeBooleanSet(e, "foo1", true, true);
        assertFalse(e.hasAttribute("foo1"));
        XmlUtil.attributeBooleanSet(e, "foo", true, false);
        assertEquals("true", e.getAttribute("foo"));
    }

    @Test
    public void contentIntGet() {
        assertEquals(123, XmlUtil.contentIntGet(null, 123));
        e.setTextContent(Integer.toString(Integer.MAX_VALUE));
        assertEquals(Integer.MAX_VALUE, XmlUtil.contentIntGet(e, 0));
    }

    @Test
    public void contentIntSet() {
        XmlUtil.contentIntSet(doc, e, "boo", 123, 321);
        assertEquals(1, e.getChildNodes().getLength());
        assertEquals("123", ((Element) e.getChildNodes().item(0)).getTextContent());
        XmlUtil.contentIntSet(doc, e, "boo1", 234, 234);
        assertEquals(1, e.getChildNodes().getLength());
    }

    @Test
    public void contentLongGet() {
        assertEquals(123l, XmlUtil.contentLongGet(null, 123l));
        e.setTextContent(Long.toString(Long.MAX_VALUE));
        assertEquals(Long.MAX_VALUE, XmlUtil.contentLongGet(e, 0l));
    }

    @Test
    public void contentDoubleGet() {
        assertEquals(123.0, XmlUtil.contentDoubleGet(null, 123.0));
        e.setTextContent(Double.toString(Double.MAX_VALUE));
        assertEquals(Double.MAX_VALUE, XmlUtil.contentDoubleGet(e, 0));
    }

    @Test
    public void contentLongSet() {
        XmlUtil.contentLongSet(doc, e, "boo", 123l, 321l);
        assertEquals(1, e.getChildNodes().getLength());
        assertEquals("123", ((Element) e.getChildNodes().item(0)).getTextContent());
        XmlUtil.contentLongSet(doc, e, "boo1", 234l, 234l);
        assertEquals(1, e.getChildNodes().getLength());
    }

    @Test
    public void contentStringGet() {
        assertEquals("123", XmlUtil.contentStringGet(null, "123"));
        e.setTextContent("foo");
        assertEquals("foo", XmlUtil.contentStringGet(e, "0"));
    }

    @Test
    public void elementTimeUnitAdd() {
        XmlUtil.elementTimeUnitAdd(doc, e, "tu1", 1, TimeUnit.NANOSECONDS, 1);
        assertEquals(0, e.getChildNodes().getLength());

        XmlUtil.elementTimeUnitAdd(doc, e, "tu1", 2, TimeUnit.NANOSECONDS, 1);
        assertEquals(1, e.getChildNodes().getLength());
        Element elem = (Element) e.getChildNodes().item(0);
        assertEquals("tu1", elem.getNodeName());
        assertEquals("2", elem.getTextContent());
        assertEquals("ns", elem.getAttribute("time-unit"));

        XmlUtil.elementTimeUnitAdd(doc, e, "tu2", 5l * 1000 * 1000 * 1000, TimeUnit.NANOSECONDS, 1);
        assertEquals(2, e.getChildNodes().getLength());
        elem = (Element) e.getChildNodes().item(1);
        assertEquals("tu2", elem.getNodeName());
        assertEquals("5", elem.getTextContent());
        assertEquals("s", elem.getAttribute("time-unit"));

        XmlUtil.elementTimeUnitAdd(doc, e, "tu3", 5005, TimeUnit.MILLISECONDS, 1);
        assertEquals(3, e.getChildNodes().getLength());
        elem = (Element) e.getChildNodes().item(2);
        assertEquals("tu3", elem.getNodeName());
        assertEquals("5005", elem.getTextContent());
        assertEquals("ms", elem.getAttribute("time-unit"));
    }

    @Test
    public void getChild() {
        assertNull(XmlUtil.getChild("a2", e));
        XmlUtil.addElementAndSetContent(doc, "a1", e, "b1");
        XmlUtil.addElementAndSetContent(doc, "a2", e, "b2");
        XmlUtil.addElementAndSetContent(doc, "a3", e, "b3");
        assertEquals("b2", XmlUtil.getChild("a2", e).getTextContent());
        assertEquals("b3", XmlUtil.getChild("a3", e).getTextContent());
        assertNull(XmlUtil.getChild("a4", e));
    }

    @Test
    public void getChildren() {
        assertEquals(0, XmlUtil.getChildren("a2", null).size());
        XmlUtil.addElementAndSetContent(doc, "a1", e, "b1");
        XmlUtil.addElementAndSetContent(doc, "a2", e, "b2");
        XmlUtil.addElementAndSetContent(doc, "a2", e, "b3");
        assertEquals(2, XmlUtil.getChildren("a2", e).size());
        assertEquals(1, XmlUtil.getChildren("a1", e).size());
        assertEquals(0, XmlUtil.getChildren("a4", e).size());
    }

    @Test
    public void loadObject() throws Exception {
        assertNull(XmlUtil.loadObject(null, Object.class));
        Element elem1 = XmlUtil.addElement(doc, "foo", e);
        Element elem2 = XmlUtil.addElement(doc, "foo2", e);
        elem1.setAttribute("type", Object.class.getName());
        elem2.setAttribute("type", HashMap.class.getName());
        assertEquals(Object.class, XmlUtil.loadObject(elem1, Object.class).getClass());
        assertEquals(HashMap.class, XmlUtil.loadObject(elem2, HashMap.class).getClass());

    }

    @Test
    public void loadChildObject() throws Exception {
        Element elem1 = XmlUtil.addElement(doc, "foo", e);
        Element elem2 = XmlUtil.addElement(doc, "foo2", e);
        elem1.setAttribute("type", Object.class.getName());
        elem2.setAttribute("type", HashMap.class.getName());
        assertEquals(Object.class, XmlUtil.loadChildObject(e, "foo", Object.class).getClass());
        assertEquals(HashMap.class, XmlUtil.loadChildObject(e, "foo2", HashMap.class).getClass());
    }

    @Test
    public void elementTimeUnitGet() {
        assertEquals(1l, XmlUtil.elementTimeUnitRead(null, TimeUnit.NANOSECONDS, 1));
        Element elem = XmlUtil.addElement(doc, "foo", e);
        elem.setAttribute("time-unit", "s");
        elem.setTextContent("123");
        assertEquals(1, e.getChildNodes().getLength());
        assertEquals(123l, XmlUtil.elementTimeUnitRead(elem, TimeUnit.SECONDS, 1));
        assertEquals(123l * 1000, XmlUtil.elementTimeUnitRead(elem, TimeUnit.MILLISECONDS, 1));
    }

    @Test
    public void prettyprint() throws TransformerException {
        // I'm not to sure about this test.
        //
        e.setAttribute("a", "b");
        e.setTextContent("c");
        String pp = XmlUtil.prettyprint(doc);
        assertTrue(pp.contains("<element a=\"b\">c</element>"));
    }

    @Before
    public void setup() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        doc = builder.newDocument();
        e = doc.createElement("element");
        doc.appendChild(e);
    }
}
