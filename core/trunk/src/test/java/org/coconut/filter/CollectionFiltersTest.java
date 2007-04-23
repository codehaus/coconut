/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
@RunWith(JMock.class)
public class CollectionFiltersTest {

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
		Filter<Map.Entry<Integer, String>> f = CollectionFilters.keyEqualsFilter(1);
		assertTrue(f.accept(e1));
		assertFalse(f.accept(e2));
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
		Filter<Map.Entry<Integer, String>> f = CollectionFilters.anyKeyEquals(1, 2);
		assertTrue(f.accept(e1));
		assertTrue(f.accept(e2));
		assertFalse(f.accept(e3));
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
		Filter<Map.Entry<Integer, String>> f = CollectionFilters
				.anyKeyInCollection(Arrays.asList(1, 2));
		assertTrue(f.accept(e1));
		assertTrue(f.accept(e2));
		assertFalse(f.accept(e3));
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
		Filter<Map.Entry<Integer, String>> f = CollectionFilters.valueEqualsFilter("A");
		assertTrue(f.accept(e1));
		assertFalse(f.accept(e2));
	}

	@Test
	public void testNullFilter() {
		Filter f = CollectionFilters.isNull();
		assertTrue(f.accept(null));
		assertFalse(f.accept(1));
		assertFalse(f.accept(f));
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
		Filter<Map.Entry<Integer, String>> f = CollectionFilters.anyValueEquals("A", "B");
		assertTrue(f.accept(e1));
		assertTrue(f.accept(e2));
		assertFalse(f.accept(e3));
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
		Filter<Map.Entry<Integer, String>> f = CollectionFilters
				.anyValueInCollection(Arrays.asList("A", "B"));
		assertTrue(f.accept(e1));
		assertTrue(f.accept(e2));
		assertFalse(f.accept(e3));
	}

}
