/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */

package org.coconut.cache.store.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class Test {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println(19 >> 4);
        
        Tester t = new Tester(3);
        FileOutputStream str = new FileOutputStream("c:/test.ser");
        ObjectOutputStream oos = new ObjectOutputStream(str);
        oos.writeObject(t);
        oos.flush();
        oos.close();

        FileInputStream stri = new FileInputStream("c:/test.ser");
        ObjectInputStream is = new ObjectInputStream(stri);
        Object o = is.readObject();
        Tester tt = (Tester) o;
        System.out.println(t == tt);
        System.out.println(t.equals(tt));
    }




}
