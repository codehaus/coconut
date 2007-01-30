/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

// START SNIPPET: nio-server
package coconut.aio.examples.helloworld;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NioServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ch = ServerSocketChannel.open();
        ByteBuffer result = ByteBuffer.allocate(10);

        ch.socket().bind(new InetSocketAddress(12345));
        SocketChannel channel = ch.accept();

        channel.read(result);
        result.flip();
        System.out.println(new String(result.slice().array()));

        channel.close();
        ch.close();
    }
}
//END SNIPPET: nio-server
