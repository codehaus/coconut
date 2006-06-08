package org.coconut.event.bus.defaults;

import org.coconut.event.bus.AbstractEventBusTestCase;

/**
 * @author <a href="mailto:kasper@codehaus.org">Kasper Nielsen </a>
 */
public class AsynchronousEventBusTest extends AbstractEventBusTestCase{
    public void testDummy() {

    }
    //
    // public void testAsyncHandle() throws InterruptedException {
    // MutableEventBus<Number> bus = createNew();
    // ExpectOneToTenLatch latch = new ExpectOneToTenLatch();
    // Subscription s = bus.subscribe(latch, Integer.class);
    // for (int i = 0; i < 10; i++) {
    // bus.offerAsync(i);
    // }
    // latch.await(1, TimeUnit.SECONDS);
    // }
    // public void testAsyncHandleNullEvent() {
    // MutableEventBus<Number> bus = createNew();
    // try {
    // bus.offerAsync((Number) null);
    // } catch (NullPointerException npe) {
    // return;
    // }
    // fail("did not throw NullPointerException");
    // }
    // public void testAsyncHandleNullHandler() {
    // MutableEventBus<Number> bus = createNew();
    // try {
    // bus.offerAsync((NotificationExceptionHandler<Number>) null, 0);
    // } catch (NullPointerException npe) {
    // return;
    // }
    // fail("did not throw NullPointerException");
    // }
    //
    // public static class ExpectOneToTenLatch implements EventHandler {
    // private final Set<Integer> set = new HashSet<Integer>();
    // private final Lock lock = new ReentrantLock();
    // private final CountDownLatch latch = new CountDownLatch(10);
    //
    // public ExpectOneToTenLatch() {
    // for (int i = 0; i < 10; i++) {
    // set.add(i);
    // }
    // }
    // public boolean await(long timeout, TimeUnit unit) throws
    // InterruptedException {
    // return latch.await(timeout, unit);
    // }
    // /**
    // * @see coconut.core.Offerable#offer(java.lang.Integer)
    // */
    // public void handle(Object i) {
    // lock.lock();
    // try {
    // if (set.remove(i)) {
    // latch.countDown();
    // }
    // } finally {
    // lock.unlock();
    // }
    // }
    // }
    //    

    // public void testHandleNotification() {
    // MutableEventBus<Number> bus = createNew();
    // RuntimeException re = new RuntimeException();
    // Mock nhMock = mock(NotificationHandler.class);
    // Mock mock = mock(EventHandler.class);
    // Mock mock2 = mock(EventHandler.class);
    // Subscription s = bus.subscribe((EventHandler< ? super Number>)
    // mock.proxy(), Integer.class);
    // Subscription s1 = bus.subscribe((EventHandler< ? super Number>)
    // mock2.proxy(),
    // Integer.class);
    // mock.expects(once()).method("handle").with(eq(1)).will(throwException(re));
    // mock2.expects(once()).method("handle").with(eq(1));
    // // this might fail if the internal implementation deliver the events in
    // // another order. If thats the case fix these tests.
    // nhMock.expects(once()).method("notificationFailed").with(eq(1), eq(s),
    // eq(re));
    // nhMock.expects(once()).method("notified").with(eq(1), eq(s1));
    // assertFalse(bus.offer((NotificationHandler) nhMock.proxy(), 1));
    // }
    //
    // public void testHandleNotificationException() {
    // MutableEventBus<Number> bus = createNew();
    // RuntimeException re = new RuntimeException();
    // Mock nehMock = mock(NotificationExceptionHandler.class);
    // Mock mock = mock(EventHandler.class);
    // Subscription s = bus.subscribe((EventHandler< ? super Number>)
    // mock.proxy(), Integer.class);
    //
    // mock.expects(once()).method("handle").with(eq(1)).will(throwException(re));
    // mock.expects(once()).method("handle").with(eq(2)).will(throwException(re));
    // nehMock.expects(once()).method("notificationFailed").with(eq(2), eq(s),
    // eq(re));
    //
    // assertFalse(bus.offer(1));
    // assertFalse(bus.offer((NotificationExceptionHandler) nehMock.proxy(),
    // 2));
    // }
    //
    // public void testHandleNotificationNotInteresting() {
    // MutableEventBus<Number> bus = createNew();
    // Subscription s = bus.subscribe(trueOfferable, Integer.class);
    // assertTrue(bus.offer(new Float(0)));
    // }

}
