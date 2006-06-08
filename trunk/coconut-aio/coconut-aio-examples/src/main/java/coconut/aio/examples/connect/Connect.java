package coconut.aio.examples.connect;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import coconut.aio.AsyncSocket;
import coconut.core.Callback;

/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class Connect {

    void connectFuture() throws IOException {
        AsyncSocket socket = AsyncSocket.open();
        //START SNIPPET: connect-future
        Future future = socket.connect(new InetSocketAddress("www.codehaus.org", 12345));
        //END SNIPPET: connect-future
    }

    void connectCallback() throws IOException {
        AsyncSocket socket = AsyncSocket.open(Executors.newCachedThreadPool());
        //START SNIPPET: connect-callback
        socket.connect(new InetSocketAddress("www.codehaus.org", 12345)).setCallback(new Callback() {
            public void completed(Object result) {
                System.out.println("Connect success");
            }
            public void failed(Throwable cause) {
                System.out.println("Connect failed");
                cause.printStackTrace();
            }
        });
        //END SNIPPET: connect-callback
    }
}