/* Copyright 2004 - 2007 Kasper Nielsen <kasper@codehaus.org> Licensed under 
 * the Apache 2.0 License, see http://coconut.codehaus.org/license.
 */
package org.coconut.management2.service.net;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.coconut.core.util.EventUtils;
import org.coconut.management.annotation.ManagedAttribute;
import org.coconut.management2.service.ServiceCheckStatus;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen</a>
 * @version $Id: Cache.java,v 1.2 2005/04/27 15:49:16 kasper Exp $
 */
public class SmtpServiceChecker extends AbstractTcpServiceChecker {
    public static final String NAME = "net.smtp";

    public static final String DESCRIPTION = "Checks whether or not a specified smtp instance is fully functional";

    public static final String SMTP_AUTH_LOGIN = "AUTH LOGIN\r\n";

    public static final String SMTP_EHLO = "EHLO ";

    public static final String SMTP_EXPECTED = "220";

    public static final String SMTP_HELO = "HELO ";

    public static final int SMTP_PORT = 25;

    public static final String SMTP_QUIT = "QUIT\r\n";

    public static final String SMTP_STARTTLS = "STARTTLS\r\n";

    private String expected = SMTP_EXPECTED;

    private boolean useSSL;

    public SmtpServiceChecker() {
        setPort(SMTP_PORT);
    }

    public static void main(String[] args) throws Exception {
        SmtpServiceChecker check = new SmtpServiceChecker();

        check.setHostName("mail.cph-metronet.dk");
        check.setConnectTimeout(10000);
        check.setUseSSL(false);
        check.createAndRun(EventUtils.toSystemOutSafe());
//        ExecutorService es = Executors.newSingleThreadExecutor();
//
//        // check.setHostName("mail.isst.edu");
//        for (int i = 0; i < 1; i++) {
//            AbstractServiceCheckerSession s = check.create(EventHandlers
//                    .toSystemOutSafe());
//            es.submit(s);
//            s.awaitTermination(100, TimeUnit.MILLISECONDS);
//            // s.cancel(true);
//            s.awaitTermination(10000, TimeUnit.MILLISECONDS);
//            // System.out.println(s);
//        }
//        es.shutdown();
//
//        System.out.println("bye");
    }

    public static <V> V getAndIgnoreTimeoutException(Future<V> f, long timeout,
            TimeUnit unit) throws InterruptedException, ExecutionException {
        try {
            return f.get(timeout, unit);
        } catch (TimeoutException e) {
            return null; // ignore
        }
    }

    static class SmtpServiceCheckerSession extends AbstractTcpServiceCheckerSession {
        private volatile String expected = SMTP_EXPECTED;

        private volatile boolean useSSL;

        /**
         * @see org.coconut.management2.service.AbstractServiceCheckerSession#doRun()
         */
        @Override
        protected Boolean doRun() {

            String host = getPort() == SMTP_PORT ? getHostName() : getHostName()
                    + ", port = " + getPort();
            connect();
            String response = readline();
            try {
                // CHECK initial welcome string
                if (!response.startsWith(expected)) {
                    String warning = "Invalid SMTP response received from host, expected something starting with '"
                            + expected + "' but was '" + response + "'";
                    setWarning(warning);
                }
                // Check for TLS

                if (!useSSL) {
                    sendNow(SMTP_HELO + "[127.0.0.1]" + "\r\n");
                    response = readline();
                } else {
                    sendNow(SMTP_EHLO + "[127.0.0.1]" + "\r\n");
                    boolean foundTLS = false;
                    do {
                        response = readline();
                        foundTLS |= response.startsWith("250 STARTTLS")
                                || response.startsWith("250-STARTTLS");
                    } while (!response.startsWith("250 "));

                    if (useSSL && !foundTLS) {
                        String warning = "TLS not supported by server (" + host
                                + ") but support was expected";
                        setWarning(warning);
                    }
                }

                setStatus(ServiceCheckStatus.OK);
            } finally {
                if (isConnected()) {
                    sendNow(SMTP_QUIT);
                    readline(); // read bye
                }
                close();
            }
            if (getStatus() == ServiceCheckStatus.OK) {
                setOk("SMTP Service Is OK");
            }
            return true;
        }

        /**
         * @return the expected
         */
        public String getExpected() {
            return expected;
        }

        /**
         * @return the useSSL
         */
        public boolean isUseSSL() {
            return useSSL;
        }

    }

    /**
     * @return the port
     */
    @ManagedAttribute(defaultValue = "port", description = "The port used for connecting, the default for SMTP is 25")
    public int getPort() {
        return super.getPort();
    }

    /**
     * @see org.coconut.management2.service.net.AbstractTcpServiceChecker#newTcpSession()
     */
    @Override
    protected AbstractTcpServiceCheckerSession newTcpSession() {
        SmtpServiceCheckerSession session = new SmtpServiceCheckerSession();
        session.expected = expected;
        session.useSSL = useSSL;
        return session;
    }

    /**
     * @return the expected
     */
    public synchronized String getExpected() {
        return expected;
    }

    /**
     * @param expected
     *            the expected to set
     */
    public synchronized void setExpected(String expected) {
        this.expected = expected;
    }

    /**
     * @return the useSSL
     */
    public synchronized boolean isUseSSL() {
        return useSSL;
    }

    /**
     * @param useSSL
     *            the useSSL to set
     */
    public synchronized void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

}
