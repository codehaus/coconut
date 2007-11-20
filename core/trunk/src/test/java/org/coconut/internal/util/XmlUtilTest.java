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

}
