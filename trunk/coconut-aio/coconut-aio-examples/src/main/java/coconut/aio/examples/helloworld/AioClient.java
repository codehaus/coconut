/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */

// START SNIPPET: aio-client
package coconut.aio.examples.helloworld;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import coconut.aio.AsyncSocket;

public class AioClient {
    public static void main(String[] args) throws IOException {
        AsyncSocket socket = AsyncSocket.open();
        InetSocketAddress adr = new InetSocketAddress(InetAddress.getLocalHost(), 12345);
        socket.connect(adr).getIO();
        socket.write(ByteBuffer.wrap("Helloworld".getBytes())).getIO();
        socket.close();
    }
}
//END SNIPPET: aio-client
