/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.policy.offline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coconut.internal.util.tabular.Tabular2;

/**
 * Implementation of OPT (Belady Min)
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id$
 */
public class OPTPolicy<T> {

    private final ArrayList<T> requestList = new ArrayList<T>();

    private final int size;

    private Map<T, Integer> map = new HashMap<T, Integer>();

    public OPTPolicy(int size) {
        this.size = size;
    }

    public static void main(String[] args) {

        OPTPolicy o = new OPTPolicy(3);
        o.access(1, 2, 3, 2, 4, 3, 5, 1, 3, 1);
        o.print();
    }

    public void print() {
        Set<T> s = new HashSet<T>(requestList);
        List<T> unique = new ArrayList<T>(s);
        Collections.sort((List) unique);
        System.out.print("  ");
        Tabular2 t = new Tabular2(unique.size() + 1, requestList.size() + 1,
                true, true);

        String[] arrayHeader = new String[requestList.size() + 1];
        t.set(0, 0, "key/time");
        for (int i = 0; i < requestList.size(); i++) {
            t.set(0, i + 1, "" + (i + 1));
        }
        for (int i = 0; i < unique.size(); i++) {
            map.put(unique.get(i), i + 1);
            t.set(i + 1, 0, unique.get(i).toString());
        }

        for (int i = 0; i < requestList.size(); i++) {
            int row = map.get(requestList.get(i));
            t.set(row, i + 1, "*");
        }
        System.out.println(t);
    }

    public void access(T... i) {
        for (int j = 0; j < i.length; j++) {
            requestList.add(i[j]);
        }
    }

    public T evictAt(int i) {
        // todo we can cache current set. going from o(n^2) to o(n) for each
        // call to evictAt

        // we can also make a backwards algorithm... o(n)
        HashSet<T> currentSet = new HashSet<T>();
        for (int j = 0; j < requestList.size(); j++) {
            T candidate = requestList.get(j);
            T evict = null;
            if (!currentSet.contains(candidate)) {
                currentSet.add(candidate); // we might remove it again
                if (currentSet.size() > size) {
                    HashSet<T> evictFrom = new HashSet<T>(currentSet);
                    for (int k = j + 1; k < requestList.size(); k++) {
                        T c = requestList.get(k);
                        evictFrom.remove(c);
                        if (evictFrom.size() == 1) {
                            evict = (T) evictFrom.toArray()[0];
                            break;
                        }
                    }
                }
            }
            if (j == i) {
                return evict;
            } else {
                if (evict != null) {
                    currentSet.remove(evict);
                }
            }
        }
        return null;
    }

}
