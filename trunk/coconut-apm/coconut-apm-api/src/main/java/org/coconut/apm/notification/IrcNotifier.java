/* Copyright 2004 - 2006 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the MIT license, see http://coconut.codehaus.org/license.
 */
package org.coconut.apm.notification;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class IrcNotifier implements Notifier{
    static final int DEFAULT_PORT = 6667;

    static final String DEFAULT_HOST = "irc.codehaus.org";

    static final String DEFAULT_NICK = "sss2";

    static final String DEFAULT_USERNAME = "asdasd";

    static final String DEFAULT_REALNAME = "Kasper N";

    static final String DEFAULT_CHANNEL = "#coconut";

    public IrcNotifier(String foo) {

    }

    private Socket s;

    private PrintWriter pw;

    public void connect() throws IOException {
        s = new Socket(DEFAULT_HOST, DEFAULT_PORT);
        pw = new PrintWriter(s.getOutputStream());
        // As dictated by RFC1459...
        // first, we send the PASS message
        // pw.send("PASS " + " " + "\r\n");

        // then we send the NICK message
        sendMsg("NICK " + DEFAULT_NICK + "\r\n");

        // finally, we send the USER message
        String d = "USER " + DEFAULT_USERNAME + " localhost " + DEFAULT_HOST + ":"
                + DEFAULT_REALNAME + "\r\n";
        sendMsg(d);

        // as a temporary kludge
        sendMsg("JOIN " + DEFAULT_CHANNEL + "\r\n");
    }

    private void sendMsg(String msg) {
        pw.println(msg);
        pw.flush();
    }

    public void send(String msg) {
        sendMsg("PRIVMSG " + DEFAULT_CHANNEL + " :" + msg + "\r\n");
    }

    public void disconnect() throws IOException {
        sendMsg("Quit\r\n");
        int i = s.getInputStream().read();
        while (i != -1) {
            System.out.print((char) i);
            i = s.getInputStream().read();
        }
        s.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        IrcNotifier in = new IrcNotifier("");
        in.connect();
        for (int i=0;i<100;i++) {
            in.send(new Date().toString());
            Thread.sleep(1000);
        }
        in.send("Helælo");
        in.send("Helælo");
        in.send("Helælo");
        in.disconnect();
    }
}
