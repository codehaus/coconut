package org.coconut.cache.tck.eclipsesucks;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ConcreteTest extends TestCase {

	public static TestSuite suite() {
		TestSuite bts = new TestSuite();
		bts.addTestSuite(DTest.class);
		return bts;
	}
}
