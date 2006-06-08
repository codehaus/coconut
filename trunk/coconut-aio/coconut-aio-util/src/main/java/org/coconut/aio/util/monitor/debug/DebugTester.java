package org.coconut.aio.util.monitor.debug;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.coconut.aio.AsyncSocket;
import org.coconut.aio.AsyncSocketBindTest;
import org.coconut.aio.AsyncSocketCloseTest;
import org.coconut.aio.AsyncSocketConnectTest;
import org.coconut.aio.AsyncSocketGroupManagementTest;
import org.coconut.aio.AsyncSocketGroupTest;
import org.coconut.aio.AsyncSocketManagementTest;
import org.coconut.aio.AsyncSocketReadTest;
import org.coconut.aio.AsyncSocketTest;
import org.coconut.aio.AsyncSocketWriteLimitTest;
import org.coconut.aio.AsyncSocketWriteTest;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class DebugTester {
    public static void main(String[] args) throws Exception {
        AsyncSocket.setDefaultMonitor(new DebugSocketMonitor(System.err));
        
        
        TestSuite suite = new TestSuite("AIO Unit Tests");

        suite.addTest(new TestSuite(AsyncSocketBindTest.class));
        suite.addTest(new TestSuite(AsyncSocketConnectTest.class));
        suite.addTest(new TestSuite(AsyncSocketCloseTest.class));
        suite.addTest(new TestSuite(AsyncSocketGroupManagementTest.class));
        suite.addTest(new TestSuite(AsyncSocketGroupTest.class));
        suite.addTest(new TestSuite(AsyncSocketManagementTest.class));
        suite.addTest(new TestSuite(AsyncSocketReadTest.class));
        suite.addTest(new TestSuite(AsyncSocketTest.class));
        suite.addTest(new TestSuite(AsyncSocketWriteLimitTest.class));
        suite.addTest(new TestSuite(AsyncSocketWriteTest.class));

        TestRunner.run(suite);
    }
}