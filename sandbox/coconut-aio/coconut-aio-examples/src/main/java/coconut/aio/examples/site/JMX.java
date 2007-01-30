/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

package coconut.aio.examples.site;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;

import org.coconut.aio.AsyncSocket;
import org.coconut.aio.management.ManagementFactory;
import org.coconut.aio.management.SocketMXBean;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class JMX {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ch = ServerSocketChannel.open();
        ch.socket().bind(new InetSocketAddress("localhost", 12345));
        (new JMX()).JXMTest1();
        ch.accept();
    }
    public void JXMTest1() throws IOException {
//START SNIPPET: jmx-simple
SocketMXBean mxBean = ManagementFactory.getSocketMXBean();
AsyncSocket socket = AsyncSocket.open();
socket.connect(new InetSocketAddress("localhost", 12345)).getIO();

System.out.println(mxBean.getBytesWritten(socket.getId()) + " bytes written");
socket.writeAsync(ByteBuffer.wrap("HelloWorld".getBytes())).getIO();
System.out.println(mxBean.getBytesWritten(socket.getId()) + " bytes written");
//END SNIPPET: jmx-simple
    }
}