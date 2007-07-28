/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.internal.util;

import org.coconut.core.Logger;
import org.coconut.core.Loggers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public final class LogHelper {

    ///CLOVER:ON
	public final static String LOG_TYPE_ATRB = "type";
    private final static String COMMONS_LOGGING = "commons";

	private final static String JDK_LOGGING = "jul";

	private final static String LOG4J = "log4j";

	///CLOVER:OFF
    /** Cannot instantiate. */
    private LogHelper() {}


    public static Logger fromCommons(String name) {
        return Loggers.Commons.from(name);
    }
    
    public static Logger fromLog4j(String name) {
        return Loggers.Log4j.from(name);
    }
    
	public static Logger readLog(Element log) {
		if (log != null) {
			String type = log.getAttribute(LOG_TYPE_ATRB);
			if (type.equals(JDK_LOGGING)) {
				return Loggers.JDK.from(log.getTextContent());
			} else if (type.equals(LOG4J)) {
				return LogHelper.fromLog4j(log.getTextContent());
			} else {
				// commons, this should guaranteed by schema validation
				Logger l = LogHelper.fromCommons(log.getTextContent());
				return l;
			}
		}
		return null;
	}

	public static void saveLogger(Document doc, Element e, String elementName, Logger log,
			String comment) {
		if (log != null) {
			String name = Loggers.getName(log);
			Element eh = add(doc, elementName, e, name);
			if (Loggers.Log4j.isLog4jLogger(log)) {
				eh.setAttribute(LOG_TYPE_ATRB, LOG4J);
			} else if (Loggers.Commons.isCommonsLogger(log)) {
				eh.setAttribute(LOG_TYPE_ATRB, COMMONS_LOGGING);
			} else if (Loggers.JDK.isJDKLogger(log)) {
				eh.setAttribute(LOG_TYPE_ATRB, JDK_LOGGING);
			} else {
			//	addComment(doc, "errorHandler.notInstanceLog", eh, log.getClass());
			}
		}
	}



	private static Element add(Document doc, String name, Element parent) {
		Element ee = doc.createElement(name);
		parent.appendChild(ee);
		return ee;
	}

	private static Element add(Document doc, String name, Element parent, String text) {
		Element ee = doc.createElement(name);
		parent.appendChild(ee);
		ee.setTextContent(text);
		return ee;
	}
}
