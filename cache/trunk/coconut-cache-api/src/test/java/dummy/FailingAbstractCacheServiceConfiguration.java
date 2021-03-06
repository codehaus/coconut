/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package dummy;

import org.coconut.cache.spi.AbstractCacheServiceConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This is placed in this package because we need AbstractCacheServiceConfiguration that
 * is not in the org.coconut.cache package
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class FailingAbstractCacheServiceConfiguration extends AbstractCacheServiceConfiguration {
    public FailingAbstractCacheServiceConfiguration() {
        super("ff");
    }

    @Override
    protected void toXML(Document doc, Element parent) throws Exception {
        throw new Exception();
    }
}
