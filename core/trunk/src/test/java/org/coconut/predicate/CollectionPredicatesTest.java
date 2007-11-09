/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.coconut.predicate.CollectionPredicates;
import org.coconut.predicate.Predicate;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
@RunWith(JMock.class)
public class CollectionPredicatesTest {

	Mockery context = new JUnit4Mockery();

	Map.Entry<Integer, String> e1;

	Map.Entry<Integer, String> e2;

	Map.Entry<Integer, String> e3;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		e1 = context.mock(Map.Entry.class);
		e2 = context.mock(Map.Entry.class);
		e3 = context.mock(Map.Entry.class);
	}

	@Test
	public void testKeyEqualsFilter() {
		context.checking(new Expectations() {
			{
				one(e1).getKey();
				will(returnValue(1));
				one(e2).getKey();
				will(returnValue(2));
			}
		});
		Predicate<Map.Entry<Integer, String>> f = CollectionPredicates.keyEqualsFilter(1);
		assertTrue(f.evaluate(e1));
		assertFalse(f.evaluate(e2));
	}

	@Test
	public void testAnyKeyEqualsFilter() {
		context.checking(new Expectations() {
			{
				one(e1).getKey();
				will(returnValue(1));
				one(e2).getKey();
				will(returnValue(2));
				one(e3).getKey();
				will(returnValue(3));

			}
		});
		Predicate<Map.Entry<Integer, String>> f = CollectionPredicates.anyKeyEquals(1, 2);
		assertTrue(f.evaluate(e1));
		assertTrue(f.evaluate(e2));
		assertFalse(f.evaluate(e3));
	}

	@Test
	public void testAnyKeyInCollectionEqualsFilter() {
		context.checking(new Expectations() {
			{
				one(e1).getKey();
				will(returnValue(1));
				one(e2).getKey();
				will(returnValue(2));
				one(e3).getKey();
				will(returnValue(3));

			}
		});
		Predicate<Map.Entry<Integer, String>> f = CollectionPredicates
				.anyKeyInCollection(Arrays.asList(1, 2));
		assertTrue(f.evaluate(e1));
		assertTrue(f.evaluate(e2));
		assertFalse(f.evaluate(e3));
	}

	@Test
	public void testValueEqualsFilter() {
		context.checking(new Expectations() {
			{
				one(e1).getValue();
				will(returnValue("A"));
				one(e2).getValue();
				will(returnValue("B"));
			}
		});
		Predicate<Map.Entry<Integer, String>> f = CollectionPredicates.valueEqualsFilter("A");
		assertTrue(f.evaluate(e1));
		assertFalse(f.evaluate(e2));
	}

	@Test
	public void testNullFilter() {
		Predicate f = CollectionPredicates.isNull();
		assertTrue(f.evaluate(null));
		assertFalse(f.evaluate(1));
		assertFalse(f.evaluate(f));
	}

	@Test
	public void testAnyValueEqualsFilter() {
		context.checking(new Expectations() {
			{
				one(e1).getValue();
				will(returnValue("A"));
				one(e2).getValue();
				will(returnValue("B"));
				one(e3).getValue();
				will(returnValue("C"));
			}
		});
		Predicate<Map.Entry<Integer, String>> f = CollectionPredicates.anyValueEquals("A", "B");
		assertTrue(f.evaluate(e1));
		assertTrue(f.evaluate(e2));
		assertFalse(f.evaluate(e3));
	}

	public void testAnyValueInCollectionFilter() {
		context.checking(new Expectations() {
			{
				one(e1).getValue();
				will(returnValue("A"));
				one(e2).getValue();
				will(returnValue("B"));
				one(e3).getValue();
				will(returnValue("C"));
			}
		});
		Predicate<Map.Entry<Integer, String>> f = CollectionPredicates
				.anyValueInCollection(Arrays.asList("A", "B"));
		assertTrue(f.evaluate(e1));
		assertTrue(f.evaluate(e2));
		assertFalse(f.evaluate(e3));
	}

}
