package org.coconut.aio;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.coconut.aio.monitor.FileMonitor;


/**
 * 
 * 
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
@SuppressWarnings("unchecked")
public class AsyncFileTest extends AioTestCase {
    public void testOpen() throws IOException {
        final AsyncFile file = getFactory().openFile();
        assertNotNull(file);
        assertFalse(file.isOpen());

        assertFalse(file.isWritable());

        assertTrue(file.getId() > 0);

        assertNull(file.getDefaultDestination());
        assertNull(file.getDefaultExecutor());
        
        file.close().getIO();
    }
    
    public void testOpenExecutor() throws IOException {
        final AsyncFile file = getFactory().openFile(OWN_THREAD);
        assertNotNull(file);
        assertFalse(file.isOpen());

        assertFalse(file.isWritable());

        assertTrue(file.getId() > 0);

        assertNull(file.getDefaultDestination());
        assertSame(OWN_THREAD, file.getDefaultExecutor());
        
        file.close().getIO();
    }
    
    public void testOpenOfferable() throws IOException {
        final AsyncFile file = getFactory().openFile(IGNORE_OFFERABLE);
        assertNotNull(file);
        assertFalse(file.isOpen());

        assertFalse(file.isWritable());

        assertTrue(file.getId() > 0);

        assertSame(IGNORE_OFFERABLE, file.getDefaultDestination());
        assertNull(file.getDefaultExecutor());
        
        file.close().getIO();
    }
    
    public void testQueue() throws IOException, InterruptedException {
        BlockingQueue q = new LinkedBlockingQueue();
        AsyncFile file = getFactory().openFile(q);
        assertNotNull(file);
        assertFalse(file.isOpen());
        assertTrue(file.getId() > 0);
        file.close();
        Object o = awaitOnQueue(q);
        assertNotNull(o);
    }
    
    public void testAttach() throws IOException {
        final AsyncFile file = getFactory().openFile();
        final Object o = new Object();
        final Object o1 = new Object();

        assertNull(file.attachment());
        assertNull(file.attach(o));
        assertSame(o, file.attachment());

        assertSame(o, file.attach(o1));
        assertSame(o1, file.attachment());

        file.close().getIO();
    }
    
    public void testSetGetMonitor() throws IOException {
        final AsyncFile file = getFactory().openFile();
        final FileMonitor monitor = new FileMonitor();
        final FileMonitor monitor1 = new FileMonitor();

        assertNull(file.getMonitor());
        assertSame(file, file.setMonitor(monitor));
        assertSame(monitor, file.getMonitor());

        assertSame(file, file.setMonitor(monitor1));
        assertSame(monitor1, file.getMonitor());

        file.close().getIO();
    }
    
    public void testDefaultMonitor() throws IOException, InterruptedException {
        final AsyncFile file = getFactory().openFile();
        final BlockingQueue q = new LinkedBlockingQueue();
        assertNull(AsyncFile.getDefaultMonitor());
        assertNull(file.getMonitor());

        file.close().getIO();
        final FileMonitor sm = new FileMonitor() {
            public void opened(AsyncFile socket) {
                q.add(socket);
            }
        };

        AsyncFile.setDefaultMonitor(sm);
        final AsyncFile file1 = getFactory().openFile();
        assertSame(file1, awaitOnQueue(q));
        assertSame(sm, AsyncFile.getDefaultMonitor());
        assertSame(sm, file1.getMonitor());

        AsyncFile.setDefaultMonitor(null);
        file1.close().getIO();
    }
    
    public void testColor() throws IOException {
        final AsyncFile file = getFactory().openFile();
        assertTrue(file.getColor() == file.getColor()); //fake
        file.close().getIO();
    }
}