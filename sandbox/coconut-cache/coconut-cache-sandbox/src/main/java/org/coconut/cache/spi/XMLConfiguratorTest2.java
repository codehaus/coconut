/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.cache.spi;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

import org.coconut.cache.CacheConfiguration;
import org.coconut.core.util.Logs;
import org.coconut.filter.Filter;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class XMLConfiguratorTest2 {

    public static void main(String[] args) throws Exception {
        CacheConfiguration cc = CacheConfiguration.create();
        cc.setName("MyCache");
        cc.setErrorHandler(new CacheErrorHandler(Logs.JDK.from("o.g.s")));
        cc.expiration().setDefaultTimeout(180, TimeUnit.SECONDS);
        cc.expiration().setRefreshInterval(3, TimeUnit.SECONDS);
        cc.expiration().setFilter(new MyFilter());
        cc.expiration().setRefreshFilter(new MyFilter());
        XmlConfigurator.getInstance().to(cc, System.out);
        XmlConfigurator.getInstance().to(cc, new FileOutputStream("c://ccache.xml"));
        CacheConfiguration c = XmlConfigurator.getInstance().from(
                new FileInputStream("c://ccache.xml"));
        System.out.println("---");
        XmlConfigurator.getInstance().to(c, System.out);

    }

    static class MyErrorHandler extends CacheErrorHandler {

    }

    public static class MyFilter implements Filter {

        /**
         * @see org.coconut.filter.Filter#accept(java.lang.Object)
         */
        public boolean accept(Object element) {
            // TODO Auto-generated method stub
            return false;
        }

    }
}
