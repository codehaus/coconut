/**
 * 
 */
package org.coconut.cache.benchmark.sample;

import java.io.PrintWriter;
import java.util.Properties;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.CompositeCacheAttributes;
import org.apache.jcs.engine.behavior.ICompositeCacheAttributes;
import org.apache.jcs.engine.control.CompositeCacheManager;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class JcsTester {
    public static void main(String[] args) throws CacheException {
        Logger.getLogger("org.apache.jcs.engine.memory.lru.LRUMemoryCache").setLevel(
                Level.OFF);
        ConsoleAppender ap = new ConsoleAppender();
        ap.setWriter(new PrintWriter(System.err));
        ap.setLayout(new SimpleLayout());

        CompositeCacheManager cc = CompositeCacheManager.getUnconfiguredInstance();
        Properties props = new Properties();
        props.put("jcs.default", "");
        cc.configure(props);
        ICompositeCacheAttributes cattr = new CompositeCacheAttributes();
        cattr.setMaxObjects(100000);

        JCS jcs = JCS.getInstance("foo", cattr);
        for (int i = 0; i < 10000; i++) {
            jcs.put(i, i);
            System.out.println(i);
        }
        for (int i = 0; i < 1000; i++) {
            System.out.println(jcs.get(i));
        }
    }
}
