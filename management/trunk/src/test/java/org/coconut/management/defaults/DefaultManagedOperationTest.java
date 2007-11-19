package org.coconut.management.defaults;

import java.lang.reflect.Method;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DefaultManagedOperationTest {

    private final static Method DUMMY = DefaultManagedOperationTest.class.getMethods()[0];

    @Test(expected = NullPointerException.class)
    public void constructorNPEMethod() {
        new DefaultManagedOperation("foo", null, "desc", "foo");
    }

    @Test(expected = NullPointerException.class)
    public void constructorNPEObject() {
        new DefaultManagedOperation(null, DUMMY, "desc", "foo");
    }

    @Test(expected = NullPointerException.class)
    public void constructorNPEName() {
        new DefaultManagedOperation("foo", DUMMY, null, "foo");
    }

    @Test(expected = NullPointerException.class)
    public void constructorNPEDescription() {
        new DefaultManagedOperation("foo", DUMMY, "desc", null);
    }

    public void getNameDescription() {
        DefaultManagedOperation o = new DefaultManagedOperation("foo",DUMMY,  "name", "desc");
        assertEquals("name", o.getName());
        assertEquals("desc", o.getDescription());
    }
}
