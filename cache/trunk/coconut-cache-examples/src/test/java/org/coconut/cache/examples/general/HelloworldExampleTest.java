package org.coconut.cache.examples.general;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class HelloworldExampleTest {
    @Test
    public void testHelloWorld() {
        MyOutput o = new MyOutput();
        PrintStream old = System.out;
        System.setOut(new PrintStream(o));
        HelloworldExample.main(new String[0]);
        System.setOut(old);
        assertTrue(o.buf.toString().startsWith("Hello world!"));
    }

    private static class MyOutput extends OutputStream {
        StringBuffer buf = new StringBuffer();

        public void write(int b) throws IOException {
            buf.append((char) b);
        }
    }
}
