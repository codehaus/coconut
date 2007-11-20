package org.coconut.internal.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import static org.junit.Assert.*;

//@RunWith(JMock.class)
public class XmlUtilTest {

    Document doc;

    Element e;

    @Before
    public void setup() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        doc = builder.newDocument();
        e = doc.createElement("element");
    }

    @Test
    public void attributeBooleanSet() {
        XmlUtil.attributeBooleanSet(e, "foo1", true, true);
        assertFalse(e.hasAttribute("foo1"));
        XmlUtil.attributeBooleanSet(e, "foo", true, false);
        assertEquals("true", e.getAttribute("foo"));
    }

    @Test
    public void attributeBooleanGet() {
        e.setAttribute("foo", "true");
        assertTrue(XmlUtil.attributeBooleanGet(e, "foo", false));
        assertFalse(XmlUtil.attributeBooleanGet(e, "foo1", false));
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
    public void contentLongGet() {
        assertEquals(123l, XmlUtil.contentLongGet(null, 123l));
        e.setTextContent(Long.toString(Long.MAX_VALUE));
        assertEquals(Long.MAX_VALUE, XmlUtil.contentLongGet(e, 0l));
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
}
