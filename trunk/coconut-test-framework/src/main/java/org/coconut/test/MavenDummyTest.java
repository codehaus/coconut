/**
 * 
 */
package org.coconut.test;

import junit.framework.Test;
import junit.framework.TestResult;

/**
 * There is a bug in the maven surefire plugin that forces
 * tests to implement tests in order to accept the suite method
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class MavenDummyTest implements Test {

    public int countTestCases() {
       throw new UnsupportedOperationException();
    }

    public void run(TestResult arg0) {
        throw new UnsupportedOperationException(); 
    }

}
