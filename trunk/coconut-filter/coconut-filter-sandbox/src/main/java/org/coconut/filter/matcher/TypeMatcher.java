/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package org.coconut.filter.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.coconut.filter.Filter;
import org.coconut.filter.Filters.IsTypeFilter;

public class TypeMatcher<K, E>  {

    private final Map<Class, K> map = new HashMap<Class, K>();

    public boolean add(K key, Filter<E> filter) {
        if (key == null) {
            throw new NullPointerException("key is null");
        } else if (filter == null) {
            throw new NullPointerException("filter is null");
        }
        if (filter instanceof IsTypeFilter) {
            IsTypeFilter f = (IsTypeFilter) filter;
            // map.put(f.)
            return true;
        } else {
            return false;
        }

    }

    public Collection<? extends K> match(E event) {
        Class clazz = event.getClass();
        // TODO Auto-generated method stub
        return null;
    }

    public boolean remove(K key) {
        // TODO Auto-generated method stub
        return false;
    }

    public static Set<Class> getClasses(final Class c) {
        final Set<Class> list = new HashSet<Class>();
        Class currentClass = c;

        while (currentClass != null) {
            list.add(currentClass);
            for (Class cl : currentClass.getInterfaces()) {
                list.addAll(getClasses(cl));
            }
            currentClass = currentClass.getSuperclass();
        }
        return list;
    }

    public static void main(String[] args) {
        System.out.println(getClasses(CopyOnWriteArrayList.class));
    }

    static Collection<Class> getAllSupers(Class from) {
        ArrayList<Class> l = new ArrayList<Class>(4);

        from.getClasses();
        return l;
    }

    static class Entry<K> {
        ArrayList<K> list = new ArrayList<K>();
    }
}
